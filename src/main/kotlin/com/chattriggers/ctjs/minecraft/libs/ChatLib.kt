package com.chattriggers.ctjs.minecraft.libs

import com.chattriggers.ctjs.minecraft.libs.renderer.Renderer
import com.chattriggers.ctjs.minecraft.listeners.ChatListener
import com.chattriggers.ctjs.minecraft.objects.message.Message
import com.chattriggers.ctjs.minecraft.objects.message.TextComponent
import com.chattriggers.ctjs.minecraft.wrappers.Client
import com.chattriggers.ctjs.minecraft.wrappers.Player
import com.chattriggers.ctjs.print
import com.chattriggers.ctjs.utils.kotlin.External
import com.chattriggers.ctjs.utils.kotlin.times
import net.minecraft.client.gui.ChatLine
import net.minecraftforge.client.ClientCommandHandler
import net.minecraftforge.client.event.ClientChatReceivedEvent
import org.mozilla.javascript.NativeObject
import java.util.regex.Pattern
import kotlin.math.roundToInt

//#if MC==11604
//$$ import com.chattriggers.ctjs.launch.plugin.INewChatGuiAccessor
//#endif

@External
object ChatLib {
    /**
     * Prints text in the chat.
     * The text can be a String, a [Message] or a [TextComponent]
     *
     * @param text the text to be printed
     */
    @JvmStatic
    fun chat(text: Any) {
        when (text) {
            is String -> Message(text).chat()
            is Message -> text.chat()
            is TextComponent -> text.chat()
            else -> Message(text.toString()).chat()
        }
    }

    @JvmStatic
    fun test(e: Any) {
        println(e)
    }

    /**
     * Shows text in the action bar.
     * The text can be a String, a [Message] or a [TextComponent]
     *
     * @param text the text to show
     */
    @JvmStatic
    fun actionBar(text: Any) {
        when (text) {
            is String -> Message(text).actionBar()
            is Message -> text.actionBar()
            is TextComponent -> text.actionBar()
        }
    }

    /**
     * Simulates a chat message to be caught by other triggers for testing.
     * The text can be a String, a [Message] or a [TextComponent]
     *
     * @param message The message to simulate
     */
    @JvmStatic
    fun simulateChat(text: Any) {
        when (text) {
            is String -> Message(text).setRecursive(true).chat()
            is Message -> text.setRecursive(true).chat()
            is TextComponent -> Message(text).setRecursive(true).chat()
        }
    }

    /**
     * Says chat message.
     * This message is actually sent to the server.
     *
     * @param text the message to be sent
     */
    @JvmStatic
    fun say(text: String) = Player.getPlayer()?.sendChatMessage(text)

    /**
     * Runs a command.
     *
     * @param text the command to run, without the leading slash (Ex. "help")
     * @param clientSide should the command be ran as a client side command
     */
    @JvmOverloads
    @JvmStatic
    fun command(text: String, clientSide: Boolean = false) {
        if (clientSide) ClientCommandHandler.instance.executeCommand(Player.getPlayer(), "/$text")
        else say("/$text")
    }

    @JvmStatic
    fun clearChat() {
        //#if MC<=10809
        Client.getChatGUI()?.clearChatMessages()
        //#else
        //$$ Client.getChatGUI()?.clearChatMessages(false);
        //#endif
    }

    /**
     * Clear chat messages with the specified message ID
     *
     * @param chatLineIDs the id(s) to be cleared
     */
    @JvmStatic
    fun clearChat(vararg chatLineIDs: Int) {
        for (chatLineID in chatLineIDs)
            Client.getChatGUI()?.deleteChatLine(chatLineID)
    }

    /**
     * Get a message that will be perfectly one line of chat,
     * the separator repeated as many times as necessary.
     * The separator defaults to "-"
     *
     * @param separator the message to split chat with
     * @return the message that would split chat
     */
    @JvmOverloads
    @JvmStatic
    fun getChatBreak(separator: String = "-"): String {
        val len = Renderer.getStringWidth(separator)
        val times = getChatWidth() / len

        return separator * times
    }

