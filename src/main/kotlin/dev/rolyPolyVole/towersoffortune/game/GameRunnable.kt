package dev.rolyPolyVole.towersoffortune.game

import dev.rolyPolyVole.towersoffortune.TowersOfFortune
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.scheduler.BukkitRunnable

class GameRunnable(private val plugin: TowersOfFortune, private val game: Game) : BukkitRunnable() {
    var age = 0

    fun start() {
        runTaskTimer(plugin, 0L, 1L)
    }

    fun stop() {
        cancel()
    }

    override fun run() {
        age++

        if (age % 60 == 0) {
            val item = ItemStack(Material.entries.filter(Material::isItem).random())

            game.players.forEach { it.inventory.addItem(item) }
        }
    }
}