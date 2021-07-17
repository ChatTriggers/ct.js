package com.chattriggers.ctjs.commands

import com.chattriggers.ctjs.Reference
import com.chattriggers.ctjs.engine.module.ModuleManager
import com.chattriggers.ctjs.engine.module.ModulesGui
import com.chattriggers.ctjs.minecraft.libs.ChatLib
import com.chattriggers.ctjs.minecraft.listeners.ChatListener
import com.chattriggers.ctjs.minecraft.objects.gui.GuiHandler
import com.chattriggers.ctjs.minecraft.objects.message.Message
import com.chattriggers.ctjs.minecraft.objects.message.TextComponent
import com.chattriggers.ctjs.printTraceToConsole
import com.chattriggers.ctjs.utils.Config
import gg.essential.api.utils.GuiUtil
import net.minecraft.command.CommandBase
import net.minecraft.command.CommandException
import net.minecraft.command.ICommandSender
import java.awt.Desktop
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection
import java.io.File
import java.io.IOException

object CTCommand : CommandBase() {
    private const val idFixed = 90123 // ID for dumped chat
    private var idFixedOffset = -1 // ID offset (increments)

    //#if MC<=10809
    override fun getCommandUsage(sender: ICommandSender?) = getUsage()
    //#else
    //$$ override fun getUsage(sender: ICommandSender) = getUsage()
    //#endif

    //#if MC<=10809
    override fun getCommandName() = "chattriggers"
    //#else
    //$$ override fun getName() = "chattriggers"
    //#endif

    //#if MC<=10809
    override fun getCommandAliases() = mutableListOf("ct")
    //#else
    //$$ override fun getAliases() = mutableListOf("ct")
    //#endif

    override fun getRequiredPermissionLevel() = 0

    @Throws(CommandException::class)
    //#if MC<=10809
    override fun processCommand(sender: ICommandSender?, args: Array<String>) = run(args)
    //#else
    //$$ override fun execute(server: net.minecraft.server.MinecraftServer?, sender: ICommandSender, args: Array<String>) = run(args)
    //#endif

    private fun run(args: Array<String>) {
        if (args.isEmpty()) {
            ChatLib.chat(getUsage())
            return
        }

        when (args[0].lowercase()) {
            "reload", "load" -> Reference.loadCT()
            "unload" -> Reference.unloadCT()
            "files", "file" -> openFileLocation()
            "import" ->
                if (args.size == 1) ChatLib.chat("&c/ct import [module name]")
                else import(args[1])
            "delete" ->
                if (args.size == 1) ChatLib.chat("&c/ct delete [module name]")
                else ChatLib.chat((if (ModuleManager.deleteModule(args[1])) "&aDeleted " else "&cFailed to delete ") + args[1])
            "modules" -> GuiHandler.openGui(ModulesGui)
            "console" ->
                if (args.size == 1) ModuleManager.generalConsole.showConsole()
                else ModuleManager.getConsole(args[1]).showConsole()
            "config", "settings", "setting" -> GuiUtil.open(Config.gui()!!)
            "sim", "simulate" -> ChatLib.simulateChat(args.copyOfRange(1, args.size).joinToString(" "))
            "dump" -> dump(args)
            "copy" -> copyArgsToClipboard(args)
            "generatebindings" -> Reference.generateBindings()
            else -> ChatLib.chat(getUsage())
        }
    }

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
            exception.printTraceToConsole()
            ChatLib.chat("&cCould not open file location")
        }
    }

    private fun dump(args: Array<String>) {
        if (args.size == 1) {
            dumpChat()
            return
        }
        when (args[1].lowercase()) {
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

    private fun copyArgsToClipboard(args: Array<String>) {
        clearOldDump()
        val toCopy = args.copyOfRange(1, args.size).joinToString(" ")
        Toolkit.getDefaultToolkit().systemClipboard.setContents(StringSelection(toCopy), null)
    }
}
