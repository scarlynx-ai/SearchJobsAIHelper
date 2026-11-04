package com.searchjobsai

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.searchjobsai.data.JobRepository
import com.searchjobsai.domain.model.DeveloperProfile
import com.searchjobsai.domain.model.JobOffer
import com.searchjobsai.presentation.components.AppHeader
import com.searchjobsai.presentation.screens.JobDetailDialog
import com.searchjobsai.presentation.screens.JobFeedScreen
import com.searchjobsai.presentation.screens.ProfileScreen
import com.searchjobsai.presentation.screens.TrackerScreen
import com.searchjobsai.presentation.theme.DarkBackground
import com.searchjobsai.presentation.theme.DarkBorder
import com.searchjobsai.presentation.theme.DarkSurface
import com.searchjobsai.presentation.theme.PrimaryIndigoLight
import com.searchjobsai.presentation.theme.SearchJobsTheme
import com.searchjobsai.presentation.theme.TextMuted
import com.searchjobsai.presentation.theme.TextPrimary

enum class AppTab(val label: String, val icon: String) {
    FEED("Vacantes", "💼"),
    TRACKER("Tracker", "📋"),
    PROFILE("Mi Perfil", "🧠")
}

@Composable
fun App() {
    val profile = remember { DeveloperProfile() }
    val repository = remember { JobRepository(profile) }

    var currentTab by remember { mutableStateOf(AppTab.FEED) }
    var selectedJobForModal by remember { mutableStateOf<JobOffer?>(null) }

    SearchJobsTheme {
        Scaffold(
            topBar = {
                AppHeader(
                    profile = profile,
                    onOpenProfile = { currentTab = AppTab.PROFILE }
                )
            },
            bottomBar = {
                NavigationBar(
                    containerColor = DarkSurface,
                    modifier = Modifier.border(1.dp, DarkBorder)
                ) {
                    AppTab.values().forEach { tab ->
                        val isSelected = currentTab == tab
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = { currentTab = tab },
                            icon = { Text(tab.icon, fontSize = 18.sp) },
                            label = {
                                Text(
                                    tab.label,
                                    color = if (isSelected) PrimaryIndigoLight else TextMuted,
                                    fontSize = 11.sp
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                indicatorColor = Color.Transparent
                            )
                        )
                    }
                }
            },
            containerColor = DarkBackground
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                when (currentTab) {
                    AppTab.FEED -> JobFeedScreen(
                        repository = repository,
                        profile = profile,
                        onOpenJobDetail = { selectedJobForModal = it }
                    )
                    AppTab.TRACKER -> TrackerScreen(
                        repository = repository,
                        onOpenJobDetail = { selectedJobForModal = it }
                    )
                    AppTab.PROFILE -> ProfileScreen(profile = profile)
                }

                selectedJobForModal?.let { job ->
                    JobDetailDialog(
                        job = job,
                        onDismiss = { selectedJobForModal = null },
                        onToggleSave = { repository.toggleSave(it) },
                        onUpdateStatus = { id, status -> repository.updateStatus(id, status) }
                    )
                }
            }
        }
    }
}
