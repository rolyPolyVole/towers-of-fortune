package dev.rolyPolyVole.towersoffortune.commands

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.arguments.Argument
import dev.jorel.commandapi.arguments.StringArgument
import dev.jorel.commandapi.executors.CommandArguments
import dev.rolyPolyVole.towersoffortune.TowersOfFortune
import dev.rolyPolyVole.towersoffortune.util.Messages
import org.bukkit.entity.Player

class JoinGameCommand(private val plugin: TowersOfFortune) : CommandAPICommand("join") {
    private val worldNameArgumentNodeName = "worldName"
    
    init {
        withShortDescription("Joins a game")
        withFullDescription("Joins a game of towers of fortune")

        withArguments(getWorldNameArgument())

        executesPlayer(::executesPlayer)
        register(plugin)
    }
    
    private fun getWorldNameArgument(): Argument<String> {
        return StringArgument(worldNameArgumentNodeName)
            .setOptional(false)
    }

    private fun executesPlayer(player: Player, args: CommandArguments) {
        val worldNameArg = args[worldNameArgumentNodeName] as String
        val game = plugin.gameManager.getGame(worldNameArg)

        if (game == null) return player.sendMessage(Messages.GAME_DOES_NOT_EXIST.format())
        if (game.isPlayerInGame(player)) return player.sendMessage(Messages.ALREADY_IN_GAME.format())
        if (game.started) return player.sendMessage(Messages.GAME_ALREADY_STARTED.format())

        game.addPlayer(player)
    }
}