package com.searchjobsai.presentation.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.searchjobsai.data.JobRepository
import com.searchjobsai.data.SortOption
import com.searchjobsai.data.SyncState
import com.searchjobsai.domain.model.DeveloperProfile
import com.searchjobsai.domain.model.JobOffer
import com.searchjobsai.presentation.components.AiMatchBadge
import com.searchjobsai.presentation.components.GeoBadge
import com.searchjobsai.presentation.components.SkillChip
import com.searchjobsai.presentation.theme.*
import kotlinx.coroutines.launch

@Composable
fun JobFeedScreen(
    repository: JobRepository,
    profile: DeveloperProfile,
    onOpenJobDetail: (JobOffer) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    var searchQuery by remember { mutableStateOf("") }
    var remoteOnly by remember { mutableStateOf(true) }
    var selectedSkills by remember { mutableStateOf(setOf<String>()) }
    var currentSort by remember { mutableStateOf(SortOption.PERU_FIRST) }
    var minSalaryK by remember { mutableStateOf(0) }

    val syncState by repository.syncState.collectAsState()
    val syncInfo by repository.syncInfo.collectAsState()
    val allJobs by repository.jobs.collectAsState()

    // Búsqueda inicial automática en la web al abrir la pantalla
    LaunchedEffect(Unit) {
        if (syncState == SyncState.IDLE) {
            repository.refreshFromWeb()
        }
    }

    val allSkills = listOf(
        "Kotlin", "Jetpack Compose", "KMP", "Coroutines", "Clean Arch", "Hilt", "FinTech", "Room"
    )

    val displayedJobs = remember(allJobs, searchQuery, selectedSkills, remoteOnly, minSalaryK, currentSort) {
        repository.filterAndSort(searchQuery, selectedSkills, remoteOnly, minSalaryK, currentSort)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Barra de Búsqueda Activa con Botón de Consulta Web
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Buscar en la web: ej. Senior, Compose, Fintech...", color = TextMuted, fontSize = 12.sp) },
            leadingIcon = {
                Icon(Icons.Default.Search, contentDescription = "Buscar", tint = PrimaryIndigoLight)
            },
            trailingIcon = {
                if (syncState == SyncState.SYNCING) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = PrimaryIndigoLight,
                        strokeWidth = 2.dp
                    )
                } else {
                    IconButton(onClick = {
                        coroutineScope.launch { repository.refreshFromWeb(searchQuery) }
                    }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refrescar web", tint = PrimaryIndigoLight)
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = DarkSurface,
                unfocusedContainerColor = DarkSurface,
                focusedBorderColor = PrimaryIndigo,
                unfocusedBorderColor = DarkBorder,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary
            )
        )

        // Banner de Estado de Sincronización Web
        Surface(
            color = DarkSurfaceVariant,
            shape = RoundedCornerShape(10.dp),
            border = BorderStroke(
                1.dp,
                if (syncState == SyncState.ERROR) PeruRed.copy(alpha = 0.5f) else PrimaryIndigo.copy(alpha = 0.3f)
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val statusDot = when (syncState) {
                        SyncState.SYNCING -> "🟡"
                        SyncState.SUCCESS -> "🟢"
                        SyncState.ERROR -> "🔴"
                        SyncState.IDLE -> "🔵"
                    }
                    Text(statusDot, fontSize = 12.sp)
                    Text(
                        text = syncInfo,
                        color = TextSecondary,
                        fontSize = 11.sp,
                        maxLines = 1
                    )
                }

                TextButton(
                    onClick = { coroutineScope.launch { repository.refreshFromWeb(searchQuery) } },
                    enabled = syncState != SyncState.SYNCING,
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = if (syncState == SyncState.SYNCING) "Buscando..." else "Buscar en la Web",
                        color = PrimaryIndigoLight,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Filtro Modalidad 100% Remoto
        Surface(
            color = DarkSurface,
            shape = RoundedCornerShape(10.dp),
            border = BorderStroke(1.dp, DarkBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text("🌍", fontSize = 16.sp)
                    Text("100% Remoto (Perú / B2B)", fontWeight = FontWeight.SemiBold, color = TextPrimary, fontSize = 12.sp)
                }

                Switch(
                    checked = remoteOnly,
                    onCheckedChange = { remoteOnly = it },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = PrimaryIndigo
                    ),
                    modifier = Modifier.height(28.dp)
                )
            }
        }

        // Fila de Filtros de Skills
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(allSkills) { skill ->
                val isSelected = selectedSkills.contains(skill)
                SkillChip(
                    text = skill,
                    isSelected = isSelected,
                    onClick = {
                        selectedSkills = if (isSelected) {
                            selectedSkills - skill
                        } else {
                            selectedSkills + skill
                        }
                    }
                )
            }
        }

        // Barra de Resumen y Ordenamiento
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${displayedJobs.size} vacantes disponibles",
                color = TextSecondary,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )

            SortDropdown(
                selectedOption = currentSort,
                onOptionSelected = { currentSort = it }
            )
        }

        // Lista de Vacantes
        if (displayedJobs.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("🔍", fontSize = 36.sp)
                    Text("No se encontraron ofertas con esos filtros", color = TextSecondary, fontSize = 13.sp)
                    Button(
                        onClick = {
                            searchQuery = ""
                            selectedSkills = emptySet()
                            coroutineScope.launch { repository.refreshFromWeb() }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryIndigo)
                    ) {
                        Text("Restablecer y Buscar de Nuevo", fontSize = 12.sp)
                    }
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(displayedJobs, key = { it.id }) { job ->
                    JobCard(
                        job = job,
                        onClick = { onOpenJobDetail(job) }
                    )
                }
            }
        }
    }
}

