package com.chattriggers.ctjs.minecraft.libs.renderer

import net.minecraft.client.entity.AbstractClientPlayer
import net.minecraft.client.renderer.entity.RenderManager
import net.minecraft.client.renderer.entity.RenderPlayer
import net.minecraft.client.renderer.entity.layers.*

class CTRenderPlayer(renderManager: RenderManager?, useSmallArms: Boolean) : RenderPlayer(renderManager, useSmallArms) {
    private var showNametag = true
    private var showArmor = true
    private var showCape = true
    private var showHeldItem = true
    private var showArrows = true

    fun setOptions(
            showNametag: Boolean,
            showArmor: Boolean,
            showCape: Boolean,
            showHeldItem: Boolean,
            showArrows: Boolean
    ) {
        this.showNametag = showNametag
        this.showArmor = showArmor
        this.showCape = showCape
        this.showHeldItem = showHeldItem
        this.showArrows = showArrows

        layerRenderers = arrayListOf<LayerRenderer<AbstractClientPlayer>>()

        if (showArmor) addLayer(LayerBipedArmor(this))
        if (showHeldItem) addLayer(LayerHeldItem(this))
        if (showArrows) addLayer(LayerArrow(this))
        addLayer(LayerDeadmau5Head(this))
        if (showCape) addLayer(LayerCape(this))
        if (showArmor) addLayer(LayerCustomHead(this.getMainModel().bipedHead))
    }

    override fun setModelVisibilities(clientPlayer: AbstractClientPlayer) {
        super.setModelVisibilities(clientPlayer)
        if (!showHeldItem) getMainModel().heldItemRight = 0
    }

    override fun canRenderName(entity: AbstractClientPlayer?) = showNametag

    override fun renderOffsetLivingLabel(
            entityIn: AbstractClientPlayer?,
            x: Double,
            y: Double,
            z: Double,
            str: String?,
            p_177069_9_: Float,
            p_177069_10_: Double
    ) {
        if (showNametag) super.renderOffsetLivingLabel(entityIn, x, y, z, str, p_177069_9_, p_177069_10_)
    }

    override fun renderName(entity: AbstractClientPlayer?, x: Double, y: Double, z: Double) {
        if (showNametag) super.renderName(entity, x, y, z)
    }

    override fun renderLivingLabel(
            entityIn: AbstractClientPlayer?,
            str: String?,
            x: Double,
            y:Double,
            z: Double,
            maxDistance: Int
    ) {
        if (showNametag) super.renderLivingLabel(entityIn, str, x, y, z, maxDistance)
    }
}