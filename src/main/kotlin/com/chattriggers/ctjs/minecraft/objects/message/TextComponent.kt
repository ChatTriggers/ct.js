package com.chattriggers.ctjs.minecraft.objects.message

import com.chattriggers.ctjs.minecraft.libs.ChatLib
import com.chattriggers.ctjs.utils.kotlin.*

//#if MC==10809
import net.minecraft.event.ClickEvent
import net.minecraft.event.HoverEvent
import net.minecraft.util.EnumChatFormatting
import net.minecraft.util.IChatComponent
import net.minecraft.util.ChatStyle
import java.util.*

//#else
//$$ import net.minecraft.util.text.*
//$$ import net.minecraft.util.text.event.*
//$$ import net.minecraft.util.IReorderingProcessor
//$$ import net.minecraft.util.text.ITextProperties.IStyledTextAcceptor
//$$ import net.minecraft.util.text.ITextProperties.ITextAcceptor
//$$ import java.util.Optional
//$$ import java.util.stream.Stream
//$$ import java.util.function.Consumer
//#endif

@Suppress("MemberVisibilityCanBePrivate")
class TextComponent : MCITextComponent {
    lateinit var component: MCITextComponent
        private set
    var text: String
        set(value) {
            field = value
            reInstance()
        }
    var formatted = true
        set(value) {
            field = value
            reInstance()
        }

    var clickAction: MCClickEventAction? = null
        set(value) {
            field = value
            reInstance()
        }
    var clickValue: String? = null
        set(value) {
            field = value
            reInstance()
        }
    var hoverAction: MCHoverEventAction? = null
        set(value) {
            field = value
            reInstance()
        }
    var hoverValue: Any? = null
        set(value) {
            field = value
            reInstance()
        }

    constructor(text: String) {
        this.text = text
        reInstance()
    }

    constructor(component: MCITextComponent) {
        this.component = component

        //#if MC==11604
        //$$ val builder = FormattedTextBuilder()
        //$$ component.getComponentWithStyle(builder, Style.EMPTY)
        //$$ text = builder.getString()
        //#else
        text = component.formattedText
        //#endif

        //#if MC==11604
        //$$ val clickEvent = component.style.clickEvent
        //#else
        val clickEvent = component.chatStyle.chatClickEvent
        //#endif

        if (clickEvent != null) {
            clickAction = clickEvent.action
            clickValue = clickEvent.value
        }

        //#if MC==11604
        //$$ val hoverEvent = component.style.hoverEvent
        //#else
        val hoverEvent = component.chatStyle.chatHoverEvent
        //#endif

        if (hoverEvent != null) {
            hoverAction = hoverEvent.action

            //#if MC==11604
            //$$ hoverValue = hoverEvent.getParameter(hoverAction)
            //#else
            hoverValue = hoverEvent.value
            //#endif
        }
    }

    fun setClick(action: MCClickEventAction, value: String) = apply {
        clickAction = action
        clickValue = value
        reInstance()
    }

    fun setHover(action: MCHoverEventAction, value: Any) = apply {
        hoverAction = action
        hoverValue = value
        reInstance()
    }

    fun chat() {
        TODO()
    }

    fun actionBar() {
        TODO()
    }

    private fun reInstance() {
        component = MCStringTextComponent(text.formatIf(formatted))

        reInstanceClick()
        reInstanceHover()
    }

    private fun reInstanceClick() {
        if (clickAction == null || clickValue == null)
            return

        val event = ClickEvent(clickAction, clickValue!!.formatIf(formatted))

        //#if MC==11604
        //$$ component.style.clickEvent = event
        //#else
        component.chatStyle.chatClickEvent = event
        //#endif
    }

    private fun reInstanceHover() {
        if (hoverAction == null || hoverValue == null)
            return

        //#if MC==11604
        //$$ val action: HoverEvent.Action<MCITextComponent> = hoverAction!! as HoverEvent.Action<MCITextComponent>
        //$$ val value: MCITextComponent = MCStringTextComponent(hoverValue!! as String)
        //$$ val event = HoverEvent<MCITextComponent>(action, value)
        //$$ setHoverEventHelper(event)
        //#else
        setHoverEventHelper(HoverEvent(
            hoverAction,
            MCStringTextComponent(hoverValue!! as String)
        ))
        //#endif
    }

    private fun setHoverEventHelper(event: HoverEvent) {
        //#if MC==11604
        //$$ component.style.hoverEvent = event
        //#else
        component.chatStyle.chatHoverEvent = event
        //#endif
    }

    private fun String.formatIf(predicate: Boolean) = if (predicate) ChatLib.addColor(this) else this


