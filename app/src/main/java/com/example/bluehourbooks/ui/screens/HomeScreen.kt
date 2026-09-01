package com.example.bluehourbooks.ui.screens

import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CollectionsBookmark
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.NightlightRound
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bluehourbooks.data.model.Book
import com.example.bluehourbooks.ui.components.BookCard
import com.example.bluehourbooks.ui.components.Starfield
import com.example.bluehourbooks.ui.theme.Cream100
import com.example.bluehourbooks.ui.theme.Cream50
import com.example.bluehourbooks.ui.theme.Gold400
import com.example.bluehourbooks.ui.theme.Gold500
import com.example.bluehourbooks.ui.theme.Lavender200
import com.example.bluehourbooks.ui.theme.Lavender300
import com.example.bluehourbooks.ui.theme.Lavender400
import com.example.bluehourbooks.ui.theme.Lavender500
import com.example.bluehourbooks.ui.theme.Midnight100
import com.example.bluehourbooks.ui.theme.Midnight200
import com.example.bluehourbooks.ui.theme.Midnight600
import com.example.bluehourbooks.ui.theme.Midnight700
import com.example.bluehourbooks.ui.theme.Midnight800
import com.example.bluehourbooks.ui.theme.Midnight900
import com.example.bluehourbooks.ui.theme.Midnight950
import java.util.Calendar

