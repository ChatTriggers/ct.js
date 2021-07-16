package com.chattriggers.ctjs.triggers

import com.chattriggers.ctjs.Reference
import com.chattriggers.ctjs.engine.ILoader
import com.chattriggers.ctjs.utils.kotlin.External

@External
abstract class OnTrigger protected constructor(
    var method: Any,
    var type: TriggerType,
    protected var loader: ILoader
) : Comparable<OnTrigger> {
    var priority: Priority = Priority.NORMAL
        private set

    init {
        register()
    }

    /**
     * Sets a triggers priority using [Priority].
     * Highest runs first.
     * @param priority the priority of the trigger
     * @return the trigger for method chaining
     */
    fun setPriority(priority: Priority): OnTrigger {
        this.priority = priority
        return this
    }

    /**
     * Registers a trigger based on its type.
     * This is done automatically with TriggerRegister.
     * @return the trigger for method chaining
     */
    fun register() = apply {
        this.loader.addTrigger(this)
    }

    /**
     * Unregisters a trigger.
     * @return the trigger for method chaining
     */
    fun unregister() = apply {
        this.loader.removeTrigger(this)
    }

    protected fun callMethod(args: Array<out Any?>) {
        if (!Reference.isLoaded) return

        this.loader.trigger(this, this.method, args)
    }

    abstract fun trigger(args: Array<out Any?>)

    override fun compareTo(other: OnTrigger): Int {
        val ordCmp = priority.ordinal - other.priority.ordinal
        return if (ordCmp == 0)
            hashCode() - other.hashCode()
        else ordCmp
    }

    enum class Priority {
        //LOWEST IS RAN LAST
        HIGHEST,
        HIGH, NORMAL, LOW, LOWEST
    }
}
