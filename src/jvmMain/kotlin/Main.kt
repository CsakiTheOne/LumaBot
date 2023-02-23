import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import discord.BotManager
import ui.Toast
import ui.screens.DashboardScreen
import ui.screens.MainScreen
import ui.theme.LumaBotTheme

@OptIn(ExperimentalAnimationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun App() {
    LumaBotTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            AnimatedContent(targetState = BotManager.isRunning) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.BottomCenter,
                ) {
                    if (it) {
                        DashboardScreen()
                    }
                    else {
                        MainScreen()
                    }
                    if (Toast.text.isNotBlank()) {
                        Card(
                            modifier = Modifier.alpha(.5f).padding(64.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color.Black,
                                contentColor = Color.White,
                            )
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(modifier = Modifier.padding(8.dp), text = Toast.text)
                                IconButton(
                                    onClick = { Toast.close() }
                                ) {
                                    Icon(imageVector = Icons.Default.Close, contentDescription = null)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

fun main() = application {
    Window(onCloseRequest = ::exitApplication, title = "LumaBot") {
        App()
    }
}
