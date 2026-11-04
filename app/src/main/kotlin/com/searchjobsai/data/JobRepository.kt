package com.searchjobsai.data

import com.searchjobsai.domain.model.ApplicationStatus
import com.searchjobsai.domain.model.DeveloperProfile
import com.searchjobsai.domain.model.GeoPriorityTier
import com.searchjobsai.domain.model.JobOffer
import com.searchjobsai.domain.engine.AiMatchEngine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class SortOption(val label: String) {
    PERU_FIRST("🇵🇪 Perú ➔ Internacional"),
    AI_MATCH("✨ Mayor AI Match"),
    SALARY_DESC("💵 Mayor Salario"),
    NEWEST("⏱️ Más Recientes")
}

class JobRepository(private val profile: DeveloperProfile = DeveloperProfile()) {

    private val initialJobs = listOf(
        JobOffer(
            id = 101L,
            title = "Senior Android Developer (Jetpack Compose & Clean Arch)",
            company = "Yape / BCP Digital",
            companyIcon = "🇵🇪",
            location = "100% Remoto (Perú / Lima)",
            is100Remote = true,
            geoTier = GeoPriorityTier.PERU_LOCAL,
            countryOrigin = "Perú",
            salaryDisplay = "S/ 18,000 - S/ 24,000 PEN / mes (~$65k-$80k USD)",
            salaryMinK = 65,
            timezone = "PET (UTC-5) Local",
            postedAgo = "Hace 1 hora",
            skills = listOf("Kotlin", "Jetpack Compose", "Coroutines", "Clean Arch", "Hilt", "FinTech"),
            description = "Lidera la evolución de la super-app de pagos más usada de Perú construyendo módulos en Compose puro y arquitectura MVI.",
            responsibilities = "Diseñar pantallas transaccionales de alta concurrencia en Jetpack Compose, modularización por features y optimización de métricas de inicio en frío.",
            aiFitScore = 99,
            aiVerdict = "¡Afinidad sobresaliente en tu país! Trabajo 100% remoto con contratación en planilla completa de Perú o modalidad mixta.",
            matchedSkills = listOf("Kotlin", "Jetpack Compose", "Coroutines & Flow", "Clean Architecture", "Hilt", "FinTech"),
            skillGaps = listOf("KMP (Evaluación para 2027)"),
            customPitch = ""
        ),
        JobOffer(
            id = 102L,
            title = "Lead Mobile Architect (KMP & Android Core)",
            company = "Mercado Libre (Perú Hub)",
            companyIcon = "🟡",
            location = "100% Remoto (Perú / Planilla)",
            is100Remote = true,
            geoTier = GeoPriorityTier.PERU_LOCAL,
            countryOrigin = "Perú",
            salaryDisplay = "$85,000 - $110,000 USD / yr",
            salaryMinK = 85,
            timezone = "PET (UTC-5) Local",
            postedAgo = "Hace 2 horas",
            skills = listOf("Kotlin", "Jetpack Compose", "KMP", "Coroutines", "Clean Arch", "FinTech"),
            description = "Lidera la arquitectura móvil del ecosistema de envíos y pagos con adopción de Kotlin Multiplatform.",
            responsibilities = "Definir estándares de arquitectura en KMP, liderar code reviews de módulos compartidos y coordinar con equipos de toda la región.",
            aiFitScore = 97,
            aiVerdict = "Oferta de primer nivel con entidad legal en Perú. Compensación indexada en USD con beneficios locales superiores.",
            matchedSkills = listOf("Kotlin", "Jetpack Compose", "KMP", "Clean Architecture", "Coroutines"),
            skillGaps = listOf("GraphQL básico"),
            customPitch = ""
        ),
        JobOffer(
            id = 103L,
            title = "Senior Android Engineer (Contractor Perú / LatAm)",
            company = "Automattic (WordPress)",
            companyIcon = "📱",
            location = "100% Remoto (Perú / Americas via Deel)",
            is100Remote = true,
            geoTier = GeoPriorityTier.AMERICAS_UTC5,
            countryOrigin = "US / Remoto Perú",
            salaryDisplay = "$115,000 - $135,000 USD / yr",
            salaryMinK = 115,
            timezone = "Async / PET (UTC-5) Compatible",
            postedAgo = "Hace 4 horas",
            skills = listOf("Kotlin", "Jetpack Compose", "Coroutines", "Hilt", "Room/SQLDelight"),
            description = "Construye nuevas experiencias visuales 100% en Jetpack Compose para aplicaciones móviles de código abierto.",
            responsibilities = "Desarrollo de nuevas features en Compose puro, refactorización de código heredado, pruebas unitarias automatizadas con Mockk y contribución a proyectos open-source.",
            aiFitScore = 95,
            aiVerdict = "Empresa con cultura 100% asíncrona ideal para trabajar desde Perú. Pago directo en USD mediante Deel.",
            matchedSkills = listOf("Kotlin", "Jetpack Compose", "Coroutines & Flow", "Hilt"),
            skillGaps = listOf("SQLDelight (Familiaridad básica recomendada)"),
            customPitch = ""
        ),
        JobOffer(
            id = 104L,
            title = "Android Engineer - FinTech & Crypto Payments",
            company = "Kraken Digital",
            companyIcon = "🐙",
            location = "Remoto (Americas: LatAm & US)",
            is100Remote = true,
            geoTier = GeoPriorityTier.AMERICAS_UTC5,
            countryOrigin = "Americas",
            salaryDisplay = "$100,000 - $130,000 USD / yr",
            salaryMinK = 100,
            timezone = "EST / PST Friendly",
            postedAgo = "Hace 1 día",
            skills = listOf("Kotlin", "Clean Arch", "Coroutines", "Room/SQLDelight", "FinTech"),
            description = "Desarrolla flujos financieros de alta seguridad y baja latencia en Android con Kotlin y SQLDelight.",
            responsibilities = "Manejo de almacenamiento encriptado, integración con APIs WebSockets y diseño de flujos reactivos resistentes a pérdida de conexión.",
            aiFitScore = 91,
            aiVerdict = "Gran oportunidad de compensación en USD para LatAm. Encajas sólidamente por tu experiencia en izipay.",
            matchedSkills = listOf("Kotlin", "Coroutines & Flow", "Clean Arch", "FinTech"),
            skillGaps = listOf("Ktor WebSockets"),
            customPitch = ""
        ),
        JobOffer(
            id = 105L,
            title = "Staff Android Architect (KMP & Jetpack Compose)",
            company = "Stripe Remote Global",
            companyIcon = "💳",
            location = "100% Remoto (Worldwide)",
            is100Remote = true,
            geoTier = GeoPriorityTier.WORLDWIDE_ASYNC,
            countryOrigin = "Global",
            salaryDisplay = "$145,000 - $170,000 USD / yr",
            salaryMinK = 145,
            timezone = "EST / UTC Flexible",
            postedAgo = "Hace 5 horas",
            skills = listOf("Kotlin", "Jetpack Compose", "KMP", "Coroutines", "Clean Arch"),
            description = "Lidera la migración de módulos core a Kotlin Multiplatform y arquitectura MVI basada en Compose.",
            responsibilities = "Diseñar componentes modulares compartidos entre Android y Desktop JVM, liderar adopción de KMP y optimizar CI/CD.",
            aiFitScore = 94,
            aiVerdict = "Afinidad casi perfecta. Tu dominio de Jetpack Compose y Clean Architecture cumple al 100% sus requerimientos principales.",
            matchedSkills = listOf("Kotlin", "Jetpack Compose", "Coroutines & Flow", "Clean Architecture", "KMP"),
            skillGaps = listOf("Nativo C++ (NDK) - Deseable"),
            customPitch = ""
        ),
        JobOffer(
            id = 106L,
            title = "Mobile Engineer (Android & Multiplatform)",
            company = "Shopify Global",
            companyIcon = "🛍️",
            location = "100% Remoto (Worldwide)",
            is100Remote = true,
            geoTier = GeoPriorityTier.WORLDWIDE_ASYNC,
            countryOrigin = "Global",
            salaryDisplay = "$130,000 - $155,000 USD / yr",
            salaryMinK = 130,
            timezone = "Flexible Global",
            postedAgo = "Hace 2 días",
            skills = listOf("Kotlin", "Jetpack Compose", "KMP", "Clean Arch"),
            description = "Construye herramientas de comercio móvil para millones de comerciantes utilizando KMP y Compose.",
            responsibilities = "Crear componentes de diseño reutilizables en Compose y coordinar lógica compartida en KMP.",
            aiFitScore = 92,
            aiVerdict = "Empresa con stack de vanguardia (Compose + KMP). Tu perfil destaca entre los candidatos.",
            matchedSkills = listOf("Kotlin", "Jetpack Compose", "KMP", "Clean Architecture"),
            skillGaps = listOf("GraphQL Apollo"),
            customPitch = ""
        )
    ).map { job ->
        val pitch = AiMatchEngine.generateTailoredPitch(profile, job)
        job.copy(customPitch = pitch)
    }

