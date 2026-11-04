package com.searchjobsai

import com.searchjobsai.data.JobRepository
import com.searchjobsai.data.SortOption
import com.searchjobsai.domain.engine.AiMatchEngine
import com.searchjobsai.domain.model.DeveloperProfile
import com.searchjobsai.domain.model.GeoPriorityTier
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class AiMatchEngineTest {

    private val profile = DeveloperProfile()
    private val repository = JobRepository(profile)

    @Test
    fun testPeruFirstSortingIsDefault() {
        val jobs = repository.filterAndSort(
            query = "",
            selectedSkills = emptySet(),
            remoteOnly = true,
            minSalaryK = 0,
            sortOption = SortOption.PERU_FIRST
        )

        assertTrue(jobs.isNotEmpty(), "Jobs list should not be empty")
        
        // The first jobs must be Tier 1 (Peru Local)
        val firstJob = jobs.first()
        assertEquals(GeoPriorityTier.PERU_LOCAL, firstJob.geoTier, "First job must be from Peru")
        assertEquals("Yape / BCP Digital", firstJob.company)
    }

    @Test
    fun testAiMatchScoreCalculatesCorrectly() {
        val yapeJob = repository.jobs.value.first { it.company.contains("Yape") }
        val (score, verdict) = AiMatchEngine.calculateMatch(profile, yapeJob)

        assertTrue(score >= 95, "Yape match should be >= 95 due to Jetpack Compose, Clean Arch and Peru tier")
        assertTrue(verdict.contains("Excepcional") || verdict.contains("Afinidad"), "Verdict should be positive")
    }

    @Test
    fun testCustomPitchMentionsJeanRamirezExperience() {
        val yapeJob = repository.jobs.value.first { it.company.contains("Yape") }
        val pitch = AiMatchEngine.generateTailoredPitch(profile, yapeJob)

        assertTrue(pitch.contains("Jean Ramirez"), "Pitch must include candidate name")
        assertTrue(pitch.contains("izipay"), "Pitch must mention izipay experience")
        assertTrue(pitch.contains("Jetpack Compose"), "Pitch must mention Jetpack Compose")
        assertTrue(pitch.contains("Clean Architecture"), "Pitch must mention Clean Architecture")
    }

    @Test
    fun testFilterBySkillRemovesNonMatchingJobs() {
        val filtered = repository.filterAndSort(
            query = "",
            selectedSkills = setOf("KMP"),
            remoteOnly = true,
            minSalaryK = 0,
            sortOption = SortOption.PERU_FIRST
        )

        assertTrue(filtered.all { it.skills.contains("KMP") }, "All returned jobs must contain KMP skill")
    }
}
