package com.searchjobsai.presentation.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.searchjobsai.domain.model.DeveloperProfile
import com.searchjobsai.presentation.theme.*

@Composable
fun ProfileScreen(profile: DeveloperProfile) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Cabecera de Perfil de Jean Ramirez
        Surface(
            color = DarkSurface,
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, DarkBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(PrimaryIndigo)
                            .border(2.dp, PeruRed, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("JR", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    }

                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(profile.name, fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 16.sp)
                            Text("🇵🇪", fontSize = 14.sp)
                        }
                        Text(
                            profile.title,
                            color = PrimaryIndigoLight,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            profile.education,
                            color = TextMuted,
                            fontSize = 11.sp
                        )
                    }
                }

                HorizontalDivider(color = DarkBorder)

                Text(
                    text = profile.summary,
                    color = TextSecondary,
                    fontSize = 12.sp,
                    lineHeight = 16.sp
                )
            }
        }

        // Historial Laboral Comprobado
        Surface(
            color = DarkSurface,
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, DarkBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text("Trayectoria Laboral Clave", fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 13.sp)

                ExperienceItem(
                    role = "Analista de programación",
                    company = "izipay (FinTech & Pasarelas de Pago)",
                    period = "Mayo 2026 - Presente",
                    highlight = "Desarrollo de módulos móviles transaccionales y pasarelas de pago de alta seguridad."
                )

                ExperienceItem(
                    role = "Android Developer",
                    company = "10Pearls",
                    period = "Marzo 2024 - Marzo 2026 (2 años 1 mes)",
                    highlight = "Migración de XML a Jetpack Compose, monederos digitales, homologación de diseño Figma y Clean Arch."
                )

                ExperienceItem(
                    role = "Desarrollador Móvil",
                    company = "Labotec Peru S.A.C",
                    period = "Septiembre 2022 - Marzo 2024",
                    highlight = "Arquitectura de Corrutinas (Kotlin), optimización offline-first y sensores IoT / MQTT."
                )
            }
        }

        // Matriz de Habilidades Técnicas
        Surface(
            color = DarkSurface,
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, DarkBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text("Skills Verificadas de Jean Ramirez", fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 13.sp)

                val skillsList = listOf(
                    "Kotlin (Avanzado)", "Jetpack Compose ⭐", "Coroutines & Flow",
                    "Clean Architecture", "SOLID", "Hilt / Dependency Injection",
                    "FinTech / Pagos Digitales", "KMP (Multiplatform)", "Room & SQLDelight",
                    "Retrofit & REST APIs", "Figma Design Homologation", "Scrum / Jira"
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        skillsList.chunked(3).forEach { rowSkills ->
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                rowSkills.forEach { skill ->
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(DarkSurfaceVariant)
                                            .border(1.dp, PrimaryIndigo.copy(alpha = 0.3f), RoundedCornerShape(6.dp))
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text(skill, color = TextPrimary, fontSize = 10.sp, fontWeight = FontWeight.Medium)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Preferencias de Contratación
        Surface(
            color = DarkSurface,
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, DarkBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("Parámetros de Búsqueda y Salario", fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 13.sp)
                Text("🎯 Modalidad: ${profile.preferredWorkType}", color = TextSecondary, fontSize = 12.sp)
                Text("💵 Rango Salarial: ${profile.targetSalaryDisplay}", color = MatchGreen, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                Text("⏱️ Zona Horaria: ${profile.location}", color = TextSecondary, fontSize = 12.sp)
            }
        }
    }
}

@Composable
fun ExperienceItem(role: String, company: String, period: String, highlight: String) {
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(role, fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 12.sp)
            Text(period, color = TextMuted, fontSize = 10.sp)
        }
        Text(company, color = PrimaryIndigoLight, fontSize = 11.sp, fontWeight = FontWeight.Medium)
        Text(highlight, color = TextSecondary, fontSize = 11.sp, lineHeight = 14.sp)
        Spacer(modifier = Modifier.height(4.dp))
    }
}
