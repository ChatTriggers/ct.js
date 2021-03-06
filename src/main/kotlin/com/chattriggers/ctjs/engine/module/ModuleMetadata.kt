package com.chattriggers.ctjs.engine.module

import com.chattriggers.ctjs.engine.loader.CTRepositoryHandler
import com.chattriggers.ctjs.engine.loader.GitHubRepositoryHandler
import com.chattriggers.ctjs.engine.loader.RepositoryHandler
import com.chattriggers.ctjs.utils.RuntimeTypeAdapterFactory
import com.google.gson.annotations.SerializedName
import java.util.*

data class ModuleMetadata(
    val name: String? = null,
    val version: String? = null,
    var entry: String? = null,
    val asmEntry: String? = null,
    val asmExposedFunctions: Map<String, String>? = null,
    val tags: ArrayList<String>? = null,
    val pictureLink: String? = null,
    val creator: String? = null,
    val description: String? = null,
    val requires: ArrayList<String>? = null,
    val helpMessage: String? = null,
    val changelog: String? = null,
    val ignored: ArrayList<String>? = null,
    var isRequired: Boolean = false,
    val repository: RepositoryInfo? = null
)

sealed class RepositoryInfo {
    // This must be a function because the RuntimeTypeAdapterFactory creates
    // objects without invoking their constructor. If it was a property, it
    // would never actually get assigned
    abstract fun handler(): RepositoryHandler

    companion object {
        val typeAdapterFactory = RuntimeTypeAdapterFactory(RepositoryInfo::class.java)
            .registerSubtype(CTRepositoryInfo::class.java, "ct")
            .registerSubtype(GitHubRepositoryInfo::class.java, "github")
    }
}

class CTRepositoryInfo(val name: String, val version: String) : RepositoryInfo() {
    override fun handler() = CTRepositoryHandler
}

class GitHubRepositoryInfo(
    val username: String,
    val repoName: String,
    val lastModification: Long
) : RepositoryInfo() {
    override fun handler() = GitHubRepositoryHandler
}
