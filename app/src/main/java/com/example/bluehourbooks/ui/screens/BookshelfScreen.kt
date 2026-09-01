package com.example.bluehourbooks.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.LocalFlorist
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.ViewWeek
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.bluehourbooks.data.model.Book
import com.example.bluehourbooks.data.model.ShelfDecoration
import com.example.bluehourbooks.ui.components.BookCoverImage
import com.example.bluehourbooks.ui.components.ShelfDecorationItem
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
import kotlin.math.abs

private val SPINE_GRADIENTS = listOf(
    listOf(Color(0xFF4C1D95), Color(0xFF2E1065)), // Royal Violet
    listOf(Color(0xFF1E3A8A), Color(0xFF172554)), // Deep Oxford Navy
    listOf(Color(0xFF78350F), Color(0xFF451A03)), // Rich Leather Auburn
    listOf(Color(0xFF065F46), Color(0xFF022C22)), // Forest Green
    listOf(Color(0xFF701A75), Color(0xFF4A044E)), // Twilight Plum
    listOf(Color(0xFFB45309), Color(0xFF78350F)), // Antique Amber
    listOf(Color(0xFF312E81), Color(0xFF1E1B4B)), // Midnight Indigo
    listOf(Color(0xFF831843), Color(0xFF500724))  // Crimson Velvet
)

private val ROMAN_NUMERALS = listOf("I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX", "X")

private fun stringHash(str: String): Int {
    var h = 0
    for (ch in str) {
        h = (31 * h + ch.code)
    }
    return abs(h)
}

