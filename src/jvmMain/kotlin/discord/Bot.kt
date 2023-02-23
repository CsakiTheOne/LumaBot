package discord

import data.StaticData
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import net.dv8tion.jda.api.events.session.ReadyEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import java.awt.Color

class Bot(
    token: String,
    val onStarted: () -> Unit,
): ListenerAdapter() {

    val jda = JDABuilder.createDefault(token)
        .addEventListeners(this)
        .build()

    init {
        jda.updateCommands().queue()
    }

    override fun onReady(event: ReadyEvent) {
        super.onReady(event)
        println("Bot logged in on these servers: ${jda.guilds.joinToString { it.name }}")
        jda.guilds.filter { BotManager.enabledGuildIds.contains(it.id) }.forEach { guild ->
            guild.updateCommands()
                .addCommands(BotCommand.getBotCommands().map { it.slashCommandData })
                .complete()
            println("Updated commands on ${guild.name}")
        }
        onStarted()
    }

    override fun onSlashCommandInteraction(event: SlashCommandInteractionEvent) {
        super.onSlashCommandInteraction(event)
        val botCommand = BotCommand.getBotCommands()
            .firstOrNull { it.slashCommandData.name == event.name }
        if (botCommand == null) {
            event.reply("I'm lost 🥴").queue()
            return
        }
        botCommand.action(event)
    }

    override fun onCommandAutoCompleteInteraction(event: CommandAutoCompleteInteractionEvent) {
        super.onCommandAutoCompleteInteraction(event)

        when (event.interaction.name) {
            "play" -> {
                event.replyChoiceStrings(StaticData.songs).complete()
            }
        }
    }

    override fun onButtonInteraction(event: ButtonInteractionEvent) {
        super.onButtonInteraction(event)

        when (event.interaction.button.id) {
            "btn_credits_nniko" -> {
                val embed = EmbedBuilder()
                    .setTitle("🧙‍♀️ Nniko")
                    .setDescription("Rooosaaaa! I know we only just met but the memories I’ve made with you (1 1/2 hours spent in the rain saving a random, sacrificing you to the wasteland Jaw Drops, and doing Eden at 12am tired as can be and Eden handing us our behinds on a plater) are some of the funniest memories I’ve made in Sky. Thank you for being my friend \uD83D\uDC9C")
                    .setColor(Color(72, 34, 103))
                    .setImage("https://media.discordapp.net/attachments/1077746661738151956/1077746674652430436/IMG_6196.png")
                    .buildMessage()
                event.reply(embed).complete()
            }
        }
    }

}