package com.chattriggers.ctjs.utils.console

import com.chattriggers.ctjs.Reference
import com.chattriggers.ctjs.engine.ILoader
import com.chattriggers.ctjs.utils.config.Config
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea
import org.fife.ui.rsyntaxtextarea.SyntaxConstants
import org.fife.ui.rsyntaxtextarea.Theme
import java.awt.*
import java.awt.event.KeyEvent
import java.awt.event.KeyListener
import java.awt.event.WindowEvent
import java.io.File
import java.io.PrintStream
import javax.swing.*
import javax.swing.text.DefaultCaret

class Console(val loader: ILoader?) {
    private val frame: JFrame = JFrame("ChatTriggers ${Reference.MODVERSION} ${loader?.getLanguage()?.langName ?: "Default"} Console")
    private val taos: TextAreaOutputStream
    private val components = mutableListOf<Component>()
    private val history = mutableListOf<String>()
    private var historyOffset = 0

    val out: PrintStream
    private val textArea = JTextArea()
    private val inputField = RSyntaxTextArea(5, 1).apply {
        syntaxEditingStyle = loader?.getLanguage()?.syntaxStyle ?: SyntaxConstants.SYNTAX_STYLE_NONE
        Theme.load(this::class.java.getResourceAsStream("/org/fife/ui/rsyntaxtextarea/themes/dark.xml")).apply(this)
        margin = Insets(5, 5, 5, 5)
        isCodeFoldingEnabled = true
    }

    init {
        this.frame.defaultCloseOperation = JFrame.HIDE_ON_CLOSE

        this.taos = TextAreaOutputStream(textArea, loader?.getLanguage()?.langName ?: "default")
        textArea.isEditable = false

        textArea.margin = Insets(5, 5, 5, 5)
        textArea.autoscrolls = true
        val caret = textArea.caret as DefaultCaret
        caret.updatePolicy = DefaultCaret.ALWAYS_UPDATE

        components.add(textArea)

        out = taos.printStream

        inputField.addKeyListener(object : KeyListener {
            override fun keyTyped(e: KeyEvent) {}

            override fun keyPressed(e: KeyEvent) {}

            override fun keyReleased(e: KeyEvent) {
                if (!e.isControlDown) return

                when (e.keyCode) {
                    KeyEvent.VK_ENTER -> {
                        val command = inputField.text
                        inputField.text = ""
                        history.add(command)
                        historyOffset = 0

                        taos.println("eval> ${command.prependIndent("    > ").substring(6)}")

                        try {
                            taos.println(loader?.eval(command) ?: return)
                        } catch (e: Throwable) {
                            printStackTrace(e)
                        }
                    }

                    KeyEvent.VK_UP -> {
                        historyOffset++

                        try {
                            val message = history[history.size - historyOffset]
                            inputField.text = message
                        } catch (exception: Exception) {
                            historyOffset--
                        }
                    }

                    KeyEvent.VK_DOWN -> {
                        historyOffset--

                        if (historyOffset < 0) historyOffset = 0

                        try {
                            val message = history[history.size - historyOffset]
                            inputField.text = message
                        } catch (exception: Exception) {
                            historyOffset = 0
                            inputField.text = ""
                        }
                    }

                    KeyEvent.VK_L -> {
                        clearConsole()
                    }
                }
            }
        })

        frame.add(JScrollPane(textArea))
        frame.add(inputField, BorderLayout.SOUTH)
        frame.pack()
        frame.isVisible = false
        frame.setSize(800, 600)
    }

    fun clearConsole() {
        SwingUtilities.invokeLater {
            this.taos.clear()
        }
    }

    fun println(obj: Any) {
        SwingUtilities.invokeLater {
            try {
                out.println(obj.toString())
            } catch (exception: Exception) {
                println(obj.toString())
            }
        }
    }

