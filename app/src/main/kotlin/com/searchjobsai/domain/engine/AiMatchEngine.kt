package com.searchjobsai.domain.engine

import com.searchjobsai.domain.model.DeveloperProfile
import com.searchjobsai.domain.model.GeoPriorityTier
import com.searchjobsai.domain.model.JobOffer

object AiMatchEngine {

    fun calculateMatch(profile: DeveloperProfile, job: JobOffer): Pair<Int, String> {
        val candidateSkills = profile.primarySkills.map { it.lowercase() }
        val jobSkills = job.skills.map { it.lowercase() }

        var matchedCount = 0
        jobSkills.forEach { skill ->
            if (candidateSkills.any { it.contains(skill) || skill.contains(it) }) {
                matchedCount++
            }
        }

        val baseRatio = if (jobSkills.isNotEmpty()) matchedCount.toDouble() / jobSkills.size else 0.8
        var score = (baseRatio * 75).toInt()

        // Geo-bonus para postulantes en Perú (huso horario coincidente y sin fricción de visado)
        when (job.geoTier) {
            GeoPriorityTier.PERU_LOCAL -> score += 18
            GeoPriorityTier.AMERICAS_UTC5 -> score += 12
            GeoPriorityTier.WORLDWIDE_ASYNC -> score += 6
        }

        // Bonus por especialización en FinTech / Pasarelas de Pago si la oferta lo requiere
        if (job.title.contains("FinTech", ignoreCase = true) || 
            job.description.contains("pago", ignoreCase = true) ||
            job.description.contains("monedero", ignoreCase = true)) {
            score += 6
        }

        val finalScore = score.coerceIn(55, 99)

        val verdict = when {
            finalScore >= 95 -> "¡Match Excepcional! Tu perfil con Jetpack Compose, Clean Architecture y experiencia en izipay/10Pearls encaja de forma directa con los requerimientos técnicos y horario de este rol."
            finalScore >= 88 -> "Alta afinidad técnica. Cubres el núcleo de Kotlin, Coroutines y arquitectura. Es una excelente oportunidad remota con compensación competitiva."
            else -> "Afinidad moderada. Cumples las bases principales de Android, aunque la vacante tiene tecnologías complementarias secundarias."
        }

        return Pair(finalScore, verdict)
    }

    fun generateTailoredPitch(profile: DeveloperProfile, job: JobOffer): String {
        val isFintech = job.title.contains("FinTech", ignoreCase = true) || job.description.contains("pago", ignoreCase = true)
        val isKmp = job.skills.any { it.contains("KMP", ignoreCase = true) }

        val focusHighlight = when {
            isFintech -> "Mi experiencia actual en izipay construyendo soluciones transaccionales y en 10Pearls desarrollando monederos digitales me permite aportar inmediatamente en seguridad, escalabilidad y buenas prácticas financieras."
            isKmp -> "Con más de 4 años en el ecosistema Android especializado en Jetpack Compose puro y Clean Architecture, cuento con la base idónea para acelerar la adopción de módulos compartidos en Kotlin Multiplatform (KMP)."
            else -> "Mi trayectoria migrando proyectos XML legados hacia Jetpack Compose puro con MVI y Coroutines/Flow en 10Pearls e izipay garantiza código limpio, mantenible y orientado a rendimiento."
        }

        return """
            Hola equipo de hiring de ${job.company},
            
            Les escribe ${profile.name} (${profile.handle}), ${profile.title}, basado en ${profile.location}.
            
            Revisé con mucho entusiasmo su vacante de "${job.title}". $focusHighlight
            
            Dominio demostrado en su stack:
            • Lenguaje & UI: Kotlin avanzado, Jetpack Compose y homologación de diseños UX/UI desde Figma.
            • Arquitectura: Clean Architecture estricto, principios SOLID y modularización.
            • Asincronismo & Estado: Kotlin Coroutines, StateFlow y Dagger Hilt.
            • Modalidad: 100% Remoto con disponibilidad inmediata en huso horario compatible (${job.timezone}).
            
            Quedo atento para coordinar una conversación técnica y profundizar en cómo puedo aportar valor a ${job.company}.
            
            Saludos cordiales,
            ${profile.name}
            LinkedIn: https://www.linkedin.com/in/${profile.handle}/
        """.trimIndent()
    }
}
