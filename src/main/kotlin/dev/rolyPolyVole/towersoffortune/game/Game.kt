package dev.rolyPolyVole.towersoffortune.game

import dev.rolyPolyVole.towersoffortune.TowersOfFortune
import dev.rolyPolyVole.towersoffortune.data.GameSettings
import dev.rolyPolyVole.towersoffortune.functions.createMap
import dev.rolyPolyVole.towersoffortune.util.Messages
import net.kyori.adventure.title.TitlePart
import org.bukkit.*
import org.bukkit.block.Block
import org.bukkit.entity.Player
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

class Game(private val plugin: TowersOfFortune, private val settings: GameSettings) {
    private val eventHandler = EventHandler(plugin, this)
    private val runnable = GameRunnable(plugin, this)

    val players = mutableListOf<Player>()
    val remainingPlayers = mutableListOf<Player>()

    var started = false

    lateinit var world: World
    lateinit var spawnLocations: List<Location>
    lateinit var glassBlocks: List<Block>
    lateinit var centerLocation: Location

    fun createWorld() {
        val mapData = createMap(settings)

        world = mapData.world
        spawnLocations = mapData.spawnLocations
        glassBlocks = mapData.glassBlocks
        centerLocation = mapData.centerLocation

        eventHandler.register()
        eventHandler.isGameOngoing = true
    }

    fun addPlayer(player: Player) {
        players.add(player)
        remainingPlayers.add(player)

        resetState(player)
        player.teleport(spawnLocations[players.indexOf(player)])

        players.forEach { it.sendMessage(Messages.JOINED_GAME.with(player.name)) }

        if (players.size == 8) start()
    }

    fun removePlayer(player: Player) {
        players.remove(player)
        remainingPlayers.remove(player)

        resetState(player)
        player.teleport(Bukkit.getWorlds().first().spawnLocation)

        players.forEach { it.sendMessage(Messages.LEFT_GAME.with(player.name)) }

        if (remainingPlayers.size == 1 && started) end()
    }

    fun isPlayerInGame(player: Player): Boolean {
        return players.contains(player)
    }

    fun start() {
        runnable.start()
        started = true

        players.forEach {
            it.sendTitlePart(TitlePart.TITLE, Messages.START_TITLE.format())
            it.sendTitlePart(TitlePart.SUBTITLE, Messages.START_SUBTITLE.format())
            it.sendMessage(Messages.GUIDE.format())

            it.addPotionEffect(PotionEffect(PotionEffectType.SLOW_FALLING, 2 * 20, 1))
        }
    }

    fun end() {
        val winner = remainingPlayers.first()

        players.forEach { it.sendMessage(Messages.PLAYER_WON.with(winner.name)) }

        eventHandler.isGameOngoing = false

        plugin.runTaskLater(20L * 5) {
            players.forEach(::resetState)
            players.forEach { it.teleport(Bukkit.getWorlds().first().spawnLocation) }

            eventHandler.unregister()
            runnable.stop()
            plugin.gameManager.unloadGame(world.name)
        }
    }

    fun becomeSpectator(player: Player) {
        player.gameMode = GameMode.SPECTATOR
        player.teleport(centerLocation)
    }

    fun resetState(player: Player) {
        player.gameMode = GameMode.SURVIVAL

        player.inventory.clear()

        player.fireTicks = 0
        player.clearActivePotionEffects()
        player.clearActiveItem()

        player.health = 20.0
        player.foodLevel = 20
        player.saturation = 20f

        player.level = 0
        player.exp = 0f
    }
}