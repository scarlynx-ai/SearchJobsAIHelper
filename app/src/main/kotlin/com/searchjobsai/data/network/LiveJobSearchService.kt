package com.searchjobsai.data.network

import com.searchjobsai.domain.engine.AiMatchEngine
import com.searchjobsai.domain.model.DeveloperProfile
import com.searchjobsai.domain.model.GeoPriorityTier
import com.searchjobsai.domain.model.JobOffer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.regex.Pattern

class LiveJobSearchService(private val profile: DeveloperProfile) {

    private val userAgent = "Mozilla/5.0 (Linux; Android 14; Mobile) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Mobile Safari/537.36"

    suspend fun searchWebJobs(query: String = ""): List<JobOffer> = withContext(Dispatchers.IO) {
        val collected = mutableListOf<JobOffer>()

        // 1. GetOnBrd - Real LATAM & Remote Mobile Jobs
        runCatching {
            fetchGetOnBrdJobs()
        }.onSuccess { collected.addAll(it) }

        // 2. Remotive - Real Remote Tech Jobs
        runCatching {
            fetchRemotiveJobs()
        }.onSuccess { collected.addAll(it) }

        // 3. Jobicy - Real Remote Jobs
        runCatching {
            fetchJobicyJobs()
        }.onSuccess { collected.addAll(it) }

        // Distinct by title and company to avoid duplicate postings across boards
        val distinctJobs = collected.distinctBy {
            "${it.company.lowercase().trim()}_${it.title.lowercase().trim()}"
        }

        // Filter by search query if specified
        val filtered = if (query.isNotBlank()) {
            val q = query.trim().lowercase()
            distinctJobs.filter { job ->
                job.title.lowercase().contains(q) ||
                job.company.lowercase().contains(q) ||
                job.description.lowercase().contains(q) ||
                job.skills.any { it.lowercase().contains(q) }
            }
        } else {
            distinctJobs
        }

        // Sort by Perú First priority (Tier 1 > Tier 2 > Tier 3) and then highest AI Match Score
        filtered.sortedWith(
            compareBy<JobOffer> { it.geoTier.weight }
                .thenByDescending { it.aiFitScore }
        )
    }

