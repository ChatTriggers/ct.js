package com.chattriggers.ctjs.engine.module.sources

import com.chattriggers.ctjs.CTJS
import com.chattriggers.ctjs.engine.module.*
import com.chattriggers.ctjs.minecraft.libs.ChatLib
import com.chattriggers.ctjs.minecraft.wrappers.Player
import com.chattriggers.ctjs.printToConsole
import com.chattriggers.ctjs.utils.kotlin.fromJson
import org.kohsuke.github.GHRelease
import org.kohsuke.github.GitHub
import java.io.File
import java.net.MalformedURLException
import java.net.URL
import java.nio.file.*

object GitHubModuleSource : ModuleSource {
    private val github = GitHub.connectAnonymously()
    private val releaseCache = mutableMapOf<GitHubRepositoryInfo, GHRelease>()

    override fun matches(identifier: String): Boolean {
        return try {
            URL(identifier).host == "github.com"
        } catch (e: MalformedURLException) {
            false
        }
    }

    override fun shouldUpdate(module: Module): Boolean {
        val repoInfo = module.metadata.repository as? GitHubRepositoryInfo ?: return false
        val latestRelease = fetchLatestRelease(repoInfo.username, repoInfo.repoName) ?: return false

        releaseCache[repoInfo] = latestRelease

        return (latestRelease.updatedAt ?: latestRelease.createdAt).time > repoInfo.lastModification
    }

    override fun import(identifier: String): String? {
        val url = URL(identifier)
        val pathParts = url.path.drop(1).split('/')
        if (pathParts.size != 2)
            return null

        return downloadRelease(pathParts[0], pathParts[1])
    }

    override fun update(module: Module) {
        val repo = module.metadata.repository as? GitHubRepositoryInfo ?: return
        downloadRelease(repo.username, repo.repoName)
    }

    private fun fetchLatestRelease(username: String, repoName: String): GHRelease? {
        return try {
            github.getRepository("$username/$repoName").latestRelease
        } catch (e: Exception) {
            val message = "&cCould not fetch latest release for $username/$repoName"
            if (Player.getPlayer() != null) {
                ChatLib.chat(message)
            } else message.printToConsole()
            e.printStackTrace()
            null
        }
    }

    private fun downloadRelease(username: String, repo: String): String? {
        val release = fetchLatestRelease(username, repo) ?: return null

        return try {
            val connection = URL(release.zipballUrl).openConnection()
            connection.setRequestProperty("User-Agent", "Mozilla/5.0")

            val moduleFolder = ModuleSource.importModuleZip(connection.getInputStream()).let { moduleFolder ->
                // The folder name will be the name of the zip, which is formatted as "username-reponame-sha1"
                File(moduleFolder.parentFile, repo).also {
                    Files.move(moduleFolder.toPath(), it.toPath())
                }
            }
            val metadataFile = File(moduleFolder, ModuleManager.METADATA_FILE_NAME)

            val metadata = CTJS.gson.fromJson<ModuleMetadata>(metadataFile.readText())
            val newMetadata = metadata.copy(
                repository = GitHubRepositoryInfo(
                    username,
                    repo,
                    release.createdAt.time
                )
            )
            metadataFile.writeText(CTJS.gson.toJson(newMetadata))

            moduleFolder.name
        } catch (e: Exception) {
            ChatLib.chat("&The latest release for $username/$repo could not be imported as a module")
            e.printStackTrace()
            null
        }
    }
}
