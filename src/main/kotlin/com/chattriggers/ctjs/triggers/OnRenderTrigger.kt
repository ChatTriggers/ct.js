package com.chattriggers.ctjs.triggers

import com.chattriggers.ctjs.engine.Lang
import com.chattriggers.ctjs.engine.Loader
import com.chattriggers.ctjs.utils.kotlin.External
import net.minecraftforge.client.event.RenderGameOverlayEvent
import org.graalvm.polyglot.Value

@External
class OnRenderTrigger(method: Value, triggerType: TriggerType, lang: Lang) : OnTrigger(method, triggerType, lang) {
    private var triggerIfCanceled: Boolean = true

    /**
     * Sets if the render trigger should run if the event has already been canceled.
     * True by default.
     * @param bool Boolean to set
     * @return the trigger object for method chaining
     */
    fun triggerIfCanceled(bool: Boolean) = apply { this.triggerIfCanceled = bool }

    override fun trigger(vararg args: Any?) {
        if (args[0] !is RenderGameOverlayEvent)
            throw IllegalArgumentException("Argument 0 must be a RenderGameOverlayEvent")

        val event = args[0] as RenderGameOverlayEvent
        if (!triggerIfCanceled && event.isCanceled) return

        callMethod(*args)
    }
}
