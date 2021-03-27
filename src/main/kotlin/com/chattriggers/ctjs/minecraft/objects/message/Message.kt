package com.chattriggers.ctjs.minecraft.objects.message

import com.chattriggers.ctjs.minecraft.libs.ChatLib
import com.chattriggers.ctjs.minecraft.wrappers.Client
import com.chattriggers.ctjs.minecraft.wrappers.Player
import com.chattriggers.ctjs.minecraft.wrappers.objects.inventory.Item
import com.chattriggers.ctjs.utils.kotlin.External
import com.chattriggers.ctjs.utils.kotlin.MCChatPacket
import com.chattriggers.ctjs.utils.kotlin.MCITextComponent
import com.chattriggers.ctjs.utils.kotlin.MCStringTextComponent
import net.minecraftforge.client.event.ClientChatReceivedEvent

//#if MC==11604
//$$ import net.minecraft.util.text.ChatType
//$$ import net.minecraft.util.IReorderingProcessor
//#endif

@External
class Message {
    private lateinit var chatMessage: MCITextComponent

    private var messageParts = mutableListOf<TextComponent>()
    private var chatLineId = -1
    private var recursive = false
    private var formatted = true

    /**
     * Creates a new Message object from a chat event.
     * @param event the chat event
     */
    constructor(event: ClientChatReceivedEvent) : this(event.message)

    /**
     * Creates a new Message object from an IChatComponent.
     * @param component the IChatComponent
     */
    constructor(component: MCITextComponent) {
        if (component.siblings.isEmpty()) {
            this.messageParts.add(TextComponent(component))
        } else {
            this.messageParts.addAll(component.siblings.map { TextComponent(it) })
        }
    }

    /**
     * Creates a new Message object in parts of TextComponents or Strings.
     * @param messageParts the list of TextComponents or Strings
     */
    constructor(messageParts: ArrayList<Any>) {
        this.messageParts.addAll(messageParts.map {
            when (it) {
                is String -> TextComponent(it)
                is TextComponent -> it
                is Item -> it.getTextComponent()
                else -> return
            }
        })
    }

    /**
     * Creates a new Message object in parts of TextComponents or Strings.
     * @param components the TextComponents or Strings
     */
    constructor(vararg components: Any) : this(ArrayList(components.asList()))

    //#if MC==11604
    //$$ internal constructor(processor: IReorderingProcessor) {
    //$$     val consumer = TextComponent.StyledCharacterConsumer()
    //$$     processor.accept(consumer)
    //$$     messageParts.add(TextComponent(consumer.toString()))
    //$$ }
    //$$
    //$$ fun getReorderingProcessor() = IReorderingProcessor { consumer ->
    //$$     // TODO: This almost certainly does not work
    //$$     var index = 0
    //$$     for (part in getMessageParts()) {
    //$$         part.unformattedText.forEach {
    //$$             consumer.accept(index++, part.style, it.toInt())
    //$$         }
    //$$     }
    //$$     true
    //$$ }
    //#endif

    /**
     * @return the parsed message as an _root_ide_package_.com.chattriggers.ctjs.utils.kotlin.MCITextComponent
     */
    fun getChatMessage(): MCITextComponent {
        parseMessage()
        return this.chatMessage
    }

    /**
     * @return the formatted text of the parsed message
     */
    fun getFormattedText(): String = getChatMessage().formattedText

    /**
     * @return the unformatted text of the parsed message
     */
    fun getUnformattedText(): String = getChatMessage().unformattedText

    /**
     * @return the message [TextComponent] parts as a list.
     */
    fun getMessageParts(): List<TextComponent> = this.messageParts

    /**
     * @return the chat line ID of the message
     */
    fun getChatLineId(): Int = this.chatLineId

    /**
     * Sets the chat line ID of the message. Useful for updating an already sent chat message.
     */
    fun setChatLineId(id: Int) = apply { this.chatLineId = id }

