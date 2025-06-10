package dev.rolyPolyVole.towersoffortune.game

import dev.rolyPolyVole.towersoffortune.TowersOfFortune
import dev.rolyPolyVole.towersoffortune.util.Messages
import dev.rolyPolyVole.towersoffortune.util.sendMessage
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.entity.Player

class Game(private val plugin: TowersOfFortune, val world: World, private val spawnLocations: List<Location>, private val startLimit: Int) {
    private val eventHandler = EventHandler(plugin, this)
    private var runnable = GameRunnable(plugin, this)

    val players = mutableListOf<Player>()
    var started = false

    val isFull: Boolean
        get() = players.size == startLimit

    fun start() {
        started = true

        eventHandler.register()
        runnable.start()

        players.forEachIndexed { index, player -> player.teleport(spawnLocations[index]) }
        players.forEach { it.sendMessage(Messages.GUIDE) }
        players.forEach { it.gameMode = GameMode.SURVIVAL }

        healPlayers()
    }

    fun end(winner: Player) {
        started = false

        eventHandler.unregister()
        runnable.stop()
        runnable = GameRunnable(plugin, this)

        print(players.size)
        print(winner.name)

        players.forEach { it.gameMode = GameMode.SURVIVAL }
        healPlayers()

        players.forEach { it.sendMessage(Messages.PLAYER_WON, winner.name) }
        players.forEach { it.teleport(plugin.lobbyWorld.spawnLocation) }
        players.clear()

        resetMap()
    }

    private fun healPlayers() {
        players.forEach {
            it.health = 20.0
            it.foodLevel = 20
            it.saturation = 20.0F
        }
    }

    private fun resetMap() {
        plugin.server.unloadWorld("game_1", false)

        createMap(plugin)
    }
}