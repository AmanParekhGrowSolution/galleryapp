package com.example.galleryapp.ui.premium

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.galleryapp.R
import com.example.galleryapp.ui.components.WarningGradientButton

private val bgGradient = listOf(Color(0xFF0A0E1A), Color(0xFF0F0C29), Color(0xFF131111))
private val primaryGradient = listOf(Color(0xFF6366F1), Color(0xFF8B5CF6))
private val warningGradient = listOf(Color(0xFFF59E0B), Color(0xFFD97706))

private val features = listOf(
    "AI Object Eraser" to "Remove unwanted objects instantly",
    "AI Photo Enhancer" to "Upscale and restore photos",
    "AI Background Remover" to "Cut out subjects in one tap",
    "Unlimited Smart Cleaner" to "Free up unlimited storage",
    "Bigger Vault" to "Store more private photos",
    "No Ads" to "Completely ad-free experience"
)

@Composable
fun PremiumScreen(
    onClose: () -> Unit,
    viewModel: PremiumViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(bgGradient))
    ) {
        LazyColumn(
            contentPadding = PaddingValues(bottom = 40.dp),
            modifier = Modifier.statusBarsPadding()
        ) {
            item {
                PremiumTopBar(onClose = onClose)
            }
            item {
                PremiumHero()
            }
            item {
                FeatureList()
            }
            item {
                PlanSelector(
                    selectedPlan = uiState.selectedPlan,
                    onPlanSelect = viewModel::selectPlan
                )
            }
            item {
                Spacer(modifier = Modifier.height(24.dp))
                WarningGradientButton(
                    label = stringResource(R.string.start_trial),
                    onClick = { onClose() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = stringResource(R.string.cancel_anytime),
                    color = Color.White.copy(alpha = 0.87f),
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun PremiumTopBar(onClose: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Close,
            contentDescription = stringResource(R.string.close),
            tint = Color.White,
            modifier = Modifier
                .size(24.dp)
                .clickable(onClick = onClose)
        )
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = stringResource(R.string.restore),
            color = Color(0xFF8B5CF6),
            fontSize = 14.sp,
            modifier = Modifier.clickable {}
        )
    }
}

@Composable
private fun PremiumHero() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(72.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(Brush.linearGradient(warningGradient))
        ) {
            Icon(
                imageVector = Icons.Default.Diamond,
                contentDescription = stringResource(R.string.content_desc_premium),
                tint = Color.White,
                modifier = Modifier.size(40.dp)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row {
            Text(
                text = "Unlock ",
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Premium",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFF59E0B)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.premium_subtitle),
            color = Color.White.copy(alpha = 0.87f),
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 32.dp)
        )
    }
}

@Composable
private fun FeatureList() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.linearGradient(
                    listOf(Color(0xFF6366F1).copy(alpha = 0.1f), Color(0xFF8B5CF6).copy(alpha = 0.1f))
                )
            )
            .padding(16.dp)
    ) {
        features.forEach { (title, subtitle) ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = Color(0xFFF59E0B),
                    modifier = Modifier.size(20.dp)
                )
                Column(modifier = Modifier.weight(1f).padding(start = 12.dp)) {
                    Text(title, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                    Text(subtitle, color = Color.White.copy(alpha = 0.87f), fontSize = 12.sp)
                }
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = Color(0xFF6366F1),
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
private fun PlanSelector(
    selectedPlan: PremiumPlan,
    onPlanSelect: (PremiumPlan) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        PlanOption(
            plan = PremiumPlan.Monthly,
            label = stringResource(R.string.monthly),
            price = "₹149/mo",
            badge = null,
            note = null,
            isSelected = selectedPlan == PremiumPlan.Monthly,
            onSelect = { onPlanSelect(PremiumPlan.Monthly) }
        )
        PlanOption(
            plan = PremiumPlan.Yearly,
            label = stringResource(R.string.yearly),
            price = "₹599/yr",
            badge = stringResource(R.string.save_65),
            note = stringResource(R.string.seven_day_trial),
            isSelected = selectedPlan == PremiumPlan.Yearly,
            onSelect = { onPlanSelect(PremiumPlan.Yearly) }
        )
        PlanOption(
            plan = PremiumPlan.Lifetime,
            label = stringResource(R.string.lifetime),
            price = "₹1,999",
            badge = stringResource(R.string.best_value),
            note = null,
            isSelected = selectedPlan == PremiumPlan.Lifetime,
            onSelect = { onPlanSelect(PremiumPlan.Lifetime) }
        )
    }
}

@Composable
private fun PlanOption(
    plan: PremiumPlan,
    label: String,
    price: String,
    badge: String?,
    note: String?,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    val borderMod = if (isSelected) {
        Modifier.border(2.dp, Color(0xFFF59E0B), RoundedCornerShape(14.dp))
    } else Modifier

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .then(borderMod)
            .clip(RoundedCornerShape(14.dp))
            .background(
                Brush.linearGradient(
                    listOf(Color(0xFF6366F1).copy(alpha = 0.12f), Color(0xFF8B5CF6).copy(alpha = 0.12f))
                )
            )
            .clickable(onClick = onSelect)
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(label, color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                    if (badge != null) {
                        Box(
                            modifier = Modifier
                                .padding(start = 8.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(Brush.linearGradient(warningGradient))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(badge, color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
                if (note != null) {
                    Text(note, color = Color.White.copy(alpha = 0.87f), fontSize = 12.sp)
                }
            }
            Text(price, color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
        }
    }
}
