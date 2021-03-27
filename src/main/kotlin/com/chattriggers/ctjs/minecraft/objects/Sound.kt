package com.chattriggers.ctjs.minecraft.objects

import com.chattriggers.ctjs.CTJS
import com.chattriggers.ctjs.minecraft.wrappers.Client
import com.chattriggers.ctjs.minecraft.wrappers.Player
import com.chattriggers.ctjs.minecraft.wrappers.World
import com.chattriggers.ctjs.utils.kotlin.External
import com.chattriggers.ctjs.utils.kotlin.MCSoundCategory
import net.minecraft.client.audio.SoundManager
import net.minecraftforge.fml.relauncher.ReflectionHelper
import org.mozilla.javascript.NativeObject
import paulscode.sound.SoundSystem
import java.io.File
import java.net.MalformedURLException

/**
 * <p>
 *     Instances a new Sound with certain properties. These properties
 *     should be passed through as a normal JavaScript object.
 * </p>
 *
 * <p>
 *     REQUIRED:<br>
 *     &emsp;source (String) - filename, relative to ChatTriggers assets directory
 * </p>
 *
 * <p>
 *     OPTIONAL:<br>
 *     &emsp;priority (boolean) - whether or not this sound should be prioritized, defaults to false<br>
 *     &emsp;loop (boolean) - whether or not to loop this sound over and over, defaults to false<br>
 *     &emsp;stream (boolean) - whether or not to stream this sound rather than preload it (should be true for large files), defaults to false
 * </p>
 *
 * <p>
 *     CONFIGURABLE (can be set in config object, or changed later, but MAKE SURE THE WORLD HAS LOADED)<br>
 *     &emsp;category (String) - which category this sound should be a part of, see {@link #setCategory(String)}.<br>
 *     &emsp;volume (float) - volume of the sound, see {@link #setVolume(float)}<br>
 *     &emsp;pitch (float) - pitch of the sound, see {@link #setPitch(float)}<br>
 *     &emsp;x, y, z (float) - location of the sound, see {@link #setPosition(float, float, float)}. Defaults to the players position.<br>
 *     &emsp;attenuation (int) - fade out model of the sound, see {@link #setAttenuation(int)}<br>
 * </p>
 *
 * @param config the JavaScript config object
 */
@External
class Sound(private val config: NativeObject) {
    private var sndSystem: SoundSystem? = null
    private val source: String = config["source"] as String
    var isListening = false

    init {
        if (World.isLoaded()) {
            loadSndSystem()

            try {
                bootstrap()
            } catch (e: MalformedURLException) {
                e.printStackTrace()
            }

        } else {
            isListening = true
        }

        CTJS.sounds.add(this)
    }

    fun onWorldLoad() {
        isListening = false

        loadSndSystem()

        try {
            bootstrap()
        } catch (exc: MalformedURLException) {
            exc.printStackTrace()
        }

    }

    private fun loadSndSystem() {
        val sndManager = Client.getMinecraft().soundHandler.sndManager

        sndSystem = ReflectionHelper.getPrivateValue<SoundSystem, SoundManager>(
            SoundManager::class.java,
            sndManager,
            "sndSystem",
            "field_148620_e"
        )
    }

    @Throws(MalformedURLException::class)
    private fun bootstrap() {
        val source = config["source"]?.toString() ?: throw IllegalArgumentException("Sound source is null.")
        val priority = config.getOrDefault("priority", false) as Boolean
        val loop = config.getOrDefault("loop", false) as Boolean
        val stream = config.getOrDefault("stream", false) as Boolean

        val url = File(CTJS.assetsDir, source).toURI().toURL()
        val x = (config.getOrDefault("x", Player.getX()) as Double).toFloat()
        val y = (config.getOrDefault("y", Player.getY()) as Double).toFloat()
        val z = (config.getOrDefault("z", Player.getZ()) as Double).toFloat()
        val attModel = config.getOrDefault("attenuation", 1) as Int
        val distOrRoll = 16

        if (stream) {
            sndSystem!!.newStreamingSource(
                priority,
                source,
                url,
                source,
                loop,
                x,
                y,
                z,
                attModel,
                distOrRoll.toFloat()
            )
        } else {
            sndSystem!!.newSource(
                priority,
                source,
                url,
                source,
                loop,
                x,
                y,
                z,
                attModel,
                distOrRoll.toFloat()
            )
        }

        if (config["volume"] != null) {
            setVolume(config["volume"] as Float)
        }

        if (config["pitch"] != null) {
            setPitch(config["pitch"] as Float)
        }

        if (config["category"] != null) {
            setCategory(config["category"] as String)
        }
    }

    /**
     * Sets the category of this sound, making it respect the Player's sound volume sliders.
     * Options are: master, music, record, weather, block, hostile, neutral, player, and ambient
     *
     * @param category the category
     */
    fun setCategory(category: String) = apply {
        val category1 = MCSoundCategory.getCategory(category.toLowerCase())
        setVolume(Client.getMinecraft().gameSettings.getSoundLevel(category1))
    }

    /**
     * Sets this sound's volume.
     * Will override the category if called after [setCategory], but not if called before.
     *
     * @param volume New volume, float value ( 0.0f - 1.0f ).
     */
    fun setVolume(volume: Float) = apply { sndSystem!!.setVolume(this.source, volume) }

    fun getVolume() = sndSystem!!.getVolume(this.source)

    /**
     * Updates the position of this sound
     *
     * @param x the x coordinate
     * @param y the y coordinate
     * @param z the z coordinate
     */
    fun setPosition(x: Float, y: Float, z: Float) = apply { sndSystem!!.setPosition(this.source, x, y, z) }

    /**
     * Sets this sound's pitch.
     *
     * @param pitch A float value ( 0.5f - 2.0f ).
     */
    fun setPitch(pitch: Float) = apply { sndSystem!!.setPitch(this.source, pitch) }

    fun getPitch() = sndSystem!!.getPitch(this.source)

    /**
     * Sets the attenuation (fade out over space) of the song.
     * Models are:
     *  NONE(0) - no fade
     *  ROLLOFF(1) - this is the default, meant to be somewhat realistic
     *  LINEAR(2) - fades out linearly, as the name implies
     *
     * @param model the model
     */
    fun setAttenuation(model: Int) = apply { sndSystem!!.setAttenuation(this.source, model) }

    /**
     * Plays/resumes the sound
     */
    fun play() {
        sndSystem!!.play(this.source)
    }

    /**
     * Pauses the sound, to be resumed later
     */
    fun pause() {
        sndSystem!!.pause(this.source)
    }

    /**
     * Completely stops the song
     */
    fun stop() {
        sndSystem!!.stop(this.source)
    }

    /**
     * I really don't know what this does
     */
    fun rewind() {
        sndSystem!!.rewind(this.source)
    }
}