    private fun fetchGetOnBrdJobs(): List<JobOffer> {
        val endpoint = "https://www.getonbrd.com/api/v0/categories/mobile-developer/jobs?per_page=25"
        val jsonStr = makeGetRequest(endpoint) ?: return emptyList()
        val root = JSONObject(jsonStr)
        val data = root.optJSONArray("data") ?: return emptyList()

        val results = mutableListOf<JobOffer>()
        for (i in 0 until data.length()) {
            val item = data.getJSONObject(i)
            val id = item.optString("id")
            val attrs = item.optJSONObject("attributes") ?: continue

            val title = attrs.optString("title", "Mobile Engineer")
            val descriptionRaw = attrs.optString("description", "")
            val description = stripHtml(descriptionRaw)
            val isRemote = attrs.optBoolean("remote", true)
            val seniority = attrs.optString("seniority", "Senior")
            val countriesArr = attrs.optJSONArray("countries")
            val countriesStr = if (countriesArr != null) {
                (0 until countriesArr.length()).map { countriesArr.getString(it) }.joinToString(", ")
            } else if (isRemote) "Remoto LATAM" else "Presencial"

            // Extract company from slug: e.g. "front-end-android-sr-grupo-mariposa-remote-bfe3"
            val company = parseCompanyFromSlug(id)

            val minSalary = attrs.optInt("min_salary", 0)
            val maxSalary = attrs.optInt("max_salary", 0)
            val salaryDisplay = if (minSalary > 0 && maxSalary > 0) {
                "$$minSalary - $$maxSalary USD / mes"
            } else if (seniority.contains("Senior", ignoreCase = true)) {
                "$3,500 - $5,000 USD / mes (Estimado)"
            } else {
                "Compensación competitiva según perfil"
            }

            val salaryMinK = if (minSalary > 0) minSalary * 12 / 1000 else 45

            val skills = extractSkills(title + " " + description)
            val geoTier = detectGeoTier(countriesStr, description)
            val applyUrl = "https://www.getonbrd.com/jobs/$id"

            val jobBase = JobOffer(
                id = 20000L + i,
                title = title,
                company = company,
                companyIcon = if (geoTier == GeoPriorityTier.PERU_LOCAL) "🇵🇪" else "🌎",
                location = if (isRemote) "100% Remoto ($countriesStr)" else countriesStr,
                is100Remote = isRemote,
                geoTier = geoTier,
                countryOrigin = if (geoTier == GeoPriorityTier.PERU_LOCAL) "Perú" else "LATAM",
                salaryDisplay = salaryDisplay,
                salaryMinK = salaryMinK,
                timezone = if (geoTier == GeoPriorityTier.PERU_LOCAL) "PET (UTC-5) Local" else "LATAM / Flexible",
                postedAgo = "En vivo (GetOnBrd)",
                skills = skills,
                description = description.take(280) + "...",
                responsibilities = "Desarrollo de módulos móviles nativos con Kotlin, integración de arquitecturas limpias y trabajo en equipo multidisciplinario.",
                aiFitScore = 0,
                aiVerdict = "",
                matchedSkills = emptyList(),
                skillGaps = emptyList(),
                customPitch = "",
                applyUrl = applyUrl,
                source = "GetOnBrd Web"
            )

            val (score, verdict) = AiMatchEngine.calculateMatch(profile, jobBase)
            val matched = profile.primarySkills.filter { ps ->
                skills.any { s -> s.contains(ps, ignoreCase = true) || ps.contains(s, ignoreCase = true) }
            }
            val gaps = skills.filterNot { s ->
                profile.primarySkills.any { ps -> ps.contains(s, ignoreCase = true) || s.contains(ps, ignoreCase = true) }
            }
            val pitch = AiMatchEngine.generateTailoredPitch(profile, jobBase)

            results.add(
                jobBase.copy(
                    aiFitScore = score,
                    aiVerdict = verdict,
                    matchedSkills = matched.ifEmpty { listOf("Kotlin", "Jetpack Compose", "Clean Arch") },
                    skillGaps = gaps.take(3),
                    customPitch = pitch
                )
            )
        }
        return results
    }

    private fun fetchRemotiveJobs(): List<JobOffer> {
        val endpoint = "https://remotive.com/api/remote-jobs?search=android&limit=25"
        val jsonStr = makeGetRequest(endpoint) ?: return emptyList()
        val root = JSONObject(jsonStr)
        val jobs = root.optJSONArray("jobs") ?: return emptyList()

        val results = mutableListOf<JobOffer>()
        for (i in 0 until jobs.length()) {
            val item = jobs.getJSONObject(i)
            val title = item.optString("title", "Android Developer")
            val company = item.optString("company_name", "Empresa Tech")
            val location = item.optString("candidate_required_location", "Worldwide")
            val url = item.optString("url", "")
            val salary = item.optString("salary", "")
            val descRaw = item.optString("description", "")
            val description = stripHtml(descRaw)

            val tagsArr = item.optJSONArray("tags")
            val tags = if (tagsArr != null) {
                (0 until tagsArr.length()).map { tagsArr.getString(it) }
            } else emptyList()

            val skills = extractSkills(title + " " + description + " " + tags.joinToString(" "))
            val geoTier = detectGeoTier(location, description)

            val salaryDisplay = if (salary.isNotBlank()) salary else "$75,000 - $110,000 USD / año (Estimado)"
            val salaryMinK = 75

            val jobBase = JobOffer(
                id = 30000L + i,
                title = title,
                company = company,
                companyIcon = if (geoTier == GeoPriorityTier.PERU_LOCAL) "🇵🇪" else if (geoTier == GeoPriorityTier.AMERICAS_UTC5) "🌎" else "🌐",
                location = "100% Remoto ($location)",
                is100Remote = true,
                geoTier = geoTier,
                countryOrigin = location,
                salaryDisplay = salaryDisplay,
                salaryMinK = salaryMinK,
                timezone = if (geoTier == GeoPriorityTier.PERU_LOCAL) "PET (UTC-5)" else "Remoto Asíncrono",
                postedAgo = "En vivo (Remotive)",
                skills = skills,
                description = description.take(280) + "...",
                responsibilities = "Diseñar, construir y mantener features robustas en la aplicación Android con arquitectura moderna y pruebas automatizadas.",
                aiFitScore = 0,
                aiVerdict = "",
                matchedSkills = emptyList(),
                skillGaps = emptyList(),
                customPitch = "",
                applyUrl = url,
                source = "Remotive Web"
            )

            val (score, verdict) = AiMatchEngine.calculateMatch(profile, jobBase)
            val matched = profile.primarySkills.filter { ps ->
                skills.any { s -> s.contains(ps, ignoreCase = true) || ps.contains(s, ignoreCase = true) }
            }
            val gaps = skills.filterNot { s ->
                profile.primarySkills.any { ps -> ps.contains(s, ignoreCase = true) || s.contains(ps, ignoreCase = true) }
            }
            val pitch = AiMatchEngine.generateTailoredPitch(profile, jobBase)

            results.add(
                jobBase.copy(
                    aiFitScore = score,
                    aiVerdict = verdict,
                    matchedSkills = matched.ifEmpty { listOf("Kotlin", "Jetpack Compose", "Coroutines") },
                    skillGaps = gaps.take(3),
                    customPitch = pitch
                )
            )
        }
        return results
    }

