package com.chattriggers.ctjs.engine

import com.chattriggers.ctjs.minecraft.listeners.ClientListener
import com.chattriggers.ctjs.minecraft.wrappers.objects.inventory.Item
import com.chattriggers.ctjs.triggers.*
import com.chattriggers.ctjs.utils.kotlin.External
import kotlin.reflect.KFunction
import kotlin.reflect.full.memberFunctions

@Suppress("unused")
@External
interface IRegister {
    companion object {
        private val methodMap = mutableMapOf<String, KFunction<*>>()
    }

    /**
     * Helper method register a trigger.
     *
     * Called by taking the original name of the method, i.e. `registerChat`,
     * removing the word register, and comparing it case-insensitively with
     * the methods below.
     *
     * @param triggerType the type of trigger
     * @param method The name of the method or the actual method to callback when the event is fired
     * @return The trigger for additional modification
     */
    fun register(triggerType: String, method: Any): OnTrigger {
        val name = triggerType.lowercase()

        var func = methodMap[name]

        if (func == null) {
            func = this::class.memberFunctions.firstOrNull {
                it.name.lowercase() == "register$name"
            } ?: throw NoSuchMethodException("No trigger type named '$triggerType'")
            methodMap[name] = func
        }

        return func.call(this, method) as OnTrigger
    }

    /**
     * Registers a new trigger that runs before a chat message is received.
     *
     * Passes through multiple arguments:
     * - Any number of chat criteria variables
     * - The chat event, which can be cancelled
     *
     * Available modifications:
     * - [OnRenderTrigger.triggerIfCanceled] Sets if triggered if event is already cancelled
     * - [OnChatTrigger.setChatCriteria] Sets the chat criteria
     * - [OnChatTrigger.setParameter] Sets the chat parameter
     * - [OnTrigger.setPriority] Sets the priority
     *
     * @param method The method to call when the event is fired
     * @return The trigger for additional modification
     */
    fun registerChat(method: Any): OnChatTrigger {
        return OnChatTrigger(method, TriggerType.CHAT, getImplementationLoader())
    }

    /**
     * Registers a new trigger that runs before an action bar message is received.
     *
     * Passes through multiple arguments:
     * - Any number of chat criteria variables
     * - The chat event, which can be cancelled
     *
     * Available modifications:
     * - [OnRenderTrigger.triggerIfCanceled] Sets if triggered if event is already cancelled
     * - [OnChatTrigger.setChatCriteria] Sets the chat criteria
     * - [OnChatTrigger.setParameter] Sets the chat parameter
     * - [OnTrigger.setPriority] Sets the priority
     *
     * @param method The method to call when the event is fired
     * @return The trigger for additional modification
     */
    fun registerActionBar(method: Any): OnChatTrigger {
        return OnChatTrigger(method, TriggerType.ACTION_BAR, getImplementationLoader())
    }

    /**
     * Registers a trigger that runs before the world loads.
     *
     * Available modifications:
     * - [OnTrigger.setPriority] Sets the priority
     *
     * @param method The method to call when the event is fired
     * @return The trigger for additional modification
     */
    fun registerWorldLoad(method: Any): OnRegularTrigger {
        return OnRegularTrigger(method, TriggerType.WORLD_LOAD, getImplementationLoader())
    }

    /**
     * Registers a new trigger that runs before the world unloads.
     *
     * Available modifications:
     * - [OnTrigger.setPriority] Sets the priority
     *
     * @param method The method to call when the event is fired
     * @return The trigger for additional modification
     */
    fun registerWorldUnload(method: Any): OnRegularTrigger {
        return OnRegularTrigger(method, TriggerType.WORLD_UNLOAD, getImplementationLoader())
    }

    /**
     * Registers a new trigger that runs before a mouse button is being pressed or released.
     *
     * Passes through four arguments:
     * - The mouse x position
     * - The mouse y position
     * - The mouse button
     * - The mouse button state (true if button is pressed, false otherwise)
     *
     * Available modifications:
     * - [OnTrigger.setPriority] Sets the priority
     *
     * @param method The method to call when the event is fired
     * @return The trigger for additional modification
     */
    fun registerClicked(method: Any): OnRegularTrigger {
        return OnRegularTrigger(method, TriggerType.CLICKED, getImplementationLoader())
    }

