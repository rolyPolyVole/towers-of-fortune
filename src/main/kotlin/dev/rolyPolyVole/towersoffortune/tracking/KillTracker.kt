package dev.rolyPolyVole.towersoffortune.tracking

import dev.rolyPolyVole.towersoffortune.TowersOfFortune
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.entity.Projectile
import org.bukkit.entity.Tameable
import org.bukkit.entity.minecart.ExplosiveMinecart
import org.bukkit.event.entity.EntityDamageByBlockEvent
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.persistence.PersistentDataType
import java.util.*

class KillTracker(private val plugin: TowersOfFortune) {
    private val placedLavaLocations = mutableMapOf<Location, UUID>()
    private val placedCactusLocations = mutableMapOf<Location, UUID>()
    private val placedAnvilLocations = mutableMapOf<Location, UUID>()
    private val placedDripstoneLocations = mutableMapOf<Location, UUID>()
    private val placedMinecartTNT = mutableMapOf<Entity, UUID>()

    // PDC Keys
    private val ownerKey = NamespacedKey(plugin, "mob_owner")
    private val deflectorKey = NamespacedKey(plugin, "projectile_deflector")

    fun trackPlacedHazard(location: Location, player: Player, type: HazardType) {
        when (type) {
            HazardType.LAVA -> placedLavaLocations[location] = player.uniqueId
            HazardType.CACTUS -> placedCactusLocations[location] = player.uniqueId
            HazardType.ANVIL -> placedAnvilLocations[location] = player.uniqueId
            HazardType.DRIPSTONE -> placedDripstoneLocations[location] = player.uniqueId
        }
    }

    fun trackMinecartTNT(minecart: Entity, player: Player) {
        placedMinecartTNT[minecart] = player.uniqueId
    }

    fun setMobOwner(entity: Entity, player: Player) {
        entity.persistentDataContainer.set(ownerKey, PersistentDataType.STRING, player.uniqueId.toString())
    }

    fun setProjectileDeflector(projectile: Entity, player: Player) {
        projectile.persistentDataContainer.set(deflectorKey, PersistentDataType.STRING, player.uniqueId.toString())
    }

    fun getKiller(victim: Entity, lastDamage: EntityDamageEvent): Player? {
        return when (lastDamage) {
            is EntityDamageByEntityEvent -> handleEntityDamage(lastDamage)
            is EntityDamageByBlockEvent -> handleBlockDamage(lastDamage)
            else -> null
        }
    }

    private fun handleEntityDamage(event: EntityDamageByEntityEvent): Player? {
        val damager = event.damager
        return when {
            // Direct player damage
            damager is Player -> damager

            // Projectiles (including deflected ones)
            damager is Projectile -> {
                val deflector = damager.persistentDataContainer.get(deflectorKey, PersistentDataType.STRING)
                if (deflector != null) {
                    Bukkit.getPlayer(UUID.fromString(deflector))
                } else {
                    (damager.shooter as? Player)
                }
            }

            // TNT Minecart
            damager is ExplosiveMinecart -> {
                placedMinecartTNT[damager]?.let { Bukkit.getPlayer(it) }
            }

            // Owned/Tamed mobs
            damager is Tameable && damager.isTamed -> damager.owner as? Player

            // Custom owned mobs (spawn eggs, constructed, etc)
            else -> {
                val ownerId = damager.persistentDataContainer.get(ownerKey, PersistentDataType.STRING)
                ownerId?.let { Bukkit.getPlayer(UUID.fromString(it)) }
            }
        }
    }

    private fun handleBlockDamage(event: EntityDamageByBlockEvent): Player? {
        val block = event.damager ?: return null
        val location = block.location

        return when (block.type) {
            Material.LAVA -> placedLavaLocations[location]?.let { Bukkit.getPlayer(it) }
            Material.CACTUS -> placedCactusLocations[location]?.let { Bukkit.getPlayer(it) }
            Material.ANVIL, Material.DAMAGED_ANVIL -> placedAnvilLocations[location]?.let { Bukkit.getPlayer(it) }
            Material.POINTED_DRIPSTONE -> placedDripstoneLocations[location]?.let { Bukkit.getPlayer(it) }
            else -> null
        }
    }

    enum class HazardType {
        LAVA, CACTUS, ANVIL, DRIPSTONE
    }
}
