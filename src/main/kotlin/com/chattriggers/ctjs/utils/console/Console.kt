package com.chattriggers.ctjs.utils.console

import com.chattriggers.ctjs.engine.Lang
import com.chattriggers.ctjs.engine.PrimaryLoader
import com.chattriggers.ctjs.triggers.OnTrigger
import com.chattriggers.ctjs.utils.config.Config
import io.sentry.Sentry
import net.minecraft.network.ThreadQuickExitException
import java.awt.*
import java.awt.event.KeyEvent
import java.awt.event.KeyListener
import java.io.PrintStream
import javax.swing.*
import javax.swing.text.DefaultCaret

object Console {
    private val frame: JFrame = JFrame("ct.js Console")
    private val taos: TextAreaOutputStream
    private val languageSelector: JComboBox<String>
    private val components = mutableListOf<Component>()
    private val history = mutableListOf<String>()
    private var historyOffset = 0

    val out: PrintStream

    init {
        this.frame.defaultCloseOperation = JFrame.HIDE_ON_CLOSE

        val jpanel = JPanel(BorderLayout())
        val textArea = JTextArea()
        this.taos = TextAreaOutputStream(textArea)
        this.languageSelector = JComboBox(Lang.values().map { it.extension }.toTypedArray())
        textArea.isEditable = false
        textArea.font = Font("DejaVu Sans Mono", Font.PLAIN, 15)
        textArea.autoscrolls = true
        val caret = textArea.caret as DefaultCaret
        caret.updatePolicy = DefaultCaret.ALWAYS_UPDATE
        val inputField = JTextField(1)
        inputField.isFocusable = true
        inputField.caretColor = Color.WHITE

        inputField.margin = Insets(5, 5, 5, 5)
        textArea.margin = Insets(5, 5, 5, 5)

        components.add(textArea)
        components.add(inputField)
        components.add(languageSelector)

        out = taos.printStream

        inputField.addKeyListener(object : KeyListener {
            override fun keyTyped(e: KeyEvent) {}

            override fun keyPressed(e: KeyEvent) {}

            override fun keyReleased(e: KeyEvent) {
                when (e.keyCode) {
                    KeyEvent.VK_ENTER -> {val command = inputField.text
                        inputField.text = ""
                        history.add(command)
                        historyOffset = 0

                        taos.println("${languageSelector.selectedItem}> $command")

                        val language = Lang.values().first { it.extension == languageSelector.selectedItem }
                        PrimaryLoader.getLoader(language).synchronized {
                            try {
                                val value = eval(language.graalName, command)
                                taos.println(value.toString())
                            } catch (e: ThreadQuickExitException) {
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
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
                }
            }
        })

        frame.add(JScrollPane(textArea))
        jpanel.add(inputField)
        jpanel.add(languageSelector, BorderLayout.EAST)
        frame.add(jpanel, BorderLayout.SOUTH)
        frame.pack()
        frame.isVisible = false
        frame.setSize(800, 600)
    }

    fun clearConsole() {
        this.taos.clear()
    }

    fun printStackTrace(error: Throwable) {
        Sentry.capture(error)

        if (Config.openConsoleOnError) {
            showConsole()
        }

        error.printStackTrace(out)
    }

    fun printStackTrace(error: Throwable, trigger: OnTrigger) {
        Sentry.getContext().addTag(
                "method",
                trigger.method.toString()
        )

        printStackTrace(error)
    }

    fun printDeprecatedWarning(method: String) {
        out.println("WARNING: Use of deprecated method $method")
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
                    bg = Color(21, 21, 21)
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
    }
}
