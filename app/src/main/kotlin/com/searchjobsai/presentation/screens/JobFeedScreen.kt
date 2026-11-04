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
import com.searchjobsai.domain.model.DeveloperProfile
import com.searchjobsai.domain.model.JobOffer
import com.searchjobsai.presentation.components.AiMatchBadge
import com.searchjobsai.presentation.components.GeoBadge
import com.searchjobsai.presentation.components.SkillChip
import com.searchjobsai.presentation.theme.*

@Composable
fun JobFeedScreen(
    repository: JobRepository,
    profile: DeveloperProfile,
    onOpenJobDetail: (JobOffer) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var remoteOnly by remember { mutableStateOf(true) }
    var selectedSkills by remember { mutableStateOf(setOf("Kotlin", "Jetpack Compose", "Clean Arch", "Coroutines")) }
    var currentSort by remember { mutableStateOf(SortOption.PERU_FIRST) }
    var minSalaryK by remember { mutableStateOf(50) }

    val allSkills = listOf(
        "Kotlin", "Jetpack Compose", "KMP", "Coroutines", "Clean Arch", "Hilt", "FinTech", "Room/SQLDelight"
    )

    val displayedJobs = remember(searchQuery, selectedSkills, remoteOnly, minSalaryK, currentSort) {
        repository.filterAndSort(searchQuery, selectedSkills, remoteOnly, minSalaryK, currentSort)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Barra de Búsqueda
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Buscar por rol, empresa o stack...", color = TextMuted, fontSize = 12.sp) },
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

        // Banner Filtro 100% Remoto
        Surface(
            color = DarkSurface,
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, PrimaryIndigo.copy(alpha = 0.3f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("🌍", fontSize = 18.sp)
                    Column {
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text("Modalidad 100% Remoto", fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 12.sp)
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(MatchGreenBg)
                                    .padding(horizontal = 4.dp, vertical = 1.dp)
                            ) {
                                Text("B2B / EOR", color = MatchGreen, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                        Text("Excluir ofertas híbridas o presenciales", color = TextSecondary, fontSize = 10.sp)
                    }
                }

                Switch(
                    checked = remoteOnly,
                    onCheckedChange = { remoteOnly = it },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = PrimaryIndigo,
                        uncheckedThumbColor = TextMuted,
                        uncheckedTrackColor = DarkSurfaceVariant
                    )
                )
            }
        }

        // Filtro de Chips de Habilidades
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                "Tus Skills Android (Filtro Activo)",
                color = TextSecondary,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold
            )

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
        }

        // Ordenamiento y Conteo
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    "${displayedJobs.size} vacantes",
                    color = TextSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(PeruRedBg)
                        .border(1.dp, PeruRed.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text("🇵🇪 Perú First", color = PeruRed, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                }
            }

            // Selector de Orden
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text("Ordenar:", color = TextMuted, fontSize = 11.sp)
                SortDropdown(
                    selectedOption = currentSort,
                    onOptionSelected = { currentSort = it }
                )
            }
        }

        // Listado de Tarjetas de Empleo
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(displayedJobs) { job ->
                JobCard(job = job, onClick = { onOpenJobDetail(job) })
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

            // Geo Badge
            GeoBadge(job.geoTier)

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
