package dev.rolyPolyVole.towersoffortune.game

import dev.rolyPolyVole.towersoffortune.TowersOfFortune
import dev.rolyPolyVole.towersoffortune.util.Messages
import dev.rolyPolyVole.towersoffortune.util.sendMessage
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.entity.Player

class Game(private val plugin: TowersOfFortune, val world: World, private val spawnLocations: List<Location>) {
    private val eventHandler = EventHandler(plugin, this)
    private var runnable = GameRunnable(plugin, this)

    val players = mutableListOf<Player>()
    var started = false

    val isFull: Boolean
        get() = players.size == spawnLocations.size

    fun start() {
        started = true

        eventHandler.register()
        runnable.start()

        players.forEachIndexed { index, player -> player.teleport(spawnLocations[index]) }
        players.forEach { it.sendMessage(Messages.GUIDE) }
    }

    fun end(winner: Player) {
        started = false

        eventHandler.unregister()
        runnable.stop()
        runnable = GameRunnable(plugin, this)

        players.forEach { it.sendMessage(Messages.PLAYER_WON, winner.name) }
        players.clear()
    }
}