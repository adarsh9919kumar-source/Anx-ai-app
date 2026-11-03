package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.NeonCyan
import kotlin.random.Random

@Composable
fun AudioWaveformVisualizer(
    isActive: Boolean,
    audioLevel: Float,
    modifier: Modifier = Modifier,
    barCount: Int = 9
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val baseHeights = remember {
            List(barCount) { index ->
                val centerDist = kotlin.math.abs(index - (barCount - 1) / 2f)
                (36f - centerDist * 4f).coerceAtLeast(10f)
            }
        }

        for (i in 0 until barCount) {
            val animHeight = remember { Animatable(8f) }

            LaunchedEffect(isActive, audioLevel) {
                if (isActive) {
                    val multiplier = (audioLevel * 1.5f).coerceIn(0.2f, 1.8f)
                    val randomVariance = 0.8f + Random.nextFloat() * 0.4f
                    val target = (baseHeights[i] * multiplier * randomVariance).coerceIn(8f, 48f)
                    animHeight.animateTo(
                        targetValue = target,
                        animationSpec = tween(
                            durationMillis = 80,
                            easing = FastOutSlowInEasing
                        )
                    )
                } else {
                    animHeight.animateTo(
                        targetValue = 6f,
                        animationSpec = tween(durationMillis = 200)
                    )
                }
            }

            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(animHeight.value.dp)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                NeonCyan,
                                ElectricBlue
                            )
                        ),
                        shape = RoundedCornerShape(2.dp)
                    )
            )
        }
    }
}
