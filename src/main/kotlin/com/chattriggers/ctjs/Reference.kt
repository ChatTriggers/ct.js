package com.chattriggers.ctjs

import com.chattriggers.ctjs.commands.CommandHandler
import com.chattriggers.ctjs.engine.ModuleManager
import com.chattriggers.ctjs.engine.PrimaryLoader
import com.chattriggers.ctjs.minecraft.imixins.IClientCommandHandler
import com.chattriggers.ctjs.minecraft.libs.ChatLib
import com.chattriggers.ctjs.minecraft.objects.display.DisplayHandler
import com.chattriggers.ctjs.minecraft.objects.gui.GuiHandler
import com.chattriggers.ctjs.minecraft.wrappers.World
import com.chattriggers.ctjs.triggers.TriggerType
import com.chattriggers.ctjs.utils.config.Config
import kotlinx.coroutines.*
import net.minecraft.launchwrapper.Launch
import net.minecraftforge.client.ClientCommandHandler

object Reference {
    const val MODID = "ct.js"
    const val MODNAME = "ChatTriggers"
    const val MODVERSION = "@MOD_VERSION@"
    val SENTRYDSN = ("https://a69c5c01577c457b88434de9b995ceec:317ddf76172b4020b80f79befe536f98@sentry.io/259416"
            + "?release=" + MODVERSION
            + "&environment=" + (if (Launch.blackboard["fml.deobfuscatedEnvironment"] as Boolean) "development" else "production")
            + "&stacktrace.app.packages=com.chattriggers,jdk.nashorn"
            + "&uncaught.handler.enabled=false")

    private var isLoaded = true

    fun reload() = load(true)

    fun unload(asCommand: Boolean = true) {
        TriggerType.WORLD_UNLOAD.triggerAll()
        TriggerType.GAME_UNLOAD.triggerAll()

        DisplayHandler.clearDisplays()
        GuiHandler.clearGuis()
        CommandHandler.getCommandList().clear()
        ModuleManager.unload()

        if (Config.clearConsoleOnLoad)
            PrimaryLoader.console.clearConsole()

        if (asCommand) {
            ChatLib.chat("&7Unloaded all of ChatTriggers")
            this.isLoaded = true
        }
    }

    @JvmOverloads
    fun load(updateCheck: Boolean = false, asCommand: Boolean = false) {
        if (!this.isLoaded) return
        this.isLoaded = false

        unload(false)

        ChatLib.chat("&cReloading ct.js scripts...")

        try {
            (ClientCommandHandler.instance as IClientCommandHandler).removeCTCommands()

            CTJS.loadConfig()

            ModuleManager.load(updateCheck, asCommand)

            ChatLib.chat("&aDone reloading scripts!")

            TriggerType.GAME_LOAD.triggerAll()
            if (World.isLoaded())
                TriggerType.WORLD_LOAD.triggerAll()

            this.isLoaded = true
        } catch (e: Exception) {
            PrimaryLoader.console.printStackTrace(e)
        }
    }
}

fun Exception.print() {
    try {
        PrimaryLoader.console.printStackTrace(this)
    } catch (exception: Exception) {
        this.printStackTrace()
    }
}

fun String.print() {
    try {
        PrimaryLoader.console.out.println(this)
    } catch (exception: Exception) {
        System.out.println(this)
    }
}