    private fun fetchJobicyJobs(): List<JobOffer> {
        val endpoint = "https://jobicy.com/api/v2/remote-jobs?count=20&tag=android"
        val jsonStr = makeGetRequest(endpoint) ?: return emptyList()
        val root = JSONObject(jsonStr)
        val jobs = root.optJSONArray("jobs") ?: return emptyList()

        val results = mutableListOf<JobOffer>()
        for (i in 0 until jobs.length()) {
            val item = jobs.getJSONObject(i)
            val title = item.optString("jobTitle", "Android Engineer")
            val company = item.optString("companyName", "Empresa Global")
            val location = item.optString("jobGeo", "Worldwide")
            val url = item.optString("url", "")
            val descRaw = item.optString("jobExcerpt", "")
            val description = stripHtml(descRaw)

            val minSal = item.optInt("annualSalaryMin", 0)
            val maxSal = item.optInt("annualSalaryMax", 0)
            val salaryDisplay = if (minSal > 0 && maxSal > 0) {
                "$$minSal - $$maxSal USD / año"
            } else {
                "$80,000 - $120,000 USD / año"
            }

            val skills = extractSkills(title + " " + description)
            val geoTier = detectGeoTier(location, description)

            val jobBase = JobOffer(
                id = 40000L + i,
                title = title,
                company = company,
                companyIcon = if (geoTier == GeoPriorityTier.PERU_LOCAL) "🇵🇪" else if (geoTier == GeoPriorityTier.AMERICAS_UTC5) "🌎" else "🌐",
                location = "100% Remoto ($location)",
                is100Remote = true,
                geoTier = geoTier,
                countryOrigin = location,
                salaryDisplay = salaryDisplay,
                salaryMinK = if (minSal > 0) minSal / 1000 else 80,
                timezone = if (geoTier == GeoPriorityTier.PERU_LOCAL) "PET (UTC-5)" else "Remoto Global",
                postedAgo = "En vivo (Jobicy)",
                skills = skills,
                description = description.take(280) + "...",
                responsibilities = "Desarrollo de módulos móviles en Kotlin, soporte de APIs RESTful y buenas prácticas de Clean Code.",
                aiFitScore = 0,
                aiVerdict = "",
                matchedSkills = emptyList(),
                skillGaps = emptyList(),
                customPitch = "",
                applyUrl = url,
                source = "Jobicy Web"
            )

            val (score, verdict) = AiMatchEngine.calculateMatch(profile, jobBase)
            val matched = profile.primarySkills.filter { ps ->
                skills.any { s -> s.contains(ps, ignoreCase = true) || ps.contains(s, ignoreCase = true) }
            }
            val gaps = skills.filterNot { s ->
                profile.primarySkills.any { ps -> ps.contains(s, ignoreCase = true) || s.contains(ps, ignoreCase = true) }
            }
            val pitch = AiMatchEngine.generateTailoredPitch(profile, jobBase)

            results.add(
                jobBase.copy(
                    aiFitScore = score,
                    aiVerdict = verdict,
                    matchedSkills = matched.ifEmpty { listOf("Kotlin", "Jetpack Compose", "Coroutines") },
                    skillGaps = gaps.take(3),
                    customPitch = pitch
                )
            )
        }
        return results
    }

