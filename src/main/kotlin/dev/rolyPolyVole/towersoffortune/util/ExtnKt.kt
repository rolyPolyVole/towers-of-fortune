package dev.rolyPolyVole.towersoffortune.util

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.entity.HumanEntity

fun String.format(): Component {
    return MiniMessage.miniMessage().deserialize(this)
}

fun HumanEntity.sendMessage(message: Messages, vararg args: Any) {
    this.sendMessage(message.with(args))
}