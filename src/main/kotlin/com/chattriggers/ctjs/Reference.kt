package com.chattriggers.ctjs

import com.chattriggers.ctjs.commands.Command
import com.chattriggers.ctjs.engine.module.ModuleManager
import com.chattriggers.ctjs.minecraft.libs.ChatLib
import com.chattriggers.ctjs.minecraft.libs.renderer.Renderer
import com.chattriggers.ctjs.minecraft.objects.display.DisplayHandler
import com.chattriggers.ctjs.minecraft.objects.gui.GuiHandler
import com.chattriggers.ctjs.minecraft.objects.message.Message
import com.chattriggers.ctjs.minecraft.wrappers.Client
import com.chattriggers.ctjs.minecraft.wrappers.World
import com.chattriggers.ctjs.triggers.TriggerType
import com.chattriggers.ctjs.utils.Config
import com.chattriggers.ctjs.utils.console.Console
import com.chattriggers.ctjs.utils.kotlin.External
import com.chattriggers.ctjs.utils.kotlin.times
import com.google.common.reflect.ClassPath
import me.ntrrgc.tsGenerator.TypeScriptGenerator
import java.io.File
import kotlin.concurrent.thread
import kotlin.math.roundToInt

@External
object Reference {
    const val MODID = "ct.js"
    const val MODNAME = "ChatTriggers"
    const val MODVERSION = "1.4.0"

    var isLoaded = true

    @Deprecated("Does not provide any additional functionality", ReplaceWith("loadCT"))
    @JvmStatic
    fun reloadCT() = loadCT()

    @JvmStatic
    fun unloadCT(asCommand: Boolean = true) {
        TriggerType.WORLD_UNLOAD.triggerAll()
        TriggerType.GAME_UNLOAD.triggerAll()

        isLoaded = false

        DisplayHandler.clearDisplays()
        GuiHandler.clearGuis()
        ModuleManager.teardown()

        Command.activeCommands.values.toList().forEach(Command::unregister)

        if (asCommand) {
            ChatLib.chat("&7Unloaded all of ChatTriggers")
            isLoaded = false
        }
    }

    @JvmStatic
    fun loadCT() {
        if (!isLoaded) return

        Client.getMinecraft().gameSettings.saveOptions()
        unloadCT(false)

        ChatLib.chat("&cReloading ct.js scripts...")

        printLoadCompletionStatus(0f)

        conditionalThread {
            ModuleManager.setup()
            ModuleManager.entryPass(completionListener = ::printLoadCompletionStatus)

            Client.getMinecraft().gameSettings.loadOptions()
            ChatLib.chat("&aDone reloading scripts!")
            isLoaded = true

            TriggerType.GAME_LOAD.triggerAll()
            if (World.isLoaded())
                TriggerType.WORLD_LOAD.triggerAll()
        }
    }

    private fun printLoadCompletionStatus(percentComplete: Float) {
        val completionInteger = (percentComplete * 100).roundToInt()
        val prefix = "$completionInteger% ["
        val postfix = "]"

        val charWidth = Renderer.getStringWidth("=")
        val availableWidth = ChatLib.getChatWidth() - Renderer.getStringWidth(prefix + postfix)
        val correctLength = availableWidth / charWidth
        val completedLength = (percentComplete * correctLength).roundToInt()
        val fullWidth = "=" * completedLength
        val spaceWidth = Renderer.getStringWidth(" ")
        val spaceLeft = (availableWidth - completedLength * charWidth) / spaceWidth
        val padding = " " * spaceLeft

        val correctLine = "&c$prefix$fullWidth$padding$postfix"

        Message(correctLine).setChatLineId(28445).chat()
    }

    @JvmStatic
    fun conditionalThread(block: () -> Unit) {
        if (Config.threadedLoading) {
            thread { 
                try {
                    block()
                } catch (e: Throwable) {
                    e.printTraceToConsole()
                }
            }
        } else {
            block()
        }
    }

    internal fun generateBindings() {
        val classpath = ClassPath.from(this::class.java.classLoader)
        val externalClasses = classpath.getTopLevelClassesRecursive("com.chattriggers.ctjs").map {
            it.load()
        }.filter { it.isAnnotationPresent(External::class.java) }.map { it.kotlin }

        val generator = TypeScriptGenerator(rootClasses = externalClasses)
        File(CTJS.assetsDir.parent, "typings.d.ts").apply { createNewFile() }.writeText(generator.definitionsText)
    }
}

fun Any.printToConsole(console: Console = ModuleManager.generalConsole) = console.println(this)
fun Throwable.printTraceToConsole(console: Console = ModuleManager.generalConsole) = console.printStackTrace(this)
