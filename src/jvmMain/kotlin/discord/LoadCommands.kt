package discord

import androidx.compose.material.Button
import androidx.compose.ui.graphics.toArgb
import data.StaticData
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.Commands
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData
import net.dv8tion.jda.api.interactions.components.buttons.Button
import net.dv8tion.jda.api.utils.messages.MessageCreateData
import ui.theme.ColorDress
import ui.theme.ColorStar
import java.util.Timer
import kotlin.concurrent.timerTask
import kotlin.math.abs

fun EmbedBuilder.buildMessage(): MessageCreateData = MessageCreateData.fromEmbeds(this.build())



fun loadCommands() {

    loadMinigames()

    BotCommand(
        Commands.slash("askpoop", "Asks someone if they had a good poop.")
            .addOption(
                OptionType.USER,
                "person",
                "The person who I will ask.",
                true,
            ),
    ) {
        val username = it.options.first().asUser.asMention
        it.reply("Have you had a good poop $username? 💩")
            .addActionRow(
                Button.link("https://clips.twitch.tv/KnottyKathishBeanVoteYea-0793gxXKxRb02Ojx", "No"),
                Button.link("https://clips.twitch.tv/KnottyKathishBeanVoteYea-0793gxXKxRb02Ojx", "Yes"),
            )
            .complete()
    }

    BotCommand(
        Commands.slash("credits", "Who made this bot?"),
    ) {
        val embed = EmbedBuilder()
            .setTitle("❤ Who did this?")
            .setColor(ColorStar.toArgb())
            .setDescription("Made by Matt, Neshy, Nniko and Csáki with love <3")
            .addField("Invited by @larsboy28", "Thank you for making this surprise possible <3", false)
            .buildMessage()
        it.reply(embed)
            .addActionRow(
                Button.link("https://www.twitch.tv/princezzrosalina/clip/AverageHilariousSnakeWutFace-4CDtmXsTRO-l5R7B", "Matt"),
                Button.link("https://www.twitch.tv/princezzrosalina/clip/BoringAggressiveTurnipDerp--MiGxlaocPnWdZlz", "Neshy"),
                Button.secondary("btn_credits_nniko", "Nniko"),
                Button.link("https://www.twitch.tv/princezzrosalina/clip/EnticingPrettyKuduPunchTrees-56dcLX9GJxLsJmOI", "Csáki"),
            )
            .complete()
    }

    BotCommand(
        Commands.slash("facts", "Tells a random fact about a topic.")
            .addSubcommands(
                SubcommandData("dolphins", "Tells random dolphin facts."),
            ),
    ) {
        val randomFact = StaticData.dolphinFacts.random()
        val embed = EmbedBuilder()
            .setTitle("🐬 Random dolphin fact for you")
            .setColor(ColorDress.toArgb())
            .setDescription(randomFact)
            .buildMessage()
        it.reply(embed).complete()
    }

    BotCommand(
        Commands.slash("play", "Plays a song from YouTube.")
            .addOption(
                OptionType.STRING,
                "song",
                "YouTube URL or search query",
                true,
                true
            ),
    ) {
        val voiceChannel = it.member?.voiceState?.channel?.asVoiceChannel()
        val songQuery = it.options.first().asString
        if (voiceChannel == null) {
            it.reply("Join a valid voice channel and try again!").complete()
            return@BotCommand
        }
        if (!BotManager.partyPlaces.containsKey(it.guild)) {
            BotManager.partyPlaces[it.guild!!] = PartyPlace(it.guild!!)
        }
        val partyPlace = BotManager.partyPlaces[it.guild!!]!!
        partyPlace.playOrQueueSong(it, voiceChannel, songQuery)
    }

    BotCommand(
        Commands.slash("rawr", "Rawr!"),
    ) {
        it.reply("Rawr!")
            .addActionRow(
                Button.link("https://youtu.be/92V8o60FVZ8", "VIDEO FOOTAGE OF ROSA SAYING RAWR")
            )
            .complete()
    }

    BotCommand(
        Commands.slash("scare", "Scare...")
            .addSubcommands(
                SubcommandData("rosa", "Scare Rosa!")
            ),
    ) {
        it.reply("🐞").complete()
    }

    BotCommand(
        Commands.slash("skip", "Skips the now playing song."),
    ) {
        if (!BotManager.partyPlaces.containsKey(it.guild)) {
            BotManager.partyPlaces[it.guild!!] = PartyPlace(it.guild!!)
        }
        val partyPlace = BotManager.partyPlaces[it.guild!!]!!
        partyPlace.skipSong(it)
    }

}

fun loadMinigames() {

    BotCommand(
        Commands.slash("8ball", "Ask the ball!")
            .addOption(
                OptionType.STRING,
                "question",
                "What do you want to know?",
                true,
            ),
    ) {
        val randomAnswer = StaticData.magic8Ball.random()
        it.deferReply().complete()
        Timer().schedule(timerTask {
            it.hook.sendMessage("${it.member?.asMention}: ${it.options.first().asString}\n**8ball**: $randomAnswer").complete()
        }, (1..5).random() * 1000L)
    }

    BotCommand(
        Commands.slash("coinflip", "Flip a coin!"),
    ) {
        val options = listOf("Heads!", "Tails!")
        it.reply(options.random()).queue()
    }

    BotCommand(
        Commands.slash("rps", "Play rock paper scissors.")
            .addSubcommands(
                SubcommandData("rock", "Play rock paper scissors and choose rock."),
                SubcommandData("paper", "Play rock paper scissors and choose paper."),
                SubcommandData("scissors", "Play rock paper scissors and choose scissors."),
            ),
    ) { event ->
        val shapeMap = listOf(
            "rock" to ":rock:",
            "paper" to ":roll_of_paper:",
            "scissors" to ":scissors:",
        )
        val choiceUser = shapeMap.map { it.first }.indexOf(event.subcommandName)
        val choiceBot = (0..2).random()
        val choicesText = "You: ${shapeMap[choiceUser].second} me: ${shapeMap[choiceBot].second}"
        val result = when ((choiceUser - choiceBot) % 3) {
            0 -> "Draw"
            1 -> "You win!"
            -1 -> "I won!"
            2 -> "I won!"
            -2 -> "You win!"
            else -> "?"
        }
        event.reply("$choicesText\n$result").queue()
    }

}