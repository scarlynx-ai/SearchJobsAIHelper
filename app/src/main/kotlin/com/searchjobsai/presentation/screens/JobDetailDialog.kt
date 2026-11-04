package com.searchjobsai.presentation.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.window.Dialog
import com.searchjobsai.domain.model.ApplicationStatus
import com.searchjobsai.domain.model.JobOffer
import com.searchjobsai.presentation.components.AiMatchBadge
import com.searchjobsai.presentation.components.GeoBadge
import com.searchjobsai.presentation.theme.*

@Composable
fun JobDetailDialog(
    job: JobOffer,
    onDismiss: () -> Unit,
    onToggleSave: (Long) -> Unit,
    onUpdateStatus: (Long, ApplicationStatus) -> Unit
) {
    var isPitchVisible by remember { mutableStateOf(false) }
    var copiedNotice by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .fillMaxHeight(0.85f),
            shape = RoundedCornerShape(20.dp),
            color = DarkSurface,
            border = BorderStroke(1.dp, DarkBorder)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Barra superior de cierre y guardado
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("← Volver al feed", color = TextSecondary, fontSize = 12.sp)
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = { onToggleSave(job.id) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (job.isSaved) MatchGreenBg else DarkSurfaceVariant,
                                contentColor = if (job.isSaved) MatchGreen else TextSecondary
                            )
                        ) {
                            Text(if (job.isSaved) "⭐ Guardado" else "☆ Guardar", fontSize = 12.sp)
                        }
                    }
                }

                // Encabezado del Empleo
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(DarkSurfaceVariant)
                            .border(1.dp, DarkBorder, RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(job.companyIcon, fontSize = 24.sp)
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = job.title,
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(job.company, color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                            Text("•", color = TextMuted)
                            Text(job.location, color = PrimaryIndigoLight, fontSize = 12.sp)
                        }
                    }
                }

                // Badges y Salario
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    GeoBadge(job.geoTier)
                    AiMatchBadge(job.aiFitScore)
                }

                Text(
                    text = "💵 ${job.salaryDisplay}",
                    color = MatchGreen,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )

                HorizontalDivider(color = DarkBorder)

                // Diagnóstico de IA
                Surface(
                    color = DarkBackground,
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, PrimaryIndigo.copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text("✨", fontSize = 14.sp)
                            Text(
                                "AI Job Fit Analysis",
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary,
                                fontSize = 13.sp
                            )
                        }

                        Text(
                            text = job.aiVerdict,
                            color = TextSecondary,
                            fontSize = 12.sp,
                            lineHeight = 16.sp
                        )

                        // Skills Cumplidas vs Gaps
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Cumplidas
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .background(DarkSurfaceVariant, RoundedCornerShape(8.dp))
                                    .padding(8.dp)
                            ) {
                                Text(
                                    "Tus Skills (✓)",
                                    color = MatchGreen,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                job.matchedSkills.forEach { skill ->
                                    Text("✓ $skill", color = TextPrimary, fontSize = 11.sp)
                                }
                            }

                            // Gaps
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .background(DarkSurfaceVariant, RoundedCornerShape(8.dp))
                                    .padding(8.dp)
                            ) {
                                Text(
                                    "Brechas (Gap)",
                                    color = MatchAmber,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                job.skillGaps.forEach { gap ->
                                    Text("⚠️ $gap", color = TextSecondary, fontSize = 11.sp)
                                }
                            }
                        }

                        // Generador de Pitch con IA
                        Button(
                            onClick = { isPitchVisible = !isPitchVisible },
                            colors = ButtonDefaults.buttonColors(containerColor = AccentPurple),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(
                                if (isPitchVisible) "Ocultar Pitch de IA" else "🪄 Generar Pitch de Postulación con IA",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 12.sp
                            )
                        }

                        if (isPitchVisible) {
                            Surface(
                                color = DarkSurface,
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.dp, AccentPurple.copy(alpha = 0.5f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier.padding(10.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            "📝 Pitch Personalizado para Reclutador",
                                            color = AccentPurple,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp
                                        )

                                        val clipboardManager = LocalClipboardManager.current
                                        TextButton(onClick = {
                                            clipboardManager.setText(AnnotatedString(job.customPitch))
                                            copiedNotice = true
                                        }) {
                                            Text(if (copiedNotice) "¡Copiado! ✓" else "Copiar 📋", fontSize = 11.sp)
                                        }
                                    }

                                    Text(
                                        text = job.customPitch,
                                        color = TextSecondary,
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 11.sp,
                                        lineHeight = 15.sp
                                    )
                                }
                            }
                        }
                    }
                }

                // Responsabilidades
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Responsabilidades Clave", fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 13.sp)
                    Text(job.responsibilities, color = TextSecondary, fontSize = 12.sp, lineHeight = 16.sp)
                }

                // Botón de Postulación
                Button(
                    onClick = {
                        onUpdateStatus(job.id, ApplicationStatus.APPLIED)
                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MatchGreen),
                    modifier = Modifier.fillMaxWidth().height(44.dp),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("🚀 Marcar como Postulado en el Tracker", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }
        }
    }
}
