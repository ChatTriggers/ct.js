package com.chattriggers.ctjs.utils.kotlin

typealias MCMinecraft = net.minecraft.client.Minecraft
typealias MCFontRenderer = net.minecraft.client.gui.FontRenderer
typealias MCChunk = net.minecraft.world.chunk.Chunk
typealias MCEntity = net.minecraft.entity.Entity
typealias MCPotionEffect = net.minecraft.potion.PotionEffect
typealias MCTessellator = net.minecraft.client.renderer.Tessellator

//#if MC==11604
//$$ typealias MCITextComponent = net.minecraft.util.text.ITextComponent
//$$ typealias MCClickEvent = net.minecraft.util.text.event.ClickEvent
//$$ typealias MCHoverEvent = net.minecraft.util.text.event.HoverEvent
//$$ typealias MCClickEventAction = net.minecraft.util.text.event.ClickEvent.Action
//$$ typealias MCSettings = net.minecraft.client.GameSettings
//$$ typealias MCWorld = net.minecraft.client.world.ClientWorld
//$$ typealias MCEntityPlayerSP = net.minecraft.client.entity.player.ClientPlayerEntity
//$$ typealias MCChatScreen = net.minecraft.client.gui.NewChatGui
//$$ typealias MCScreen = net.minecraft.client.gui.screen.Screen
//$$ typealias MCClientNetworkHandler = net.minecraft.client.network.play.ClientPlayNetHandler
//$$ typealias MCMainMenuScreen = net.minecraft.client.gui.screen.MainMenuScreen
//$$ typealias MCSChatPacket = net.minecraft.network.play.server.SChatPacket
//$$ typealias MCButton = net.minecraft.client.gui.widget.button.Button
//$$ typealias MCHoverEventAction = net.minecraft.util.text.event.HoverEvent.Action<*>
//$$ typealias MCStringTextComponent = net.minecraft.util.text.StringTextComponent
//#else
typealias MCITextComponent = net.minecraft.util.IChatComponent
typealias MCClickEvent = net.minecraft.event.ClickEvent
typealias MCHoverEvent = net.minecraft.event.HoverEvent
typealias MCClickEventAction = net.minecraft.event.ClickEvent.Action
typealias MCSettings = net.minecraft.client.settings.GameSettings
typealias MCWorld = net.minecraft.client.multiplayer.WorldClient
typealias MCEntityPlayerSP = net.minecraft.client.entity.EntityPlayerSP
typealias MCScreen = net.minecraft.client.gui.GuiScreen
typealias MCChatScreen = net.minecraft.client.gui.GuiNewChat
typealias MCMainMenuScreen = net.minecraft.client.gui.GuiMainMenu
typealias MCClientNetworkHandler = net.minecraft.client.network.NetHandlerPlayClient
typealias MCButton = net.minecraft.client.gui.GuiButton
typealias MCStringTextComponent = net.minecraft.util.ChatComponentText
typealias MCSChatPacket = net.minecraft.network.play.server.S02PacketChat
typealias MCHoverEventAction = net.minecraft.event.HoverEvent.Action
//#endif

//#if MC<=10809
typealias MCParticle = net.minecraft.client.particle.EntityFX
typealias MCNBTBase = net.minecraft.nbt.NBTBase
typealias MCNBTTagCompound = net.minecraft.nbt.NBTTagCompound
typealias MCNBTTagList = net.minecraft.nbt.NBTTagList
typealias MCNBTTagString = net.minecraft.nbt.NBTTagString

typealias MCBlockPos = net.minecraft.util.BlockPos
typealias MCRayTraceType = net.minecraft.util.MovingObjectPosition.MovingObjectType
typealias MCMathHelper = net.minecraft.util.MathHelper
typealias MCChatPacket = net.minecraft.network.play.server.S02PacketChat
typealias MCBaseTextComponent = net.minecraft.util.ChatComponentText
typealias MCTextStyle = net.minecraft.util.ChatStyle
typealias MCGameType = net.minecraft.world.WorldSettings.GameType
typealias MCWorldRenderer = net.minecraft.client.renderer.WorldRenderer
typealias MCTextComponentSerializer = net.minecraft.util.IChatComponent.Serializer
typealias MCSoundCategory = net.minecraft.client.audio.SoundCategory
//#else
//$$ typealias MCMCParticle = net.minecraft.client.particle.Particle
//$$ typealias MCBlockPos = net.minecraft.util.math.BlockPos
//$$ typealias MCRayTraceType = net.minecraft.util.math.RayTraceResult.Type
//$$ typealias MCMathHelper = net.minecraft.util.math.MathHelper
//$$ typealias MCChatPacket = net.minecraft.network.play.server.SPacketChat
//$$ typealias MCITextComponent = net.minecraft.util.text.ITextComponent
//$$ typealias MCBaseTextComponent = net.minecraft.util.text.TextComponentString
//$$ typealias MCTextClickEvent = net.minecraft.util.text.event.ClickEvent
//$$ typealias MCTextHoverEvent = net.minecraft.util.text.event.HoverEvent
//$$ typealias MCClickEventAction = net.minecraft.util.text.event.ClickEvent.Action
//$$ typealias MCHoverEventAction = net.minecraft.util.text.event.HoverEvent.Action
//$$ typealias MCTextStyle = net.minecraft.util.text.Style
//$$ typealias MCGameType = net.minecraft.world.GameType
//$$ typealias MCMCClickType = net.minecraft.inventory.ClickType
//$$ typealias MCWorldRenderer = net.minecraft.client.renderer.BufferBuilder
//$$ typealias MCTextComponentSerializer = net.minecraft.util.text.ITextComponent.Serializer
//$$ typealias MCSoundCategory = net.minecraft.util.SoundCategory
//#endif
