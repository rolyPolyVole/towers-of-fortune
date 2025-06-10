package dev.rolyPolyVole.towersoffortune.game

import dev.rolyPolyVole.towersoffortune.TowersOfFortune
import dev.rolyPolyVole.towersoffortune.util.Messages
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.EventHandler
import org.bukkit.event.block.Action
import org.bukkit.event.entity.CreatureSpawnEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.persistence.PersistentDataType
import java.util.UUID

class EventHandler(private val plugin: TowersOfFortune, private val game: Game) : Listener {
    private val mobOwnerKey = NamespacedKey(plugin, "mob_owner")
    private val playerToMobEggUsageLocationMap = mutableMapOf<UUID, Location>()

    fun register() {
        plugin.server.pluginManager.registerEvents(this, plugin)
    }

    fun unregister() {
        HandlerList.unregisterAll(this)
    }

    @EventHandler
    fun onSpawnEggUse(event: PlayerInteractEvent) {
        if (event.action != Action.RIGHT_CLICK_BLOCK) return

        if (event.item?.type?.name?.endsWith("SPAWN_EGG") != true) return

        val location = event.interactionPoint

        if (location == null) return

        playerToMobEggUsageLocationMap[event.player.uniqueId] = location

    }

    @EventHandler
    fun onCreatureSpawn(event: CreatureSpawnEvent) {
        if (event.spawnReason != CreatureSpawnEvent.SpawnReason.SPAWNER_EGG) return

        val location = event.location

        val playerId = playerToMobEggUsageLocationMap.entries.find {
            it.value == location
        }?.key ?: return

        event.entity.persistentDataContainer.set(mobOwnerKey, PersistentDataType.STRING, playerId.toString())
    }

    @EventHandler
    fun onPlayerDeath(event: PlayerDeathEvent) {
        val player = event.player

        player.gameMode = GameMode.SPECTATOR
        player.inventory.clear()
        event.isCancelled = true

        val killerName = player.lastDamageCause?.entity?.name
        val killer = player.lastDamageCause?.entity

        val mobOwnerName = killer?.persistentDataContainer?.get(mobOwnerKey, PersistentDataType.STRING)?.let { plugin.server.getPlayer(it) }?.name

        val message =
            if (killerName == null) Messages.PLAYER_DIED.with(player.name)
            else if (killer is Player || mobOwnerName == null) Messages.PLAYER_KILLED.with(player.name, killerName)
            else Messages.PLAYER_KILLED_BY_SPAWNED_MOB.with(player.name, mobOwnerName, killerName)

        game.players.forEach { it.sendMessage(message) }

        val remainingPlayers = game.players.filter { it.gameMode == GameMode.SURVIVAL }

        if (remainingPlayers.size == 1) {
            game.end(remainingPlayers.first())
        }
    }
}