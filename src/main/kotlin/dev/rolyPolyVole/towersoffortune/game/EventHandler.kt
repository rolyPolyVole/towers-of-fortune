package dev.rolyPolyVole.towersoffortune.game

import dev.rolyPolyVole.towersoffortune.TowersOfFortune
import dev.rolyPolyVole.towersoffortune.util.Messages
import org.bukkit.GameMode
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.EventHandler

class EventHandler(private val plugin: TowersOfFortune, private val game: Game) : Listener {
    fun register() {
        plugin.server.pluginManager.registerEvents(this, plugin)
    }

    fun unregister() {
        HandlerList.unregisterAll(this)
    }

    @EventHandler
    fun onPlayerDeath(event: PlayerDeathEvent) {
        val player = event.player

        player.gameMode = GameMode.SPECTATOR
        player.inventory.clear()
        event.isCancelled = true

        val message =
            if (player.killer == null) Messages.PLAYER_DIED.with(player.name)
            else Messages.PLAYER_KILLED.with(player.name, player.killer!!.name)

        game.players.forEach { it.sendMessage(message) }

        val remainingPlayers = game.players.filter { it.gameMode == GameMode.SURVIVAL }

        if (remainingPlayers.size == 1) {
            game.end(remainingPlayers.first())
        }
    }
}