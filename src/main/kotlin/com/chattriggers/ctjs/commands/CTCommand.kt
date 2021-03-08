package com.chattriggers.ctjs.commands

import com.chattriggers.ctjs.Reference
import com.chattriggers.ctjs.engine.module.ModuleManager
import com.chattriggers.ctjs.engine.module.ModulesGui
import com.chattriggers.ctjs.minecraft.libs.ChatLib
import com.chattriggers.ctjs.minecraft.listeners.ChatListener
import com.chattriggers.ctjs.minecraft.objects.gui.GuiHandler
import com.chattriggers.ctjs.minecraft.objects.message.Message
import com.chattriggers.ctjs.minecraft.objects.message.TextComponent
import com.chattriggers.ctjs.print
import com.chattriggers.ctjs.utils.config.Config
import com.chattriggers.ctjs.utils.config.GuiConfig
import java.awt.Desktop
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection
import java.io.File
import java.io.IOException
import java.util.*

//#if MC==10809
import net.minecraft.command.CommandBase
import net.minecraft.command.CommandException
import net.minecraft.command.ICommandSender
//#else
//$$ import com.mojang.brigadier.CommandDispatcher
//$$ import com.mojang.brigadier.arguments.StringArgumentType
//$$ import com.mojang.brigadier.builder.LiteralArgumentBuilder
//$$ import com.mojang.brigadier.context.CommandContext
//$$ import net.minecraft.command.CommandSource
//$$ import net.minecraft.command.Commands
//#endif

//#if MC==10809
object CTCommand : CommandBase() {
//#else
//$$ object CTCommand {
//#endif
    private const val idFixed = 90123 // ID for dumped chat
    private var idFixedOffset = -1 // ID offset (increments)

    private val commands = listOf(
        subcommand("load", "reload") { Reference.loadCT() },
        subcommand("unload") { Reference.unloadCT() },
        subcommand("files", "file") { openFileLocation() },
        subcommand("import") {
            if (it.isEmpty()) {
                ChatLib.chat("&c/ct import [module name]")
            } else import(it[0])
        },
        subcommand("delete") {
            when {
                it.isEmpty() -> ChatLib.chat("&c/ct delete [module name]")
                ModuleManager.deleteModule(it[0]) -> ChatLib.chat("&aDeleted")
                else -> ChatLib.chat("&cFailed to delete ${it[0]}")
            }
        },
        subcommand("modules") { GuiHandler.openGui(ModulesGui) },
        subcommand("console") {
            if (it.isEmpty()) {
                ModuleManager.generalConsole.showConsole()
            } else ModuleManager.getConsole(it[0]).showConsole()
        },
        subcommand("config", "settings", "setting") { GuiHandler.openGui(GuiConfig()) },
        subcommand("sim", "simulate") { ChatLib.simulateChat(it.joinToString(" ")) },
        subcommand("dump") { dump(it) },
        subcommand("copy") { copyArgsToClipboard(it) },
        subcommand("generatebindings") { Reference.generateBindings() }
    )

    //#if MC==10809
    override fun getCommandUsage(sender: ICommandSender?) = getUsage()

    override fun getCommandName() = "chattriggers"

    override fun getCommandAliases() = mutableListOf("ct")

    override fun getRequiredPermissionLevel() = 0

    @Throws(CommandException::class)
    override fun processCommand(sender: ICommandSender?, args: Array<String>) {
        if (args.isEmpty()) {
            ChatLib.chat(getUsage())
            return
        }

        commands.firstOrNull { args[0] in it.first }?.second?.invoke(args.drop(1))
            ?: ChatLib.chat(getUsage())
    }
    //#else
    //$$ fun register(dispatcher: CommandDispatcher<CommandSource?>) {
    //$$     var command = Commands.literal("ct")
    //$$
    //$$     for ((aliases, action) in commands) {
    //$$         for (alias in aliases) {
    //$$             command = command.then(Commands.literal(alias).executes { ctx ->
    //$$                 action(ctx.nodes.map {
    //$$                     ctx.input.substring(it.range.start, it.range.end)
    //$$                 })
    //$$                 1
    //$$             })
    //$$         }
    //$$     }
    //$$
    //$$     dispatcher.register(command)
    //$$ }
    //#endif

