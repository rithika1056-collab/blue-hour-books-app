package com.example.bluehourbooks.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.CollectionsBookmark
import androidx.compose.material.icons.filled.ImportExport
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bluehourbooks.data.model.Book
import com.example.bluehourbooks.ui.components.BookCard
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
import com.example.bluehourbooks.ui.viewmodel.SortOption

@Composable
fun LibraryScreen(
    books: List<Book>,
    totalBooksCount: Int,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    sortOption: SortOption,
    onSortOptionChange: (SortOption) -> Unit,
    minRatingFilter: Double = 0.0,
    onMinRatingFilterChange: (Double) -> Unit = {},
    onOpenBook: (Book) -> Unit,
    onAddBook: () -> Unit,
    modifier: Modifier = Modifier
) {
    var sortMenuExpanded by remember { mutableStateOf(false) }
    val filterScrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Header
        Text(
            text = "Library",
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.SemiBold,
            fontSize = 30.sp,
            color = Cream50
        )
        Text(
            text = if (totalBooksCount == 0) {
                "Your personal collection of finished books."
            } else {
                "${books.size} of $totalBooksCount ${if (totalBooksCount == 1) "book" else "books"} shown"
            },
            fontSize = 13.sp,
            color = Lavender200.copy(alpha = 0.7f)
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Search bar & Sort Controls
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                placeholder = {
                    Text(
                        "Search title, author, ISBN…",
                        color = Midnight200.copy(alpha = 0.5f),
                        fontSize = 13.5.sp
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = "Search",
                        tint = Lavender300.copy(alpha = 0.6f),
                        modifier = Modifier.size(18.dp)
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { onSearchQueryChange("") }) {
                            Icon(
                                imageVector = Icons.Filled.Clear,
                                contentDescription = "Clear search",
                                tint = Midnight200,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Midnight900.copy(alpha = 0.7f),
                    unfocusedContainerColor = Midnight900.copy(alpha = 0.5f),
                    focusedBorderColor = Lavender300.copy(alpha = 0.7f),
                    unfocusedBorderColor = Midnight600.copy(alpha = 0.5f),
                    focusedTextColor = Cream50,
                    unfocusedTextColor = Cream50
                ),
                modifier = Modifier
                    .weight(1f)
                    .testTag("library_search_input")
            )

            // Sort Dropdown Button
            Box {
                Row(
                    modifier = Modifier
                        .testTag("library_sort_button")
                        .clip(RoundedCornerShape(14.dp))
                        .background(Midnight900.copy(alpha = 0.7f))
                        .border(1.dp, Midnight600.copy(alpha = 0.6f), RoundedCornerShape(14.dp))
                        .clickable { sortMenuExpanded = true }
                        .padding(horizontal = 12.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.ImportExport,
                        contentDescription = "Sort",
                        tint = Lavender300,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = sortOption.label,
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.Medium,
                        color = Cream50
                    )
                }

                DropdownMenu(
                    expanded = sortMenuExpanded,
                    onDismissRequest = { sortMenuExpanded = false },
                    modifier = Modifier
                        .background(Midnight800)
                        .border(1.dp, Midnight600.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
                ) {
                    SortOption.entries.forEach { option ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = option.label,
                                    fontSize = 13.sp,
                                    color = if (sortOption == option) Lavender200 else Midnight100.copy(alpha = 0.85f),
                                    fontWeight = if (sortOption == option) FontWeight.SemiBold else FontWeight.Normal
                                )
                            },
                            onClick = {
                                onSortOptionChange(option)
                                sortMenuExpanded = false
                            }
                        )
                    }
                }
            }
        }

        // Quick Rating Filter Chips
        if (totalBooksCount > 0) {
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(filterScrollState),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RatingFilterChip(
                    label = "All Ratings",
                    isSelected = minRatingFilter == 0.0,
                    onClick = { onMinRatingFilterChange(0.0) }
                )
                RatingFilterChip(
                    label = "★ 5.0 Stars",
                    isSelected = minRatingFilter == 5.0,
                    onClick = { onMinRatingFilterChange(if (minRatingFilter == 5.0) 0.0 else 5.0) }
                )
                RatingFilterChip(
                    label = "★ 4.0+ Stars",
                    isSelected = minRatingFilter == 4.0,
                    onClick = { onMinRatingFilterChange(if (minRatingFilter == 4.0) 0.0 else 4.0) }
                )
                RatingFilterChip(
                    label = "★ 3.0+ Stars",
                    isSelected = minRatingFilter == 3.0,
                    onClick = { onMinRatingFilterChange(if (minRatingFilter == 3.0) 0.0 else 3.0) }
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Books Grid or Empty State
        if (totalBooksCount == 0) {
            EmptyLibraryState(onAddBook = onAddBook)
        } else if (books.isEmpty()) {
            NoSearchResultsState(
                searchQuery = searchQuery,
                hasFilter = minRatingFilter > 0.0,
                onResetFilters = {
                    onSearchQueryChange("")
                    onMinRatingFilterChange(0.0)
                }
            )
        } else {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 145.dp),
                contentPadding = PaddingValues(bottom = 96.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(books, key = { it.id }) { book ->
                    BookCard(
                        book = book,
                        onClick = onOpenBook,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
private fun RatingFilterChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val bg by animateColorAsState(
        targetValue = if (isSelected) Lavender500.copy(alpha = 0.35f) else Midnight900.copy(alpha = 0.5f),
        label = "filter_chip_bg"
    )
    val borderCol by animateColorAsState(
        targetValue = if (isSelected) Lavender300 else Midnight700.copy(alpha = 0.6f),
        label = "filter_chip_border"
    )
    val textCol by animateColorAsState(
        targetValue = if (isSelected) Cream50 else Lavender300.copy(alpha = 0.8f),
        label = "filter_chip_text"
    )

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(bg)
            .border(1.dp, borderCol, RoundedCornerShape(50))
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
            color = textCol
        )
    }
}

@Composable
private fun EmptyLibraryState(onAddBook: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(Midnight800.copy(alpha = 0.5f))
            .border(1.dp, Midnight700.copy(alpha = 0.6f), RoundedCornerShape(24.dp))
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            listOf(Lavender500.copy(alpha = 0.25f), Midnight700.copy(alpha = 0.4f))
                        )
                    )
                    .border(1.dp, Lavender300.copy(alpha = 0.35f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.CollectionsBookmark,
                    contentDescription = null,
                    tint = Lavender200,
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
                text = "You haven't added any books yet. Search for a book you've finished and start building your library.",
                fontSize = 13.5.sp,
                lineHeight = 19.sp,
                color = Midnight100.copy(alpha = 0.75f),
                textAlign = TextAlign.Center
            )

            Button(
                onClick = onAddBook,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Lavender500,
                    contentColor = Cream50
                ),
                shape = CircleShape,
                modifier = Modifier.testTag("empty_library_add_book_button")
            ) {
                Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Add your first book", fontSize = 13.5.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
private fun NoSearchResultsState(
    searchQuery: String,
    hasFilter: Boolean,
    onResetFilters: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Icon(
            imageVector = Icons.Filled.AutoStories,
            contentDescription = null,
            tint = Midnight200.copy(alpha = 0.4f),
            modifier = Modifier.size(42.dp)
        )
        Text(
            text = if (searchQuery.isNotEmpty()) "No books match “$searchQuery”" else "No books match the filter",
            fontFamily = FontFamily.Serif,
            fontSize = 18.sp,
            color = Cream50
        )
        Text(
            text = "Try searching with a different keyword or resetting filters.",
            fontSize = 13.sp,
            color = Midnight100.copy(alpha = 0.65f),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(4.dp))
        Button(
            onClick = onResetFilters,
            colors = ButtonDefaults.buttonColors(
                containerColor = Midnight800,
                contentColor = Lavender200
            ),
            shape = CircleShape
        ) {
            Text("Reset Filters", fontSize = 12.5.sp)
        }
    }
}