    /**
     * @return true if the message can trip other triggers.
     */
    fun isRecursive(): Boolean = this.recursive

    /**
     * Sets whether or not the message can trip other triggers.
     * @param recursive true if message can trip other triggers.
     */
    fun setRecursive(recursive: Boolean) = apply { this.recursive = recursive }

    /**
     * @return true if the message is formatted
     */
    fun isFormatted(): Boolean = this.formatted

    /**
     * Sets if the message is to be formatted
     * @param formatted true if formatted
     */
    fun setFormatted(formatted: Boolean) = apply { this.formatted = formatted }

    /**
     * Sets the TextComponent or String in the Message at index.
     * @param index    the index of the TextComponent or String to change
     * @param component the new TextComponent or String to replace with
     * @return the Message for method chaining
     */
    fun setTextComponent(index: Int, component: Any) = apply {
        when (component) {
            is String -> this.messageParts[index] = TextComponent(component)
            is TextComponent -> this.messageParts[index] = component
        }
    }

    /**
     * Adds a TextComponent or String to the end of the Message.
     * @param component the new TextComponent or String to add
     * @return the Message for method chaining
     */
    fun addTextComponent(component: Any) = apply {
        when (component) {
            is String -> this.messageParts.add(TextComponent(component))
            is TextComponent -> this.messageParts.add(component)
        }
    }

    /**
     * Adds a TextComponent or String at index of the Message.
     * @param index the index to insert the new TextComponent or String
     * @param component the new TextComponent or String to insert
     * @return the Message for method chaining
     */
    fun addTextComponent(index: Int, component: Any) = apply {
        when (component) {
            is String -> this.messageParts.add(index, TextComponent(component))
            is TextComponent -> this.messageParts.add(index, component)
        }
    }

    fun clone(): Message = copy()
    fun copy(): Message {
        val copy = Message(this.messageParts)
            .setChatLineId(this.chatLineId)
        copy.recursive = this.recursive
        copy.formatted = this.formatted
        return copy
    }

    /**
     * Edits this message (once it is already sent)
     * @param replacements the new message(s) to be put in place of the old one
     */
    fun edit(vararg replacements: Message) {
        ChatLib.editChat(this, *replacements)
    }

    /**
     * Outputs the Message into the client's chat.
     */
    fun chat() {
        parseMessage()
        if (!ChatLib.isPlayer("[CHAT]: " + this.chatMessage.formattedText)) return

        if (this.chatLineId != -1) {
            Client.getChatGUI()?.printChatMessageWithOptionalDeletion(this.chatMessage, this.chatLineId)
            return
        }

        //#if MC<=10809
        if (this.recursive) {
            Client.getConnection().handleChat(MCChatPacket(this.chatMessage, 0))
        } else {
            Player.getPlayer()?.addChatMessage(this.chatMessage)
        }
        //#else
        //$$ if (this.recursive) {
        //$$    Client.getConnection().handleChat(MCChatPacket(this.chatMessage, ChatType.CHAT))
        //$$ } else {
        //$$    Player.getPlayer()?.sendMessage(this.chatMessage)
        //$$ }
        //#endif
    }

    /**
     * Outputs the Message into the client's action bar.
     */
    fun actionBar() {
        parseMessage()
        if (!ChatLib.isPlayer("[ACTION BAR]: " + this.chatMessage.formattedText)) return

        Client.getConnection().handleChat(
            MCChatPacket(
                this.chatMessage,
                //#if MC<=10809
                2
                //#else
                //$$ ChatType.GAME_INFO
                //#endif
            )
        )
    }

    override fun toString() =
        "Message{" +
                "formatted=$formatted, " +
                "recursive=$recursive, " +
                "chatLineId=$chatLineId, " +
                "messageParts=$messageParts" +
                "}"

    private fun parseMessage() {
        this.chatMessage = MCStringTextComponent("")

        this.messageParts.map {
            this.chatMessage.appendSibling(it.component)
        }
    }
}