    /**
     * Registers a new trigger that runs before the mouse is scrolled.
     *
     * Passes through three arguments:
     * - The mouse x position
     * - The mouse y position
     * - The scroll direction
     *
     * Available modifications:
     * - [OnTrigger.setPriority] Sets the priority
     *
     * @param method The method to call when the event is fired
     * @return The trigger for additional modification
     */
    fun registerScrolled(method: Any): OnRegularTrigger {
        return OnRegularTrigger(method, TriggerType.SCROLLED, getImplementationLoader())
    }

    /**
     * Registers a new trigger that runs while a mouse button is being held down.
     *
     * Passes through five arguments:
     * - The mouse delta x position (relative to last frame)
     * - The mouse delta y position (relative to last frame)
     * - The mouse x position
     * - The mouse y position
     * - The mouse button
     *
     * Available modifications:
     * - [OnTrigger.setPriority] Sets the priority
     *
     * @param method The method to call when the event is fired
     * @return The trigger for additional modification
     */
    fun registerDragged(method: Any): OnRegularTrigger {
        return OnRegularTrigger(method, TriggerType.DRAGGED, getImplementationLoader())
    }

    /**
     * Registers a new trigger that runs before a sound is played.
     *
     * Passes through six arguments:
     * - The sound event's position
     * - The sound event's name
     * - The sound event's volume
     * - The sound event's pitch
     * - The sound event's category's name
     * - The sound event, which can be cancelled
     *
     * Available modifications:
     * - [OnSoundPlayTrigger.setCriteria] Sets the sound name criteria
     * - [OnTrigger.setPriority] Sets the priority
     *
     * @param method The method to call when the event is fired
     * @return The trigger for additional modification
     */
    fun registerSoundPlay(method: Any): OnSoundPlayTrigger {
        return OnSoundPlayTrigger(method, getImplementationLoader())
    }

    /**
     * Registers a new trigger that runs before a noteblock is played.
     *
     * Passes through four arguments:
     * - The note block play event's Vector3d position
     * - The note block play event's note's name
     * - The note block play event's octave
     * - The note block play event, which can be cancelled
     *
     * Available modifications:
     * - [OnTrigger.setPriority] Sets the priority
     *
     * @param method The method to call when the event is fired
     * @return The trigger for additional modification
     */
    fun registerNoteBlockPlay(method: Any): OnRegularTrigger {
        return OnRegularTrigger(method, TriggerType.NOTE_BLOCK_PLAY, getImplementationLoader())
    }

    /**
     * Registers a new trigger that runs before a noteblock is changed.
     *
     * Passes through four arguments:
     * - The note block change event's Vector3d position
     * - The note block change event's note's name
     * - The note block change event's octave
     * - The note block change event, which can be cancelled
     *
     * Available modifications:
     * - [OnTrigger.setPriority] Sets the priority
     *
     * @param method The method to call when the event is fired
     * @return The trigger for additional modification
     */
    fun registerNoteBlockChange(method: Any): OnRegularTrigger {
        return OnRegularTrigger(method, TriggerType.NOTE_BLOCK_CHANGE, getImplementationLoader())
    }

    /**
     * Registers a new trigger that runs before every game tick.
     *
     * Passes through one argument:
     * - Ticks elapsed
     *
     * Available modifications:
     * - [OnTrigger.setPriority] Sets the priority
     *
     * @param method The method to call when the event is fired
     * @return The trigger for additional modification
     */
    fun registerTick(method: Any): OnRegularTrigger {
        return OnRegularTrigger(method, TriggerType.TICK, getImplementationLoader())
    }

    /**
     * Registers a new trigger that runs in predictable intervals. (60 per second by default)
     *
     * Passes through one argument:
     * - Steps elapsed
     *
     * Available modifications:
     * - [OnStepTrigger.setFps] Sets the fps, ie. how many times this trigger will fire
     *      per second
     * - [OnStepTrigger.setDelay] Sets the delay in seconds, ie how many seconds it take
     *      to fire. Overrides [OnStepTrigger.setFps].
     * - [OnTrigger.setPriority] Sets the priority
     *
     * @param method The method to call when the event is fired
     * @return The trigger for additional modification
     */
    fun registerStep(method: Any): OnStepTrigger {
        return OnStepTrigger(method, getImplementationLoader())
    }