    /**
     * Gets the width of Minecraft's chat
     *
     * @return the width of chat
     */
    @JvmStatic
    fun getChatWidth(): Int {
        return if (Client.getChatGUI() != null) {
            Client.getChatGUI()!!.chatWidth
        } else {
            0
        }
    }

    /**
     * Remove all formatting
     *
     * @param text the string to un-format
     * @return the unformatted string
     */
    @JvmStatic
    fun removeFormatting(text: String): String {
        return text.replace("[\\u00a7&][0-9a-fklmnor]".toRegex(), "")
    }

    /**
     * Replaces Minecraft formatted text with normal formatted text
     *
     * @param text the formatted string
     * @return the unformatted string
     */
    @JvmStatic
    fun replaceFormatting(text: String): String {
        return text.replace("\\u00a7(?![^0-9a-fklmnor]|$)".toRegex(), "&")
    }

    /**
     * Get a message that will be perfectly centered in chat.
     *
     * @param text the text to be centered
     * @return the centered message
     */
    @JvmStatic
    fun getCenteredText(text: String): String {
        val textWidth = Renderer.getStringWidth(addColor(text))
        val chatWidth = getChatWidth()

        if (textWidth >= chatWidth)
            return text

        val spaceWidth = (chatWidth - textWidth) / 2f
        val spaceBuilder = StringBuilder().apply {
            repeat((spaceWidth / Renderer.getStringWidth(" ")).roundToInt()) {
                append(' ')
            }
        }

        return spaceBuilder.append(text).toString()
    }

    /**
     * Edits an already sent chat message by regex.
     * If the JavaScript RegExp object passed in matches a message, it will be replaced.
     * The regex object will be created by the {@code new RegExp()} constructor,
     * or the {@code //} regex literal. All flags will be respected.
     *
     * @param regexp the regex object to match to the message
     * @param replacements the new message(s) to be put in replace of the old one
     */
    @JvmStatic
    fun editChat(regexp: NativeObject, vararg replacements: Message) {
        val global = regexp["global"] as Boolean
        val ignoreCase = regexp["ignoreCase"] as Boolean
        val multiline = regexp["multiline"] as Boolean

        val flags = (if (ignoreCase) Pattern.CASE_INSENSITIVE else 0) or if (multiline) Pattern.MULTILINE else 0
        val pattern = Pattern.compile(regexp["source"] as String, flags)

        editChat(
            {
                val matcher = pattern.matcher(it.getChatMessage().unformattedText)
                if (global) matcher.find() else matcher.matches()
            },
            *replacements
        )
    }

    /**
     * Edits an already sent chat message by the text of the chat
     *
     * @param toReplace the unformatted text of the message to be replaced
     * @param replacements the new message(s) to be put in place of the old one
     */
    @JvmStatic
    fun editChat(toReplace: String, vararg replacements: Message) {
        editChat(
            {
                removeFormatting(it.getChatMessage().unformattedText) == toReplace
            },
            *replacements
        )
    }

    /**
     * Edits an already sent chat message by the [Message]
     *
     * @param toReplace the message to be replaced
     * @param replacements the new message(s) to be put in place of the old one
     */
    @JvmStatic
    fun editChat(toReplace: Message, vararg replacements: Message) {
        editChat(
            {
                toReplace.getChatMessage().formattedText == it.getChatMessage().formattedText.replaceFirst(
                    "\\u00a7r".toRegex(),
                    ""
                )
            },
            *replacements
        )
    }

    /**
     * Edits an already sent chat message by its chat line id
     *
     * @param chatLineId the chat line id of the message to be replaced
     * @param replacements the new message(s) to be put in place of the old one
     */
    @JvmStatic
    fun editChat(chatLineId: Int, vararg replacements: Message) {
        editChat(
            { message ->
                message.getChatLineId() == chatLineId
            },
            *replacements
        )
    }

