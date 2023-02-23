package ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.unit.dp
import discord.BotCommand
import discord.BotManager
import discord.PartyPlace
import ui.Toast
import java.awt.Desktop
import java.net.URI

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
@Preview
fun DashboardScreen() {
    val clipboard = LocalClipboardManager.current

    var partyPlaces: List<PartyPlace> by remember { mutableStateOf(listOf()) }

    Column(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {

        Text(
            modifier = Modifier.padding(8.dp),
            text = "Hi! It's a me, Luma!",
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.titleLarge,
        )
        Text(
            modifier = Modifier.padding(8.dp),
            text = "A Discord bot from your friends to You! And in your hands of course. You can take care of me " +
                    "by waking me up only when I'm needed and taking me to sleep by closing this window.",
            color = MaterialTheme.colorScheme.onBackground,
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                modifier = Modifier.padding(8.dp),
                text = "Music dashboard",
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.titleLarge,
            )
            Spacer(modifier = Modifier.weight(1f))
            IconButton(
                modifier = Modifier.padding(8.dp),
                onClick = {
                    partyPlaces = BotManager.partyPlaces.values.toList()
                },
            ) {
                Icon(imageVector = Icons.Default.Refresh, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            }
        }
        AnimatedContent(partyPlaces) {
            Column {
                it.map { partyPlace ->
                    Card(modifier = Modifier.padding(8.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                modifier = Modifier.padding(8.dp),
                                text = partyPlace.guild.name,
                                style = MaterialTheme.typography.titleLarge,
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            TextButton(
                                modifier = Modifier.padding(8.dp),
                                onClick = {
                                    partyPlace.clearQueue()
                                },
                            ) {
                                Text(text = "Clear queue")
                            }
                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(bottomStart = 16.dp))
                                    .clickable {
                                        partyPlace.isLooping = !partyPlace.isLooping
                                    },
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Switch(
                                    modifier = Modifier.padding(8.dp),
                                    checked = partyPlace.isLooping,
                                    onCheckedChange = { partyPlace.isLooping = it },
                                    thumbContent = {
                                        Icon(
                                            imageVector = if (partyPlace.isLooping) Icons.Default.Refresh else Icons.Default.ArrowForward,
                                            contentDescription = null,
                                        )
                                    },
                                )
                                Text(
                                    modifier = Modifier.padding(8.dp),
                                    text = "Loop queue",
                                )
                            }
                        }
                        partyPlace.songQueue.map { song ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(
                                    modifier = Modifier.padding(8.dp),
                                    text = "${song.info.title} - ${song.info.author}",
                                )
                                if (partyPlace.currentSongIndex == partyPlace.songQueue.indexOf(song)) {
                                    Text(
                                        modifier = Modifier.padding(8.dp),
                                        text = "🎶",
                                    )
                                }
                                Spacer(modifier = Modifier.weight(1f))
                                IconButton(
                                    modifier = Modifier.padding(8.dp),
                                    onClick = {
                                        Desktop.getDesktop().browse(URI.create("https://www.youtube.com/watch?v=${song.identifier}"))
                                    },
                                ) {
                                    Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                }
                            }
                        }
                    }
                }
            }
        }

        Text(
            modifier = Modifier.padding(8.dp),
            text = "Commands",
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.titleLarge,
        )
        Card(
            modifier = Modifier.padding(8.dp).fillMaxWidth(),
        ) {
            BotCommand.getBotCommands().map { command ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            clipboard.setText(AnnotatedString("/${command.slashCommandData.name}", ParagraphStyle()))
                            Toast.show("Copied to clipboard")
                        },
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(
                        modifier = Modifier.padding(8.dp),
                    ) {
                        Text(text = "/$command")
                        Text(text = command.slashCommandData.description, style = MaterialTheme.typography.bodySmall)
                    }
                }
                Divider()
            }
        }

    }
}