@Composable
fun BookshelfScreen(
    books: List<Book>,
    decorations: List<ShelfDecoration> = emptyList(),
    onOpenBook: (Book) -> Unit,
    onAddBook: () -> Unit,
    onAddDecoration: (ShelfDecoration) -> Unit = {},
    onUpdateDecoration: (ShelfDecoration) -> Unit = {},
    onDeleteDecoration: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var displayMode by remember { mutableStateOf("spines") } // "spines" | "covers"
    var selectedShelfFilter by remember { mutableIntStateOf(-1) } // -1 = All Shelves, 0 = Shelf 1, 1 = Shelf 2...
    
    // Bottom Sheet states
    var isAddToShelfOpen by remember { mutableStateOf(false) }
    var selectedDecorationTypeForAdd by remember { mutableStateOf<String?>(null) }
    var editingDecoration by remember { mutableStateOf<ShelfDecoration?>(null) }

    val sortedBooks = remember(books) {
        books.sortedBy { it.title.lowercase() }
    }

    val perShelf = if (displayMode == "spines") 7 else 4
    val calculatedShelves = remember(sortedBooks, displayMode) {
        if (sortedBooks.isEmpty()) {
            listOf(emptyList<Book>())
        } else {
            sortedBooks.chunked(perShelf)
        }
    }

    val totalShelvesCount = maxOf(calculatedShelves.size, 2)

    val activeShelves = remember(calculatedShelves, selectedShelfFilter) {
        if (selectedShelfFilter == -1) {
            calculatedShelves
        } else {
            val list = calculatedShelves.getOrNull(selectedShelfFilter) ?: emptyList()
            listOf(list)
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            contentPadding = PaddingValues(bottom = 100.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            // Header Section
            item {
                BookshelfHeader(
                    totalBooksCount = books.size,
                    displayMode = displayMode,
                    onDisplayModeChange = { displayMode = it }
                )
            }

            // Shelf Selector Pills
            item {
                ShelfSelectorPills(
                    totalShelves = totalShelvesCount,
                    selectedShelf = selectedShelfFilter,
                    onSelectShelf = { selectedShelfFilter = it }
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Wooden Shelves Area
            itemsIndexed(activeShelves) { idx, shelfBooks ->
                val realShelfIndex = if (selectedShelfFilter == -1) idx else selectedShelfFilter
                val shelfDecorations = decorations.filter { it.shelfIndex == realShelfIndex }

                RealisticBookshelfSection(
                    shelfIndex = realShelfIndex,
                    shelfBooks = shelfBooks,
                    shelfDecorations = shelfDecorations,
                    displayMode = displayMode,
                    onOpenBook = onOpenBook,
                    onOpenDecoration = { editingDecoration = it },
                    onAddBook = onAddBook
                )

                Spacer(modifier = Modifier.height(28.dp))
            }

            // Empty State Card when ZERO books
            if (books.isEmpty()) {
                item {
                    EmptyShelfCard(onAddBook = onAddBook)
                    Spacer(modifier = Modifier.height(28.dp))
                }
            }

            // Shelf Overview Section
            item {
                ShelfOverviewSection(
                    books = books,
                    decorations = decorations
                )
                Spacer(modifier = Modifier.height(28.dp))
            }

            // Recently Added Section (using actual user books)
            if (books.isNotEmpty()) {
                item {
                    RecentlyAddedSection(
                        books = books.sortedByDescending { it.createdAt },
                        onOpenBook = onOpenBook
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }

        // Floating Action Button on Bottom Right
        FloatingActionButton(
            onClick = { isAddToShelfOpen = true },
            containerColor = Lavender500,
            contentColor = Cream50,
            shape = CircleShape,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 16.dp, end = 20.dp)
                .shadow(10.dp, CircleShape)
                .testTag("shelf_fab_add")
        ) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = "Add to Shelf",
                modifier = Modifier.size(24.dp)
            )
        }

        // Add to Shelf Modal Sheet
        if (isAddToShelfOpen) {
            AddToShelfBottomSheet(
                onDismiss = { isAddToShelfOpen = false },
                onSelectAddBooks = {
                    isAddToShelfOpen = false
                    onAddBook()
                },
                onSelectDecorationType = { type ->
                    isAddToShelfOpen = false
                    selectedDecorationTypeForAdd = type
                }
            )
        }

        // Decoration Style Picker Sheet
        if (selectedDecorationTypeForAdd != null) {
            DecorationPickerBottomSheet(
                decorationType = selectedDecorationTypeForAdd!!,
                totalShelves = totalShelvesCount,
                currentShelfIndex = if (selectedShelfFilter >= 0) selectedShelfFilter else 0,
                onDismiss = { selectedDecorationTypeForAdd = null },
                onAddDecoration = { newDec ->
                    onAddDecoration(newDec)
                    selectedDecorationTypeForAdd = null
                }
            )
        }

        // Edit / Delete Decoration Sheet
        if (editingDecoration != null) {
            EditDecorationBottomSheet(
                decoration = editingDecoration!!,
                totalShelves = totalShelvesCount,
                onDismiss = { editingDecoration = null },
                onUpdate = { updated ->
                    onUpdateDecoration(updated)
                    editingDecoration = null
                },
                onDelete = { id ->
                    onDeleteDecoration(id)
                    editingDecoration = null
                }
            )
        }
    }
}

// MARK: - Header
@Composable
private fun BookshelfHeader(
    totalBooksCount: Int,
    displayMode: String,
    onDisplayModeChange: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "Bookshelf",
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.SemiBold,
                fontSize = 28.sp,
                color = Cream50
            )
            Text(
                text = "Your digital study space",
                fontSize = 13.sp,
                color = Lavender200.copy(alpha = 0.8f)
            )
        }

        // View Mode Switcher (Spines vs Covers)
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(50))
                .background(Midnight900.copy(alpha = 0.8f))
                .border(1.dp, Midnight700.copy(alpha = 0.6f), RoundedCornerShape(50))
                .padding(3.dp),
            horizontalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            val spinesBg by animateColorAsState(
                targetValue = if (displayMode == "spines") Lavender500 else Color.Transparent,
                label = "spines_bg"
            )
            val coversBg by animateColorAsState(
                targetValue = if (displayMode == "covers") Lavender500 else Color.Transparent,
                label = "covers_bg"
            )

            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(spinesBg)
                    .clickable { onDisplayModeChange("spines") }
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.ViewWeek,
                    contentDescription = "Spines View",
                    tint = if (displayMode == "spines") Cream50 else Midnight100.copy(alpha = 0.6f),
                    modifier = Modifier.size(16.dp)
                )
            }

            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(coversBg)
                    .clickable { onDisplayModeChange("covers") }
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.GridView,
                    contentDescription = "Covers View",
                    tint = if (displayMode == "covers") Cream50 else Midnight100.copy(alpha = 0.6f),
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

