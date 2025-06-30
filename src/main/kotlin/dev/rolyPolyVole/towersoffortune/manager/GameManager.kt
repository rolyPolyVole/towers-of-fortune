package dev.rolyPolyVole.towersoffortune.manager

import dev.rolyPolyVole.towersoffortune.TowersOfFortune
import dev.rolyPolyVole.towersoffortune.data.GameSettings
import dev.rolyPolyVole.towersoffortune.game.Game
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.world.WorldLoadEvent

class GameManager(private val plugin: TowersOfFortune) : Listener {
    private val games = mutableMapOf<String, Game>()
    private val loadStates = mutableMapOf<String, Boolean>()

    fun init() {
        plugin.registerEvents(this)
    }

    fun createGame(settings: GameSettings): Game {
        val game = Game(plugin, settings)

        games[settings.worldName] = game
        loadStates[settings.worldName] = false

        game.createWorld()
        return game
    }

    fun unloadGame(worldName: String) {
        Bukkit.unloadWorld(worldName, false)

        games.remove(worldName)
        loadStates.remove(worldName)
    }

    fun getGame(worldName: String): Game? {
        return games[worldName]
    }

    fun isGameLoaded(worldName: String): Boolean {
        return loadStates[worldName] ?: false
    }

    fun getAllGameWorldNames(): List<String> {
        return games.keys.toList()
    }

    @EventHandler
    fun onWorldLoad(event: WorldLoadEvent) {
        val name = event.world.name

        if (loadStates.containsKey(name)) {
            loadStates[name] = true
        }
    }
}