@Composable
fun SortDropdown(
    selectedOption: SortOption,
    onOptionSelected: (SortOption) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = DarkSurface,
            border = BorderStroke(1.dp, DarkBorder),
            modifier = Modifier.clickable { expanded = true }
        ) {
            Text(
                text = "${selectedOption.label} ▼",
                color = PrimaryIndigoLight,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(DarkSurface)
        ) {
            SortOption.values().forEach { option ->
                DropdownMenuItem(
                    text = {
                        Text(
                            option.label,
                            color = if (option == selectedOption) PrimaryIndigoLight else TextPrimary,
                            fontSize = 11.sp
                        )
                    },
                    onClick = {
                        onOptionSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun JobCard(
    job: JobOffer,
    onClick: () -> Unit
) {
    Surface(
        color = DarkSurface,
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, if (job.geoTier.weight == 1) PeruRed.copy(alpha = 0.35f) else DarkBorder),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(DarkSurfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(job.companyIcon, fontSize = 20.sp)
                    }

                    Column {
                        Text(
                            text = job.title,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            fontSize = 13.sp,
                            lineHeight = 17.sp
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(job.company, color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                            Text("•", color = TextMuted)
                            Text(job.location, color = PrimaryIndigoLight, fontSize = 11.sp)
                        }
                    }
                }

                AiMatchBadge(job.aiFitScore)
            }

            // Geo Badge & Fuente Web
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                GeoBadge(job.geoTier)
                Text(
                    text = "🌐 ${job.source}",
                    color = TextMuted,
                    fontSize = 10.sp
                )
            }

            // Skills Pills
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                job.skills.take(4).forEach { skill ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(DarkSurfaceVariant)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(skill, color = TextSecondary, fontSize = 9.sp)
                    }
                }
            }

            // Pie con salario y zona horaria
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = job.salaryDisplay,
                    color = MatchGreen,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
                Text(
                    text = "⏱️ ${job.timezone}",
                    color = TextMuted,
                    fontSize = 10.sp
                )
            }
        }
    }
}