    private val _jobs = MutableStateFlow(initialJobs)
    val jobs: StateFlow<List<JobOffer>> = _jobs.asStateFlow()

    fun filterAndSort(
        query: String,
        selectedSkills: Set<String>,
        remoteOnly: Boolean,
        minSalaryK: Int,
        sortOption: SortOption
    ): List<JobOffer> {
        val current = _jobs.value
        val filtered = current.filter { job ->
            if (remoteOnly && !job.is100Remote) return@filter false
            if (job.salaryMinK < minSalaryK) return@filter false

            if (selectedSkills.isNotEmpty()) {
                val matchesAnySkill = job.skills.any { s -> selectedSkills.contains(s) }
                if (!matchesAnySkill) return@filter false
            }

            if (query.isNotBlank()) {
                val q = query.trim().lowercase()
                val matchTitle = job.title.lowercase().contains(q)
                val matchCompany = job.company.lowercase().contains(q)
                val matchSkill = job.skills.any { it.lowercase().contains(q) }
                if (!matchTitle && !matchCompany && !matchSkill) return@filter false
            }

            true
        }

        return when (sortOption) {
            SortOption.PERU_FIRST -> filtered.sortedWith(
                compareBy<JobOffer> { it.geoTier.weight }
                    .thenByDescending { it.aiFitScore }
            )
            SortOption.AI_MATCH -> filtered.sortedByDescending { it.aiFitScore }
            SortOption.SALARY_DESC -> filtered.sortedByDescending { it.salaryMinK }
            SortOption.NEWEST -> filtered.sortedBy { it.id }
        }
    }

    fun toggleSave(jobId: Long) {
        _jobs.value = _jobs.value.map { job ->
            if (job.id == jobId) {
                val newSaved = !job.isSaved
                val newStatus = if (newSaved && job.status == ApplicationStatus.NONE) {
                    ApplicationStatus.SAVED
                } else if (!newSaved && job.status == ApplicationStatus.SAVED) {
                    ApplicationStatus.NONE
                } else job.status
                job.copy(isSaved = newSaved, status = newStatus)
            } else job
        }
    }

    fun updateStatus(jobId: Long, newStatus: ApplicationStatus) {
        _jobs.value = _jobs.value.map { job ->
            if (job.id == jobId) {
                job.copy(status = newStatus, isSaved = newStatus != ApplicationStatus.NONE)
            } else job
        }
    }
}
