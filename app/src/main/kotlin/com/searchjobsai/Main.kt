package com.searchjobsai

import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState

fun main() = application {
    val windowState = rememberWindowState(width = 480.dp, height = 850.dp)

    Window(
        onCloseRequest = ::exitApplication,
        state = windowState,
        title = "SearchJobsAIHelper - Jean Ramirez (@jcramirezdev)"
    ) {
        App()
    }
}
