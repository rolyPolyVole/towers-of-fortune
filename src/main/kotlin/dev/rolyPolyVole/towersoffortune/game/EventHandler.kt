package dev.rolyPolyVole.towersoffortune.game

import dev.rolyPolyVole.towersoffortune.TowersOfFortune
import dev.rolyPolyVole.towersoffortune.tracking.KillTracker
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.FoodLevelChangeEvent
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerChangedWorldEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.player.PlayerRespawnEvent

class EventHandler(private val plugin: TowersOfFortune, private val game: Game) : Listener {
    private val killTracker = KillTracker(plugin)
    var isGameOngoing = false

    fun register() {
        plugin.registerEvents(this)
    }

    fun unregister() {
        HandlerList.unregisterAll(this)
    }

    @EventHandler
    fun onPlayerDeath(event: PlayerDeathEvent) {
        val player = event.player

        if (game !== plugin.gameManager.getGame(player.world.name)) return

        game.remainingPlayers.remove(player)

        player.respawnLocation = game.centerLocation

        if (game.remainingPlayers.size == 1) game.end()
    }

    @EventHandler
    fun onPlayerRespawn(event: PlayerRespawnEvent) {
        val player = event.player

        if (game !== plugin.gameManager.getGame(player.world.name)) return

        game.becomeSpectator(player)
    }

    @EventHandler
    fun onPlayerHurt(event: EntityDamageEvent) {
        val entity = event.entity

        if (isGameOngoing) return
        if (entity !is Player) return

        if (game !== plugin.gameManager.getGame(entity.world.name)) return
        
        event.isCancelled = true
    }

    @EventHandler
    fun onBlockBreak(event: BlockBreakEvent) {
        val player = event.player

        if (isGameOngoing) return

        if (game !== plugin.gameManager.getGame(player.world.name)) return

        event.isCancelled = true
    }

    @EventHandler
    fun onPlayerLoseHunger(event: FoodLevelChangeEvent) {
        val player = event.entity

        if (isGameOngoing) return

        if (game !== plugin.gameManager.getGame(player.world.name)) return

        event.isCancelled = true
    }

    @EventHandler
    fun onPlayerDisconnect(event: PlayerQuitEvent) {
        val player = event.player

        if (game !== plugin.gameManager.getGame(player.world.name)) return

        game.removePlayer(player)
    }

    @EventHandler
    fun onPlayerChangeWorld(event: PlayerChangedWorldEvent) {
        val player = event.player

        if (game !== plugin.gameManager.getGame(event.from.name)) return

        game.removePlayer(player)
    }

//    @EventHandler
//    fun onBlockPlace(event: BlockPlaceEvent) {
//        val block = event.block
//        val player = event.player
//
//        when (block.type) {
//            Material.LAVA -> killTracker.trackPlacedHazard(block.location, player, KillTracker.HazardType.LAVA)
//            Material.CACTUS -> killTracker.trackPlacedHazard(block.location, player, KillTracker.HazardType.CACTUS)
//            Material.ANVIL -> killTracker.trackPlacedHazard(block.location, player, KillTracker.HazardType.ANVIL)
//            Material.POINTED_DRIPSTONE -> killTracker.trackPlacedHazard(block.location, player, KillTracker.HazardType.DRIPSTONE)
//            else -> return
//        }
//    }
//
//    @EventHandler
//    fun onEntitySpawn(event: CreatureSpawnEvent) {
//        val entity = event.entity
//        event.spawnReason
//        event.
//        // Get the player who spawned the entity (from spawn egg, building, etc)
//        player?.let { spawner ->
//            when (entity) {
//                is IronGolem, is SnowGolem, is Wither,
//                is Wolf, is Cat, // Add other tameable mobs
//                is PufferFish -> killTracker.setMobOwner(entity, spawner)
//            }
//        }
//    }
//
//    @EventHandler
//    fun onProjectileHit(event: ProjectileHitEvent) {
//        val projectile = event.entity
//
//        if (projectile is Fireball) {
//            val hitEntity = event.hitEntity as? Player ?: return
//            killTracker.setProjectileDeflector(projectile, hitEntity)
//        }
//    }
//
//    @EventHandler
//    fun onPlayerDeath(event: PlayerDeathEvent) {
//        val victim = event.entity
//        val lastDamage = victim.lastDamageCause ?: return
//
//        val killer = killTracker.getKiller(victim, lastDamage)
//        if (killer != null) {
//            when {
//                // Handle different death messages based on the cause
//                lastDamage.cause == DamageCause.ENTITY_ATTACK &&
//                        lastDamage is EntityDamageByEntityEvent &&
//                        lastDamage.damager is LivingEntity -> {
//                    broadcast(Messages.PLAYER_KILLED_BY_SPAWNED_MOB.with(
//                        victim.name,
//                        killer.name,
//                        lastDamage.damager.type.name.toLowerCase()
//                    ))
//                }
//                else -> broadcast(Messages.PLAYER_KILLED.with(victim.name, killer.name))
//            }
//        } else {
//            broadcast(Messages.PLAYER_DIED.with(victim.name))
//        }
//    }

}