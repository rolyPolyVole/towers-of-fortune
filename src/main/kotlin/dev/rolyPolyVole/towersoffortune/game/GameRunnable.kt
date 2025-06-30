package dev.rolyPolyVole.towersoffortune.game

import dev.rolyPolyVole.towersoffortune.TowersOfFortune
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.scheduler.BukkitRunnable

class GameRunnable(private val plugin: TowersOfFortune, private val game: Game) : BukkitRunnable() {
    private val interval = 60
    var age = 0

    val items = Material.entries
        .filterNot(Material::isLegacy)
        .filter(Material::isItem)

    fun start() {
        runTaskTimer(plugin, 0L, 1L)
    }

    fun stop() {
        cancel()
    }

    override fun run() {
        age++

        if (age % interval == 0) {
            game.players.forEach {
                val item = ItemStack(items.random())
                it.inventory.addItem(item)
            }
        }
    }
}