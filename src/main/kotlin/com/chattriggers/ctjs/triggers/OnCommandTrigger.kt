package com.chattriggers.ctjs.triggers

import com.chattriggers.ctjs.commands.Command
import com.chattriggers.ctjs.commands.CommandHandler
import com.chattriggers.ctjs.engine.loader.ILoader
import com.chattriggers.ctjs.utils.kotlin.External
import net.minecraftforge.client.ClientCommandHandler

@External
class OnCommandTrigger(method: Any, loader: ILoader) : OnTrigger(method, TriggerType.COMMAND, loader) {
    private lateinit var commandName: String
    private var tabCompletions: MutableList<String> = mutableListOf()
    private lateinit var command: Command

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
        reInstance()
    }

    /**
     * Alias for [setCommandName]
     *
     * @param commandName The command name
     * @return the trigger for additional modification
     */
    fun setName(commandName: String) = setCommandName(commandName)

    // helper method to re instance the command
    private fun reInstance() {
        for (command in CommandHandler.getCommandList()) {
            //#if MC<=10809
            if (command.commandName == this.commandName) {
            //#else
            //$$ if (command.name == this.commandName) {
            //#endif
                command.triggers.add(this)
                return
            }
        }

        this.command = Command(this, this.commandName, "/${this.commandName}", this.tabCompletions)
        ClientCommandHandler.instance.registerCommand(this.command)
        CommandHandler.getCommandList().add(this.command)
    }
}
