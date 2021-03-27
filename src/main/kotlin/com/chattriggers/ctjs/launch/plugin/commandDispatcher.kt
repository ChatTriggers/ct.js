package com.chattriggers.ctjs.launch.plugin


//#if MC==11604
//$$ import com.chattriggers.ctjs.utils.kotlin.MCITextComponent
//$$ import dev.falsehonesty.asmhelper.dsl.instructions.InsnListBuilder
//$$ import dev.falsehonesty.asmhelper.dsl.modify
//$$ import net.minecraft.client.gui.ChatLine
//$$ import net.minecraft.util.IReorderingProcessor
//$$ import org.objectweb.asm.tree.MethodNode
//$$
//$$ fun setupNewChatGuiAccessor() = modify("com/mojang/brigadier/CommandDispatcher") {
//$$     makeFieldPublic(it, "root", "com/mojang/brigadier/tree/RootCommandNode")
//$$
//$$     val getRootNode = MethodNode(
//$$         public,
//$$         "getRoot",
//$$         "()Lcom/mojang/brigadier/tree/RootCommandNode;",
//$$         null,
//$$         arrayOf()
//$$     )
//$$
//$$     getRootNode.instructions = InsnListBuilder(getChatLinesNode).apply {
//$$         aload(0)
//$$         getField("com/mojang/brigadier/CommandDispatcher", "root", "com/mojang/brigadier/tree/RootCommandNode")
//$$         areturn()
//$$     }.build()
//$$
//$$     it.methods.add(getRootNode)
//$$ }
//$$
//$$ interface ICommandDispatcherAccessor<S> {
//$$     fun getRoot(): RootCommandNode<S>
//$$ }
//#endif

