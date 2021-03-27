package com.chattriggers.ctjs.launch.plugin

//#if MC==11604
//$$ import com.chattriggers.ctjs.utils.kotlin.MCITextComponent
//$$ import dev.falsehonesty.asmhelper.dsl.instructions.InsnListBuilder
//$$ import dev.falsehonesty.asmhelper.dsl.modify
//$$ import net.minecraft.client.gui.ChatLine
//$$ import net.minecraft.util.IReorderingProcessor
//$$ import org.objectweb.asm.tree.MethodNode
//$$
//$$ fun setupNewChatGuiAccessor() = modify("net/minecraft/client/gui/NewChatGui") {
//$$     makeFieldPublic(it, "chatLines", "java/util/List")
//$$     makeFieldPublic(it, "drawnChatLines", "java/util/List")
//$$
//$$     val getChatLinesNode = MethodNode(
//$$         public,
//$$         "getChatLines",
//$$         "()Ljava/util/List;",
//$$         null,
//$$         arrayOf()
//$$     )
//$$
//$$     getChatLinesNode.instructions = InsnListBuilder(getChatLinesNode).apply {
//$$         getField("net/minecraft/client/gui/NewChatGui", "chatLines", "java/util/List")
//$$         areturn()
//$$     }.build()
//$$
//$$     val getDrawnChatLinesNode = MethodNode(
//$$         public,
//$$         "getDrawnChatLines",
//$$         "()Ljava/util/List;",
//$$         null,
//$$         arrayOf()
//$$     )
//$$
//$$     getDrawnChatLinesNode.instructions = InsnListBuilder(getDrawnChatLinesNode).apply {
//$$         getField("net/minecraft/client/gui/NewChatGui", "drawnChatLines", "java/util/List")
//$$         areturn()
//$$     }.build()
//$$
//$$     it.methods.add(getChatLinesNode)
//$$     it.methods.add(getDrawnChatLinesNode)
//$$ }
//$$
//$$ interface INewChatGuiAccessor {
//$$     fun getChatLines(): MutableList<ChatLine<MCITextComponent>>
//$$
//$$     fun getDrawnChatLines(): MutableList<ChatLine<IReorderingProcessor>>
//$$ }
//#endif
