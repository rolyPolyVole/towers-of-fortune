package dev.rolyPolyVole.towersoffortune.game

import dev.rolyPolyVole.towersoffortune.TowersOfFortune
import org.bukkit.Difficulty
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.WorldCreator
import org.bukkit.WorldType
import kotlin.math.cos
import kotlin.math.sin

fun createMap(plugin: TowersOfFortune): List<Location> {
    val worldCreator = WorldCreator("game_1").apply {
        type(WorldType.FLAT)
        generatorSettings("""{"lakes"\:false,"features"\:false,"layers"\:[{"block"\:"minecraft\:air","height"\:1}],"structures"\:{"structures"\:{}}}""")
        generateStructures(false)
    }

    val world = plugin.server.createWorld(worldCreator)!!.apply {
        time = 6000
        difficulty = Difficulty.HARD
        isAutoSave = false
    }

    val size = 20
    val baseY = 64

    for (x in -size..size) {
        for (z in -size..size) {
            world.getBlockAt(x, baseY, z).type = Material.GRASS_BLOCK
        }
    }

    world.setSpawnLocation(0, baseY + 1, 0)

    val pillarDistanceToCenter = 14
    val pillarHeight = 50

    val spawnLocations = mutableListOf<Location>()

    for (i in 0 until 8) {
        val angle = Math.toRadians(i * 45.0)
        val x = (pillarDistanceToCenter * cos(angle)).toInt()
        val z = (pillarDistanceToCenter * sin(angle)).toInt()

        for (y in baseY until baseY + pillarHeight) {
            world.getBlockAt(x, y, z).type = Material.BEDROCK
        }

        spawnLocations.add(Location(world, x.toDouble(), baseY + pillarHeight + 1.0, z.toDouble()))
    }

    return spawnLocations
}