// MARK: - Shelf Selector
@Composable
private fun ShelfSelectorPills(
    totalShelves: Int,
    selectedShelf: Int,
    onSelectShelf: (Int) -> Unit
) {
    val scrollState = rememberScrollState()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(scrollState)
            .padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // "ALL SHELVES" Pill
        ShelfPill(
            label = "ALL SHELVES",
            isSelected = selectedShelf == -1,
            onClick = { onSelectShelf(-1) }
        )

        for (i in 0 until totalShelves) {
            val numeral = ROMAN_NUMERALS.getOrElse(i) { "${i + 1}" }
            ShelfPill(
                label = "SHELF $numeral",
                isSelected = selectedShelf == i,
                onClick = { onSelectShelf(i) }
            )
        }
    }
}

@Composable
private fun ShelfPill(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val bgCol by animateColorAsState(
        targetValue = if (isSelected) Lavender500 else Midnight900.copy(alpha = 0.6f),
        label = "pill_bg"
    )
    val borderCol by animateColorAsState(
        targetValue = if (isSelected) Lavender300 else Midnight700.copy(alpha = 0.5f),
        label = "pill_border"
    )

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(bgCol)
            .border(1.dp, borderCol, RoundedCornerShape(50))
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 7.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            letterSpacing = 0.8.sp,
            color = if (isSelected) Cream50 else Midnight100
        )
    }
}