    /**
     * Registers a new trigger that runs before the world is drawn.
     *
     * Passes through one argument:
     * - Partial ticks elapsed
     *
     * Available modifications:
     * - [OnTrigger.setPriority] Sets the priority
     *
     * @param method The method to call when the event is fired
     * @return The trigger for additional modification
     */
    fun registerRenderWorld(method: Any): OnRegularTrigger {
        return OnRegularTrigger(method, TriggerType.RENDER_WORLD, getImplementationLoader())
    }

    /**
     * Registers a new trigger that runs before the overlay is drawn.
     *
     * Passes through one argument:
     * - The render event, which cannot be cancelled
     *
     * Available modifications:
     * - [OnTrigger.setPriority] Sets the priority
     *
     * @param method The method to call when the event is fired
     * @return The trigger for additional modification
     */
    fun registerRenderOverlay(method: Any): OnRenderTrigger {
        return OnRenderTrigger(method, TriggerType.RENDER_OVERLAY, getImplementationLoader())
    }

    /**
     * Registers a new trigger that runs before the player list is being drawn.
     *
     * Passes through one argument:
     * - The render event, which can be cancelled
     *
     * Available modifications:
     * - [OnRenderTrigger.triggerIfCanceled] Sets if triggered if event is already cancelled
     * - [OnTrigger.setPriority] Sets the priority
     *
     * @param method The method to call when the event is fired
     * @return The trigger for additional modification
     */
    fun registerRenderPlayerList(method: Any): OnRenderTrigger {
        return OnRenderTrigger(method, TriggerType.RENDER_PLAYER_LIST, getImplementationLoader())
    }

    /**
     * Registers a new trigger that runs before the crosshair is being drawn.
     *
     * Passes through one argument:
     * - The render event, which can be cancelled
     *
     * Available modifications:
     * - [OnRenderTrigger.triggerIfCanceled] Sets if triggered if event is already cancelled
     * - [OnTrigger.setPriority] Sets the priority
     *
     * @param method The method to call when the event is fired
     * @return The trigger for additional modification
     */
    fun registerRenderCrosshair(method: Any): OnRenderTrigger {
        return OnRenderTrigger(method, TriggerType.RENDER_CROSSHAIR, getImplementationLoader())
    }

    /**
     * Registers a trigger that runs before the debug screen is being drawn.
     *
     * Passes through one argument:
     * - The render event, which can be cancelled
     *
     * Available modifications:
     * - [OnRenderTrigger.triggerIfCanceled] Sets if triggered if event is already cancelled
     * - [OnTrigger.setPriority] Sets the priority
     *
     * @param method The method to call when the event is fired
     * @return The trigger for additional modification
     */
    fun registerRenderDebug(method: Any): OnRenderTrigger {
        return OnRenderTrigger(method, TriggerType.RENDER_DEBUG, getImplementationLoader())
    }

    /**
     * Registers a new trigger that runs before the boss health bar is being drawn.
     *
     * Passes through one argument:
     * - The render event, which can be cancelled
     *
     * Available modifications:
     * - [OnRenderTrigger.triggerIfCanceled] Sets if triggered if event is already cancelled
     * - [OnTrigger.setPriority] Sets the priority
     *
     * @param method The method to call when the event is fired
     * @return The trigger for additional modification
     */
    fun registerRenderBossHealth(method: Any): OnRenderTrigger {
        return OnRenderTrigger(method, TriggerType.RENDER_BOSS_HEALTH, getImplementationLoader())
    }

    /**
     * Registers a new trigger that runs before the player's health is being drawn.
     *
     * Passes through one argument:
     * - The render event, which can be cancelled
     *
     * Available modifications:
     * - [OnRenderTrigger.triggerIfCanceled] Sets if triggered if event is already cancelled
     * - [OnTrigger.setPriority] Sets the priority
     *
     * @param method The method to call when the event is fired
     * @return The trigger for additional modification
     */
    fun registerRenderHealth(method: Any): OnRenderTrigger {
        return OnRenderTrigger(method, TriggerType.RENDER_HEALTH, getImplementationLoader())
    }

