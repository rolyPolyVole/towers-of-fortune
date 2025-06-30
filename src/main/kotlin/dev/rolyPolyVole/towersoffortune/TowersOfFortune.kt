package dev.rolyPolyVole.towersoffortune

import dev.jorel.commandapi.CommandAPI
import dev.jorel.commandapi.CommandAPIBukkitConfig
import dev.rolyPolyVole.towersoffortune.commands.CreateGameCommand
import dev.rolyPolyVole.towersoffortune.commands.JoinGameCommand
import dev.rolyPolyVole.towersoffortune.commands.LeaveGameCommand
import dev.rolyPolyVole.towersoffortune.commands.StartGameCommand
import dev.rolyPolyVole.towersoffortune.manager.GameManager
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin

class TowersOfFortune : JavaPlugin(), Listener {
    val gameManager = GameManager(this)

    private val commandApiConfig = CommandAPIBukkitConfig(this)

    override fun onLoad() {
        CommandAPI.onLoad(commandApiConfig)
    }

    override fun onEnable() {
        logger.info("Enabling Towers of Fortune")

        CommandAPI.onEnable()

        gameManager.init()

        CreateGameCommand(this)
        JoinGameCommand(this)
        LeaveGameCommand(this)
        StartGameCommand(this)
    }

    override fun onDisable() {
        logger.info("Disabling Towers of Fortune")
    }

    fun registerEvents(listener: Listener) {
        server.pluginManager.registerEvents(listener, this)
    }

    fun runTaskLater(delay: Long, task: Runnable) {
        server.scheduler.runTaskLater(this, task, delay)
    }
}