// MARK: - Realistic Wooden Bookshelf Section
@Composable
private fun RealisticBookshelfSection(
    shelfIndex: Int,
    shelfBooks: List<Book>,
    shelfDecorations: List<ShelfDecoration>,
    displayMode: String,
    onOpenBook: (Book) -> Unit,
    onOpenDecoration: (ShelfDecoration) -> Unit,
    onAddBook: () -> Unit
) {
    val scrollState = rememberScrollState()
    val romanNumeral = ROMAN_NUMERALS.getOrElse(shelfIndex) { "${shelfIndex + 1}" }

    // Partition decorations by shelf position (0=Start, 1=Middle, 2=End)
    val startDecs = shelfDecorations.filter { it.position == 0 }
    val midDecs = shelfDecorations.filter { it.position == 1 }
    val endDecs = shelfDecorations.filter { it.position == 2 }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        // Shelf Header Plate
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Brass Label
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(
                        Brush.horizontalGradient(
                            listOf(
                                Gold500.copy(alpha = 0.3f),
                                Gold400.copy(alpha = 0.15f),
                                Gold500.copy(alpha = 0.3f)
                            )
                        )
                    )
                    .border(0.75.dp, Gold400.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
                    .padding(horizontal = 10.dp, vertical = 3.dp)
            ) {
                Text(
                    text = "SHELF $romanNumeral",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.4.sp,
                    color = Gold400
                )
            }

            Text(
                text = "${shelfBooks.size} ${if (shelfBooks.size == 1) "book" else "books"}${if (shelfDecorations.isNotEmpty()) " • ${shelfDecorations.size} decor" else ""}",
                fontSize = 11.5.sp,
                color = Lavender200.copy(alpha = 0.6f)
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Books & Decor Shelf Stage with Warm Lighting Ambient Backlight
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Midnight900.copy(alpha = 0.3f),
                            Midnight800.copy(alpha = 0.6f)
                        )
                    )
                )
                .border(
                    width = 1.dp,
                    brush = Brush.verticalGradient(
                        listOf(Midnight700.copy(alpha = 0.4f), Color.Transparent)
                    ),
                    shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
                )
        ) {
            // Ambient Warm Glow Behind Books
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
                    .align(Alignment.BottomCenter)
                    .background(
                        Brush.radialGradient(
                            listOf(
                                Lavender500.copy(alpha = 0.12f),
                                Gold400.copy(alpha = 0.05f),
                                Color.Transparent
                            )
                        )
                    )
            )

            // Items Row (Decorations + Books standing on shelf)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(scrollState)
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.Bottom
            ) {
                // 1. Start Decorations
                startDecs.forEach { dec ->
                    ShelfDecorationItem(
                        decoration = dec,
                        onClick = { onOpenDecoration(dec) },
                        modifier = Modifier.padding(bottom = 2.dp)
                    )
                }

                // 2. Books (or empty space if empty)
                if (shelfBooks.isEmpty() && shelfDecorations.isEmpty()) {
                    // Empty shelf indicator
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(130.dp)
                            .padding(vertical = 20.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Empty Shelf Ledge — Add books or cozy decor",
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Serif,
                            color = Midnight200.copy(alpha = 0.4f)
                        )
                    }
                } else {
                    val halfIndex = shelfBooks.size / 2

                    shelfBooks.forEachIndexed { bIndex, book ->
                        // Insert Middle Decorations in the center of the books
                        if (bIndex == halfIndex) {
                            midDecs.forEach { dec ->
                                ShelfDecorationItem(
                                    decoration = dec,
                                    onClick = { onOpenDecoration(dec) },
                                    modifier = Modifier.padding(bottom = 2.dp)
                                )
                            }
                        }

                        if (displayMode == "spines") {
                            BookSpineItem(book = book, onClick = { onOpenBook(book) })
                        } else {
                            BookCoverItem(book = book, onClick = { onOpenBook(book) })
                        }
                    }

                    // If books count was 0 but midDecs exist
                    if (shelfBooks.isEmpty()) {
                        midDecs.forEach { dec ->
                            ShelfDecorationItem(
                                decoration = dec,
                                onClick = { onOpenDecoration(dec) },
                                modifier = Modifier.padding(bottom = 2.dp)
                            )
                        }
                    }
                }

                // 3. End Decorations
                endDecs.forEach { dec ->
                    ShelfDecorationItem(
                        decoration = dec,
                        onClick = { onOpenDecoration(dec) },
                        modifier = Modifier.padding(bottom = 2.dp)
                    )
                }
            }
        }

        // Realistic Wooden Plank with Bevel, Depth & Wood Shadow
        RealisticWoodPlank()
    }
}

// MARK: - Book Spine Item
@Composable
private fun BookSpineItem(
    book: Book,
    onClick: () -> Unit
) {
    val h = stringHash(book.id)
    val spineHeight = (150 + (h % 40)).dp
    val spineWidth = (40 + (h % 16)).dp
    val gradient = SPINE_GRADIENTS[h % SPINE_GRADIENTS.size]
    val tiltAngle = ((h % 5) - 2f) * 0.6f

    Box(
        modifier = Modifier
            .testTag("shelf_book_${book.id}")
            .width(spineWidth)
            .height(spineHeight)
            .rotate(tiltAngle)
            .shadow(8.dp, RoundedCornerShape(3.dp))
            .clip(RoundedCornerShape(3.dp))
            .background(Brush.verticalGradient(gradient))
            .border(0.5.dp, Color.Black.copy(alpha = 0.4f), RoundedCornerShape(3.dp))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.BottomCenter
    ) {
        // Spine Lighting Curve & Highlights
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.horizontalGradient(
                        listOf(
                            Color.Black.copy(alpha = 0.45f),
                            Color.White.copy(alpha = 0.12f),
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.35f)
                        )
                    )
                )
        )

        // Embossed Spine Art & Title
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 3.dp, vertical = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Golden Embossed Bands
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(modifier = Modifier.fillMaxWidth(0.85f).height(1.5.dp).background(Gold400.copy(alpha = 0.6f)))
                Box(modifier = Modifier.fillMaxWidth(0.6f).height(1.dp).background(Gold400.copy(alpha = 0.4f)))
            }

            // Title & Author Text
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(horizontal = 2.dp)
            ) {
                Text(
                    text = book.title,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 10.sp,
                    lineHeight = 12.sp,
                    color = Cream50,
                    textAlign = TextAlign.Center,
                    maxLines = 4,
                    overflow = TextOverflow.Ellipsis
                )
                if (!book.author.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = book.author,
                        fontSize = 7.5.sp,
                        color = Gold400.copy(alpha = 0.8f),
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            // Bottom Embossed Bands & Rating
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                if (book.rating > 0) {
                    Text(
                        text = "★ ${if (book.rating % 1.0 == 0.0) book.rating.toInt().toString() else book.rating.toString()}",
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        color = Gold400
                    )
                }
                Box(modifier = Modifier.fillMaxWidth(0.6f).height(1.dp).background(Gold400.copy(alpha = 0.4f)))
                Box(modifier = Modifier.fillMaxWidth(0.85f).height(1.5.dp).background(Gold400.copy(alpha = 0.6f)))
            }
        }
    }
}

