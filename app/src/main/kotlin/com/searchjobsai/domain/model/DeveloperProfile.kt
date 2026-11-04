package com.searchjobsai.domain.model

data class DeveloperProfile(
    val name: String = "Jean Ramirez",
    val handle: String = "jcramirezdev",
    val title: String = "Android Developer | Jetpack Compose Specialist",
    val currentRole: String = "Analista de programación en izipay",
    val education: String = "Bachiller en Ingeniería de Sistemas - Universidad Tecnológica del Perú (UTP)",
    val location: String = "Lima, Perú (Huso Horario PET / UTC-5)",
    val yearsOfExperience: Double = 4.5,
    val summary: String = "Más de 4 años de experiencia transformando ideas en aplicaciones de alto rendimiento. Especializado en el ecosistema Android, construyendo interfaces modernas con Jetpack Compose y garantizando escalabilidad con Clean Architecture y principios SOLID. Experiencia en FinTech, monederos digitales, pasarelas de pago y migración de XML a Compose.",
    val targetSalaryDisplay: String = "$70k - $120k USD / año (o S/ 18,000 - S/ 25,000 PEN)",
    val preferredWorkType: String = "100% Remoto (Perú / Internacional B2B)",
    val primarySkills: List<String> = listOf(
        "Kotlin",
        "Jetpack Compose",
        "Coroutines",
        "Flow",
        "Clean Arch",
        "Hilt",
        "Retrofit",
        "FinTech",
        "KMP",
        "Room/SQLDelight",
        "SOLID",
        "Scrum"
    ),
    val previousCompanies: List<String> = listOf("izipay", "10Pearls", "Labotec Peru", "Pisco Tech")
)
