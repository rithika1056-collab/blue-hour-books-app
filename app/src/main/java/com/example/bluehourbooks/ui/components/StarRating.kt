package com.example.bluehourbooks.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.StarHalf
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bluehourbooks.ui.theme.Cream50
import com.example.bluehourbooks.ui.theme.Gold400
import com.example.bluehourbooks.ui.theme.Gold500
import com.example.bluehourbooks.ui.theme.Lavender300
import com.example.bluehourbooks.ui.theme.Lavender500
import com.example.bluehourbooks.ui.theme.Midnight600
import com.example.bluehourbooks.ui.theme.Midnight700
import com.example.bluehourbooks.ui.theme.Midnight800
import com.example.bluehourbooks.ui.theme.Midnight900

@Composable
fun StarRating(
    rating: Double,
    onRatingChanged: ((Double) -> Unit)? = null,
    modifier: Modifier = Modifier,
    maxStars: Int = 5,
    starSize: Dp = 18.dp,
    activeColor: Color = Gold400,
    inactiveColor: Color = Midnight600,
    showValueText: Boolean = false
) {
    val isInteractive = onRatingChanged != null

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(3.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (i in 1..maxStars) {
            val starValue = i.toDouble()
            val isFull = rating >= starValue
            val isHalf = !isFull && rating >= (starValue - 0.5)

            val starIcon = when {
                isFull -> Icons.Filled.Star
                isHalf -> Icons.AutoMirrored.Filled.StarHalf
                else -> Icons.Filled.StarOutline
            }

            val starTint = if (isFull || isHalf) activeColor else inactiveColor

            val starModifier = if (isInteractive) {
                Modifier
                    .size(starSize)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        role = Role.RadioButton,
                        onClick = {
                            // Cycle through: Full -> Half -> Off
                            val newRating = when {
                                rating == starValue -> starValue - 0.5
                                rating == (starValue - 0.5) -> 0.0
                                else -> starValue
                            }
                            onRatingChanged(newRating)
                        }
                    )
            } else {
                Modifier.size(starSize)
            }

            Icon(
                imageVector = starIcon,
                contentDescription = if (isInteractive) "Rating $rating out of $maxStars" else "$rating stars",
                tint = starTint,
                modifier = starModifier
            )
        }

        if (showValueText && rating > 0) {
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = formatRatingNumber(rating),
                fontSize = (starSize.value * 0.7f).sp,
                fontWeight = FontWeight.SemiBold,
                color = activeColor
            )
        }
    }
}

// Overload for integer ratings backward compatibility
@Composable
fun StarRating(
    rating: Int,
    onRatingChanged: ((Int) -> Unit)? = null,
    modifier: Modifier = Modifier,
    maxStars: Int = 5,
    starSize: Dp = 18.dp,
    activeColor: Color = Gold400,
    inactiveColor: Color = Midnight600,
    showValueText: Boolean = false
) {
    StarRating(
        rating = rating.toDouble(),
        onRatingChanged = onRatingChanged?.let { cb -> { d -> cb(d.toInt()) } },
        modifier = modifier,
        maxStars = maxStars,
        starSize = starSize,
        activeColor = activeColor,
        inactiveColor = inactiveColor,
        showValueText = showValueText
    )
}

/**
 * An interactive, aesthetic Star Rating Selector with half-star support (0.5 to 5.0)
 * Designed for AddBook / EditBook screens.
 */
@Composable
fun InteractiveStarRatingSelector(
    rating: Double,
    onRatingChanged: (Double) -> Unit,
    modifier: Modifier = Modifier
) {
    val ratingOptions = listOf(0.5, 1.0, 1.5, 2.0, 2.5, 3.0, 3.5, 4.0, 4.5, 5.0)
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Main Star display & Value Badge
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            StarRating(
                rating = rating,
                onRatingChanged = onRatingChanged,
                starSize = 32.dp,
                maxStars = 5
            )

            // Rating Pill / Badge
            val pillColors = if (rating > 0) {
                listOf(Gold500.copy(alpha = 0.25f), Gold400.copy(alpha = 0.15f))
            } else {
                listOf(Midnight800.copy(alpha = 0.5f), Midnight800.copy(alpha = 0.5f))
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(Brush.horizontalGradient(pillColors))
                    .border(
                        1.dp,
                        if (rating > 0) Gold400.copy(alpha = 0.4f) else Midnight700.copy(alpha = 0.5f),
                        RoundedCornerShape(50)
                    )
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = if (rating > 0) "★ ${formatRatingNumber(rating)}" else "Unrated",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (rating > 0) Gold400 else Lavender300.copy(alpha = 0.6f)
                )
            }
        }

        // Quick rating chips (0.5 to 5.0) for effortless 1-tap half-star selection
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(scrollState),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // "0" Clear option
            RatingChip(
                label = "None",
                isSelected = rating == 0.0,
                onClick = { onRatingChanged(0.0) }
            )

            ratingOptions.forEach { opt ->
                RatingChip(
                    label = formatRatingNumber(opt),
                    isSelected = rating == opt,
                    onClick = { onRatingChanged(opt) }
                )
            }
        }
    }
}

@Composable
private fun RatingChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val bg by animateColorAsState(
        targetValue = if (isSelected) Lavender500 else Midnight900.copy(alpha = 0.6f),
        label = "chip_bg"
    )
    val textCol by animateColorAsState(
        targetValue = if (isSelected) Cream50 else Lavender300.copy(alpha = 0.8f),
        label = "chip_text"
    )
    val borderCol by animateColorAsState(
        targetValue = if (isSelected) Lavender300 else Midnight700.copy(alpha = 0.6f),
        label = "chip_border"
    )

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(bg)
            .border(1.dp, borderCol, RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 9.dp, vertical = 5.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            fontSize = 11.5.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = textCol
        )
    }
}

fun formatRatingNumber(rating: Double): String {
    return if (rating % 1.0 == 0.0) {
        rating.toInt().toString()
    } else {
        String.format(java.util.Locale.US, "%.1f", rating)
    }
}
