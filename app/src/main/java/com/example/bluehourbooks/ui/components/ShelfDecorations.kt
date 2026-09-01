package com.example.bluehourbooks.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.LocalFlorist
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bluehourbooks.data.model.ShelfDecoration
import com.example.bluehourbooks.ui.theme.Cream100
import com.example.bluehourbooks.ui.theme.Cream400
import com.example.bluehourbooks.ui.theme.Cream50
import com.example.bluehourbooks.ui.theme.Cream500
import com.example.bluehourbooks.ui.theme.Gold400
import com.example.bluehourbooks.ui.theme.Gold500
import com.example.bluehourbooks.ui.theme.Lavender200
import com.example.bluehourbooks.ui.theme.Lavender300
import com.example.bluehourbooks.ui.theme.Lavender400
import com.example.bluehourbooks.ui.theme.Lavender500
import com.example.bluehourbooks.ui.theme.Lavender700
import com.example.bluehourbooks.ui.theme.Midnight100
import com.example.bluehourbooks.ui.theme.Midnight200
import com.example.bluehourbooks.ui.theme.Midnight400
import com.example.bluehourbooks.ui.theme.Midnight600
import com.example.bluehourbooks.ui.theme.Midnight700
import com.example.bluehourbooks.ui.theme.Midnight800
import com.example.bluehourbooks.ui.theme.Midnight900
import com.example.bluehourbooks.ui.theme.Midnight950

@Composable
fun ShelfDecorationItem(
    decoration: ShelfDecoration,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .testTag("shelf_decoration_${decoration.id}")
            .clickable(onClick = onClick),
        contentAlignment = Alignment.BottomCenter
    ) {
        when (decoration.type.uppercase()) {
            "PLANT" -> PlantDecoration(styleKey = decoration.styleKey, title = decoration.title)
            "FRAME" -> FrameDecoration(styleKey = decoration.styleKey, title = decoration.title)
            "QUOTE" -> QuoteDecoration(
                styleKey = decoration.styleKey,
                title = decoration.title,
                subtitle = decoration.subtitle
            )
            "CURIO" -> CurioDecoration(styleKey = decoration.styleKey, title = decoration.title)
            else -> PlantDecoration(styleKey = decoration.styleKey, title = decoration.title)
        }
    }
}