// MARK: - Book Cover Item
@Composable
private fun BookCoverItem(
    book: Book,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .testTag("shelf_cover_${book.id}")
            .width(112.dp)
            .height(164.dp)
            .shadow(8.dp, RoundedCornerShape(8.dp))
            .clip(RoundedCornerShape(8.dp))
            .background(Midnight900)
            .border(1.dp, Midnight700.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
    ) {
        BookCoverImage(coverUrl = book.cover, title = book.title)

        // Rating Badge
        if (book.rating > 0) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(6.dp)
                    .clip(RoundedCornerShape(50))
                    .background(Midnight950.copy(alpha = 0.85f))
                    .border(0.5.dp, Gold400.copy(alpha = 0.5f), RoundedCornerShape(50))
                .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = "★ ${if (book.rating % 1.0 == 0.0) book.rating.toInt().toString() else book.rating.toString()}",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = Gold400
                )
            }
        }
    }
}

// MARK: - Realistic Wood Plank Shelf Ledge
@Composable
private fun RealisticWoodPlank() {
    Column(modifier = Modifier.fillMaxWidth()) {
        // Shelf Top Surface (Polished Timber)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(14.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color(0xFF8B5A2B), // Rich Warm Timber
                            Color(0xFF5C3A21),
                            Color(0xFF3E2716)
                        )
                    )
                )
                .border(
                    width = 0.75.dp,
                    brush = Brush.horizontalGradient(
                        listOf(Gold400.copy(alpha = 0.4f), Color(0xFF8B5A2B), Gold400.copy(alpha = 0.4f))
                    ),
                    shape = RoundedCornerShape(4.dp)
                )
        ) {
            // Wood Grain Line Accents
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .align(Alignment.TopCenter)
                    .background(Gold400.copy(alpha = 0.4f))
            )
        }

        // Shelf Front Lip & Shadow Depth
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .background(
                    Brush.verticalGradient(
                        listOf(Color.Black.copy(alpha = 0.7f), Color.Transparent)
                    )
                )
        )
    }
}

