package com.chattriggers.ctjs.minecraft.wrappers.objects.block

import com.chattriggers.ctjs.minecraft.wrappers.Player
import com.chattriggers.ctjs.minecraft.wrappers.World
import com.chattriggers.ctjs.minecraft.wrappers.objects.inventory.Item
import com.chattriggers.ctjs.utils.kotlin.MCBlockPos
import com.chattriggers.ctjs.utils.kotlin.External
import net.minecraft.block.state.IBlockState
import net.minecraft.util.EnumFacing
import net.minecraft.block.Block as MCBlock

@External
open class Block {
    var block: MCBlock
    var blockPos: MCBlockPos = MCBlockPos(0, 0, 0)
    var face: BlockFace? = null

    constructor(block: MCBlock) {
        this.block = block
    }

    /**
     * Copies the block and blockPos of the provided block into
     * this object.
     */
    constructor(block: Block) {
        this.block = block.block
        this.blockPos = block.blockPos
    }

    constructor(blockName: String) {
        block = MCBlock.getBlockFromName(blockName)!!
    }

    constructor(blockID: Int) {
        block = MCBlock.getBlockById(blockID)
    }

    constructor(item: Item) {
        block = MCBlock.getBlockFromItem(item.item)
    }
    /* End of constructors */

    /**
     * Sets the block position in the world.<br>
     * This is automatically set by {@link Player#lookingAt()}.<br>
     * This method is not meant for public use.
     *
     * @param blockPos the block position
     * @return the Block object
     */
    fun setBlockPos(blockPos: MCBlockPos) = apply {
        this.blockPos = blockPos
    }

    /**
     * Sets the face that is being looked at by the player.<br>
     * This is automatically set by {@link Player#lookingAt()}.<br>
     * This method is not meant for public use.
     *
     * @param face this face that is being looked at
     * @return the Block object
     */
    fun setFace(face: BlockFace) = apply {
        this.face = face
    }

    fun getID(): Int = MCBlock.getIdFromBlock(block)

    /**
     * Gets the block's registry name.<br>
     * Example: <code>minecraft:planks</code>
     *
     * @return the block's registry name
     */
    fun getRegistryName(): String {
        //#if MC<=10809
        return this.block.registryName
        //#else
        //$$ return this.block.registryName.toString();
        //#endif
    }

    /**
     * Gets the block's unlocalized name.<br>
     * Example: <code>tile.wood</code>
     *
     * @return the block's unlocalized name
     */
    fun getUnlocalizedName(): String = block.unlocalizedName

    /**
     * Gets the block's localized name.<br>
     * Example: <code>Wooden Planks</code>
     *
     * @return the block's localized name
     */
    fun getName(): String = block.localizedName

    fun getLightValue(): Int {
        //#if MC<=10809
        return this.block.lightValue
        //#else
        //$$ return this.block.getLightValue(
        //$$         World.getWorld()!!.getBlockState(blockPos),
        //$$         World.getWorld(),
        //$$         blockPos
        //$$ );
        //#endif
    }

    fun getState(): IBlockState = World.getWorld()!!.getBlockState(blockPos)

    fun getDefaultState(): IBlockState = block.defaultState

    fun getX(): Int = blockPos.x
    fun getY(): Int = blockPos.y
    fun getZ(): Int = blockPos.z

    fun getMetadata(): Int = block.getMetaFromState(getState())
    fun getDefaultMetadata(): Int = block.getMetaFromState(getDefaultState())

    fun canProvidePower(): Boolean {
        //#if MC<=10809
        return this.block.canProvidePower()
        //#else
        //$$ return this.block.canProvidePower(
        //$$         World.getWorld()!!.getBlockState(blockPos)
        //$$ );
        //#endif
    }

    fun isPowered(): Boolean = World.getWorld()!!.isBlockPowered(blockPos)

    fun getRedstoneStrength(): Int = World.getWorld()!!.getStrongPower(blockPos)

    /**
     * Checks whether the block can be mined with the tool in the player's hand
     *
     * @return whether the block can be mined
     */
    fun canBeHarvested(): Boolean = block.canHarvestBlock(World.getWorld(), blockPos, Player.getPlayer())

    fun canBeHarvestedWith(item: Item): Boolean = item.canHarvest(this)

    fun getHarvestLevel(): Int = block.getHarvestLevel(getDefaultState())

    fun isTranslucent(): Boolean {
        //#if MC<=10809
        return this.block.isTranslucent
        //#else
        //$$ return this.block.isTranslucent(
        //$$         World.getWorld()!!.getBlockState(blockPos)
        //$$ );
        //#endif
    }

    override fun toString(): String = "Block{name=${block.registryName}, x=${getX()}, y=${getY()}, z=${getZ()}}"
}