@Composable
fun HomeScreen(
    books: List<Book>,
    onAddBook: () -> Unit,
    onViewAll: () -> Unit,
    onOpenBook: (Book) -> Unit,
    onSeedSampleData: () -> Unit,
    modifier: Modifier = Modifier
) {
    val completedCount = books.size
    val ratedBooks = books.filter { it.rating > 0 }
    val avgRating = if (ratedBooks.isNotEmpty()) {
        ratedBooks.sumOf { it.rating } / ratedBooks.size
    } else {
        0.0
    }

    val currentYear = remember { Calendar.getInstance().get(Calendar.YEAR).toString() }
    val thisYearCount = remember(books, currentYear) {
        books.count { it.dateCompleted?.startsWith(currentYear) == true }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Hero Section with Indian-inspired Arch & Twilight Atmosphere
        item {
            AtmosphericHeroBanner(
                onAddBook = onAddBook
            )
        }

        // Stats Section
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatCard(
                    icon = Icons.Filled.AutoStories,
                    label = "Completed",
                    value = completedCount.toString(),
                    subtitle = "Books read",
                    accentColor = Lavender300,
                    gradient = listOf(Lavender500.copy(alpha = 0.22f), Midnight900.copy(alpha = 0.8f)),
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    icon = Icons.Filled.Star,
                    label = "Avg Rating",
                    value = if (avgRating > 0) String.format(java.util.Locale.US, "%.1f", avgRating) else "—",
                    subtitle = "Out of 5.0",
                    accentColor = Gold400,
                    gradient = listOf(Gold500.copy(alpha = 0.20f), Midnight900.copy(alpha = 0.8f)),
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    icon = Icons.Filled.CalendarMonth,
                    label = "This Year",
                    value = thisYearCount.toString(),
                    subtitle = currentYear,
                    accentColor = Lavender200,
                    gradient = listOf(Lavender400.copy(alpha = 0.18f), Midnight900.copy(alpha = 0.8f)),
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Recently Completed Section
        if (books.isNotEmpty()) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Recently Completed",
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 22.sp,
                            color = Cream50
                        )
                        Text(
                            text = "Your latest finished stories in twilight.",
                            fontSize = 13.sp,
                            color = Lavender200.copy(alpha = 0.7f)
                        )
                    }

                    TextButton(
                        onClick = onViewAll,
                        modifier = Modifier.testTag("view_all_books_button")
                    ) {
                        Text(
                            text = "View all",
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.Medium,
                            color = Lavender300
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = Lavender300,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }

            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    contentPadding = PaddingValues(end = 8.dp)
                ) {
                    items(books.take(6), key = { it.id }) { book ->
                        BookCard(
                            book = book,
                            onClick = onOpenBook,
                            modifier = Modifier.width(155.dp)
                        )
                    }
                }
            }

            // Blue Hour Reading Inspiration Quote Card
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            Brush.linearGradient(
                                listOf(
                                    Midnight900.copy(alpha = 0.9f),
                                    Midnight800.copy(alpha = 0.6f)
                                )
                            )
                        )
                        .border(1.dp, Midnight700.copy(alpha = 0.6f), RoundedCornerShape(20.dp))
                        .padding(18.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(Lavender500.copy(alpha = 0.2f))
                                .border(1.dp, Lavender400.copy(alpha = 0.35f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.NightlightRound,
                                contentDescription = null,
                                tint = Gold400,
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Text(
                                text = "“In the quiet blue hour between daylight and dusk, stories come alive.”",
                                fontFamily = FontFamily.Serif,
                                fontStyle = FontStyle.Italic,
                                fontSize = 13.5.sp,
                                lineHeight = 18.sp,
                                color = Cream100.copy(alpha = 0.9f)
                            )
                            Text(
                                text = "A personal space to cherish finished reads",
                                fontSize = 11.5.sp,
                                color = Lavender300.copy(alpha = 0.7f)
                            )
                        }
                    }
                }
            }
        } else {
            // Empty starter callout
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp))
                        .background(
                            Brush.verticalGradient(
                                listOf(Midnight800.copy(alpha = 0.6f), Midnight900.copy(alpha = 0.9f))
                            )
                        )
                        .border(1.dp, Midnight700.copy(alpha = 0.6f), RoundedCornerShape(24.dp))
                        .padding(28.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(60.dp)
                                .clip(CircleShape)
                                .background(Lavender500.copy(alpha = 0.2f))
                                .border(1.dp, Lavender400.copy(alpha = 0.3f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.CollectionsBookmark,
                                contentDescription = null,
                                tint = Lavender300,
                                modifier = Modifier.size(30.dp)
                            )
                        }

                        Text(
                            text = "Your shelf is waiting",
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 22.sp,
                            color = Cream50
                        )

                        Text(
                            text = "Search for a book you've finished, or load sample reads to explore your digital bookshelf.",
                            fontSize = 13.5.sp,
                            lineHeight = 19.sp,
                            color = Midnight100.copy(alpha = 0.75f),
                            textAlign = TextAlign.Center
                        )

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.padding(top = 4.dp)
                        ) {
                            Button(
                                onClick = onAddBook,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Lavender500,
                                    contentColor = Cream50
                                ),
                                shape = CircleShape,
                                modifier = Modifier.testTag("empty_home_add_book_button")
                            ) {
                                Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Add Book", fontSize = 13.5.sp, fontWeight = FontWeight.SemiBold)
                            }

                            Button(
                                onClick = onSeedSampleData,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Midnight800,
                                    contentColor = Lavender200
                                ),
                                shape = CircleShape,
                                modifier = Modifier.border(1.dp, Midnight600.copy(alpha = 0.5f), CircleShape)
                            ) {
                                Text("Load Sample Shelf", fontSize = 13.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AtmosphericHeroBanner(
    onAddBook: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(28.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Midnight800.copy(alpha = 0.95f),
                        Midnight900,
                        Midnight950
                    )
                )
            )
            .border(1.dp, Midnight700.copy(alpha = 0.7f), RoundedCornerShape(28.dp))
            .padding(vertical = 30.dp, horizontal = 20.dp)
    ) {
        // Animated twinkling starfield background
        Starfield(modifier = Modifier.matchParentSize(), starCount = 35)

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Elegant glowing celestial lantern/book icon
            Box(
                modifier = Modifier
                    .size(62.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        Brush.radialGradient(
                            listOf(
                                Lavender500.copy(alpha = 0.45f),
                                Midnight800.copy(alpha = 0.6f)
                            )
                        )
                    )
                    .border(1.5.dp, Lavender400.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                    .shadow(12.dp, RoundedCornerShape(20.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.AutoStories,
                    contentDescription = null,
                    tint = Cream50,
                    modifier = Modifier.size(30.dp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Subtitle Tag
            Text(
                text = "BLUE HOUR BOOKS",
                fontSize = 11.5.sp,
                letterSpacing = 3.sp,
                fontWeight = FontWeight.SemiBold,
                color = Lavender300.copy(alpha = 0.85f)
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Main Title
            Text(
                text = "Your Finished Books",
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.SemiBold,
                fontSize = 28.sp,
                lineHeight = 34.sp,
                color = Cream50,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Poetic Quote
            Text(
                text = "“Every finished book becomes a little part of your story.”",
                fontFamily = FontFamily.Serif,
                fontStyle = FontStyle.Italic,
                fontSize = 14.5.sp,
                lineHeight = 20.sp,
                color = Cream100.copy(alpha = 0.8f),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 14.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Hero Add Book Button
            Button(
                onClick = onAddBook,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Lavender500,
                    contentColor = Cream50
                ),
                shape = CircleShape,
                modifier = Modifier
                    .testTag("hero_add_book_button")
                    .height(46.dp)
                    .shadow(8.dp, CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Add Book",
                    fontFamily = FontFamily.SansSerif,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
private fun StatCard(
    icon: ImageVector,
    label: String,
    value: String,
    subtitle: String,
    accentColor: Color,
    gradient: List<Color>,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(Brush.verticalGradient(gradient))
            .border(1.dp, Midnight700.copy(alpha = 0.6f), RoundedCornerShape(20.dp))
            .padding(14.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Midnight950.copy(alpha = 0.6f))
                    .border(1.dp, Midnight700.copy(alpha = 0.5f), RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(16.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = value,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.SemiBold,
                fontSize = 22.sp,
                color = Cream50
            )

            Text(
                text = label.uppercase(),
                fontSize = 9.5.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = 0.6.sp,
                color = Lavender300.copy(alpha = 0.8f)
            )

            Text(
                text = subtitle,
                fontSize = 10.sp,
                color = Midnight200.copy(alpha = 0.5f)
            )
        }
    }
}
