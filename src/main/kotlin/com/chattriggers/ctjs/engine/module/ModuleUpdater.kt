package com.chattriggers.ctjs.engine.module

import com.chattriggers.ctjs.CTJS
import com.chattriggers.ctjs.engine.module.sources.CTModuleSource
import com.chattriggers.ctjs.engine.module.sources.GitHubModuleSource
import com.chattriggers.ctjs.engine.module.sources.ModuleSource
import com.chattriggers.ctjs.minecraft.libs.ChatLib
import com.chattriggers.ctjs.printToConsole
import com.chattriggers.ctjs.utils.config.Config
import java.io.File

object ModuleUpdater {
    // CTRepositoryHandler must be last in this list
    private val repositoryHandlers = listOf(GitHubModuleSource, CTModuleSource)

    private fun repositoryForModule(module: Module): ModuleSource {
        return module.metadata.repository?.handler() ?: return CTModuleSource
    }

    fun importPending() {
        val toDownload = File(ModuleManager.modulesFolder, ".to_download.txt")
        if (!toDownload.exists()) return

        toDownload.readText().split(",").filter(String::isBlank).forEach {
            import(it)
        }

        toDownload.delete()
    }

    fun update(module: Module) {
        if (!Config.autoUpdateModules) return

        "Checking for update in ${module.metadata.name}".printToConsole()

        val repositoryHandler = repositoryForModule(module)
        if (!repositoryHandler.shouldUpdate(module)) return

        repositoryHandler.update(module)

        "Updated module ${module.metadata.name}".printToConsole()

        val newMetadata = CTJS.gson.fromJson(
            File(module.folder, ModuleManager.METADATA_FILE_NAME).readText(),
            ModuleMetadata::class.java
        )

        if (Config.moduleChangelog && newMetadata.changelog != null) {
            ChatLib.chat("&a[ChatTriggers] ${newMetadata.name} has updated to version ${newMetadata.version}")
            ChatLib.chat("&aChangelog: &r${newMetadata.changelog}")
        }

        module.metadata = newMetadata

        // Try to import any dependencies, as they could have changed. Note that
        // we don't need to update them if they exist, since this is only called
        // during ct load which updates _all_ modules.
        newMetadata.requires?.forEach { import(it) }
    }

    fun import(identifier: String): List<Module> {
        if (ModuleManager.cachedModules.any { it.name == identifier }) return emptyList()

        val repositoryHandler = repositoryHandlers.first { it.matches(identifier) }

        val realModuleName = repositoryHandler.import(identifier) ?: return emptyList()

        val moduleDir = File(ModuleManager.modulesFolder, realModuleName)
        val module = ModuleManager.parseModule(moduleDir)

        ModuleManager.cachedModules.add(module)

        // Try to import any dependencies
        module.metadata.requires?.forEach { import(it) }

        return listOf(module) + (module.metadata.requires?.map(ModuleUpdater::import)?.flatten() ?: emptyList())
    }
}
