package discord

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData

data class BotCommand(
    val slashCommandData: SlashCommandData,
    val action: (event: SlashCommandInteractionEvent) -> Unit,
) {

    init {
        botCommands.add(this)
    }

    override fun toString(): String {
        val options = slashCommandData.options.joinToString {
            if (it.isRequired) "<${it.name}>"
            else "[${it.name}]"
        }
        val subcommands = slashCommandData.subcommands.joinToString("/") { it.name }
        return "${slashCommandData.name} $options$subcommands"
    }

    companion object {

        private val botCommands = mutableListOf<BotCommand>()

        fun getBotCommands(): List<BotCommand> = botCommands

    }

}