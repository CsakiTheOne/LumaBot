package ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun LumaBotTheme(
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = darkColorScheme(
            primary = ColorStar,
            primaryContainer = ColorStarDark,
            secondary = ColorDress,
        ),
        shapes = Shapes(
            small = Shapes.Full,
            large = Shapes.Full,
        ),
        content = content,
    )
}