// MARK: - Empty Shelf Card
@Composable
private fun EmptyShelfCard(onAddBook: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .testTag("empty_shelf_card"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Midnight900.copy(alpha = 0.7f)),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = Brush.linearGradient(
                listOf(Lavender500.copy(alpha = 0.4f), Midnight700.copy(alpha = 0.4f))
            )
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            listOf(Lavender500.copy(alpha = 0.25f), Midnight800)
                        )
                    )
                    .border(1.dp, Lavender300.copy(alpha = 0.3f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.MenuBook,
                    contentDescription = null,
                    tint = Lavender300,
                    modifier = Modifier.size(28.dp)
                )
            }

            Text(
                text = "Your shelf is empty",
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.SemiBold,
                fontSize = 20.sp,
                color = Cream50
            )

            Text(
                text = "Add some books to get started and build your digital library.",
                fontSize = 13.sp,
                lineHeight = 18.sp,
                color = Midnight100.copy(alpha = 0.75f),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(4.dp))

            Button(
                onClick = onAddBook,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Lavender500,
                    contentColor = Cream50
                ),
                shape = CircleShape,
                modifier = Modifier
                    .height(46.dp)
                    .testTag("empty_shelf_add_books_button")
            ) {
                Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "+ Add Books",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

// MARK: - Shelf Overview Section
@Composable
private fun ShelfOverviewSection(
    books: List<Book>,
    decorations: List<ShelfDecoration>
) {
    val avgRating = if (books.isNotEmpty() && books.any { it.rating > 0 }) {
        val rated = books.filter { it.rating > 0 }
        String.format("%.1f", rated.map { it.rating }.average())
    } else {
        "–"
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
    ) {
        Text(
            text = "Shelf Overview",
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.SemiBold,
            fontSize = 18.sp,
            color = Cream50
        )
        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            OverviewStatCard(
                label = "Books on Shelf",
                value = books.size.toString(),
                icon = Icons.Filled.AutoStories,
                color = Lavender300,
                modifier = Modifier.weight(1f)
            )
            OverviewStatCard(
                label = "Decorations",
                value = decorations.size.toString(),
                icon = Icons.Filled.Spa,
                color = Color(0xFF34D399),
                modifier = Modifier.weight(1f)
            )
            OverviewStatCard(
                label = "Avg Rating",
                value = if (avgRating == "–") "–" else "★ $avgRating",
                icon = Icons.Filled.Star,
                color = Gold400,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun OverviewStatCard(
    label: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Midnight900.copy(alpha = 0.7f))
            .border(1.dp, Midnight700.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
            .padding(12.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(18.dp)
            )
            Text(
                text = value,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Cream50
            )
            Text(
                text = label,
                fontSize = 11.sp,
                color = Lavender200.copy(alpha = 0.65f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

// MARK: - Recently Added Horizontal Cards
@Composable
private fun RecentlyAddedSection(
    books: List<Book>,
    onOpenBook: (Book) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Recently Added",
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp,
                color = Cream50
            )
            Text(
                text = "${books.size} total",
                fontSize = 12.sp,
                color = Lavender200.copy(alpha = 0.6f)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        LazyRow(
            contentPadding = PaddingValues(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(books.take(8)) { book ->
                RecentBookCard(book = book, onClick = { onOpenBook(book) })
            }
        }
    }
}

@Composable
private fun RecentBookCard(
    book: Book,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .width(136.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Midnight900.copy(alpha = 0.8f))
            .border(1.dp, Midnight700.copy(alpha = 0.6f), RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(10.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            // Cover Image
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Midnight950)
            ) {
                BookCoverImage(coverUrl = book.cover, title = book.title)
            }

            // Title & Author
            Text(
                text = book.title,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = Cream50,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            if (!book.author.isNullOrBlank()) {
                Text(
                    text = book.author,
                    fontSize = 10.5.sp,
                    color = Lavender200.copy(alpha = 0.7f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Completed date / Rating
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (book.rating > 0) {
                    Text(
                        text = "★ ${if (book.rating % 1.0 == 0.0) book.rating.toInt().toString() else book.rating.toString()}",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Gold400
                    )
                } else {
                    Text(
                        text = "Read",
                        fontSize = 10.sp,
                        color = Midnight100.copy(alpha = 0.5f)
                    )
                }

                if (!book.dateCompleted.isNullOrBlank()) {
                    Text(
                        text = book.dateCompleted.takeLast(5),
                        fontSize = 9.5.sp,
                        color = Lavender200.copy(alpha = 0.5f)
                    )
                }
            }
        }
    }
}