    /**
     * Registers a new trigger that runs before the player's armor bar is drawn.
     *
     * Passes through one argument:
     * - The render event, which can be cancelled
     *
     * Available modifications:
     * - [OnRenderTrigger.triggerIfCanceled] Sets if triggered if event is already cancelled
     * - [OnTrigger.setPriority] Sets the priority
     *
     * @param method The method to call when the event is fired
     * @return The trigger for additional modification
     */
    fun registerRenderArmor(method: Any): OnRenderTrigger {
        return OnRenderTrigger(method, TriggerType.RENDER_ARMOR, getImplementationLoader())
    }

    /**
     * Registers a new trigger that runs before the player's food is being drawn.
     *
     * Passes through one argument:
     * - The render event, which can be cancelled
     *
     * Available modifications:
     * - [OnRenderTrigger.triggerIfCanceled] Sets if triggered if event is already cancelled
     * - [OnTrigger.setPriority] Sets the priority
     *
     * @param method The method to call when the event is fired
     * @return The trigger for additional modification
     */
    fun registerRenderFood(method: Any): OnRenderTrigger {
        return OnRenderTrigger(method, TriggerType.RENDER_FOOD, getImplementationLoader())
    }

    /**
     * Registers a new trigger that runs before the player's mount's health is being drawn.
     *
     * Passes through one argument:
     * - The render event, which can be cancelled
     *
     * Available modifications:
     * - [OnRenderTrigger.triggerIfCanceled] Sets if triggered if event is already cancelled
     * - [OnTrigger.setPriority] Sets the priority
     *
     * @param method The method to call when the event is fired
     * @return The trigger for additional modification
     */
    fun registerRenderMountHealth(method: Any): OnRenderTrigger {
        return OnRenderTrigger(method, TriggerType.RENDER_MOUNT_HEALTH, getImplementationLoader())
    }

    /**
     * Registers a new trigger that runs before the player's experience is being drawn.
     *
     *
     * Passes through one argument:
     * - The render event, which can be cancelled
     *
     * Available modifications:
     * - [OnRenderTrigger.triggerIfCanceled] Sets if triggered if event is already cancelled
     * - [OnTrigger.setPriority] Sets the priority
     *
     * @param method The method to call when the event is fired
     * @return The trigger for additional modification
     */
    fun registerRenderExperience(method: Any): OnRenderTrigger {
        return OnRenderTrigger(method, TriggerType.RENDER_EXPERIENCE, getImplementationLoader())
    }

    /**
     * Registers a new trigger that runs before the player's hotbar is drawn.
     *
     * Passes through one argument:
     * - The render event, which can be cancelled
     *
     * Available modifications:
     * - [OnRenderTrigger.triggerIfCanceled] Sets if triggered if event is already cancelled
     * - [OnTrigger.setPriority] Sets the priority
     *
     * @param method The method to call when the event is fired
     * @return The trigger for additional modification
     */
    fun registerRenderHotbar(method: Any): OnRenderTrigger {
        return OnRenderTrigger(method, TriggerType.RENDER_HOTBAR, getImplementationLoader())
    }

    /**
     * Registers a new trigger that runs before the player's air level is drawn.
     *
     * Passes through one argument:
     * - The render event, which can be cancelled
     *
     * Available modifications:
     * - [OnRenderTrigger.triggerIfCanceled] Sets if triggered if event is already cancelled
     * - [OnTrigger.setPriority] Sets the priority
     *
     * @param method The method to call when the event is fired
     * @return The trigger for additional modification
     */
    fun registerRenderAir(method: Any): OnRenderTrigger {
        return OnRenderTrigger(method, TriggerType.RENDER_AIR, getImplementationLoader())
    }

    /**
     * Registers a new trigger that runs before the block highlight box is drawn.
     *
     * Passes through two arguments:
     * - The draw block highlight event's position
     * - The draw block highlight event, which can be cancelled
     *
     * Available modifications:
     * - [OnTrigger.setPriority] Sets the priority
     *
     * @param method The method to call when the event is fired
     * @return The trigger for additional modification
     */
    fun registerDrawBlockHighlight(method: Any): OnRegularTrigger {
        return OnRegularTrigger(method, TriggerType.BLOCK_HIGHLIGHT, getImplementationLoader())
    }

    /**
     * Registers a new trigger that runs after the game loads.
     *
     * This runs after the initial loading of the game directly after scripts are
     * loaded and after "/ct loadExtra" happens.
     *
     * Available modifications:
     * - [OnTrigger.setPriority] Sets the priority
     *
     * @param method The method to call when the event is fired
     * @return The trigger for additional modification
     */
    fun registerGameLoad(method: Any): OnRegularTrigger {
        return OnRegularTrigger(method, TriggerType.GAME_LOAD, getImplementationLoader())
    }

