package com.chattriggers.ctjs.minecraft.listeners

import com.chattriggers.ctjs.minecraft.libs.ChatLib
import com.chattriggers.ctjs.minecraft.libs.EventLib
import com.chattriggers.ctjs.printToConsole
import com.chattriggers.ctjs.triggers.TriggerType
import com.chattriggers.ctjs.utils.Config
import net.minecraftforge.client.event.ClientChatReceivedEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

object ChatListener {
    val chatHistory = mutableListOf<String>()
    val actionBarHistory = mutableListOf<String>()

    @SubscribeEvent
    fun onReceiveChat(event: ClientChatReceivedEvent) {
        when (EventLib.getType(event)) {
            in 0..1 -> {
                // save to chatHistory
                chatHistory += ChatLib.getChatMessage(event, true)
                if (chatHistory.size > 1000) chatHistory.removeAt(0)

                // normal Chat Message
                TriggerType.CHAT.triggerAll(ChatLib.getChatMessage(event, false), event)

                // print to console
                if (Config.printChatToConsole) {
                    "[CHAT] ${ChatLib.replaceFormatting(ChatLib.getChatMessage(event, true))}".printToConsole()
                }
            }
            2 -> {
                // save to actionbar history
                actionBarHistory += ChatLib.getChatMessage(event, true)
                if (actionBarHistory.size > 1000) actionBarHistory.removeAt(0)

                // action bar
                TriggerType.ACTION_BAR.triggerAll(ChatLib.getChatMessage(event, false), event)
            }
        }
    }
}
