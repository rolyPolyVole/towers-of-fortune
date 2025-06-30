package dev.rolyPolyVole.towersoffortune.functions

import dev.rolyPolyVole.towersoffortune.data.CreatedMapData
import dev.rolyPolyVole.towersoffortune.data.GameSettings
import org.bukkit.*
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import kotlin.math.cos
import kotlin.math.sin

fun generateWorld(settings: GameSettings): World {
    val worldCreator = WorldCreator(settings.worldName).apply {
        type(WorldType.FLAT)
        generatorSettings("""{"lakes":false,"features":false,"layers":[{"block":"minecraft:air","height":1}],"structures":{"structures":{}}}""")
        generateStructures(false)
        environment(settings.dimension)
    }

    val world = Bukkit.createWorld(worldCreator)!!.apply {
        time = settings.time
        difficulty = settings.difficulty
        isAutoSave = false
    }

    world.setGameRule(GameRule.SPAWN_CHUNK_RADIUS, 2)
    world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false)
    world.setGameRule(GameRule.ENDER_PEARLS_VANISH_ON_DEATH, true)
    world.setGameRule(GameRule.DO_IMMEDIATE_RESPAWN, true)

    world.setGameRule(GameRule.DO_FIRE_TICK, settings.doFireTick)
    world.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, settings.announceAdvancements)

    return world
}

fun createMap(settings: GameSettings): CreatedMapData {
    val world = generateWorld(settings)

    val radius = 21
    val radiusSquared = radius * radius
    val baseY = 64

    for (x in -radius..radius) {
        for (z in -radius..radius) {
            if (x * x + z * z <= radiusSquared) {
                world.getBlockAt(x, baseY, z).type = settings.surfaceMaterial
            }
        }
    }

    world.setSpawnLocation(0, baseY + 1, 0)

    val pillarDistanceToCenter = 14
    val pillarHeight = 50

    val spawnLocations = mutableListOf<Location>()
    val glassBlocks = mutableListOf<Block>()

    for (i in 0 until 8) {
        val angle = Math.toRadians(i * 45.0)
        val x = (pillarDistanceToCenter * cos(angle)).toInt()
        val z = (pillarDistanceToCenter * sin(angle)).toInt()

        // Bedrock pillar
        for (y in baseY + 1 until baseY + pillarHeight) {
            world.getBlockAt(x, y, z).type = Material.BEDROCK
        }

        // Glass cage, floor block is the third block above each bedrock pillar
        val floor = world.getBlockAt(x, baseY + pillarHeight + 2, z)
        val feet = floor.getRelative(BlockFace.UP)
        val head = feet.getRelative(BlockFace.UP)
        val roof = head.getRelative(BlockFace.UP)

        floor.type = Material.GLASS
        roof.type = Material.GLASS

        glassBlocks.addAll(listOf(floor, roof))

        listOf(feet, head).forEach {
            it.getRelative(BlockFace.NORTH).apply{ glassBlocks.add(it) }.type = Material.GLASS
            it.getRelative(BlockFace.WEST).apply{ glassBlocks.add(it) }.type = Material.GLASS
            it.getRelative(BlockFace.EAST).apply{ glassBlocks.add(it) }.type = Material.GLASS
            it.getRelative(BlockFace.SOUTH).apply{ glassBlocks.add(it) }.type = Material.GLASS
        }

        // Wood around the base of each pillar
        for (xOffset in (-1..1)) {
            for (zOffset in (-1..1)) {
                if (xOffset == 0 && zOffset == 0) continue

                world.getBlockAt(x + xOffset, baseY + 1, z + zOffset).type = settings.woodMaterial
            }
        }

        spawnLocations.add(Location(world, x.toDouble() + 0.5, baseY + pillarHeight + 3.0, z.toDouble() + 0.5))
    }

    return CreatedMapData(world, spawnLocations, glassBlocks, Location(world, 0.0, baseY + pillarHeight + 1.0, 0.0))
}