    /**
     * Registers a new trigger that runs before the game unloads.
     *
     * This runs before shutdown of the JVM and before "/ct loadExtra" happens.
     *
     * Available modifications:
     * - [OnTrigger.setPriority] Sets the priority
     *
     * @param method The method to call when the event is fired
     * @return The trigger for additional modification
     */
    fun registerGameUnload(method: Any): OnRegularTrigger {
        return OnRegularTrigger(method, TriggerType.GAME_UNLOAD, getImplementationLoader())
    }

    /**
     * Registers a new command that will run the method provided.
     *
     * Passes through multiple arguments:
     * - The arguments supplied to the command by the user
     *
     * Available modifications:
     * - [OnCommandTrigger.setCommandName] Sets the command name
     * - [OnTrigger.setPriority] Sets the priority
     *
     * @param method The method to call when the event is fired
     * @return The trigger for additional modification
     */
    fun registerCommand(method: Any): OnCommandTrigger {
        return OnCommandTrigger(method, getImplementationLoader())
    }

    /**
     * Registers a new trigger that runs when a new gui is first opened.
     *
     * Passes through one argument:
     * - The gui opened event, which can be cancelled
     *
     * Available modifications:
     * - [OnTrigger.setPriority] Sets the priority
     *
     * @param method The method to call when the event is fired
     * @return The trigger for additional modification
     */
    fun registerGuiOpened(method: Any): OnRegularTrigger {
        return OnRegularTrigger(method, TriggerType.GUI_OPENED, getImplementationLoader())
    }

    /**
     * Registers a new trigger that runs when a player joins the world.
     *
     * Maximum is one per tick. Any extras will queue and run in later ticks.
     * This trigger is asynchronous.
     *
     * Passes through one argument:
     * - The [com.chattriggers.ctjs.minecraft.wrappers.objects.PlayerMP] object
     *
     * Available modifications:
     * - [OnTrigger.setPriority] Sets the priority
     *
     * @param method The method to call when the event is fired
     * @return The trigger for additional modification
     */
    fun registerPlayerJoined(method: Any): OnRegularTrigger {
        return OnRegularTrigger(method, TriggerType.PLAYER_JOIN, getImplementationLoader())
    }

    /**
     * Registers a new trigger that runs when a player leaves the world.
     *
     * Maximum is one per tick. Any extras will queue and run in later ticks.
     * This trigger is asynchronous.
     *
     * Passes through one argument:
     * - The name of the player that left
     *
     * Available modifications:
     * - [OnTrigger.setPriority] Sets the priority
     *
     * @param method The method to call when the event is fired
     * @return The trigger for additional modification
     */
    fun registerPlayerLeft(method: Any): OnRegularTrigger {
        return OnRegularTrigger(method, TriggerType.PLAYER_LEAVE, getImplementationLoader())
    }

    /**
     * Registers a new trigger that runs before an item is picked up.
     *
     * Passes through five arguments:
     * - The [Item] that is picked up
     * - The [com.chattriggers.ctjs.minecraft.wrappers.objects.PlayerMP] that picked up the item
     * - The item's position vector
     * - The item's motion vector
     * - The event, which can be cancelled
     *
     * Available modifications:
     * - [OnTrigger.setPriority] Sets the priority
     *
     * @param method The method to call when the event is fired
     * @return The trigger for additional modification
     */
    fun registerPickupItem(method: Any): OnRegularTrigger {
        return OnRegularTrigger(method, TriggerType.PICKUP_ITEM, getImplementationLoader())
    }

    /**
     * Registers a new trigger that runs before an item is dropped.
     *
     * Passes through five arguments:
     * - The [Item] that is dropped up
     * - The [com.chattriggers.ctjs.minecraft.wrappers.objects.PlayerMP] that dropped the item
     * - The item's position vector
     * - The item's motion vector
     * - The event, which can be cancelled
     *
     * Available modifications:
     * - [OnTrigger.setPriority] Sets the priority
     *
     * @param method The method to call when the event is fired
     * @return The trigger for additional modification
     */
    fun registerDropItem(method: Any): OnRegularTrigger {
        return OnRegularTrigger(method, TriggerType.DROP_ITEM, getImplementationLoader())
    }

