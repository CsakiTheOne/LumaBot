package discord

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import net.dv8tion.jda.api.entities.Guild
import java.util.*
import kotlin.concurrent.timerTask

class BotManager {
    companion object {

        var partyPlaces = mutableMapOf<Guild, PartyPlace>()

        val enabledGuildIds = listOf(
            "906968040036503572", // Comet Observatory's server
            "936292033943973928", // Csáki's test chamber
        )

        var isStarting by mutableStateOf(false)
        var isRunning by mutableStateOf(false)

        private var bot: Bot? = null

        fun start() {
            if (isStarting || isRunning) return

            isStarting = true
            loadCommands()
            Timer().schedule(timerTask {
                bot = Bot(
                    token = Secret.requestToken(),
                    onStarted = {
                        isRunning = true
                        isStarting = false
                    },
                )
            }, 200)
        }

    }
}