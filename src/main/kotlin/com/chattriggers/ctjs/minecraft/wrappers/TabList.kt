package com.chattriggers.ctjs.minecraft.wrappers

import com.chattriggers.ctjs.minecraft.objects.message.Message
import com.chattriggers.ctjs.utils.kotlin.External
import com.chattriggers.ctjs.utils.kotlin.MCGameType
import com.chattriggers.ctjs.utils.kotlin.MCITextComponent
import com.google.common.collect.ComparisonChain
import com.google.common.collect.Ordering
import net.minecraft.client.network.NetworkPlayerInfo
import net.minecraft.scoreboard.ScorePlayerTeam
import java.util.*

@External
object TabList {
    private val playerComparator = Ordering.from(PlayerComparator())

    /**
     * Gets names set in scoreboard objectives
     *
     * @return The formatted names
     */
    @JvmStatic
    fun getNamesByObjectives(): List<String> {
        return try {
            val scoreboard = World.getWorld()?.scoreboard ?: return emptyList()
            val sidebarObjective = scoreboard.getObjectiveInDisplaySlot(0)
            val scores = scoreboard.getSortedScores(sidebarObjective)

            scores.map {
                val team = scoreboard.getPlayersTeam(it.playerName)
                ScorePlayerTeam.formatPlayerName(team, it.playerName)
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    @JvmStatic
    fun getNames(): List<String> {
        if (Client.getTabGui() == null) return listOf()

        val playerList = playerComparator.sortedCopy(Client.getMinecraft().thePlayer.sendQueue.playerInfoMap)

        return playerList.map {
            Client.getTabGui()!!.getPlayerName(it)
        }
    }

    /**
     * Gets all names in tabs without formatting
     *
     * @return the unformatted names
     */
    @JvmStatic
    fun getUnformattedNames(): List<String> {
        if (Player.getPlayer() == null) return listOf()

        val tab = Ordering.from(PlayerComparator())
        val conn = Client.getConnection()
        val list = tab.sortedCopy(conn.playerInfoMap)

        return list.map {
            it.gameProfile.name
        }
    }

    @JvmStatic
    fun getHeaderMessage() = Client.getTabGui()?.header?.let(::Message)

    @JvmStatic
    fun getHeader() = Client.getTabGui()?.header?.formattedText

    @JvmStatic
    fun setHeader(header: Any) {
        when (header) {
            is String -> Client.getTabGui()?.setHeader(Message(header).getChatMessage())
            is Message -> Client.getTabGui()?.setHeader(header.getChatMessage())
            is MCITextComponent -> Client.getTabGui()?.setHeader(header)
        }
    }

    @JvmStatic
    fun getFooterMessage() = Client.getTabGui()?.footer?.let(::Message)

    @JvmStatic
    fun getFooter() = Client.getTabGui()?.footer?.formattedText

    @JvmStatic
    fun setFooter(footer: Any) {
        when (footer) {
            is String -> Client.getTabGui()?.setHeader(Message(footer).getChatMessage())
            is Message -> Client.getTabGui()?.setHeader(footer.getChatMessage())
            is MCITextComponent -> Client.getTabGui()?.setHeader(footer)
        }
    }

    internal class PlayerComparator internal constructor() : Comparator<NetworkPlayerInfo> {
        override fun compare(playerOne: NetworkPlayerInfo, playerTwo: NetworkPlayerInfo): Int {
            val teamOne = playerOne.playerTeam
            val teamTwo = playerTwo.playerTeam

            return ComparisonChain
                .start()
                .compareTrueFirst(
                    playerOne.gameType != MCGameType.SPECTATOR,
                    playerTwo.gameType != MCGameType.SPECTATOR
                ).compare(
                    //#if MC<=10809
                    teamOne?.registeredName ?: "",
                    teamTwo?.registeredName ?: ""
                    //#else
                    //$$ teamOne?.name ?: "",
                    //$$ teamTwo?.name ?: ""
                    //#endif
                ).compare(
                    playerOne.gameProfile.name,
                    playerTwo.gameProfile.name
                ).result()
        }
    }
}
