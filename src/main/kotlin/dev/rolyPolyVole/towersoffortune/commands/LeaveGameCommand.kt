package dev.rolyPolyVole.towersoffortune.commands

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.executors.CommandArguments
import dev.rolyPolyVole.towersoffortune.TowersOfFortune
import dev.rolyPolyVole.towersoffortune.util.Messages
import org.bukkit.entity.Player

class LeaveGameCommand(private val plugin: TowersOfFortune) : CommandAPICommand("leave") {
    init {
        withShortDescription("Leaves the current game")
        withFullDescription("Leaves the current game of fortune")

        executesPlayer(::executesPlayer)
        register(plugin)
    }

    private fun executesPlayer(player: Player, args: CommandArguments) {
        val game = plugin.gameManager.getGame(player.world.name)

        if (game == null) return player.sendMessage(Messages.NOT_IN_GAME.format())

        game.removePlayer(player)
    }
}