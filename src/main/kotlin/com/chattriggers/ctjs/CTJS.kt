package com.chattriggers.ctjs

import com.chattriggers.ctjs.commands.CTCommand
import com.chattriggers.ctjs.engine.module.ModuleManager
import com.chattriggers.ctjs.engine.module.RepositoryInfo
import com.chattriggers.ctjs.loader.UriScheme
import com.chattriggers.ctjs.minecraft.listeners.ChatListener
import com.chattriggers.ctjs.minecraft.listeners.ClientListener
import com.chattriggers.ctjs.minecraft.listeners.WorldListener
import com.chattriggers.ctjs.minecraft.objects.Sound
import com.chattriggers.ctjs.minecraft.objects.gui.GuiHandler
import com.chattriggers.ctjs.minecraft.wrappers.CPS
import com.chattriggers.ctjs.triggers.TriggerType
import com.chattriggers.ctjs.utils.Config
import com.chattriggers.ctjs.utils.UpdateChecker
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import gg.essential.vigilance.Vigilance
import net.minecraftforge.client.ClientCommandHandler
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import java.io.File

@Mod(
    modid = Reference.MODID,
    name = Reference.MODNAME,
    version = Reference.MODVERSION,
    clientSideOnly = true,
    modLanguage = "Kotlin",
    modLanguageAdapter = "com.chattriggers.ctjs.utils.kotlin.KotlinAdapter"
)
object CTJS {
    val configLocation = File("./config")
    val assetsDir = File(configLocation, "ChatTriggers/images/").apply { mkdirs() }
    val sounds = mutableListOf<Sound>()
    val gson: Gson = GsonBuilder().run {
        setPrettyPrinting()
        registerTypeAdapterFactory(RepositoryInfo.typeAdapterFactory)
        create()
    }

    @Mod.EventHandler
    fun preInit(event: FMLPreInitializationEvent) {
        listOf(ChatListener, WorldListener, CPS, GuiHandler, ClientListener, UpdateChecker).forEach {
            MinecraftForge.EVENT_BUS.register(it)
        }

        UriScheme.installUriScheme()
        UriScheme.createSocketListener()
    }

    @Mod.EventHandler
    fun init(event: FMLInitializationEvent) {
        Vigilance.initialize()
        Config.preload()

        Reference.conditionalThread {
            ModuleManager.entryPass()
        }

        registerHooks()
    }

    private fun registerHooks() {
        ClientCommandHandler.instance.registerCommand(CTCommand)

        Runtime.getRuntime().addShutdownHook(Thread(TriggerType.GAME_UNLOAD::triggerAll))
    }
}
