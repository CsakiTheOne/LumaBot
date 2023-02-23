package ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class Toast {
    companion object {
        var text by mutableStateOf("")

        fun show(text: String) {
            this.text = text
        }

        fun close() {
            text = ""
        }
    }
}