    /**
     * Registers a new trigger that runs before a screenshot is taken.
     *
     * Passes through two arguments:
     * - The name of the screenshot
     * - The screenshot event, which can be cancelled
     *
     * Available modifications:
     * - [OnTrigger.setPriority] Sets the priority
     *
     * @param method The method to call when the event is fired
     * @return The trigger for additional modification
     */
    fun registerScreenshotTaken(method: Any): OnRegularTrigger {
        return OnRegularTrigger(method, TriggerType.SCREENSHOT_TAKEN, getImplementationLoader())
    }

    /**
     * Registers a new trigger that runs before a message is sent in chat.
     *
     * Passes through two arguments:
     * - The message
     * - The message event, which can be cancelled
     *
     * Available modifications:
     * - [OnTrigger.setPriority] Sets the priority
     *
     * @param method The method to call when the event is fired
     * @return The trigger for additional modification
     */
    fun registerMessageSent(method: Any): OnRegularTrigger {
        return OnRegularTrigger(method, TriggerType.MESSAGE_SENT, getImplementationLoader())
    }

    /**
     * Registers a new trigger that runs when a tooltip is being rendered.
     * This allows for the user to modify what text is in the tooltip, and even the
     * ability to cancel rendering completely.
     *
     * Passes through three arguments:
     * - The list of lore to modify.
     * - The [Item] that this lore is attached to.
     * - The cancellable event.
     *
     * Available modifications:
     * - [OnTrigger.setPriority] Sets the priority
     *
     * @param method The method to call when the event is fired
     * @return The trigger for additional modification
     */
    fun registerItemTooltip(method: Any): OnRegularTrigger {
        return OnRegularTrigger(method, TriggerType.TOOLTIP, getImplementationLoader())
    }

    /**
     * Registers a new trigger that runs before the player interacts.
     *
     * In 1.8.9, the following events will activate this trigger:
     * - Left clicking a block
     * - Right clicking a block
     * - Right clicking the air
     *
     * In 1.12.2, the following events will activate this trigger:
     * - Left clicking a block
     * - Left clicking air
     * - Right clicking an entity
     * - Right clicking a block
     * - Right clicking an item
     * - Right clicking air
     *
     * Passes through three arguments:
     * - The [ClientListener.PlayerInteractAction]
     * - The position of the target as a Vector3d
     * - The event, which can be cancelled
     *
     * Available modifications:
     * - [OnTrigger.setPriority] Sets the priority
     *
     * @param method The method to call when the event is fired
     * @return The trigger for additional modification
     */
    fun registerPlayerInteract(method: Any): OnRegularTrigger {
        return OnRegularTrigger(method, TriggerType.PLAYER_INTERACT, getImplementationLoader())
    }

    /**
     * Registers a new trigger that runs before the player breaks a block
     *
     * Passes through three arguments:
     * - The block
     * - The player who broke the block
     * - The event, which can be cancelled
     *
     * Available modifications:
     * - [OnTrigger.setPriority] Sets the priority
     *
     * @param method The method to call when the event is fired
     * @return The trigger for additional modification
     */
    fun registerBlockBreak(method: Any): OnRegularTrigger {
        return OnRegularTrigger(method, TriggerType.BLOCK_BREAK, getImplementationLoader())
    }

    /**
     * Registers a new trigger that runs before an entity is damaged
     *
     * Passes through two arguments:
     * - The target Entity that is damaged
     * - The PlayerMP attacker
     *
     * @param method The method to call when the event is fired
     * @return The trigger for additional modification
     */
    fun registerEntityDamage(method: Any): OnRegularTrigger {
        return OnRegularTrigger(method, TriggerType.ENTITY_DAMAGE, getImplementationLoader())
    }

    /**
     * Registers a new trigger that runs before an entity dies
     *
     * Passes through one argument:
     * - The Entity that died
     *
     * @param method The method to call when the event is fired
     * @return The trigger for additional modification
     */
    fun registerEntityDeath(method: Any): OnRegularTrigger {
        return OnRegularTrigger(method, TriggerType.ENTITY_DEATH, getImplementationLoader())
    }

