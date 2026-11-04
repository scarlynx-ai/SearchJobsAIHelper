package com.searchjobsai.presentation.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.searchjobsai.data.JobRepository
import com.searchjobsai.domain.model.ApplicationStatus
import com.searchjobsai.domain.model.JobOffer
import com.searchjobsai.presentation.components.AiMatchBadge
import com.searchjobsai.presentation.components.GeoBadge
import com.searchjobsai.presentation.theme.*

@Composable
fun TrackerScreen(
    repository: JobRepository,
    onOpenJobDetail: (JobOffer) -> Unit
) {
    val allJobs by repository.jobs.collectAsState()
    var selectedFilter by remember { mutableStateOf(ApplicationStatus.SAVED) }

    val savedOrTracked = remember(allJobs) {
        allJobs.filter { it.isSaved || it.status != ApplicationStatus.NONE }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Encabezado
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Pipeline de Postulaciones", fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 16.sp)
                Text("Seguimiento organizado de tus postulaciones", color = TextSecondary, fontSize = 11.sp)
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(PrimaryIndigo.copy(alpha = 0.2f))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text("${savedOrTracked.size} Activas", color = PrimaryIndigoLight, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }

        // Pestañas de estado
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            val tabs = listOf(
                ApplicationStatus.SAVED,
                ApplicationStatus.APPLIED,
                ApplicationStatus.INTERVIEWING
            )

            tabs.forEach { status ->
                val isSelected = selectedFilter == status
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isSelected) PrimaryIndigo else DarkSurfaceVariant)
                        .border(1.dp, if (isSelected) PrimaryIndigoLight else DarkBorder, RoundedCornerShape(10.dp))
                        .clickable { selectedFilter = status }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = status.label,
                        color = if (isSelected) TextPrimary else TextSecondary,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }

        val filteredByStatus = savedOrTracked.filter {
            if (selectedFilter == ApplicationStatus.SAVED) it.isSaved else it.status == selectedFilter
        }

        if (filteredByStatus.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 40.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("📋", fontSize = 32.sp)
                    Text("No hay vacantes en esta etapa", color = TextSecondary, fontSize = 13.sp)
                    Text("Guarda o postúlate a empleos desde el Feed principal.", color = TextMuted, fontSize = 11.sp)
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(filteredByStatus) { job ->
                    TrackerCard(
                        job = job,
                        onClick = { onOpenJobDetail(job) },
                        onAdvanceStatus = {
                            val nextStatus = when (job.status) {
                                ApplicationStatus.NONE, ApplicationStatus.SAVED -> ApplicationStatus.APPLIED
                                ApplicationStatus.APPLIED -> ApplicationStatus.INTERVIEWING
                                ApplicationStatus.INTERVIEWING -> ApplicationStatus.OFFER_RECEIVED
                                ApplicationStatus.OFFER_RECEIVED -> ApplicationStatus.NONE
                            }
                            repository.updateStatus(job.id, nextStatus)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun TrackerCard(
    job: JobOffer,
    onClick: () -> Unit,
    onAdvanceStatus: () -> Unit
) {
    Surface(
        color = DarkSurface,
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, DarkBorder),
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
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(job.companyIcon, fontSize = 20.sp)
                    Column {
                        Text(job.title, fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 13.sp)
                        Text("${job.company} • ${job.salaryDisplay}", color = TextSecondary, fontSize = 11.sp)
                    }
                }

                AiMatchBadge(job.aiFitScore)
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                GeoBadge(job.geoTier)

                TextButton(onClick = onAdvanceStatus) {
                    Text("Mover de Fase ➔", fontSize = 11.sp, color = PrimaryIndigoLight, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}
