package com.example.bluehourbooks.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.example.bluehourbooks.ui.theme.Lavender500
import com.example.bluehourbooks.ui.theme.Midnight700
import kotlin.random.Random

private data class StarPoint(
    val relX: Float,
    val relY: Float,
    val radius: Float,
    val baseAlpha: Float,
    val group: Int
)

@Composable
fun Starfield(
    modifier: Modifier = Modifier,
    starCount: Int = 36
) {
    val stars = remember(starCount) {
        val random = Random(42)
        List(starCount) {
            StarPoint(
                relX = random.nextFloat(),
                relY = random.nextFloat(),
                radius = random.nextFloat() * 1.8f + 0.8f,
                baseAlpha = random.nextFloat() * 0.5f + 0.3f,
                group = it % 3
            )
        }
    }

    val transition = rememberInfiniteTransition(label = "twinkle")
    val alphaAnim0 by transition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(2800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha0"
    )
    val alphaAnim1 by transition.animateFloat(
        initialValue = 0.8f,
        targetValue = 0.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(3400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha1"
    )
    val alphaAnim2 by transition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha2"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        // Draw soft ambient gradient
        drawRect(
            brush = Brush.radialGradient(
                colors = listOf(
                    Lavender500.copy(alpha = 0.12f),
                    Midnight700.copy(alpha = 0.08f),
                    Color.Transparent
                ),
                center = Offset(size.width * 0.5f, size.height * 0.2f),
                radius = size.width * 0.8f
            )
        )

        for (star in stars) {
            val factor = when (star.group) {
                0 -> alphaAnim0
                1 -> alphaAnim1
                else -> alphaAnim2
            }
            val currentAlpha = (star.baseAlpha * factor).coerceIn(0.1f, 1.0f)
            val center = Offset(star.relX * size.width, star.relY * size.height)

            drawCircle(
                color = Color.White.copy(alpha = currentAlpha),
                radius = star.radius,
                center = center
            )
        }
    }
}