// MARK: - Plant Rendering
@Composable
fun PlantDecoration(styleKey: String, title: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom,
        modifier = Modifier.width(68.dp)
    ) {
        when (styleKey) {
            "peace_lily" -> {
                // Tall Peace Lily Leaves + Flower
                Box(
                    modifier = Modifier
                        .height(84.dp)
                        .width(62.dp),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    // White flower bloom
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .offset(y = 2.dp)
                            .size(16.dp, 26.dp)
                            .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp, bottomStart = 3.dp, bottomEnd = 3.dp))
                            .background(Brush.verticalGradient(listOf(Cream50, Lavender200.copy(alpha = 0.8f))))
                            .border(0.5.dp, Gold400.copy(alpha = 0.5f), RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp, bottomStart = 3.dp, bottomEnd = 3.dp))
                    )
                    // Yellow spadix center
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .offset(y = 8.dp)
                            .size(3.dp, 12.dp)
                            .clip(CircleShape)
                            .background(Gold400)
                    )
                    // Arched Green Leaves
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .offset(x = 2.dp, y = (-4).dp)
                            .rotate(-26f)
                            .size(16.dp, 44.dp)
                            .clip(RoundedCornerShape(topStart = 16.dp, bottomEnd = 16.dp))
                            .background(Brush.verticalGradient(listOf(Color(0xFF34D399), Color(0xFF065F46))))
                    )
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .offset(x = (-2).dp, y = (-2).dp)
                            .rotate(24f)
                            .size(16.dp, 48.dp)
                            .clip(RoundedCornerShape(topEnd = 16.dp, bottomStart = 16.dp))
                            .background(Brush.verticalGradient(listOf(Color(0xFF10B981), Color(0xFF064E3B))))
                    )
                }

                // White Ceramic Cylinder Pot with gold rim
                CeramicPot(
                    potWidth = 46.dp,
                    potHeight = 36.dp,
                    potColors = listOf(Cream100, Cream400),
                    rimColor = Gold400
                )
            }
            "hanging_ivy" -> {
                // Cascading Ivy Vine
                Box(
                    modifier = Modifier
                        .height(86.dp)
                        .width(66.dp),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    // Upward foliage
                    Row(
                        horizontalArrangement = Arrangement.spacedBy((-4).dp),
                        modifier = Modifier.align(Alignment.Center)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF10B981))
                        )
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF059669))
                        )
                        Box(
                            modifier = Modifier
                                .size(18.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF34D399))
                        )
                    }

                    // Hanging vine trailing down
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .offset(x = 6.dp, y = 14.dp),
                        verticalArrangement = Arrangement.spacedBy((-4).dp)
                    ) {
                        Box(modifier = Modifier.size(10.dp).rotate(45f).clip(RoundedCornerShape(3.dp)).background(Color(0xFF10B981)))
                        Box(modifier = Modifier.size(12.dp).rotate(-30f).clip(RoundedCornerShape(3.dp)).background(Color(0xFF059669)))
                        Box(modifier = Modifier.size(9.dp).rotate(20f).clip(RoundedCornerShape(3.dp)).background(Color(0xFF047857)))
                    }
                }

                // Terracotta Pot
                CeramicPot(
                    potWidth = 44.dp,
                    potHeight = 32.dp,
                    potColors = listOf(Color(0xFFE07A5F), Color(0xFFC05C40)),
                    rimColor = Color(0xFFF4A261)
                )
            }
            "bonsai_pine" -> {
                // Miniature Bonsai
                Box(
                    modifier = Modifier
                        .height(78.dp)
                        .width(64.dp),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    // Gnarled Trunk
                    Box(
                        modifier = Modifier
                            .size(12.dp, 36.dp)
                            .rotate(-15f)
                            .offset(x = 2.dp, y = (-6).dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(Brush.verticalGradient(listOf(Color(0xFF78350F), Color(0xFF451A03))))
                    )
                    // Cloud foliage clusters
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .offset(x = (-4).dp, y = 4.dp)
                            .size(34.dp, 20.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Brush.radialGradient(listOf(Color(0xFF10B981), Color(0xFF064E3B))))
                    )
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .offset(x = 6.dp, y = 14.dp)
                            .size(28.dp, 16.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Brush.radialGradient(listOf(Color(0xFF34D399), Color(0xFF065F46))))
                    )
                }

                // Earthen low dish
                Box(
                    modifier = Modifier
                        .size(54.dp, 16.dp)
                        .clip(RoundedCornerShape(bottomStart = 8.dp, bottomEnd = 8.dp))
                        .background(Brush.verticalGradient(listOf(Midnight700, Midnight900)))
                        .border(1.dp, Gold400.copy(alpha = 0.5f), RoundedCornerShape(bottomStart = 8.dp, bottomEnd = 8.dp))
                )
            }
            else -> {
                // Default: Cozy Succulent Rosette
                Box(
                    modifier = Modifier
                        .height(64.dp)
                        .width(58.dp),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    // Succulent Petals
                    Box(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    listOf(
                                        Color(0xFF6EE7B7),
                                        Color(0xFF10B981),
                                        Lavender500.copy(alpha = 0.7f),
                                        Color(0xFF064E3B)
                                    )
                                )
                            )
                            .border(1.dp, Color(0xFFA7F3D0).copy(alpha = 0.6f), CircleShape)
                    ) {
                        // Inner rosette star
                        Icon(
                            imageVector = Icons.Filled.Spa,
                            contentDescription = null,
                            tint = Color(0xFFA7F3D0),
                            modifier = Modifier
                                .size(22.dp)
                                .align(Alignment.Center)
                        )
                    }
                }

                // Round Ceramic Planter Pot
                CeramicPot(
                    potWidth = 42.dp,
                    potHeight = 30.dp,
                    potColors = listOf(Lavender400.copy(alpha = 0.8f), Lavender700),
                    rimColor = Gold400
                )
            }
        }
    }
}

