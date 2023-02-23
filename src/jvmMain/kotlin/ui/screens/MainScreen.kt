package ui.screens

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import discord.BotManager
import java.awt.Desktop
import java.net.URI

@Composable
@Preview
fun MainScreen() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        ExtendedFloatingActionButton(
            modifier = Modifier.padding(8.dp),
            onClick = {
                 BotManager.start()
            },
            icon = {
                if (BotManager.isStarting || BotManager.isRunning) {
                    CircularProgressIndicator()
                }
            },
            text = {
                Text(text = "Wake up Luma")
            },
            expanded = !BotManager.isStarting && !BotManager.isRunning,
        )
        /*
        Button(
            modifier = Modifier.padding(8.dp),
            onClick = {
                Desktop.getDesktop().browse(URI.create(Secret.requestInviteUrl()))
            }
        ) {
            Text(text = "Invite Luma to a Discord server")
        }
        */
    }
}