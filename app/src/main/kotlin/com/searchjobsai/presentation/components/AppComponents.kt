package com.searchjobsai.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.searchjobsai.domain.model.GeoPriorityTier
import com.searchjobsai.presentation.theme.*

@Composable
fun AppHeader(
    profile: DeveloperProfile,
    onOpenProfile: () -> Unit
) {
    Surface(
        color = DarkSurface,
        modifier = Modifier.fillMaxWidth(),
        border = BorderStroke(1.dp, DarkBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(PrimaryIndigo)
                        .border(1.5.dp, PeruRed, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "JR",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }

                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = profile.name,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "🇵🇪 @${profile.handle}",
                            color = PeruRed,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    Text(
                        text = "💳 izipay • ex-10Pearls • ${profile.yearsOfExperience} años exp",
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                }
            }

            TextButton(
                onClick = onOpenProfile,
                colors = ButtonDefaults.textButtonColors(contentColor = PrimaryIndigoLight)
            ) {
                Text(text = "Ver Perfil", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
fun GeoBadge(geoTier: GeoPriorityTier) {
    val (bgColor, textColor, borderColor) = when (geoTier) {
        GeoPriorityTier.PERU_LOCAL -> Triple(PeruRedBg, PeruRed, PeruRed)
        GeoPriorityTier.AMERICAS_UTC5 -> Triple(AmericasBlueBg, AmericasBlue, AmericasBlue)
        GeoPriorityTier.WORLDWIDE_ASYNC -> Triple(GlobalPurpleBg, GlobalPurple, GlobalPurple)
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(bgColor)
            .border(1.dp, borderColor.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(
            text = geoTier.label,
            color = textColor,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun AiMatchBadge(score: Int) {
    val (bgColor, textColor, borderColor) = if (score >= 90) {
        Triple(MatchGreenBg, MatchGreen, MatchGreen)
    } else {
        Triple(MatchAmberBg, MatchAmber, MatchAmber)
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .border(1.dp, borderColor.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(
            text = "✨ $score% Match",
            color = textColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.Black
        )
    }
}

@Composable
fun SkillChip(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(if (isSelected) PrimaryIndigo else DarkSurfaceVariant)
            .border(
                1.dp,
                if (isSelected) PrimaryIndigoLight else DarkBorder,
                RoundedCornerShape(16.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 10.dp, vertical = 5.dp)
    ) {
        Text(
            text = if (isSelected) "$text ✓" else text,
            color = if (isSelected) Color.White else TextSecondary,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}
