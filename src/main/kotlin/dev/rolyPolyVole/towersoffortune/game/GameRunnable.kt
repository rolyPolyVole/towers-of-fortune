package dev.rolyPolyVole.towersoffortune.game

import dev.rolyPolyVole.towersoffortune.TowersOfFortune
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.scheduler.BukkitRunnable

class GameRunnable(private val plugin: TowersOfFortune, private val game: Game) : BukkitRunnable() {
    private val interval = 50
    var age = 0

    fun start() {
        runTaskTimer(plugin, 0L, 1L)
    }

    fun stop() {
        cancel()
    }

    override fun run() {
        age++

        if (age % interval == 0) {
            val item = ItemStack(Material.entries.filter(Material::isItem).filterNot(Material::isLegacy).random())

            game.players.forEach { it.inventory.addItem(item) }
        }
    }
}