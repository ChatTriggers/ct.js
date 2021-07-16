package com.chattriggers.ctjs.triggers

import com.chattriggers.ctjs.commands.Command
import com.chattriggers.ctjs.engine.ILoader
import com.chattriggers.ctjs.utils.kotlin.External

@External
class OnCommandTrigger(method: Any, loader: ILoader) : OnTrigger(method, TriggerType.COMMAND, loader) {
    private lateinit var commandName: String
    private var tabCompletions: MutableList<String> = mutableListOf()
    private var command: Command? = null

    override fun trigger(args: Array<out Any?>) {
        if (args::javaClass == Array<String>::javaClass) throw IllegalArgumentException("Arguments must be string array")

        callMethod(args)
    }

    /**
     * Sets the tab completion options for the command.
     * This method must be used before setting the command name, otherwise, the tab completions will not be set.
     *
     * @param args all the tab completion options.
     */
    fun setTabCompletions(vararg args: String) = apply {
        this.tabCompletions = args.toMutableList()
    }

    /**
     * Sets the command name.<br></br>
     * Example:<br></br>
     * OnCommandTrigger.setCommandName("test")<br></br>
     * would result in the command being /test
     *
     * @param commandName The command name
     * @return the trigger for additional modification
     */
    fun setCommandName(commandName: String) = apply {
        this.commandName = commandName

        command?.unregister()
        command = Command(this, commandName, "/$commandName", tabCompletions)
        command!!.register()
    }

    /**
     * Alias for [setCommandName]
     *
     * @param commandName The command name
     * @return the trigger for additional modification
     */
    fun setName(commandName: String) = setCommandName(commandName)
}
