package com.chattriggers.ctjs.utils

import com.chattriggers.ctjs.CTJS
import com.chattriggers.ctjs.engine.module.sources.ModuleSource
import gg.essential.vigilance.Vigilant
import gg.essential.vigilance.data.*
import java.awt.Color
import java.io.File

object Config : Vigilant(File(CTJS.configLocation, "ChatTriggers.toml")) {
    @Property(
        PropertyType.TEXT,
        name = "Modules Folders",
        category = "General",
        description = "Folder where CT modules are stored",
    )
    var modulesFolder: String = "./config/ChatTriggers/modules"

    @Property(
        PropertyType.SWITCH,
        name = "Threaded loading",
        category = "General",
        description = "Load CT modules in a background thread"
    )
    var threadedLoading = true

    @Property(
        PropertyType.SWITCH,
        name = "Show module help on import",
        category = "General",
        description = "If a module is imported and it has a help message, display it in chat"
    )
    var moduleImportHelp = true

    @Property(
        PropertyType.SWITCH,
        name = "Show module changelog on update",
        category = "General",
        description = "If a module is updated and it has a changelog, display it in chat"
    )
    var moduleChangelog = true

    @Property(
        PropertyType.SWITCH,
        name = "Print chat to console",
        category = "Console",
        description = "Prints the user's chat messages (with explicit color codes) to the general console for easy copy-pasting",
    )
    var printChatToConsole = true

    @Property(
        PropertyType.SWITCH,
        name = "Show updates in chat",
        category = "General",
        description = "Show CT module import/update messages in the chat",
    )
    var showUpdatesInChat = true

    @Property(
        PropertyType.SWITCH,
        name = "Auto-update modules",
        category = "General",
        description = "Check for and download module updates every time CT loads",
    )
    var autoUpdateModules = true

    @Property(
        PropertyType.SWITCH,
        name = "Clear console on CT load",
        category = "Console",
    )
    var clearConsoleOnLoad = true

    @Property(
        PropertyType.SWITCH,
        name = "Open console on error",
        category = "Console",
        description = "Opens the language-specific console if there is an error in a module",
    )
    var openConsoleOnError = false

    @Property(
        PropertyType.SWITCH,
        name = "Use Fira Code font for console",
        category = "Console",
    )
    var consoleFiraCodeFont = true

    @Property(
        PropertyType.NUMBER,
        name = "Console font size",
        category = "Console",
        min = 6,
        max = 32,
    )
    var consoleFontSize = 9

    @Property(
        PropertyType.SWITCH,
        name = "Use custom console theme",
        category = "Console",
    )
    var customTheme = false

    @Property(
        PropertyType.TEXT,
        name = "Console custom theme",
        category = "Console",
    )
    var consoleTheme = "default.dark"

    @Property(
        PropertyType.COLOR,
        name = "Console foreground color",
        category = "Console",
    )
    var consoleForegroundColor = Color(208, 208, 208)

    @Property(
        PropertyType.COLOR,
        name = "Console background color",
        category = "Console",
    )
    var consoleBackgroundColor = Color(21, 21, 21)
}
