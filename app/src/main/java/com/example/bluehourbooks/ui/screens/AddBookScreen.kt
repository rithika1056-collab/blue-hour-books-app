package com.example.bluehourbooks.ui.screens

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bluehourbooks.data.model.Book
import com.example.bluehourbooks.data.model.BookSearchResult
import com.example.bluehourbooks.ui.components.BookCoverImage
import com.example.bluehourbooks.ui.components.InteractiveStarRatingSelector
import com.example.bluehourbooks.ui.theme.Cream100
import com.example.bluehourbooks.ui.theme.Cream50
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
import com.example.bluehourbooks.ui.theme.Red400
import com.example.bluehourbooks.ui.viewmodel.BookViewModel
import com.example.bluehourbooks.ui.viewmodel.OnlineSearchUiState
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddBookBottomSheet(
    onDismiss: () -> Unit,
    searchState: OnlineSearchUiState,
    onSearch: (String) -> Unit,
    onLoadMore: () -> Unit,
    onAddBook: (Book) -> Unit,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
) {
    var mode by remember { mutableStateOf("search") } // "search" | "manual"
    var selectedSearchResult by remember { mutableStateOf<BookSearchResult?>(null) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Midnight950,
        contentColor = Cream50,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 10.dp)
                    .width(44.dp)
                    .height(4.dp)
                    .clip(CircleShape)
                    .background(Midnight600)
            )
        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp)
        ) {
            Crossfade(
                targetState = Triple(mode, selectedSearchResult != null, selectedSearchResult),
                label = "add_book_mode_transition"
            ) { (_, isConfirming, result) ->
                if (isConfirming && result != null) {
                    ConfirmSearchResultView(
                        result = result,
                        onBack = { selectedSearchResult = null },
                        onConfirm = { book ->
                            onAddBook(book)
                            onDismiss()
                        }
                    )
                } else if (mode == "search") {
                    OnlineSearchTab(
                        searchState = searchState,
                        onSearch = onSearch,
                        onLoadMore = onLoadMore,
                        onSelectResult = { selectedSearchResult = it },
                        onSwitchToManual = { mode = "manual" }
                    )
                } else {
                    ManualAddBookForm(
                        onBackToSearch = { mode = "search" },
                        onSave = { book ->
                            onAddBook(book)
                            onDismiss()
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun OnlineSearchTab(
    searchState: OnlineSearchUiState,
    onSearch: (String) -> Unit,
    onLoadMore: () -> Unit,
    onSelectResult: (BookSearchResult) -> Unit,
    onSwitchToManual: () -> Unit
) {
    var inputQuery by remember { mutableStateOf(searchState.query) }
    val focusManager = LocalFocusManager.current

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "Add a Finished Book",
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.SemiBold,
            fontSize = 24.sp,
            color = Cream50
        )
        Text(
            text = "Search by book title, author, or ISBN to add it to your shelf.",
            fontSize = 13.sp,
            color = Lavender200.copy(alpha = 0.7f)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Search Input & Action
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = inputQuery,
                onValueChange = { inputQuery = it },
                placeholder = {
                    Text(
                        "Search title, author, or ISBN…",
                        color = Midnight200.copy(alpha = 0.5f),
                        fontSize = 13.5.sp
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = null,
                        tint = Lavender300.copy(alpha = 0.6f),
                        modifier = Modifier.size(18.dp)
                    )
                },
                trailingIcon = {
                    if (inputQuery.isNotEmpty()) {
                        IconButton(onClick = { inputQuery = "" }) {
                            Icon(
                                imageVector = Icons.Filled.Clear,
                                contentDescription = "Clear",
                                tint = Midnight200,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        if (inputQuery.isNotBlank()) {
                            focusManager.clearFocus()
                            onSearch(inputQuery)
                        }
                    }
                ),
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Midnight900.copy(alpha = 0.7f),
                    unfocusedContainerColor = Midnight900.copy(alpha = 0.5f),
                    focusedBorderColor = Lavender300.copy(alpha = 0.7f),
                    unfocusedBorderColor = Midnight600.copy(alpha = 0.6f),
                    focusedTextColor = Cream50,
                    unfocusedTextColor = Cream50
                ),
                modifier = Modifier
                    .weight(1f)
                    .testTag("online_search_input")
            )

            Button(
                onClick = {
                    focusManager.clearFocus()
                    onSearch(inputQuery)
                },
                enabled = inputQuery.isNotBlank() && !searchState.isSearching,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Lavender500,
                    contentColor = Cream50
                ),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .height(54.dp)
                    .testTag("online_search_submit_button")
            ) {
                if (searchState.isSearching) {
                    CircularProgressIndicator(
                        color = Cream50,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(18.dp)
                    )
                } else {
                    Text("Search", fontSize = 13.5.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Error message if any
        if (searchState.searchError != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Red400.copy(alpha = 0.15f))
                    .border(1.dp, Red400.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                    .padding(12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.ErrorOutline,
                        contentDescription = null,
                        tint = Red400,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = searchState.searchError,
                        fontSize = 12.5.sp,
                        color = Red400
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Search Results List
        if (searchState.isSearching && searchState.results.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    CircularProgressIndicator(color = Lavender300, modifier = Modifier.size(34.dp))
                    Text(
                        text = "Searching library catalogs…",
                        fontSize = 13.5.sp,
                        color = Midnight100.copy(alpha = 0.7f)
                    )
                }
            }
        } else if (searchState.hasSearched && searchState.results.isEmpty() && searchState.searchError == null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.AutoStories,
                        contentDescription = null,
                        tint = Midnight200.copy(alpha = 0.5f),
                        modifier = Modifier.size(34.dp)
                    )
                    Text(
                        text = "No books found",
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 17.sp,
                        color = Cream50
                    )
                    Text(
                        text = "Try another title or author, or enter the book details manually.",
                        fontSize = 12.5.sp,
                        color = Midnight100.copy(alpha = 0.65f),
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else if (searchState.results.isNotEmpty()) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(350.dp),
                contentPadding = PaddingValues(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(searchState.results, key = { it.id }) { result ->
                    SearchResultRow(
                        result = result,
                        onSelect = { onSelectResult(result) }
                    )
                }

                if (searchState.hasMore) {
                    item {
                        Button(
                            onClick = onLoadMore,
                            enabled = !searchState.isLoadingMore,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Midnight800,
                                contentColor = Lavender200
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            if (searchState.isLoadingMore) {
                                CircularProgressIndicator(
                                    color = Lavender200,
                                    strokeWidth = 2.dp,
                                    modifier = Modifier.size(16.dp)
                                )
                            } else {
                                Text("Load More Results", fontSize = 13.sp)
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Switch to Manual Entry button
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(Midnight900.copy(alpha = 0.5f))
                .border(1.dp, Midnight700.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
                .clickable { onSwitchToManual() }
                .padding(14.dp)
                .testTag("switch_to_manual_add_button"),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.EditNote,
                    contentDescription = null,
                    tint = Lavender300,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = "Can't find your book? Enter details manually",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = Lavender200
                )
            }
        }
    }
}

@Composable
private fun SearchResultRow(
    result: BookSearchResult,
    onSelect: () -> Unit
) {
    Row(
        modifier = Modifier
            .testTag("search_result_item_${result.id}")
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Midnight900.copy(alpha = 0.6f))
            .border(1.dp, Midnight700.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
            .clickable { onSelect() }
            .padding(10.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Thumbnail
        Box(
            modifier = Modifier
                .width(48.dp)
                .height(68.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Midnight800)
        ) {
            BookCoverImage(
                coverUrl = result.cover,
                title = result.title
            )
        }

        // Info
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = result.title,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.5.sp,
                color = Cream50,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if (!result.author.isNullOrBlank()) {
                Text(
                    text = result.author,
                    fontSize = 12.sp,
                    color = Lavender200.copy(alpha = 0.85f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            val metaParts = listOfNotNull(
                result.publicationYear?.toString(),
                result.publisher,
                result.isbn?.let { "ISBN $it" }
            )
            if (metaParts.isNotEmpty()) {
                Text(
                    text = metaParts.joinToString(" · "),
                    fontSize = 11.sp,
                    color = Midnight200.copy(alpha = 0.6f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        // Add Button
        Button(
            onClick = onSelect,
            colors = ButtonDefaults.buttonColors(
                containerColor = Lavender500.copy(alpha = 0.25f),
                contentColor = Lavender200
            ),
            shape = RoundedCornerShape(10.dp),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
            modifier = Modifier.border(1.dp, Lavender400.copy(alpha = 0.35f), RoundedCornerShape(10.dp))
        ) {
            Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("Add", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun ConfirmSearchResultView(
    result: BookSearchResult,
    onBack: () -> Unit,
    onConfirm: (Book) -> Unit
) {
    var rating by remember { mutableDoubleStateOf(0.0) }
    var dateCompleted by remember { mutableStateOf(BookViewModel.todayIso()) }
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(scrollState)
    ) {
        Row(
            modifier = Modifier
                .clickable { onBack() }
                .padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back to results",
                tint = Lavender300,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "Back to search",
                fontSize = 13.sp,
                color = Lavender300
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Book Header Overview
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .width(115.dp)
                    .aspectRatio(0.67f)
                    .shadow(6.dp, RoundedCornerShape(14.dp))
                    .clip(RoundedCornerShape(14.dp))
                    .background(Midnight900)
            ) {
                BookCoverImage(coverUrl = result.cover, title = result.title)
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = result.title,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 19.sp,
                    lineHeight = 23.sp,
                    color = Cream50
                )
                if (!result.author.isNullOrBlank()) {
                    Text(
                        text = "by ${result.author}",
                        fontSize = 13.sp,
                        color = Lavender200.copy(alpha = 0.85f)
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                result.publicationYear?.let {
                    Text("Published: $it", fontSize = 11.sp, color = Midnight100.copy(alpha = 0.6f))
                }
                result.publisher?.let {
                    Text("Publisher: $it", fontSize = 11.sp, color = Midnight100.copy(alpha = 0.6f))
                }
                result.isbn?.let {
                    Text("ISBN: $it", fontSize = 11.sp, color = Midnight100.copy(alpha = 0.6f))
                }
            }
        }

        // Categories
        if (!result.categories.isNullOrEmpty()) {
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                result.categories.take(4).forEach { cat ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(Lavender500.copy(alpha = 0.15f))
                            .border(1.dp, Lavender400.copy(alpha = 0.25f), RoundedCornerShape(50))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(cat, fontSize = 11.sp, color = Lavender200)
                    }
                }
            }
        }

        // Description
        if (!result.description.isNullOrBlank()) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = result.description,
                fontSize = 12.sp,
                lineHeight = 17.sp,
                color = Midnight100.copy(alpha = 0.7f),
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Rating & Date completed section
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(Midnight900.copy(alpha = 0.6f))
                .border(1.dp, Midnight700.copy(alpha = 0.5f), RoundedCornerShape(18.dp))
                .padding(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "Your Rating",
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.Medium,
                        color = Lavender300
                    )
                    InteractiveStarRatingSelector(
                        rating = rating,
                        onRatingChanged = { rating = it }
                    )
                }

                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "Date Completed",
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.Medium,
                        color = Lavender300
                    )
                    OutlinedTextField(
                        value = dateCompleted,
                        onValueChange = { dateCompleted = it },
                        placeholder = { Text("YYYY-MM-DD", fontSize = 13.sp) },
                        leadingIcon = {
                            Icon(Icons.Filled.CalendarMonth, contentDescription = null, tint = Lavender300, modifier = Modifier.size(18.dp))
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Midnight800,
                            unfocusedContainerColor = Midnight800,
                            focusedBorderColor = Lavender400,
                            unfocusedBorderColor = Midnight700,
                            focusedTextColor = Cream50,
                            unfocusedTextColor = Cream50
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                val newBook = Book(
                    id = UUID.randomUUID().toString(),
                    title = result.title,
                    author = result.author,
                    cover = result.cover,
                    isbn = result.isbn,
                    publicationYear = result.publicationYear,
                    publisher = result.publisher,
                    description = result.description,
                    rating = rating,
                    dateCompleted = dateCompleted.ifBlank { null },
                    catalogSource = result.catalogSource,
                    externalBookId = result.externalBookId,
                    categories = result.categories?.joinToString(", ")
                )
                onConfirm(newBook)
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = Lavender500,
                contentColor = Cream50
            ),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
                .testTag("confirm_add_book_button")
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Icon(Icons.Filled.Check, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Add to My Library", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun ManualAddBookForm(
    onBackToSearch: () -> Unit,
    onSave: (Book) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var author by remember { mutableStateOf("") }
    var coverUrl by remember { mutableStateOf("") }
    var isbn by remember { mutableStateOf("") }
    var year by remember { mutableStateOf("") }
    var publisher by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var rating by remember { mutableDoubleStateOf(0.0) }
    var dateCompleted by remember { mutableStateOf(BookViewModel.todayIso()) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier
                .clickable { onBackToSearch() }
                .padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back to search",
                tint = Lavender300,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "Back to search",
                fontSize = 13.sp,
                color = Lavender300
            )
        }

        Text(
            text = "Manual Book Entry",
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.SemiBold,
            fontSize = 22.sp,
            color = Cream50
        )

        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Book Title *") },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = defaultFieldColors(),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("manual_title_input")
        )

        OutlinedTextField(
            value = author,
            onValueChange = { author = it },
            label = { Text("Author") },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = defaultFieldColors(),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("manual_author_input")
        )

        OutlinedTextField(
            value = coverUrl,
            onValueChange = { coverUrl = it },
            label = { Text("Cover Image URL") },
            placeholder = { Text("https://…") },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = defaultFieldColors(),
            modifier = Modifier.fillMaxWidth()
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = isbn,
                onValueChange = { isbn = it },
                label = { Text("ISBN") },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = defaultFieldColors(),
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = year,
                onValueChange = { year = it },
                label = { Text("Year") },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = defaultFieldColors(),
                modifier = Modifier.weight(1f)
            )
        }

        OutlinedTextField(
            value = publisher,
            onValueChange = { publisher = it },
            label = { Text("Publisher") },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = defaultFieldColors(),
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = dateCompleted,
            onValueChange = { dateCompleted = it },
            label = { Text("Date Completed") },
            leadingIcon = {
                Icon(Icons.Filled.CalendarMonth, contentDescription = null, tint = Lavender300, modifier = Modifier.size(18.dp))
            },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = defaultFieldColors(),
            modifier = Modifier.fillMaxWidth()
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(Midnight900.copy(alpha = 0.5f))
                .border(1.dp, Midnight700.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
                .padding(12.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "Your Rating",
                    fontSize = 12.5.sp,
                    fontWeight = FontWeight.Medium,
                    color = Lavender300
                )
                InteractiveStarRatingSelector(
                    rating = rating,
                    onRatingChanged = { rating = it }
                )
            }
        }

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Description") },
            maxLines = 4,
            shape = RoundedCornerShape(12.dp),
            colors = defaultFieldColors(),
            modifier = Modifier.fillMaxWidth()
        )

        if (errorMessage != null) {
            Text(
                text = errorMessage!!,
                color = Red400,
                fontSize = 12.sp
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                if (title.isBlank()) {
                    errorMessage = "Book title is required."
                    return@Button
                }
                val newBook = Book(
                    id = UUID.randomUUID().toString(),
                    title = title.trim(),
                    author = author.trim().ifBlank { null },
                    cover = coverUrl.trim().ifBlank { null },
                    isbn = isbn.trim().ifBlank { null },
                    publicationYear = year.trim().toIntOrNull(),
                    publisher = publisher.trim().ifBlank { null },
                    description = description.trim().ifBlank { null },
                    rating = rating,
                    dateCompleted = dateCompleted.trim().ifBlank { null },
                    catalogSource = "manual"
                )
                onSave(newBook)
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = Lavender500,
                contentColor = Cream50
            ),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
                .testTag("manual_save_button")
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Save Book", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun defaultFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedContainerColor = Midnight900.copy(alpha = 0.6f),
    unfocusedContainerColor = Midnight900.copy(alpha = 0.4f),
    focusedBorderColor = Lavender300.copy(alpha = 0.6f),
    unfocusedBorderColor = Midnight600.copy(alpha = 0.5f),
    focusedTextColor = Cream50,
    unfocusedTextColor = Cream50,
    focusedLabelColor = Lavender300,
    unfocusedLabelColor = Midnight200.copy(alpha = 0.7f)
)
