package dev.rolyPolyVole.towersoffortune.commands

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.arguments.Argument
import dev.jorel.commandapi.arguments.ArgumentSuggestions
import dev.jorel.commandapi.arguments.StringArgument
import dev.jorel.commandapi.executors.CommandArguments
import dev.rolyPolyVole.towersoffortune.TowersOfFortune
import dev.rolyPolyVole.towersoffortune.data.GameSettings
import dev.rolyPolyVole.towersoffortune.util.Messages
import org.bukkit.Difficulty
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.util.function.Predicate

class CreateGameCommand(private val plugin: TowersOfFortune) : CommandAPICommand("creategame") {
    private val settingsArgumentNodeName = "settings"

    private val overworldSettings = GameSettings(
        worldName = "towers_of_fortune_overworld",
        dimension = World.Environment.NORMAL, time = 6000L, difficulty = Difficulty.HARD,
        doFireTick = true, announceAdvancements = false,
        surfaceMaterial = Material.GRASS_BLOCK, woodMaterial = Material.OAK_LOG
    )

    private val netherSettings = GameSettings(
        worldName = "towers_of_fortune_nether",
        dimension = World.Environment.NETHER, time = 18000L, difficulty = Difficulty.HARD,
        doFireTick = true, announceAdvancements = false,
        surfaceMaterial = Material.NETHERRACK, woodMaterial = Material.CRIMSON_STEM
    )

    private val endSettings = GameSettings(
        worldName = "towers_of_fortune_end",
        dimension = World.Environment.THE_END, time = 6000L, difficulty = Difficulty.HARD,
        doFireTick = true, announceAdvancements = false,
        surfaceMaterial = Material.END_STONE, woodMaterial = Material.WARPED_STEM
    )

    init {
        requirements = Predicate { it.isOp }

        withShortDescription("Creates a new world and game of towers of fortune")
        withFullDescription("Creates a new world and game of towers of fortune")

        withArguments(getSettingsArgument())

        executesPlayer(::executesPlayer)
        register(plugin)
    }

    private fun getSettingsArgument(): Argument<String> {
        val suggestions = ArgumentSuggestions.strings<CommandSender>(listOf("overworld", "nether", "end"))

        return StringArgument(settingsArgumentNodeName)
            .includeSuggestions(suggestions)
            .setOptional(false)
    }


    private fun executesPlayer(player: Player, args: CommandArguments) {
        val settingsArg = args[settingsArgumentNodeName] as String

        val settings = when (settingsArg) {
            "overworld" -> overworldSettings
            "nether" -> netherSettings
            "end" -> endSettings
            else -> return player.sendMessage(Messages.INVALID_ARGUMENT.with("overworld, nether or end", settingsArg))
        }

        if (settings.isWorldLoaded()) {
            return player.sendMessage(Messages.WORLD_ALREADY_USED.format())
        }

        plugin.gameManager.createGame(settings)
        player.sendMessage(Messages.GAME_CREATED.with(settings.worldName))
    }
}