    fun printStackTrace(error: Throwable) {
        SwingUtilities.invokeLater {
            try {
                if (Config.openConsoleOnError)
                    showConsole()

                val index = error.stackTrace.indexOfFirst {
                    it?.fileName?.lowercase()?.contains("jsloader") ?: false
                }

                error.stackTrace = error.stackTrace.dropLast(error.stackTrace.size - index - 1).map {
                    val fileNameIndex = it.fileName?.indexOf("ChatTriggers/modules/") ?: return@map it
                    val classNameIndex = it.className.indexOf("ChatTriggers_modules_")

                    if (fileNameIndex != -1) {
                        StackTraceElement(
                            it.className.substring(classNameIndex + 21),
                            it.methodName,
                            it.fileName!!.substring(fileNameIndex + 21),
                            it.lineNumber
                        )
                    } else it
                }.toTypedArray()

                error.printStackTrace(out)
            } catch (ignored: Throwable) {
                error.printStackTrace()
            }
        }
    }

    fun showConsole() {
        this.frame.isVisible = true

        val bg: Color
        val fg: Color

        if (Config.customTheme) {
            bg = Config.consoleBackgroundColor
            fg = Config.consoleForegroundColor
        } else {
            when (Config.consoleTheme) {
                "ashes.dark" -> {
                    bg = Color(28, 32, 35)
                    fg = Color(199, 204, 209)
                }
                "atelierforest.dark" -> {
                    bg = Color(28, 32, 35)
                    fg = Color(199, 204, 209)
                }
                "isotope.dark" -> {
                    bg = Color(0, 0, 0)
                    fg = Color(208, 208, 208)
                }
                "codeschool.dark" -> {
                    bg = Color(22, 27, 29)
                    fg = Color(126, 162, 180)
                }
                "gotham" -> {
                    bg = Color(10, 15, 20)
                    fg = Color(152, 209, 206)
                }
                "hybrid" -> {
                    bg = Color(29, 31, 33)
                    fg = Color(197, 200, 198)
                }
                "3024.light" -> {
                    bg = Color(247, 247, 247)
                    fg = Color(74, 69, 67)
                }
                "chalk.light" -> {
                    bg = Color(245, 245, 245)
                    fg = Color(48, 48, 48)
                }
                "blue" -> {
                    bg = Color(15, 18, 32)
                    fg = Color(221, 223, 235)
                }
                "slate" -> {
                    bg = Color(33, 36, 41)
                    fg = Color(193, 199, 208)
                }
                "red" -> {
                    bg = Color(26, 9, 11)
                    fg = Color(231, 210, 212)
                }
                "green" -> {
                    bg = Color(6, 10, 10)
                    fg = Color(47, 227, 149)
                }
                "aids" -> {
                    bg = Color(251, 251, 28)
                    fg = Color(192, 20, 214)
                }
                "default.dark" -> {
                    bg = Color(41, 49, 52)
                    fg = Color(208, 208, 208)
                }
                else -> {
                    bg = Color(21, 21, 21)
                    fg = Color(208, 208, 208)
                }
            }
        }

        for (comp in this.components) {
            comp.background = bg
            comp.foreground = fg
        }

        this.frame.toFront()
        this.frame.repaint()

        val chosenFont = if (Config.consolePrettyFont) FIRA_FONT.deriveFont(Config.consoleFontSize.toFloat()) else Font("DejaVu Sans Mono", Font.PLAIN, 15).deriveFont(Config.consoleFontSize.toFloat())

        textArea.font = chosenFont
        // TODO: Ligatures make everything extremely slow for some reason. Is this fixable?
//        val attrs = FIRA_FONT.attributes.apply { (this as MutableMap<TextAttribute, Any>)[TextAttribute.LIGATURES] = TextAttribute.LIGATURES_ON }
//        inputField.font = FIRA_FONT.deriveFont(attrs)
        inputField.font = chosenFont

        this.frame.toFront()
        this.frame.repaint()
    }

    protected fun finalize() {
        this.frame.defaultCloseOperation = WindowConstants.EXIT_ON_CLOSE
        this.frame.dispatchEvent(WindowEvent(frame, WindowEvent.WINDOW_CLOSING))
    }

    companion object {
        val FIRA_FONT: Font = Font.createFont(
            Font.TRUETYPE_FONT,
            System.getProperty("ct.firaFile", null)?.let { File(it).inputStream() }
                ?: this::class.java.getResourceAsStream("/FiraCode-Regular.otf")
        ).deriveFont(9f)

        init {

            GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(FIRA_FONT)
        }
    }
}
