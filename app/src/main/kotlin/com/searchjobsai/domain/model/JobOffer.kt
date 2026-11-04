package com.searchjobsai.domain.model

enum class GeoPriorityTier(val weight: Int, val label: String) {
    PERU_LOCAL(1, "🇵🇪 Remoto Perú (UTC-5)"),
    AMERICAS_UTC5(2, "🌎 LatAm / US Remoto (UTC-5)"),
    WORLDWIDE_ASYNC(3, "🌐 Global Remote Asíncrono")
}

enum class ApplicationStatus(val label: String) {
    NONE("Sin postular"),
    SAVED("Guardado ⭐"),
    APPLIED("Postulado 🚀"),
    INTERVIEWING("En Entrevista 🎯"),
    OFFER_RECEIVED("Oferta Recibida 🎉")
}

data class JobOffer(
    val id: Long,
    val title: String,
    val company: String,
    val companyIcon: String,
    val location: String,
    val is100Remote: Boolean,
    val geoTier: GeoPriorityTier,
    val countryOrigin: String,
    val salaryDisplay: String,
    val salaryMinK: Int,
    val timezone: String,
    val postedAgo: String,
    val skills: List<String>,
    val description: String,
    val responsibilities: String,
    val aiFitScore: Int,
    val aiVerdict: String,
    val matchedSkills: List<String>,
    val skillGaps: List<String>,
    val customPitch: String,
    var isSaved: Boolean = false,
    var status: ApplicationStatus = ApplicationStatus.NONE
)