    //#if MC==11604
    //$$ class StyledStringAcceptor : IStyledTextAcceptor<Any> {
    //$$     private val builder = FormattedTextBuilder()
    //$$
    //$$     override fun accept(style: Style, string: String): Optional<Any> {
    //$$         builder.accept(style, string)
    //$$         return Optional.empty()
    //$$     }
    //$$
    //$$     override fun toString() = builder.toString()
    //$$ }
    //$$
    //$$ class StyledCharacterConsumer : ICharacterConsumer {
    //$$     private val builder = FormattedTextBuilder()
    //$$
    //$$     override fun accept(ignored: Int, style: Style, charCode: Int): Boolean {
    //$$         builder.accept(style, charCode.toChar().toString())
    //$$         return true
    //$$     }
    //$$
    //$$     override fun toString() = builder.toString()
    //$$ }
    //$$
    //$$ class FormattedTextBuilder {
    //$$     private val builder = StringBuilder()
    //$$     private var cachedStyle: Style? = null
    //$$
    //$$     fun accept(style: Style, string: String) {
    //$$         if (style != cachedStyle) {
    //$$             cachedStyle = style
    //$$             builder.append(formatString(style))
    //$$         }
    //$$
    //$$         builder.append(string)
    //$$     }
    //$$
    //$$     override fun toString() = builder.toString()
    //$$
    //$$     private fun formatString(style: Style): String {
    //$$         val builder = StringBuilder("§r")
    //$$
    //$$         when {
    //$$             style.bold -> builder.append("§l")
    //$$             style.italic -> builder.append("§o")
    //$$             style.underlined -> builder.append("§n")
    //$$             style.strikethrough -> builder.append("§m")
    //$$             style.obfuscated -> builder.append("§k")
    //$$         }
    //$$
    //$$         if (style.color != null)
    //$$             builder.append(style.color.toString())
    //$$         return builder.toString()
    //$$     }
    //$$ }
    //#endif

    // **********************
    // * METHOD DELEGATIONS *
    // **********************

    //#if MC==11604
    //$$ fun appendSibling(component: ITextComponent) {
    //$$     siblings.add(component)
    //$$ }
    //$$
    //$$ val unformattedText: String get() = unformattedComponentText
    //$$
    //$$ val formattedText: String get() = text
    //$$
    //$$ override fun getString(): String = component.string
    //$$
    //$$ override fun getStringTruncated(maxLen: Int): String = component.getStringTruncated(maxLen)
    //$$
    //$$ override fun <T : Any?> getComponent(acceptor: ITextAcceptor<T>): Optional<T> {
    //$$     return component.getComponent(acceptor)
    //$$ }
    //$$
    //$$ override fun <T : Any?> getComponentWithStyle(acceptor: IStyledTextAcceptor<T>, styleIn: Style): Optional<T> {
    //$$     return component.getComponentWithStyle(acceptor, styleIn)
    //$$ }
    //$$
    //$$ override fun <T> func_230534_b_(p_230534_1_: IStyledTextAcceptor<T>, p_230534_2_: Style): Optional<T> {
    //$$     return component.func_230534_b_(p_230534_1_, p_230534_2_)
    //$$ }
    //$$
    //$$ override fun <T> func_230533_b_(p_230533_1_: ITextAcceptor<T>): Optional<T> {
    //$$     return component.func_230533_b_(p_230533_1_)
    //$$ }
    //$$
    //$$ override fun getStyle(): Style = component.style
    //$$
    //$$ override fun getUnformattedComponentText(): String = component.unformattedComponentText
    //$$
    //$$ override fun getSiblings(): MutableList<ITextComponent> = component.siblings
    //$$
    //$$ override fun copyRaw(): IFormattableTextComponent = component.copyRaw()
    //$$
    //$$ override fun deepCopy(): IFormattableTextComponent = component.deepCopy()
    //$$
    //$$ override fun func_241878_f(): IReorderingProcessor = component.func_241878_f()
    //#else
    override fun setChatStyle(style: ChatStyle): IChatComponent = component.setChatStyle(style)

    override fun getChatStyle(): ChatStyle = component.chatStyle

    override fun appendText(text: String): IChatComponent = component.appendText(text)

    override fun appendSibling(component: IChatComponent): IChatComponent = component.appendSibling(component)

    override fun getUnformattedTextForChat(): String = component.unformattedTextForChat

    override fun getUnformattedText(): String = component.unformattedText

    override fun getFormattedText(): String = component.formattedText

    override fun getSiblings(): MutableList<IChatComponent> = component.siblings

    override fun createCopy(): IChatComponent = component.createCopy()

    override fun iterator(): MutableIterator<IChatComponent> = component.iterator()
    //#endif

    companion object {
        fun from(obj: Any): TextComponent? {
            return when (obj) {
                is TextComponent -> obj
                is String -> TextComponent(obj)
                is MCITextComponent -> TextComponent(obj)
                else -> null
            }
        }

        fun stripFormatting(string: String): String {
            //#if MC==11604
            //$$ return TextFormatting.getTextWithoutFormattingCodes(string)!!
            //#else
            return EnumChatFormatting.getTextWithoutFormattingCodes(string)
            //#endif
        }
    }
}