@Composable
private fun CeramicPot(
    potWidth: Dp,
    potHeight: Dp,
    potColors: List<Color>,
    rimColor: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(potWidth)
    ) {
        // Rim
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(rimColor)
        )
        // Body
        Box(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .height(potHeight)
                .clip(RoundedCornerShape(bottomStart = 8.dp, bottomEnd = 8.dp))
                .background(Brush.verticalGradient(potColors))
                .border(0.5.dp, rimColor.copy(alpha = 0.4f), RoundedCornerShape(bottomStart = 8.dp, bottomEnd = 8.dp))
        )
    }
}

// MARK: - Photo Frame Rendering
@Composable
fun FrameDecoration(styleKey: String, title: String) {
    val frameWidth = 84.dp
    val frameHeight = 108.dp

    Box(
        modifier = Modifier
            .width(frameWidth)
            .height(frameHeight)
            .shadow(8.dp, RoundedCornerShape(6.dp))
            .clip(RoundedCornerShape(6.dp))
            .background(
                Brush.linearGradient(
                    if (styleKey == "vintage_library" || styleKey == "golden_solitude") {
                        listOf(Gold500, Gold400, Color(0xFF78350F))
                    } else {
                        listOf(Midnight600, Midnight800, Color(0xFF1E1B4B))
                    }
                )
            )
            .border(
                2.5.dp,
                if (styleKey == "vintage_library" || styleKey == "golden_solitude") Gold400 else Lavender400.copy(alpha = 0.7f),
                RoundedCornerShape(6.dp)
            )
            .padding(5.dp),
        contentAlignment = Alignment.Center
    ) {
        // Artwork Canvas inside
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(3.dp))
                .background(
                    when (styleKey) {
                        "starry_twilight" -> Brush.verticalGradient(
                            listOf(Color(0xFF0F172A), Color(0xFF312E81), Color(0xFF4C1D95), Color(0xFF701A75))
                        )
                        "vintage_library" -> Brush.verticalGradient(
                            listOf(Color(0xFF451A03), Color(0xFF78350F), Color(0xFFB45309), Color(0xFFD97706))
                        )
                        "golden_solitude" -> Brush.verticalGradient(
                            listOf(Color(0xFF1E1B4B), Color(0xFF312E81), Color(0xFFD97706))
                        )
                        "botanical_print" -> Brush.verticalGradient(
                            listOf(Cream100, Cream400)
                        )
                        else -> Brush.verticalGradient(
                            listOf(Midnight900, Lavender700)
                        )
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            // Miniature artwork illustration
            when (styleKey) {
                "starry_twilight" -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Icon(Icons.Filled.Star, contentDescription = null, tint = Gold400, modifier = Modifier.size(10.dp))
                            Icon(Icons.Filled.AutoAwesome, contentDescription = null, tint = Cream50, modifier = Modifier.size(12.dp))
                            Icon(Icons.Filled.Star, contentDescription = null, tint = Lavender200, modifier = Modifier.size(8.dp))
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Box(
                            modifier = Modifier
                                .size(18.dp)
                                .clip(CircleShape)
                                .background(Gold400)
                        )
                    }
                }
                "vintage_library" -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.padding(4.dp)
                    ) {
                        Text(
                            text = "BIBLIOTHECA",
                            fontFamily = FontFamily.Serif,
                            fontSize = 7.5.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                            color = Gold400
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(2.dp),
                            modifier = Modifier.height(20.dp),
                            verticalAlignment = Alignment.Bottom
                        ) {
                            Box(modifier = Modifier.width(3.dp).height(18.dp).background(Cream50))
                            Box(modifier = Modifier.width(4.dp).height(14.dp).background(Gold400))
                            Box(modifier = Modifier.width(3.dp).height(20.dp).background(Cream100))
                            Box(modifier = Modifier.width(4.dp).height(16.dp).background(Gold500))
                        }
                    }
                }
                "botanical_print" -> {
                    Icon(
                        imageVector = Icons.Filled.Spa,
                        contentDescription = null,
                        tint = Color(0xFF065F46),
                        modifier = Modifier.size(32.dp)
                    )
                }
                else -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Filled.Image,
                            contentDescription = null,
                            tint = Lavender200,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = title,
                            fontSize = 7.sp,
                            fontWeight = FontWeight.Medium,
                            color = Cream50,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            // Glass Reflection Overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.linearGradient(
                            listOf(
                                Color.White.copy(alpha = 0.18f),
                                Color.Transparent,
                                Color.White.copy(alpha = 0.05f)
                            )
                        )
                    )
            )
        }
    }
}

