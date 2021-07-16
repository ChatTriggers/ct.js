package com.chattriggers.ctjs.engine.module.sources

import com.chattriggers.ctjs.engine.module.*
import org.apache.commons.io.FileUtils
import java.io.File
import java.io.InputStream
import java.nio.file.FileSystems
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption

interface ModuleSource {
    fun matches(identifier: String): Boolean

    fun shouldUpdate(module: Module): Boolean

    /**
     * @return the real name of the module, i.e. the folder name in the modules folder
     */
    fun import(identifier: String): String?

    fun update(module: Module)

    companion object {
        fun importModuleZip(source: InputStream): File {
            val downloadZip = File(ModuleManager.modulesFolder, "currDownload.zip")

            return try {
                FileUtils.copyInputStreamToFile(source, downloadZip)

                FileSystems.newFileSystem(downloadZip.toPath(), null).use {
                    val rootFolder = Files.newDirectoryStream(it.rootDirectories.first()).iterator()
                    if (!rootFolder.hasNext()) throw Exception("Module .zip is empty")
                    val moduleFolder = rootFolder.next()
                    if (rootFolder.hasNext()) throw Exception("Module .zip has too many files")

                    val realName = moduleFolder.fileName.toString().trimEnd(File.separatorChar)
                    val realModuleFolder = File(ModuleManager.modulesFolder, realName).apply { mkdir() }

                    Files.walk(moduleFolder).forEach { path ->
                        val resolvedPath = Paths.get(ModuleManager.modulesFolder.toString(), path.toString())
                        if (!Files.isDirectory(resolvedPath))
                            Files.copy(path, resolvedPath, StandardCopyOption.REPLACE_EXISTING)
                    }

                    val metadataFile = File(realModuleFolder, ModuleManager.METADATA_FILE_NAME)
                    if (!metadataFile.exists() || !metadataFile.isFile)
                        throw Exception("Module does not contain a metadata.json file")

                    realModuleFolder
                }
            } finally {
                downloadZip.delete()
            }
        }
    }
}
