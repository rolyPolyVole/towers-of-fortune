package dev.rolyPolyVole.towersoffortune.data

import org.bukkit.Location
import org.bukkit.World
import org.bukkit.block.Block

data class CreatedMapData(val world: World, val spawnLocations: List<Location>, val glassBlocks: List<Block>, val centerLocation: Location)