    private fun subcommand(vararg aliases: String, executor: (List<String>) -> Unit) = Pair(aliases.toList(), executor)

    private fun import(moduleName: String) {
        if (ModuleManager.cachedModules.any { it.name.equals(moduleName, ignoreCase = true) }) {
            ChatLib.chat("&cModule $moduleName is already installed!")
        } else {
            ChatLib.chat("&cImporting ${moduleName}...")
            Reference.conditionalThread {
                val module = ModuleManager.importModule(moduleName)
                if (module == null) {
                    ChatLib.chat("&cUnable to import module $moduleName")
                } else {
                    ChatLib.chat("&aSuccessfully imported ${module.metadata.name ?: module.name}")
                    if (Config.moduleImportHelp && module.metadata.helpMessage != null) {
                        ChatLib.chat("${if (module.metadata.helpMessage.toString().length <= 384) module.metadata.helpMessage else module.metadata.helpMessage.toString().substring(0, 383)}")
                    }
                }
            }
        }
    }

    private fun getUsage() = """
        &b&m${ChatLib.getChatBreak()}
        &c/ct load &7- &oReloads all of the ChatTriggers modules.
        &c/ct import [module] &7- &oImports a module.
        &c/ct delete [module] &7- &oDeletes a module.
        &c/ct files &7- &oOpens the ChatTriggers folder.
        &c/ct modules &7- &oOpens the modules GUI.
        &c/ct console [language] &7- &oOpens the ChatTriggers console.
        &c/ct simulate [message] &7- &oSimulates a received chat message.
        &c/ct dump &7- &oDumps previous chat messages into chat.
        &c/ct settings &7- &oOpens the ChatTriggers settings.
        &c/ct &7- &oDisplays this help dialog.
        &b&m${ChatLib.getChatBreak()}
    """.trimIndent()

    private fun openFileLocation() {
        try {
            Desktop.getDesktop().open(File("./config/ChatTriggers"))
        } catch (exception: IOException) {
            exception.print()
            ChatLib.chat("&cCould not open file location")
        }
    }

    private fun dump(args: List<String>) {
        if (args.size == 1) {
            dumpChat()
            return
        }
        when (args[1].toLowerCase()) {
            "chat" -> {
                if (args.size == 2) dumpChat()
                else dumpChat(args[2].toInt())
            }
            "actionbar" -> {
                if (args.size == 2) dumpActionBar()
                else dumpActionBar(args[2].toInt())
            }
        }
    }

    private fun dumpChat(lines: Int = 100) = dumpList(ChatListener.chatHistory, lines)

    private fun dumpActionBar(lines: Int = 100) = dumpList(ChatListener.actionBarHistory, lines)

    private fun dumpList(messages: List<String>, lines: Int) {
        clearOldDump()

        var toDump = lines
        if (toDump > messages.size) toDump = messages.size
        Message("&6&m${ChatLib.getChatBreak()}").setChatLineId(this.idFixed).chat()
        var msg: String
        for (i in 0 until toDump) {
            msg = ChatLib.replaceFormatting(messages[messages.size - toDump + i])
            Message(
                TextComponent(msg)
                    .setClick("run_command", "/ct copy $msg")
                    .setHoverValue(ChatLib.addColor("&eClick here to copy this message."))
                    .setFormatted(false)
            ).setFormatted(false).setChatLineId(this.idFixed + i + 1).chat()
        }
        Message("&6&m${ChatLib.getChatBreak()}").setChatLineId(this.idFixed + lines + 1).chat()

        this.idFixedOffset = this.idFixed + lines + 1
    }

    private fun clearOldDump() {
        if (this.idFixedOffset == -1) return
        while (this.idFixedOffset >= this.idFixed)
            ChatLib.clearChat(this.idFixedOffset--)
        this.idFixedOffset = -1
    }

    private fun copyArgsToClipboard(args: List<String>) {
        clearOldDump()
        Toolkit.getDefaultToolkit().systemClipboard.setContents(StringSelection(args.joinToString(" ")), null)
    }
}
