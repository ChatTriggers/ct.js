package com.chattriggers.ctjs.minecraft.wrappers

import com.chattriggers.ctjs.utils.kotlin.External
import com.chattriggers.ctjs.utils.kotlin.MCSoundCategory
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.EnumPlayerModelParts
import net.minecraft.world.EnumDifficulty

@External
object Settings {
    @JvmStatic
    fun getSettings() = Client.getMinecraft().gameSettings

    @JvmStatic
    fun getFOV() = getSettings().fovSetting

    @JvmStatic
    fun setFOV(fov: Float) {
        getSettings().fovSetting = fov
    }

    @JvmStatic
    fun getDifficulty() = getSettings().difficulty.difficultyId

    @JvmStatic
    fun setDifficulty(difficulty: Int) {
        getSettings().difficulty = EnumDifficulty.getDifficultyEnum(difficulty)
    }

    object skin {
        @JvmStatic
        fun getCape() = getSettings().modelParts.contains(EnumPlayerModelParts.CAPE)

        @JvmStatic
        fun setCape(toggled: Boolean) {
            setModelPart(toggled, EnumPlayerModelParts.CAPE)
        }

        @JvmStatic
        fun getJacket() = getSettings().modelParts.contains(EnumPlayerModelParts.JACKET)

        @JvmStatic
        fun setJacket(toggled: Boolean) {
            setModelPart(toggled, EnumPlayerModelParts.JACKET)
        }

        @JvmStatic
        fun getLeftSleeve() = getSettings().modelParts.contains(EnumPlayerModelParts.LEFT_SLEEVE)

        @JvmStatic
        fun setLeftSleeve(toggled: Boolean) {
            setModelPart(toggled, EnumPlayerModelParts.LEFT_SLEEVE)
        }

        @JvmStatic
        fun getRightSleeve() = getSettings().modelParts.contains(EnumPlayerModelParts.RIGHT_SLEEVE)

        @JvmStatic
        fun setRightSleeve(toggled: Boolean) {
            setModelPart(toggled, EnumPlayerModelParts.RIGHT_SLEEVE)
        }

        @JvmStatic
        fun getLeftPantsLeg() = getSettings().modelParts.contains(EnumPlayerModelParts.LEFT_PANTS_LEG)

        @JvmStatic
        fun setLeftPantsLeg(toggled: Boolean) {
            setModelPart(toggled, EnumPlayerModelParts.LEFT_PANTS_LEG)
        }

        @JvmStatic
        fun getRightPantsLeg() = getSettings().modelParts.contains(EnumPlayerModelParts.RIGHT_PANTS_LEG)

        @JvmStatic
        fun setRightPantsLeg(toggled: Boolean) {
            setModelPart(toggled, EnumPlayerModelParts.RIGHT_PANTS_LEG)
        }

        @JvmStatic
        fun getHat() = getSettings().modelParts.contains(EnumPlayerModelParts.HAT)

        @JvmStatic
        fun setHat(toggled: Boolean) {
            setModelPart(toggled, EnumPlayerModelParts.HAT)
        }

        private fun setModelPart(toggled: Boolean, modelPart: EnumPlayerModelParts) {
            if (toggled) getSettings().modelParts.add(modelPart)
            else getSettings().modelParts.remove(modelPart)
        }
    }

    object sound {
        @JvmStatic
        fun getMasterVolume() = getSettings().getSoundLevel(MCSoundCategory.MASTER)

        @JvmStatic
        fun setMasterVolume(level: Float) = getSettings().setSoundLevel(MCSoundCategory.MASTER, level)

        @JvmStatic
        fun getMusicVolume() = getSettings().getSoundLevel(MCSoundCategory.MUSIC)

        @JvmStatic
        fun setMusicVolume(level: Float) = getSettings().setSoundLevel(MCSoundCategory.MUSIC, level)

        @JvmStatic
        fun getNoteblockVolume() = getSettings().getSoundLevel(MCSoundCategory.RECORDS)

        @JvmStatic
        fun setNoteblockVolume(level: Float) = getSettings().setSoundLevel(MCSoundCategory.RECORDS, level)

        @JvmStatic
        fun getWeather() = getSettings().getSoundLevel(MCSoundCategory.WEATHER)

        @JvmStatic
        fun setWeather(level: Float) = getSettings().setSoundLevel(MCSoundCategory.WEATHER, level)

        @JvmStatic
        fun getBlocks() = getSettings().getSoundLevel(MCSoundCategory.BLOCKS)

        @JvmStatic
        fun setBlocks(level: Float) = getSettings().setSoundLevel(MCSoundCategory.BLOCKS, level)

        //#if MC<=10809
        @JvmStatic
        fun getHostileCreatures() = getSettings().getSoundLevel(MCSoundCategory.MOBS)

        @JvmStatic
        fun setHostileCreatures(level: Float) = getSettings().setSoundLevel(MCSoundCategory.MOBS, level)

        @JvmStatic
        fun getFriendlyCreatures() = getSettings().getSoundLevel(MCSoundCategory.ANIMALS)

        @JvmStatic
        fun setFriendlyCreatures(level: Float) = getSettings().setSoundLevel(MCSoundCategory.ANIMALS, level)
        //#else
        //$$ @JvmStatic fun getHostileCreatures() = getSettings().getSoundLevel(MCSoundCategory.HOSTILE)
        //$$ @JvmStatic fun setHostileCreatures(level: Float) = getSettings().setSoundLevel(MCSoundCategory.HOSTILE, level)
        //$$
        //$$ @JvmStatic fun getFriendlyCreatures() = getSettings().getSoundLevel(MCSoundCategory.NEUTRAL)
        //$$ @JvmStatic fun setFriendlyCreatures(level: Float) = getSettings().setSoundLevel(MCSoundCategory.NEUTRAL, level)
        //#endif

