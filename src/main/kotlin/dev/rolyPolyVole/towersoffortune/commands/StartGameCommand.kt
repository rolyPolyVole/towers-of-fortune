package dev.rolyPolyVole.towersoffortune.commands

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.executors.CommandArguments
import dev.rolyPolyVole.towersoffortune.TowersOfFortune
import dev.rolyPolyVole.towersoffortune.util.Messages
import org.bukkit.entity.Player
import java.util.function.Predicate

class StartGameCommand(private val plugin: TowersOfFortune) : CommandAPICommand("start") {
    init {
        requirements = Predicate { it.isOp }

        withShortDescription("Starts the current game")
        withFullDescription("Force starts the current game of towers of fortune")

        executesPlayer(::executesPlayer)
        register(plugin)
    }

    private fun executesPlayer(player: Player, args: CommandArguments) {
        val game = plugin.gameManager.getGame(player.world.name)

        if (game == null || !game.isPlayerInGame(player)) return player.sendMessage(Messages.NOT_IN_GAME.format())
        if (game.started) return player.sendMessage(Messages.GAME_ALREADY_STARTED.format())
        if (game.players.size < 2) return player.sendMessage(Messages.NOT_ENOUGH_PLAYERS.with(2))

        game.start()
    }
}