package com.searchjobsai.presentation.screens

import android.content.Intent
import android.net.Uri
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
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
    val context = LocalContext.current

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.9f),
            shape = RoundedCornerShape(20.dp),
            color = DarkSurface,
            border = BorderStroke(1.dp, DarkBorder)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(18.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
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
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(job.company, color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                            Text("•", color = TextMuted)
                            Text(job.location, color = PrimaryIndigoLight, fontSize = 12.sp)
                        }
                        Text(
                            text = "Fuente: ${job.source}",
                            color = TextMuted,
                            fontSize = 10.sp
                        )
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

                Surface(
                    color = DarkSurfaceVariant,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Rango Salarial", color = TextMuted, fontSize = 10.sp)
                            Text(job.salaryDisplay, color = MatchGreen, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("Huso Horario", color = TextMuted, fontSize = 10.sp)
                            Text(job.timezone, color = TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }

                // Descripción
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Descripción de la Vacante", fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 13.sp)
                    Text(job.description, color = TextSecondary, fontSize = 12.sp, lineHeight = 17.sp)
                }

                // Diagnóstico con IA
                Surface(
                    color = DarkSurfaceVariant,
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, PrimaryIndigo.copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("🤖", fontSize = 16.sp)
                            Text(
                                "Diagnóstico de Afinidad IA para Jean Ramirez",
                                fontWeight = FontWeight.Bold,
                                color = PrimaryIndigoLight,
                                fontSize = 12.sp
                            )
                        }

                        Text(job.aiVerdict, color = TextPrimary, fontSize = 11.sp, lineHeight = 15.sp)

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Habilidades coincidentes
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(DarkSurface)
                                    .padding(8.dp)
                            ) {
                                Text(
                                    "Habilidades Cubiertas",
                                    color = MatchGreen,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                job.matchedSkills.forEach { skill ->
                                    Text("✓ $skill", color = TextSecondary, fontSize = 11.sp)
                                }
                            }

                            // Gaps
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(DarkSurface)
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
                                if (isPitchVisible) "Ocultar Pitch de IA" else "🪄 Ver Pitch Personalizado con IA",
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

                // Botón Acción 1: Postular en la Web Oficial
                if (job.applyUrl.isNotBlank()) {
                    Button(
                        onClick = {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(job.applyUrl)).apply {
                                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            }
                            runCatching { context.startActivity(intent) }
                            onUpdateStatus(job.id, ApplicationStatus.APPLIED)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryIndigo),
                        modifier = Modifier.fillMaxWidth().height(46.dp),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("🌐 Postular en la Web Oficial (${job.source})", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }

                // Botón Acción 2: Marcar en el Tracker
                OutlinedButton(
                    onClick = {
                        onUpdateStatus(job.id, ApplicationStatus.APPLIED)
                        onDismiss()
                    },
                    modifier = Modifier.fillMaxWidth().height(42.dp),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, MatchGreen)
                ) {
                    Text("🚀 Marcar como Postulado en el Tracker", color = MatchGreen, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }
    }
}
