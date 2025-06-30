package dev.rolyPolyVole.towersoffortune.data

import org.bukkit.Bukkit
import org.bukkit.Difficulty
import org.bukkit.Material
import org.bukkit.World

data class GameSettings(
    val worldName: String,
    val dimension: World.Environment,
    val time: Long,
    val difficulty: Difficulty,
    val doFireTick: Boolean,
    val announceAdvancements: Boolean,
    val surfaceMaterial: Material,
    val woodMaterial: Material
) {
    fun isWorldLoaded(): Boolean {
        return Bukkit.getWorld(worldName) != null
    }

    fun getWorld(): World {
        return Bukkit.getWorld(worldName)!!
    }
}
