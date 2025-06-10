package dev.rolyPolyVole.towersoffortune.util

import net.kyori.adventure.text.Component

enum class Messages(val message: String) {
    JOINED_GAME("<yellow><blue>{0}</blue> has joined the game!</yellow>"),
    LEFT_GAME("<yellow><blue>{0}</blue> has left the game!</yellow>"),

    GUIDE("<yellow>You will receive a random item every few seconds!</yellow><newline/><yellow>The last one standing wins!</yellow>"),

    PLAYER_DIED("<red><gray>{0}</gray> has died!</red>"),
    PLAYER_KILLED("<red><gray>{0}</gray> was killed by <gray>{1}</gray>!<red>"),
    PLAYER_KILLED_BY_SPAWNED_MOB("<red><gray>{0}</gray> was killed by <gray>{1}'s</gray> <dark_green>{2}</dark_green></red>"),
    PLAYER_DISCONNECTED("<yellow><blue>{0}</blue> has disconnected from the game!</yellow>"),

    PLAYER_WON("<green><bold><dark_green>{0}</dark_green></bold> has won the game!</green>");

    /**
     * Converts this message to a component using the format() extension function.
     * @return The formatted component
     */
    fun format() = message.format()

    /**
     * Replaces placeholders in the message with the provided arguments and returns it as a component.
     * @param args The arguments to replace placeholders with
     * @return The formatted component with placeholders replaced
     */
    fun with(vararg args: Any): Component {
        var formattedMessage = message

        args.forEachIndexed { index, arg ->
            formattedMessage = formattedMessage.replace("{$index}", arg.toString())
        }

        return formattedMessage.format()
    }
}
