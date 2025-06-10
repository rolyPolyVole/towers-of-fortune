package dev.rolyPolyVole.towersoffortune

import dev.rolyPolyVole.towersoffortune.game.Game
import dev.rolyPolyVole.towersoffortune.game.createMap
import dev.rolyPolyVole.towersoffortune.util.Messages
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.world.WorldLoadEvent
import org.bukkit.event.world.WorldUnloadEvent
import org.bukkit.plugin.java.JavaPlugin

class TowersOfFortune : JavaPlugin(), Listener {
    lateinit var lobbyWorld: World
    lateinit var game: Game

    var isWorldLoaded = true

    override fun onEnable() {
        logger.info("Enabling Towers of Fortune")

        val spawnLocations = createMap(this)

        lobbyWorld = server.worlds.first()
        game = Game(this, server.getWorld("game_1")!!, spawnLocations, 2)

        server.pluginManager.registerEvents(this, this)
    }

    override fun onDisable() {
        logger.info("Disabling Towers of Fortune")

        server.unloadWorld("game_1", false)
    }

    @EventHandler
    fun onButtonClick(event: PlayerInteractEvent) {
        if (event.clickedBlock?.type != Material.STONE_BUTTON || !event.action.isRightClick || !isWorldLoaded || game.started || game.isFull || game.players.contains(event.player)) return

        game.players.add(event.player)
        game.players.forEach { it.sendMessage(Messages.JOINED_GAME.with(event.player.name)) }

        if (game.isFull) game.start()
    }

    @EventHandler
    fun onPlayerQuit(event: PlayerQuitEvent) {
        if (game.started) {
            event.player.teleport(lobbyWorld.spawnLocation)
            game.players.remove(event.player)

            game.players.forEach { it.sendMessage(Messages.PLAYER_DISCONNECTED.with(event.player)) }
        } else {
            game.players.remove(event.player)
            game.players.forEach { it.sendMessage(Messages.LEFT_GAME.with(event.player.name)) }
        }
    }

    @EventHandler
    fun onWorldLoad(event: WorldLoadEvent) {
        if (event.world.name == "game_1") {
            isWorldLoaded = true
        }
    }

    @EventHandler
    fun onWorldUnload(event: WorldUnloadEvent) {
        if (event.world.name == "game_1") {
            isWorldLoaded = false
        }
    }
}