// MARK: - Quote Card Rendering
@Composable
fun QuoteDecoration(
    styleKey: String,
    title: String,
    subtitle: String?
) {
    val quoteText = subtitle ?: when (styleKey) {
        "quote_thousand_lives" -> "A reader lives a thousand lives before he dies."
        "quote_sanctuary" -> "Between pages, we find our quiet sanctuary."
        "quote_infinite" -> "There is no friend as loyal as a book."
        else -> "In the quiet blue hour, stories come alive."
    }

    // Mini Parchment Plaque on Wooden Easel Stand
    Box(
        modifier = Modifier
            .width(96.dp)
            .height(100.dp)
            .shadow(6.dp, RoundedCornerShape(8.dp))
            .clip(RoundedCornerShape(8.dp))
            .background(
                Brush.verticalGradient(
                    listOf(
                        Midnight800,
                        Midnight900,
                        Color(0xFF1E1B4B)
                    )
                )
            )
            .border(1.dp, Gold400.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
            .padding(7.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxSize()
        ) {
            Icon(
                imageVector = Icons.Filled.FormatQuote,
                contentDescription = null,
                tint = Gold400,
                modifier = Modifier.size(14.dp)
            )

            Text(
                text = "“$quoteText”",
                fontFamily = FontFamily.Serif,
                fontStyle = FontStyle.Italic,
                fontWeight = FontWeight.Medium,
                fontSize = 8.5.sp,
                lineHeight = 11.sp,
                color = Cream50,
                textAlign = TextAlign.Center,
                maxLines = 4,
                overflow = TextOverflow.Ellipsis
            )

            Box(
                modifier = Modifier
                    .width(32.dp)
                    .height(1.dp)
                    .background(Gold400.copy(alpha = 0.5f))
            )
        }
    }
}

// MARK: - Study Curio Rendering (Lamp, Hourglass, Crystal)
@Composable
fun CurioDecoration(styleKey: String, title: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom,
        modifier = Modifier.width(66.dp)
    ) {
        when (styleKey) {
            "brass_lamp" -> {
                // Brass Banker / Desk Lamp
                Box(
                    modifier = Modifier
                        .height(78.dp)
                        .width(58.dp),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    // Warm amber reading glow
                    Box(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .offset(y = (-6).dp)
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    listOf(Gold400.copy(alpha = 0.35f), Color.Transparent)
                                )
                            )
                    )

                    // Lamp Shade (Green glass or gold hood)
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .offset(y = 6.dp)
                            .size(44.dp, 16.dp)
                            .clip(RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp, bottomStart = 3.dp, bottomEnd = 3.dp))
                            .background(Brush.horizontalGradient(listOf(Color(0xFF064E3B), Color(0xFF10B981), Color(0xFF064E3B))))
                            .border(1.dp, Gold400, RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp, bottomStart = 3.dp, bottomEnd = 3.dp))
                    )

                    // Brass Stem
                    Box(
                        modifier = Modifier
                            .size(4.dp, 44.dp)
                            .offset(y = (-8).dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(Brush.verticalGradient(listOf(Gold400, Gold500, Color(0xFF78350F))))
                    )
                }

                // Weighted Brass Circular Base
                Box(
                    modifier = Modifier
                        .size(36.dp, 8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(Brush.horizontalGradient(listOf(Gold500, Gold400, Gold500)))
                        .border(0.5.dp, Color(0xFF78350F), RoundedCornerShape(4.dp))
                )
            }
            "sand_timer" -> {
                // Hourglass Sand Timer
                Box(
                    modifier = Modifier
                        .height(72.dp)
                        .width(44.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        // Top Wood Base
                        Box(
                            modifier = Modifier
                                .size(34.dp, 5.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(Color(0xFF78350F))
                        )

                        // Glass hourglass bulbs
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy((-2).dp)
                        ) {
                            // Top bulb
                            Box(
                                modifier = Modifier
                                    .size(24.dp, 24.dp)
                                    .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp, bottomStart = 3.dp, bottomEnd = 3.dp))
                                    .background(Color.White.copy(alpha = 0.15f))
                                    .border(0.5.dp, Gold400.copy(alpha = 0.5f), RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp, bottomStart = 3.dp, bottomEnd = 3.dp)),
                                contentAlignment = Alignment.BottomCenter
                            ) {
                                Box(modifier = Modifier.size(16.dp, 8.dp).clip(RoundedCornerShape(2.dp)).background(Gold400))
                            }
                            // Bottom bulb
                            Box(
                                modifier = Modifier
                                    .size(24.dp, 24.dp)
                                    .clip(RoundedCornerShape(bottomStart = 12.dp, bottomEnd = 12.dp, topStart = 3.dp, topEnd = 3.dp))
                                    .background(Color.White.copy(alpha = 0.15f))
                                    .border(0.5.dp, Gold400.copy(alpha = 0.5f), RoundedCornerShape(bottomStart = 12.dp, bottomEnd = 12.dp, topStart = 3.dp, topEnd = 3.dp)),
                                contentAlignment = Alignment.BottomCenter
                            ) {
                                Box(modifier = Modifier.size(18.dp, 12.dp).clip(RoundedCornerShape(bottomStart = 8.dp, bottomEnd = 8.dp)).background(Gold400))
                            }
                        }

                        // Bottom Wood Base
                        Box(
                            modifier = Modifier
                                .size(34.dp, 5.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(Color(0xFF78350F))
                        )
                    }
                }
            }
            else -> {
                // Amethyst Celestial Crystal Cluster
                Box(
                    modifier = Modifier
                        .height(68.dp)
                        .width(48.dp),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    // Shimmering facets
                    Box(
                        modifier = Modifier
                            .offset(y = (-4).dp)
                            .size(34.dp, 48.dp)
                            .rotate(10f)
                            .clip(RoundedCornerShape(topStart = 14.dp, topEnd = 6.dp, bottomStart = 4.dp, bottomEnd = 4.dp))
                            .background(
                                Brush.verticalGradient(
                                    listOf(
                                        Lavender200,
                                        Lavender500,
                                        Lavender700,
                                        Midnight900
                                    )
                                )
                            )
                            .border(1.dp, Lavender300.copy(alpha = 0.8f), RoundedCornerShape(topStart = 14.dp, topEnd = 6.dp, bottomStart = 4.dp, bottomEnd = 4.dp))
                    )
                    Box(
                        modifier = Modifier
                            .offset(x = (-8).dp, y = (-2).dp)
                            .size(22.dp, 32.dp)
                            .rotate(-15f)
                            .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                            .background(Brush.verticalGradient(listOf(Lavender300, Lavender700)))
                    )
                }

                // Raw stone base
                Box(
                    modifier = Modifier
                        .size(38.dp, 8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(Midnight700)
                        .border(0.5.dp, Lavender500.copy(alpha = 0.4f), RoundedCornerShape(4.dp))
                )
            }
        }
    }
}
