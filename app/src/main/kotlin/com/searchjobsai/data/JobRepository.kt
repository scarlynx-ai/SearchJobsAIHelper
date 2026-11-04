package com.searchjobsai.data

import com.searchjobsai.data.network.LiveJobSearchService
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

enum class SyncState {
    IDLE,
    SYNCING,
    SUCCESS,
    ERROR
}

class JobRepository(private val profile: DeveloperProfile = DeveloperProfile()) {

    private val liveService = LiveJobSearchService(profile)

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
            postedAgo = "Destacado Perú",
            skills = listOf("Kotlin", "Jetpack Compose", "Coroutines", "Clean Arch", "Hilt", "FinTech"),
            description = "Lidera la evolución de la super-app de pagos más usada de Perú construyendo módulos en Compose puro y arquitectura MVI.",
            responsibilities = "Diseñar pantallas transaccionales de alta concurrencia en Jetpack Compose, modularización por features y optimización de métricas de inicio en frío.",
            aiFitScore = 99,
            aiVerdict = "¡Afinidad sobresaliente en tu país! Trabajo 100% remoto con contratación en planilla completa de Perú o modalidad mixta.",
            matchedSkills = listOf("Kotlin", "Jetpack Compose", "Coroutines & Flow", "Clean Architecture", "Hilt", "FinTech"),
            skillGaps = listOf("KMP (Evaluación para 2027)"),
            customPitch = "",
            applyUrl = "https://www.bcp.com.pe/trabaja-con-nosotros",
            source = "BCP Careers Perú"
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
            postedAgo = "Destacado Perú",
            skills = listOf("Kotlin", "Jetpack Compose", "KMP", "Coroutines", "Clean Arch", "FinTech"),
            description = "Lidera la arquitectura móvil del ecosistema de envíos y pagos con adopción de Kotlin Multiplatform.",
            responsibilities = "Definir estándares de arquitectura en KMP, liderar code reviews de módulos compartidos y coordinar con equipos de toda la región.",
            aiFitScore = 97,
            aiVerdict = "Oferta de primer nivel con entidad legal en Perú. Compensación indexada en USD con beneficios locales superiores.",
            matchedSkills = listOf("Kotlin", "Jetpack Compose", "KMP", "Clean Architecture", "Coroutines"),
            skillGaps = listOf("GraphQL básico"),
            customPitch = "",
            applyUrl = "https://mercadolibre.com/empleos",
            source = "Mercado Libre Careers"
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
            postedAgo = "Remoto Americas",
            skills = listOf("Kotlin", "Jetpack Compose", "Coroutines", "Hilt", "Room"),
            description = "Construye nuevas experiencias visuales 100% en Jetpack Compose para aplicaciones móviles de código abierto.",
            responsibilities = "Desarrollo de nuevas features en Compose puro, refactorización de código heredado, pruebas unitarias automatizadas con Mockk y contribución a proyectos open-source.",
            aiFitScore = 95,
            aiVerdict = "Empresa con cultura 100% asíncrona ideal para trabajar desde Perú. Pago directo en USD mediante Deel.",
            matchedSkills = listOf("Kotlin", "Jetpack Compose", "Coroutines & Flow", "Hilt"),
            skillGaps = listOf("SQLDelight"),
            customPitch = "",
            applyUrl = "https://automattic.com/work-with-us/",
            source = "Automattic Careers"
        ),
        JobOffer(
            id = 104L,
            title = "Staff Mobile Engineer (Fintech & Core Payments)",
            company = "Kushki Pagos LATAM",
            companyIcon = "💳",
            location = "100% Remoto (LatAm / Perú)",
            is100Remote = true,
            geoTier = GeoPriorityTier.AMERICAS_UTC5,
            countryOrigin = "LatAm",
            salaryDisplay = "$80,000 - $105,000 USD / yr",
            salaryMinK = 80,
            timezone = "PET / COT (UTC-5)",
            postedAgo = "Fintech LatAm",
            skills = listOf("Kotlin", "Jetpack Compose", "MVI", "Clean Arch", "FinTech"),
            description = "Escala el SDK de pagos móviles y la infraestructura de cobros digitales en toda América Latina.",
            responsibilities = "Optimizar la tasa de éxito de pagos móviles, mantener SDKs livianos y aplicar Clean Architecture estricto.",
            aiFitScore = 96,
            aiVerdict = "Match altísimo por tu background en izipay y billeteras digitales. Entorno de trabajo 100% alineado con tu huso horario.",
            matchedSkills = listOf("Kotlin", "FinTech", "Clean Architecture", "Jetpack Compose"),
            skillGaps = listOf("Seguridad PCI-DSS Nivel 1"),
            customPitch = "",
            applyUrl = "https://kushkipagos.com/careers",
            source = "Kushki Careers"
        ),
        JobOffer(
            id = 105L,
            title = "Principal Android Engineer (KMP & Mobile Core)",
            company = "DuckDuckGo",
            companyIcon = "🦆",
            location = "100% Remoto (Global B2B)",
            is100Remote = true,
            geoTier = GeoPriorityTier.WORLDWIDE_ASYNC,
            countryOrigin = "Global",
            salaryDisplay = "$145,000 - $170,000 USD / yr",
            salaryMinK = 145,
            timezone = "EST / UTC Flexible",
            postedAgo = "Global Remote",
            skills = listOf("Kotlin", "Jetpack Compose", "KMP", "Coroutines", "Clean Arch"),
            description = "Lidera la migración de módulos core a Kotlin Multiplatform y arquitectura MVI basada en Compose.",
            responsibilities = "Diseñar componentes modulares compartidos entre Android y Desktop JVM, liderar adopción de KMP y optimizar CI/CD.",
            aiFitScore = 94,
            aiVerdict = "Afinidad casi perfecta. Tu dominio de Jetpack Compose y Clean Architecture cumple al 100% sus requerimientos principales.",
            matchedSkills = listOf("Kotlin", "Jetpack Compose", "Coroutines & Flow", "Clean Architecture", "KMP"),
            skillGaps = listOf("Nativo C++ (NDK)"),
            customPitch = "",
            applyUrl = "https://duckduckgo.com/hiring",
            source = "DuckDuckGo Global"
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
            postedAgo = "Global Remote",
            skills = listOf("Kotlin", "Jetpack Compose", "KMP", "Clean Arch"),
            description = "Construye herramientas de comercio móvil para millones de comerciantes utilizando KMP y Compose.",
            responsibilities = "Crear componentes de diseño reutilizables en Compose y coordinar lógica compartida en KMP.",
            aiFitScore = 92,
            aiVerdict = "Empresa con stack de vanguardia (Compose + KMP). Tu perfil destaca entre los candidatos.",
            matchedSkills = listOf("Kotlin", "Jetpack Compose", "KMP", "Clean Architecture"),
            skillGaps = listOf("GraphQL Apollo"),
            customPitch = "",
            applyUrl = "https://www.shopify.com/careers",
            source = "Shopify Careers"
        )
    ).map { job ->
        val pitch = AiMatchEngine.generateTailoredPitch(profile, job)
        job.copy(customPitch = pitch)
    }

    private val _jobs = MutableStateFlow(initialJobs)
    val jobs: StateFlow<List<JobOffer>> = _jobs.asStateFlow()

    private val _syncState = MutableStateFlow(SyncState.IDLE)
    val syncState: StateFlow<SyncState> = _syncState.asStateFlow()

    private val _syncInfo = MutableStateFlow("Listo para buscar ofertas en la web")
    val syncInfo: StateFlow<String> = _syncInfo.asStateFlow()

    suspend fun refreshFromWeb(query: String = "") {
        _syncState.value = SyncState.SYNCING
        _syncInfo.value = "Consultando ofertas reales en GetOnBrd, Remotive y Jobicy..."
        try {
            val liveJobs = liveService.searchWebJobs(query)
            if (liveJobs.isNotEmpty()) {
                val combined = (liveJobs + initialJobs).distinctBy {
                    "${it.company.lowercase().trim()}_${it.title.lowercase().trim()}"
                }
                _jobs.value = combined
                _syncState.value = SyncState.SUCCESS
                _syncInfo.value = "Sincronizado: ${liveJobs.size} ofertas reales vivas desde la web"
            } else {
                _syncState.value = SyncState.SUCCESS
                _syncInfo.value = "Búsqueda web completada. Mostrando vacantes activas."
            }
        } catch (e: Exception) {
            _syncState.value = SyncState.ERROR
            _syncInfo.value = "Fallo de red al consultar APIs web. Mostrando vacantes en caché."
        }
    }

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
                val matchLoc = job.location.lowercase().contains(q)
                if (!matchTitle && !matchCompany && !matchSkill && !matchLoc) return@filter false
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
