package discord

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager
import com.sedmelluq.discord.lavaplayer.player.event.TrackEndEvent
import com.sedmelluq.discord.lavaplayer.player.event.TrackStartEvent
import com.sedmelluq.discord.lavaplayer.source.local.LocalAudioSourceManager
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent

data class PartyPlace(
    val guild: Guild,
) {
    private val playerManager = DefaultAudioPlayerManager().apply {
        registerSourceManager(YoutubeAudioSourceManager(true))
        registerSourceManager(LocalAudioSourceManager())
    }
    private val audioPlayer = playerManager.createPlayer().apply {
        addListener {
            if (it is TrackStartEvent) {
                println("${guild.name}: track started: ${it.track.identifier}")
                isPlaying = true
            }
            else if (it is TrackEndEvent) {
                println("${guild.name}: track stopped: ${it.track.identifier}")
                isPlaying = false
                currentSongIndex++
                if (currentSongIndex >= songQueue.size && isLooping) {
                    currentSongIndex = 0
                }
                playCurrentSong()
            }
        }
    }

    private var isPlaying = false
    val songQueue = mutableStateListOf<AudioTrack>()
    var currentSongIndex by mutableStateOf(0)
    var isLooping by mutableStateOf(false)

    private fun handlePlayOrQueue(track: AudioTrack): Boolean {
        songQueue.add(track)
        if (songQueue.isEmpty()) {
            currentSongIndex = 0
            playCurrentSong()
        }
        else if (!isPlaying) {
            currentSongIndex = songQueue.lastIndex
            playCurrentSong()
        }
        else return false
        return true
    }

    private fun playCurrentSong() {
        if (currentSongIndex < 0 || currentSongIndex >= songQueue.size) {
            return
        }
        val track = songQueue[currentSongIndex].makeClone()
        audioPlayer.playTrack(track)
    }

    fun joinVoiceChannel(voiceChannel: VoiceChannel) {
        if (voiceChannel.guild != guild) {
            return
        }

        val audioManager = voiceChannel.guild.audioManager
        audioManager.openAudioConnection(voiceChannel)
    }

    fun skipSong(slashCommandEvent: SlashCommandInteractionEvent) {
        audioPlayer.stopTrack()

        slashCommandEvent.reply("Skipped").queue()
    }

    fun playOrQueueSong(slashCommandEvent: SlashCommandInteractionEvent, voiceChannel: VoiceChannel, songQuery: String) {
        if (slashCommandEvent.guild != guild) {
            return
        }

        println("${voiceChannel.guild.name}: query received: $songQuery")

        joinVoiceChannel(voiceChannel)

        val trimmedQuery = songQuery.replace("||", "").trim()
        val isUrl = trimmedQuery.startsWith("http")
        val finalQuery = if (songQuery == "\uD83C\uDF19" || songQuery == ":crescent_moon:") "F:\\jocka\\Music\\moonman.mp3"
        else if (isUrl) trimmedQuery
        else "ytsearch:$trimmedQuery"

        guild.audioManager.sendingHandler = AudioPlayerSendHandler(audioPlayer)

        playerManager.loadItem(finalQuery, object: AudioLoadResultHandler {
            override fun trackLoaded(track: AudioTrack?) {
                if (track == null) {
                    return
                }
                val isPlaying = handlePlayOrQueue(track)
                slashCommandEvent.reply(if (isPlaying) "Have fun! Now playing: $trimmedQuery" else "Added to queue: $trimmedQuery").queue()
            }
            override fun playlistLoaded(playlist: AudioPlaylist?) {
                if (playlist?.isSearchResult == true) {
                    val luckySong = playlist.tracks.first()
                    val isPlaying = handlePlayOrQueue(luckySong)
                    slashCommandEvent.reply(if (isPlaying) "Have fun! Now playing: ${luckySong.info.title}" else "Added to queue: ${luckySong.info.title}").queue()
                    return
                }
                playlist?.tracks?.forEach {
                    handlePlayOrQueue(it)
                }
                slashCommandEvent.reply("Added ${playlist?.tracks?.size} tracks to the queue.").queue()
            }
            override fun noMatches() {
                slashCommandEvent.reply("Song not found!").queue()
            }
            override fun loadFailed(exception: FriendlyException?) {
                slashCommandEvent.reply("Couldn't load song!").queue()
            }
        })
    }

    fun clearQueue(slashCommandEvent: SlashCommandInteractionEvent? = null) {
        songQueue.clear()
        currentSongIndex = 0
        slashCommandEvent?.reply("Queue cleared")?.queue()
    }

    fun getQueue(slashCommandEvent: SlashCommandInteractionEvent) {
        val queueString = songQueue.joinToString("\n") {
            val isPlaying = songQueue.indexOf(it) == currentSongIndex
            "${if (isPlaying) "▶: " else ""}${it.info.title} - ${it.info.author}"
        }

        slashCommandEvent.reply(queueString).queue()
    }
}