    /**
     * Registers a new trigger that runs as a gui is rendered
     *
     * Passes through three arguments:
     * - The mouse x position
     * - The mouse y position
     * - The gui
     *
     * Available modifications:
     * - [OnTrigger.setPriority] Sets the priority
     *
     * @param method The method to call when the event is fired
     * @return The trigger for additional modification
     */
    fun registerGuiRender(method: Any): OnRegularTrigger {
        return OnRegularTrigger(method, TriggerType.GUI_RENDER, getImplementationLoader())
    }

    /**
     * Registers a new trigger that runs whenever a key is typed with a gui open
     *
     * Passes through four arguments:
     * - The character pressed (eg. 'd')
     * - The key code pressed (eg. 41)
     * - The gui
     * - The event, which can be cancelled
     *
     * Available modifications:
     * - [OnTrigger.setPriority] Sets the priority
     *
     * @param method The method to call when the event is fired
     * @return The trigger for additional modification
     */
    fun registerGuiKey(method: Any): OnRegularTrigger {
        return OnRegularTrigger(method, TriggerType.GUI_KEY, getImplementationLoader())
    }

    /**
     * Registers a new trigger that runs whenever the mouse is clicked with a
     * gui open
     *
     * Passes through five arguments:
     * - The mouse x position
     * - The mouse y position
     * - The mouse button
     * - The gui
     * - The event, which can be cancelled
     *
     * Available modifications:
     * - [OnTrigger.setPriority] Sets the priority
     *
     * @param method The method to call when the event is fired
     * @return The trigger for additional modification
     */
    fun registerGuiMouseClick(method: Any): OnRegularTrigger {
        return OnRegularTrigger(method, TriggerType.GUI_MOUSE_CLICK, getImplementationLoader())
    }

    /**
     * Registers a new trigger that runs whenever a mouse button is released
     * with a gui open
     *
     * Passes through five arguments:
     * - The mouse x position
     * - The mouse y position
     * - The mouse button
     * - The gui
     * - The event, which can be cancelled
     *
     * Available modifications:
     * - [OnTrigger.setPriority] Sets the priority
     *
     * @param method The method to call when the event is fired
     * @return The trigger for additional modification
     */
    fun registerGuiMouseRelease(method: Any): OnRegularTrigger {
        return OnRegularTrigger(method, TriggerType.GUI_MOUSE_RELEASE, getImplementationLoader())
    }

    /**
     * Registers a new trigger that runs whenever a mouse button held and dragged
     * with a gui open
     *
     * Passes through five arguments:
     * - The mouse x position
     * - The mouse y position
     * - The mouse button
     * - The gui
     * - The event, which can be cancelled
     *
     * Available modifications:
     * - [OnTrigger.setPriority] Sets the priority
     *
     * @param method The method to call when the event is fired
     * @return The trigger for additional modification
     */
    fun registerGuiMouseDrag(method: Any): OnRegularTrigger {
        return OnRegularTrigger(method, TriggerType.GUI_MOUSE_DRAG, getImplementationLoader())
    }

    /**
     * Registers a new trigger that runs whenever a packet is sent from the client to the server
     *
     * Passes through two arguments:
     * - The packet
     * - The event, which can be cancelled
     *
     * Available modifications:
     * - [OnTrigger.setPriority] Sets the priority
     *
     * @param method The method to call when the event is fired
     * @return The trigger for additional modification
     */
    fun registerPacketSent(method: Any): OnRegularTrigger {
        return OnRegularTrigger(method, TriggerType.PACKET_SENT, getImplementationLoader())
    }

    /**
     * Registers a new trigger that runs whenever a packet is sent to the client from the server
     *
     * Passes through two arguments:
     * - The packet
     * - The event, which can be cancelled
     *
     * Available modifications:
     * - [OnTrigger.setPriority] Sets the priority
     *
     * @param method The method to call when the event is fired
     * @return The trigger for additional modification
     */
    fun registerPacketReceived(method: Any): OnRegularTrigger {
        return OnRegularTrigger(method, TriggerType.PACKET_RECEIVED, getImplementationLoader())
    }