    /**
     * Edits an already sent chat message.
     * Whether each specific message is edited or not is up to the first parameter, the "comparator" function.
     * This function will be passed a {@link Message} object and has to return a boolean for whether or not
     * that specific message should be edited. (true for yes, false for no). There are overrides of this function
     * that already implement different versions of this method and those should be used in place of this one
     * if there is already a suitable replacement. Otherwise, create one and use this method.
     *
     * @param shouldReplace the "comparator" function
     * @param replacements the replacement messages
     */
    @JvmStatic
    private fun editChat(shouldReplace: (Message) -> Boolean, vararg replacements: Message) {
        //#if MC==10809
        val chatLines = Client.getChatGUI()!!.chatLines
        val drawnChatLines = Client.getChatGUI()!!.drawnChatLines

        editChatLineList(chatLines, shouldReplace, *replacements)
        editChatLineList(drawnChatLines, shouldReplace, *replacements)
        //#else
        //$$ val chatLines = (Client.getChatGUI()!! as INewChatGuiAccessor).getChatLines()
        //$$ val drawnChatLines = (Client.getChatGUI()!! as INewChatGuiAccessor).getDrawnChatLines()
        //$$
        //$$ editChatLineList(
        //$$     chatLines,
        //$$     shouldReplace,
        //$$     *replacements,
        //$$     tToMessage = ::Message,
        //$$     messageToT = Message::getChatMessage
        //$$ )
        //$$ editChatLineList(
        //$$     drawnChatLines,
        //$$     shouldReplace,
        //$$     *replacements,
        //$$     tToMessage = ::Message,
        //$$     messageToT = Message::getReorderingProcessor
        //$$ )
        //#endif
    }


    //#if MC==10809
    private fun editChatLineList(
        lineList: MutableList<ChatLine>,
        shouldReplace: (Message) -> Boolean,
        vararg replacements: Message
    ) {
    //#else
    //$$ private fun <T> editChatLineList(
    //$$     lineList: MutableList<ChatLine<T>>,
    //$$     shouldReplace: (Message) -> Boolean,
    //$$     vararg replacements: Message,
    //$$     tToMessage: (T) -> Message,
    //$$     messageToT: (Message) -> T
    //$$ ) {
    //#endif
        val chatLineIterator = lineList.listIterator()

        while (chatLineIterator.hasNext()) {
            val chatLine = chatLineIterator.next()

            //#if MC==10809
            val message = Message(chatLine.chatComponent).setChatLineId(chatLine.chatLineID)
            //#else
            //$$ val message = tToMessage(chatLine.lineString).setChatLineId(chatLine.chatLineID)
            //#endif

            if (!shouldReplace(message)) {
                continue
            }

            chatLineIterator.remove()

            replacements.map {
                val lineId = if (it.getChatLineId() == -1) 0 else it.getChatLineId()

                //#if MC==10809
                ChatLine(chatLine.updatedCounter, it.getChatMessage(), lineId)
                //#else
                //$$ ChatLine(chatLine.updatedCounter, messageToT(it), lineId)
                //#endif
            }.forEach {
                chatLineIterator.add(it)
            }
        }
    }

    /**
     * Gets the previous 1000 lines of chat
     *
     * @return A list of the last 1000 chat lines
     */
    @JvmStatic
    fun getChatLines(): List<String> {
        val hist = ChatListener.chatHistory.toMutableList()
        hist.reverse()
        return hist
    }

    /**
     * Get the text of a chat event.
     * Defaults to the unformatted version.
     *
     * @param event     The chat event passed in by a chat trigger
     * @param formatted If true, returns formatted text. Otherwise, returns
     * unformatted text
     * @return The text of the event
     */
    @JvmOverloads
    @JvmStatic
    fun getChatMessage(event: ClientChatReceivedEvent, formatted: Boolean = false): String {
        return if (formatted) {
            replaceFormatting(EventLib.getMessage(event).formattedText)
        } else {
            EventLib.getMessage(event).unformattedText
        }
    }

    /**
     * Replaces the easier to type '&amp;' color codes with proper color codes in a string.
     *
     * @param message The string to add color codes to
     * @return the formatted message
     */
    @JvmStatic
    fun addColor(message: String?): String {
        return message.toString().replace("(?:(?<!\\\\))&(?![^0-9a-fklmnor]|$)".toRegex(), "\u00a7")
    }

    // helper method to make sure player exists before putting something in chat
    fun isPlayer(out: String): Boolean {
        if (Player.getPlayer() == null) {
            out.print()
            return false
        }

        return true
    }
}
