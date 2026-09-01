package com.example.bluehourbooks.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bluehourbooks.data.model.Book
import com.example.bluehourbooks.ui.components.BookCoverImage
import com.example.bluehourbooks.ui.components.InteractiveStarRatingSelector
import com.example.bluehourbooks.ui.components.StarRating
import com.example.bluehourbooks.ui.components.formatDisplayDate
import com.example.bluehourbooks.ui.components.formatRatingNumber
import com.example.bluehourbooks.ui.theme.Cream100
import com.example.bluehourbooks.ui.theme.Cream50
import com.example.bluehourbooks.ui.theme.Gold400
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
import com.example.bluehourbooks.ui.theme.Red500

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookDetailsBottomSheet(
    book: Book,
    onDismiss: () -> Unit,
    onUpdateBook: (Book) -> Unit,
    onDeleteBook: (String) -> Unit,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
) {
    var mode by remember { mutableStateOf("view") } // "view" | "edit" | "delete_confirm"

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
            when (mode) {
                "view" -> {
                    BookDetailsView(
                        book = book,
                        onEdit = { mode = "edit" },
                        onDeleteClick = { mode = "delete_confirm" }
                    )
                }
                "edit" -> {
                    BookEditForm(
                        book = book,
                        onCancel = { mode = "view" },
                        onSave = { updated ->
                            onUpdateBook(updated)
                            mode = "view"
                        }
                    )
                }
                "delete_confirm" -> {
                    BookDeleteConfirmView(
                        book = book,
                        onCancel = { mode = "view" },
                        onConfirmDelete = {
                            onDeleteBook(book.id)
                            onDismiss()
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun BookDetailsView(
    book: Book,
    onEdit: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(scrollState)
    ) {
        // Book Header Overview
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .width(118.dp)
                    .aspectRatio(0.67f)
                    .shadow(10.dp, RoundedCornerShape(16.dp))
                    .clip(RoundedCornerShape(16.dp))
                    .background(Midnight900)
            ) {
                BookCoverImage(coverUrl = book.cover, title = book.title)
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Text(
                    text = book.title,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 21.sp,
                    lineHeight = 25.sp,
                    color = Cream50
                )
                if (!book.author.isNullOrBlank()) {
                    Text(
                        text = "by ${book.author}",
                        fontSize = 13.5.sp,
                        color = Lavender200.copy(alpha = 0.9f)
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    StarRating(rating = book.rating, starSize = 18.dp)
                    if (book.rating > 0) {
                        Text(
                            text = "${formatRatingNumber(book.rating)} / 5",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Gold400
                        )
                    } else {
                        Text(
                            text = "Unrated",
                            fontSize = 12.sp,
                            color = Midnight200.copy(alpha = 0.6f)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Metadata grid
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(Midnight900.copy(alpha = 0.7f))
                .border(1.dp, Midnight700.copy(alpha = 0.6f), RoundedCornerShape(18.dp))
                .padding(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                if (!book.dateCompleted.isNullOrBlank()) {
                    DetailRow("Completed", formatDisplayDate(book.dateCompleted))
                }
                book.publicationYear?.let {
                    DetailRow("Published", it.toString())
                }
                book.publisher?.let {
                    DetailRow("Publisher", it)
                }
                book.isbn?.let {
                    DetailRow("ISBN", it)
                }
                DetailRow("Source", formatCatalogSource(book.catalogSource))
            }
        }

        // Categories
        if (!book.categories.isNullOrBlank()) {
            Spacer(modifier = Modifier.height(12.dp))
            val cats = book.categories.split(",").map { it.trim() }.filter { it.isNotEmpty() }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                cats.take(5).forEach { cat ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(Lavender500.copy(alpha = 0.15f))
                            .border(1.dp, Lavender400.copy(alpha = 0.3f), RoundedCornerShape(50))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(cat, fontSize = 11.5.sp, color = Lavender200)
                    }
                }
            }
        }

        // Description
        if (!book.description.isNullOrBlank()) {
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = "About this book",
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                color = Cream50
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = book.description,
                fontSize = 13.sp,
                lineHeight = 20.sp,
                color = Midnight100.copy(alpha = 0.8f)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Actions: Edit & Delete
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = onEdit,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Midnight800,
                    contentColor = Cream50
                ),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .weight(1f)
                    .border(1.dp, Midnight600.copy(alpha = 0.6f), RoundedCornerShape(14.dp))
                    .testTag("edit_book_button")
            ) {
                Icon(Icons.Filled.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Edit Book", fontSize = 13.5.sp, fontWeight = FontWeight.Medium)
            }

            Button(
                onClick = onDeleteClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Red500.copy(alpha = 0.15f),
                    contentColor = Red400
                ),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .weight(1f)
                    .border(1.dp, Red400.copy(alpha = 0.35f), RoundedCornerShape(14.dp))
                    .testTag("delete_book_button")
            ) {
                Icon(Icons.Filled.DeleteOutline, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Delete", fontSize = 13.5.sp, fontWeight = FontWeight.Medium)
            }
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label.uppercase(),
            fontSize = 10.5.sp,
            letterSpacing = 0.8.sp,
            fontWeight = FontWeight.Medium,
            color = Lavender300.copy(alpha = 0.7f)
        )
        Text(
            text = value,
            fontSize = 13.sp,
            color = Cream100.copy(alpha = 0.95f)
        )
    }
}

@Composable
private fun BookEditForm(
    book: Book,
    onCancel: () -> Unit,
    onSave: (Book) -> Unit
) {
    var title by remember { mutableStateOf(book.title) }
    var author by remember { mutableStateOf(book.author ?: "") }
    var coverUrl by remember { mutableStateOf(book.cover ?: "") }
    var isbn by remember { mutableStateOf(book.isbn ?: "") }
    var year by remember { mutableStateOf(book.publicationYear?.toString() ?: "") }
    var publisher by remember { mutableStateOf(book.publisher ?: "") }
    var description by remember { mutableStateOf(book.description ?: "") }
    var rating by remember { mutableDoubleStateOf(book.rating) }
    var dateCompleted by remember { mutableStateOf(book.dateCompleted ?: "") }

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Edit Book",
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
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = author,
            onValueChange = { author = it },
            label = { Text("Author") },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = defaultFieldColors(),
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = coverUrl,
            onValueChange = { coverUrl = it },
            label = { Text("Cover URL") },
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
            label = { Text("Date Completed (YYYY-MM-DD)") },
            leadingIcon = {
                Icon(Icons.Filled.CalendarMonth, contentDescription = null, tint = Lavender300, modifier = Modifier.size(18.dp))
            },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = defaultFieldColors(),
            modifier = Modifier.fillMaxWidth()
        )

        // Rating selector with half-star chips
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
                    text = "Rating",
                    fontSize = 12.sp,
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

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            OutlinedButton(
                onClick = onCancel,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                Text("Cancel", color = Midnight100)
            }

            Button(
                onClick = {
                    val updated = book.copy(
                        title = title.trim().ifBlank { book.title },
                        author = author.trim().ifBlank { null },
                        cover = coverUrl.trim().ifBlank { null },
                        isbn = isbn.trim().ifBlank { null },
                        publicationYear = year.trim().toIntOrNull(),
                        publisher = publisher.trim().ifBlank { null },
                        description = description.trim().ifBlank { null },
                        rating = rating,
                        dateCompleted = dateCompleted.trim().ifBlank { null }
                    )
                    onSave(updated)
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Lavender500,
                    contentColor = Cream50
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.weight(1.3f)
            ) {
                Text("Save Changes", fontSize = 13.5.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
private fun BookDeleteConfirmView(
    book: Book,
    onCancel: () -> Unit,
    onConfirmDelete: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(Red500.copy(alpha = 0.15f))
                .border(1.dp, Red400.copy(alpha = 0.35f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.WarningAmber,
                contentDescription = null,
                tint = Red400,
                modifier = Modifier.size(28.dp)
            )
        }

        Text(
            text = "Delete this book?",
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.SemiBold,
            fontSize = 22.sp,
            color = Cream50
        )

        Text(
            text = "“${book.title}” will be removed from your library. This cannot be undone.",
            fontSize = 13.5.sp,
            color = Midnight100.copy(alpha = 0.75f),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(6.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = onCancel,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                Text("Keep Book", color = Midnight100)
            }

            Button(
                onClick = onConfirmDelete,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Red500,
                    contentColor = Cream50
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.weight(1.2f)
            ) {
                Text("Delete", fontSize = 13.5.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

private fun formatCatalogSource(source: String?): String {
    return when (source) {
        "google_books" -> "Google Books"
        "open_library" -> "Open Library"
        "manual" -> "Manual entry"
        else -> "Personal Record"
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