    /**
     * Registers a new trigger that runs whenever the user clicks on a clickable
     * chat component
     *
     * Passes through two arguments:
     * - The [com.chattriggers.ctjs.minecraft.objects.message.TextComponent]
     * - The event, which can be cancelled
     *
     * Available modifications:
     * - [OnTrigger.setPriority] Sets the priority
     *
     * @param method The method to call when the event is fired
     * @return The trigger for additional modification
     */
    fun registerChatComponentClicked(method: Any): OnRegularTrigger {
        return OnRegularTrigger(method, TriggerType.CHAT_COMPONENT_CLICKED, getImplementationLoader())
    }

    /**
     * Registers a new trigger that runs whenever the user hovers over a
     * hoverable chat component
     *
     * Passes through two arguments:
     * - The [com.chattriggers.ctjs.minecraft.objects.message.TextComponent]
     * - The event, which can be cancelled
     *
     * Available modifications:
     * - [OnTrigger.setPriority] Sets the priority
     *
     * @param method The method to call when the event is fired
     * @return The trigger for additional modification
     */
    fun registerChatComponentHovered(method: Any): OnRegularTrigger {
        return OnRegularTrigger(method, TriggerType.CHAT_COMPONENT_HOVERED, getImplementationLoader())
    }

    /**
     * Registers a new trigger that runs whenever an entity is rendered
     *
     * Passes through four arguments:
     * - The [com.chattriggers.ctjs.minecraft.wrappers.objects.Entity]
     * - The position as a Vector3d
     * - The partial ticks
     * - The event, which can be cancelled
     *
     * Available modifications:
     * - [OnTrigger.setPriority] Sets the priority
     *
     * @param method The method to call when the event is fired
     * @return The trigger for additional modification
     */
    fun registerRenderEntity(method: Any): OnRegularTrigger {
        return OnRegularTrigger(method, TriggerType.RENDER_ENTITY, getImplementationLoader())
    }

    /**
     * Registers a new trigger that runs after the current screen is rendered
     *
     * Passes through three arguments:
     * - The GuiScreen
     * - The mouseX
     * - The mouseY
     *
     * Available modifications:
     * - [OnTrigger.setPriority] Sets the priority
     *
     * @param method The method to call when the event is fired
     * @return The trigger for additional modification
     */
    fun registerPostGuiRender(method: Any): OnRegularTrigger {
        return OnRegularTrigger(method, TriggerType.POST_GUI_RENDER, getImplementationLoader())
    }

    /**
     * Registers a new trigger that runs whenever a particle is spawned
     *
     * Passes through three arguments:
     * - The [com.chattriggers.ctjs.minecraft.wrappers.objects.Particle]
     * - The [net.minecraft.util.EnumParticleTypes]
     * - The event, which can be cancelled
     *
     * Available modifications:
     * - [OnTrigger.setPriority] Sets the priority
     *
     * @param method The method to call when the event is fired
     * @return The trigger for additional modification
     */
    fun registerSpawnParticle(method: Any): OnRegularTrigger {
        return OnRegularTrigger(method, TriggerType.SPAWN_PARTICLE, getImplementationLoader())
    }

    /**
     * Registers a new trigger that runs whenever the player has left clicked on an entity
     *
     * Passes through three arguments:
     * - The [com.chattriggers.ctjs.minecraft.wrappers.objects.Entity] that is being hit
     * - The event, which can be cancelled
     *
     * Available modifications:
     * - [OnTrigger.setPriority] Sets the priority
     *
     * @param method The method to call when the event is fired
     * @return The trigger for additional modification
     */
    fun registerAttackEntity(method: Any): OnRegularTrigger {
        return OnRegularTrigger(method, TriggerType.ATTACK_ENTITY, getImplementationLoader())
    }

    /**
     * Registers a new trigger that runs whenever a block is left clicked
     *
     * Note: this is not continuously called while the block is being broken, only once
     * when first left clicked.
     *
     * Passes through three arguments:
     * - The [com.chattriggers.ctjs.minecraft.wrappers.objects.block.Block] being hit
     * - The specific [com.chattriggers.ctjs.minecraft.wrappers.objects.block.BlockFace] being hit
     * - The event, which can be cancelled
     *
     * Available modifications:
     * - [OnTrigger.setPriority] Sets the priority
     *
     * @param method The method to call when the event is fired
     * @return The trigger for additional modification
     */
    fun registerHitBlock(method: Any): OnRegularTrigger {
        return OnRegularTrigger(method, TriggerType.HIT_BLOCK, getImplementationLoader())
    }

    fun getImplementationLoader(): ILoader
}