    private fun makeGetRequest(urlString: String): String? {
        var conn: HttpURLConnection? = null
        return try {
            val url = URL(urlString)
            conn = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "GET"
                connectTimeout = 8000
                readTimeout = 8000
                setRequestProperty("User-Agent", userAgent)
                setRequestProperty("Accept", "application/json")
            }

            if (conn.responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader(InputStreamReader(conn.inputStream)).use { reader ->
                    reader.readText()
                }
            } else {
                null
            }
        } catch (e: Exception) {
            null
        } finally {
            conn?.disconnect()
        }
    }

    private fun stripHtml(text: String): String {
        if (text.isBlank()) return ""
        val clean = Pattern.compile("<[^>]+>").matcher(text).replaceAll(" ")
        return clean.replace(Regex("\\s+"), " ").trim()
    }

    private fun extractSkills(fullText: String): List<String> {
        val lower = fullText.lowercase()
        val found = mutableListOf<String>()

        val knownSkills = listOf(
            "Jetpack Compose" to listOf("compose", "jetpack compose"),
            "Kotlin" to listOf("kotlin"),
            "Coroutines" to listOf("coroutines", "coroutine"),
            "Flow" to listOf("stateflow", "sharedflow", " flow"),
            "Clean Architecture" to listOf("clean architecture", "clean arch"),
            "Hilt" to listOf("hilt", "dagger"),
            "SOLID" to listOf("solid", "s.o.l.i.d"),
            "Room" to listOf("room database", "room"),
            "Retrofit" to listOf("retrofit"),
            "MVVM" to listOf("mvvm"),
            "MVI" to listOf("mvi"),
            "KMP" to listOf("kmp", "kotlin multiplatform"),
            "FinTech" to listOf("fintech", "pagos", "billetera", "payments", "banking"),
            "Git" to listOf("git", "github", "gitlab")
        )

        for ((skillName, keywords) in knownSkills) {
            if (keywords.any { lower.contains(it) }) {
                found.add(skillName)
            }
        }

        if (found.none { it == "Kotlin" }) found.add(0, "Kotlin")
        if (found.none { it == "Jetpack Compose" }) found.add(1, "Jetpack Compose")
        return found.distinct()
    }

    private fun detectGeoTier(location: String, description: String): GeoPriorityTier {
        val full = (location + " " + description).lowercase()
        return when {
            full.contains("peru") || full.contains("perú") || full.contains("lima") || full.contains("pet (utc-5)") || full.contains("soles") -> {
                GeoPriorityTier.PERU_LOCAL
            }
            full.contains("latam") || full.contains("latin") || full.contains("colombia") || full.contains("chile") ||
            full.contains("argentina") || full.contains("mexico") || full.contains("méxico") || full.contains("americas") -> {
                GeoPriorityTier.AMERICAS_UTC5
            }
            else -> {
                GeoPriorityTier.WORLDWIDE_ASYNC
            }
        }
    }

    private fun parseCompanyFromSlug(slug: String): String {
        // e.g. "front-end-android-sr-grupo-mariposa-remote-bfe3" -> "Grupo Mariposa"
        val parts = slug.split("-").filter { it.isNotBlank() }
        val blacklist = setOf("senior", "sr", "lead", "developer", "engineer", "mobile", "front", "end", "android", "remote", "chile", "lima", "santiago", "bogota", "full", "stack", "react", "native", "flutter", "ios")
        val companyTokens = parts.filterNot { blacklist.contains(it.lowercase()) || it.length <= 2 || it.all { c -> c.isDigit() || c in 'a'..'f' } }
        return if (companyTokens.isNotEmpty()) {
            companyTokens.joinToString(" ") { it.replaceFirstChar { c -> c.uppercase() } }
        } else {
            "Empresa Tech LATAM"
        }
    }
}
