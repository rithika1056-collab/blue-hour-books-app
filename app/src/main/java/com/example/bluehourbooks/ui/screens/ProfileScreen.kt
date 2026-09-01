package com.example.bluehourbooks.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bluehourbooks.data.model.Book
import com.example.bluehourbooks.ui.theme.Cream100
import com.example.bluehourbooks.ui.theme.Cream50
import com.example.bluehourbooks.ui.theme.Gold400
import com.example.bluehourbooks.ui.theme.Lavender200
import com.example.bluehourbooks.ui.theme.Lavender300
import com.example.bluehourbooks.ui.theme.Lavender500
import com.example.bluehourbooks.ui.theme.Midnight100
import com.example.bluehourbooks.ui.theme.Midnight200
import com.example.bluehourbooks.ui.theme.Midnight600
import com.example.bluehourbooks.ui.theme.Midnight700
import com.example.bluehourbooks.ui.theme.Midnight800
import com.example.bluehourbooks.ui.theme.Midnight900
import com.example.bluehourbooks.ui.theme.Midnight950
import com.example.bluehourbooks.ui.theme.Red400
import com.example.bluehourbooks.ui.theme.Red500

@Composable
fun ProfileScreen(
    books: List<Book>,
    onSeedSampleData: () -> Unit,
    onClearAllData: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showClearDialog by remember { mutableStateOf(false) }

    val totalBooks = books.size
    val fiveStarBooks = books.count { it.rating >= 5.0 }
    val ratedBooks = books.filter { it.rating > 0.0 }
    val avgRating = if (ratedBooks.isNotEmpty()) {
        ratedBooks.sumOf { it.rating } / ratedBooks.size
    } else {
        0.0
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Header Profile Card
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(
                        Brush.verticalGradient(
                            listOf(Midnight800, Midnight900)
                        )
                    )
                    .border(1.dp, Midnight700.copy(alpha = 0.6f), RoundedCornerShape(24.dp))
                    .padding(20.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    listOf(Lavender500.copy(alpha = 0.4f), Midnight700)
                                )
                            )
                            .border(1.5.dp, Lavender300.copy(alpha = 0.5f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Person,
                            contentDescription = null,
                            tint = Lavender200,
                            modifier = Modifier.size(36.dp)
                        )
                    }

                    Column {
                        Text(
                            text = "Reader",
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 22.sp,
                            color = Cream50
                        )
                        Text(
                            text = "A sanctuary for books finished in twilight.",
                            fontSize = 13.sp,
                            color = Lavender200.copy(alpha = 0.7f)
                        )
                    }
                }
            }
        }

        // Stats Row
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatTile(
                    title = "Finished",
                    value = totalBooks.toString(),
                    subtitle = "Books read",
                    modifier = Modifier.weight(1f)
                )
                StatTile(
                    title = "5-Star",
                    value = fiveStarBooks.toString(),
                    subtitle = "Favorites",
                    modifier = Modifier.weight(1f)
                )
                StatTile(
                    title = "Average",
                    value = if (avgRating > 0) String.format(java.util.Locale.US, "%.1f", avgRating) else "—",
                    subtitle = "Out of 5",
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Library Actions Section
        item {
            Text(
                text = "Library Management",
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.SemiBold,
                fontSize = 20.sp,
                color = Cream50
            )
            Spacer(modifier = Modifier.height(10.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(Midnight900.copy(alpha = 0.6f))
                    .border(1.dp, Midnight700.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Seed Sample Library
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Restore,
                            contentDescription = null,
                            tint = Lavender300,
                            modifier = Modifier.size(22.dp)
                        )
                        Column {
                            Text(
                                text = "Load Sample Books",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = Cream50
                            )
                            Text(
                                text = "Add curated classic finished reads.",
                                fontSize = 12.sp,
                                color = Midnight100.copy(alpha = 0.5f)
                            )
                        }
                    }

                    Button(
                        onClick = onSeedSampleData,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Midnight800,
                            contentColor = Lavender200
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag("seed_sample_books_button")
                    ) {
                        Text("Load", fontSize = 12.sp)
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(Midnight700.copy(alpha = 0.5f))
                )

                // Clear All Data
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.DeleteSweep,
                            contentDescription = null,
                            tint = Red400,
                            modifier = Modifier.size(22.dp)
                        )
                        Column {
                            Text(
                                text = "Clear All Books",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = Red400
                            )
                            Text(
                                text = "Empty your local library collection.",
                                fontSize = 12.sp,
                                color = Midnight100.copy(alpha = 0.5f)
                            )
                        }
                    }

                    Button(
                        onClick = { showClearDialog = true },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Red500.copy(alpha = 0.15f),
                            contentColor = Red400
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .border(1.dp, Red400.copy(alpha = 0.3f), RoundedCornerShape(10.dp))
                            .testTag("clear_all_books_button")
                    ) {
                        Text("Clear", fontSize = 12.sp)
                    }
                }
            }
        }

        // About Application
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(Midnight900.copy(alpha = 0.4f))
                    .border(1.dp, Midnight700.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
                    .padding(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Info,
                        contentDescription = null,
                        tint = Lavender300.copy(alpha = 0.7f),
                        modifier = Modifier.size(20.dp)
                    )
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "About Blue Hour Books",
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 15.sp,
                            color = Cream50
                        )
                        Text(
                            text = "Blue Hour Books is an offline-capable reading journal designed to celebrate finished reads. Powered by Google Books & Open Library catalogs.",
                            fontSize = 12.sp,
                            lineHeight = 17.sp,
                            color = Midnight100.copy(alpha = 0.6f)
                        )
                    }
                }
            }
        }
    }

    // Clear Confirmation Dialog
    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            containerColor = Midnight900,
            title = {
                Text(
                    text = "Clear All Books?",
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.SemiBold,
                    color = Cream50
                )
            },
            text = {
                Text(
                    text = "This will permanently remove all $totalBooks books from your library.",
                    color = Midnight100.copy(alpha = 0.8f)
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onClearAllData()
                        showClearDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Red500)
                ) {
                    Text("Clear Everything", color = Cream50)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearDialog = false }) {
                    Text("Cancel", color = Midnight100)
                }
            }
        )
    }
}

@Composable
private fun StatTile(
    title: String,
    value: String,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Midnight900.copy(alpha = 0.6f))
            .border(1.dp, Midnight700.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
            .padding(14.dp)
    ) {
        Column {
            Text(
                text = title.uppercase(),
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = 0.5.sp,
                color = Lavender300.copy(alpha = 0.8f)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.SemiBold,
                fontSize = 22.sp,
                color = Cream50
            )
            Text(
                text = subtitle,
                fontSize = 11.sp,
                color = Midnight200.copy(alpha = 0.5f)
            )
        }
    }
}