        @JvmStatic
        fun getPlayers() = getSettings().getSoundLevel(MCSoundCategory.PLAYERS)

        @JvmStatic
        fun setPlayers(level: Float) = getSettings().setSoundLevel(MCSoundCategory.PLAYERS, level)

        @JvmStatic
        fun getAmbient() = getSettings().getSoundLevel(MCSoundCategory.AMBIENT)

        @JvmStatic
        fun setAmbient(level: Float) = getSettings().setSoundLevel(MCSoundCategory.AMBIENT, level)
    }

    object video {
        @JvmStatic
        fun getGraphics() = getSettings().fancyGraphics

        @JvmStatic
        fun setGraphics(fancy: Boolean) {
            getSettings().fancyGraphics = fancy
        }

        @JvmStatic
        fun getRenderDistance() = getSettings().renderDistanceChunks

        @JvmStatic
        fun setRenderDistance(distance: Int) {
            getSettings().renderDistanceChunks = distance
        }

        @JvmStatic
        fun getSmoothLighting() = getSettings().ambientOcclusion

        @JvmStatic
        fun setSmoothLighting(level: Int) {
            getSettings().ambientOcclusion = level
        }

        @JvmStatic
        fun getMaxFrameRate() = getSettings().limitFramerate

        @JvmStatic
        fun setMaxFrameRate(frameRate: Int) {
            getSettings().limitFramerate = frameRate
        }

        @JvmStatic
        fun get3dAnaglyph() = getSettings().anaglyph

        @JvmStatic
        fun set3dAnaglyph(toggled: Boolean) {
            getSettings().anaglyph = toggled
        }

        @JvmStatic
        fun getBobbing() = getSettings().viewBobbing

        @JvmStatic
        fun setBobbing(toggled: Boolean) {
            getSettings().viewBobbing = toggled
        }

        @JvmStatic
        fun getGuiScale() = getSettings().guiScale

        @JvmStatic
        fun setGuiScale(scale: Int) {
            getSettings().guiScale = scale
        }

        @JvmStatic
        fun getBrightness() = getSettings().gammaSetting

        @JvmStatic
        fun setBrightness(brightness: Float) {
            getSettings().gammaSetting = brightness
        }

        @JvmStatic
        fun getClouds() = getSettings().clouds

        @JvmStatic
        fun setClouds(clouds: Int) {
            getSettings().clouds = clouds
        }

        @JvmStatic
        fun getParticles() = getSettings().particleSetting

        @JvmStatic
        fun setParticles(particles: Int) {
            getSettings().particleSetting = particles
        }

        @JvmStatic
        fun getFullscreen() = getSettings().fullScreen

        @JvmStatic
        fun setFullscreen(toggled: Boolean) {
            getSettings().fullScreen = toggled
        }

        @JvmStatic
        fun getVsync() = getSettings().enableVsync

        @JvmStatic
        fun setVsync(toggled: Boolean) {
            getSettings().enableVsync = toggled
        }

        @JvmStatic
        fun getMipmapLevels() = getSettings().mipmapLevels

        @JvmStatic
        fun setMipmapLevels(mipmapLevels: Int) {
            getSettings().mipmapLevels = mipmapLevels
        }

        //#if MC<=10809
        @JvmStatic
        fun getAlternateBlocks() = getSettings().allowBlockAlternatives

        @JvmStatic
        fun setAlternateBlocks(toggled: Boolean) {
            getSettings().allowBlockAlternatives = toggled
        }
        //#else
        //$$ @JvmStatic fun getAlternateBlocks() = UnsupportedOperationException("1.12 has no Alternative Blocks settings")
        //$$ @JvmStatic fun setAlternateBlocks(toggled: Boolean) = UnsupportedOperationException("1.12 has no Alternative Blocks settings")
        //#endif

        @JvmStatic
        fun getVBOs() = getSettings().useVbo

        @JvmStatic
        fun setVBOs(toggled: Boolean) {
            getSettings().useVbo = toggled
        }

        @JvmStatic
        fun getEntityShadows() = getSettings().entityShadows

        @JvmStatic
        fun setEntityShadows(toggled: Boolean) {
            getSettings().entityShadows = toggled
        }
    }

    object chat {
        // show chat
        @JvmStatic
        fun getVisibility() = getSettings().chatVisibility

        @JvmStatic
        fun setVisibility(visibility: String) {
            when (visibility.toLowerCase()) {
                "hidden" -> getSettings().chatVisibility = EntityPlayer.EnumChatVisibility.HIDDEN
                "commands", "system" -> getSettings().chatVisibility = EntityPlayer.EnumChatVisibility.SYSTEM
                else -> getSettings().chatVisibility = EntityPlayer.EnumChatVisibility.FULL
            }
        }

        // colors
        @JvmStatic
        fun getColors() = getSettings().chatColours

        @JvmStatic
        fun setColors(toggled: Boolean) {
            getSettings().chatColours = toggled
        }

        // web links
        @JvmStatic
        fun getWebLinks() = getSettings().chatLinks

        @JvmStatic
        fun setWebLinks(toggled: Boolean) {
            getSettings().chatLinks = toggled
        }

        // opacity
        @JvmStatic
        fun getOpacity() = getSettings().chatOpacity

        @JvmStatic
        fun setOpacity(opacity: Float) {
            getSettings().chatOpacity = opacity
        }

        // prompt on links
        @JvmStatic
        fun getPromptOnWebLinks() = getSettings().chatLinksPrompt

        @JvmStatic
        fun setPromptOnWebLinks(toggled: Boolean) {
            getSettings().chatLinksPrompt = toggled
        }

        // scale

        // focused height

        // unfocused height

        // width

        // reduced debug info
    }
}
