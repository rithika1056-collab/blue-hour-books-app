package com.example.bluehourbooks.ui

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.CollectionsBookmark
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ViewAgenda
import androidx.compose.material.icons.outlined.CollectionsBookmark
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.ViewAgenda
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.bluehourbooks.ui.screens.AddBookBottomSheet
import com.example.bluehourbooks.ui.screens.BookDetailsBottomSheet
import com.example.bluehourbooks.ui.screens.BookshelfScreen
import com.example.bluehourbooks.ui.screens.HomeScreen
import com.example.bluehourbooks.ui.screens.LibraryScreen
import com.example.bluehourbooks.ui.screens.ProfileScreen
import com.example.bluehourbooks.ui.theme.Cream50
import com.example.bluehourbooks.ui.theme.Lavender300
import com.example.bluehourbooks.ui.theme.Lavender500
import com.example.bluehourbooks.ui.theme.Midnight200
import com.example.bluehourbooks.ui.theme.Midnight700
import com.example.bluehourbooks.ui.theme.Midnight800
import com.example.bluehourbooks.ui.theme.Midnight900
import com.example.bluehourbooks.ui.theme.Midnight950
import com.example.bluehourbooks.ui.viewmodel.BookViewModel

enum class NavigationTab(
    val route: String,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    HOME("home", "Home", Icons.Filled.Home, Icons.Outlined.Home),
    LIBRARY("library", "Library", Icons.Filled.CollectionsBookmark, Icons.Outlined.CollectionsBookmark),
    BOOKSHELF("bookshelf", "Shelf", Icons.Filled.ViewAgenda, Icons.Outlined.ViewAgenda),
    PROFILE("profile", "Profile", Icons.Filled.Person, Icons.Outlined.Person)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BlueHourApp(
    viewModel: BookViewModel = viewModel()
) {
    val allBooks by viewModel.allBooks.collectAsState()
    val allDecorations by viewModel.allDecorations.collectAsState()
    val filteredBooks by viewModel.filteredBooks.collectAsState()
    val searchFilter by viewModel.searchFilter.collectAsState()
    val sortOption by viewModel.sortOption.collectAsState()
    val minRatingFilter by viewModel.minRatingFilter.collectAsState()
    val onlineSearchState by viewModel.onlineSearchState.collectAsState()
    val detailBook by viewModel.detailBook.collectAsState()

    var currentTab by remember { mutableStateOf(NavigationTab.HOME) }
    var isAddBookOpen by remember { mutableStateOf(false) }

    // Seed on first run if empty
    LaunchedEffect(Unit) {
        if (allBooks.isEmpty()) {
            viewModel.seedSampleData()
        }
    }

    Scaffold(
        contentWindowInsets = WindowInsets.navigationBars,
        containerColor = Midnight950,
        bottomBar = {
            BlueHourNavigationBar(
                currentTab = currentTab,
                onTabSelected = { currentTab = it }
            )
        },
        floatingActionButton = {
            if (currentTab != NavigationTab.PROFILE && currentTab != NavigationTab.BOOKSHELF) {
                FloatingActionButton(
                    onClick = { isAddBookOpen = true },
                    containerColor = Lavender500,
                    contentColor = Cream50,
                    shape = CircleShape,
                    modifier = Modifier
                        .testTag("global_add_book_fab")
                        .shadow(8.dp, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "Add Book",
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Midnight950)
                .padding(paddingValues)
        ) {
            Crossfade(
                targetState = currentTab,
                label = "tab_crossfade"
            ) { tab ->
                when (tab) {
                    NavigationTab.HOME -> {
                        HomeScreen(
                            books = allBooks,
                            onAddBook = { isAddBookOpen = true },
                            onViewAll = { currentTab = NavigationTab.LIBRARY },
                            onOpenBook = { viewModel.openBookDetails(it) },
                            onSeedSampleData = { viewModel.seedSampleData() }
                        )
                    }
                    NavigationTab.LIBRARY -> {
                        LibraryScreen(
                            books = filteredBooks,
                            totalBooksCount = allBooks.size,
                            searchQuery = searchFilter,
                            onSearchQueryChange = { viewModel.setSearchFilter(it) },
                            sortOption = sortOption,
                            onSortOptionChange = { viewModel.setSortOption(it) },
                            minRatingFilter = minRatingFilter,
                            onMinRatingFilterChange = { viewModel.setMinRatingFilter(it) },
                            onOpenBook = { viewModel.openBookDetails(it) },
                            onAddBook = { isAddBookOpen = true }
                        )
                    }
                    NavigationTab.BOOKSHELF -> {
                        BookshelfScreen(
                            books = allBooks,
                            decorations = allDecorations,
                            onOpenBook = { viewModel.openBookDetails(it) },
                            onAddBook = { isAddBookOpen = true },
                            onAddDecoration = { viewModel.addDecoration(it) },
                            onUpdateDecoration = { viewModel.updateDecoration(it) },
                            onDeleteDecoration = { viewModel.deleteDecoration(it) }
                        )
                    }
                    NavigationTab.PROFILE -> {
                        ProfileScreen(
                            books = allBooks,
                            onSeedSampleData = { viewModel.seedSampleData() },
                            onClearAllData = { viewModel.clearAllData() }
                        )
                    }
                }
            }
        }

        // Add Book Bottom Sheet
        if (isAddBookOpen) {
            AddBookBottomSheet(
                onDismiss = {
                    isAddBookOpen = false
                    viewModel.clearOnlineSearch()
                },
                searchState = onlineSearchState,
                onSearch = { viewModel.searchOnline(it) },
                onLoadMore = { viewModel.loadMoreOnline() },
                onAddBook = { newBook ->
                    viewModel.addBook(newBook)
                }
            )
        }

        // Book Details / Edit / Delete Bottom Sheet
        if (detailBook != null) {
            BookDetailsBottomSheet(
                book = detailBook!!,
                onDismiss = { viewModel.closeBookDetails() },
                onUpdateBook = { viewModel.updateBook(it) },
                onDeleteBook = { viewModel.deleteBook(it) }
            )
        }
    }
}

@Composable
private fun BlueHourNavigationBar(
    currentTab: NavigationTab,
    onTabSelected: (NavigationTab) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Midnight950)
            .padding(horizontal = 16.dp, vertical = 6.dp)
    ) {
        NavigationBar(
            containerColor = Midnight900.copy(alpha = 0.95f),
            tonalElevation = 0.dp,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .border(1.dp, Midnight700.copy(alpha = 0.6f), RoundedCornerShape(24.dp))
                .shadow(12.dp, RoundedCornerShape(24.dp))
        ) {
            NavigationTab.entries.forEach { tab ->
                val selected = currentTab == tab
                NavigationBarItem(
                    selected = selected,
                    onClick = { onTabSelected(tab) },
                    icon = {
                        Icon(
                            imageVector = if (selected) tab.selectedIcon else tab.unselectedIcon,
                            contentDescription = tab.label,
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    label = {
                        Text(
                            text = tab.label,
                            fontSize = 11.sp
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Cream50,
                        selectedTextColor = Lavender300,
                        indicatorColor = Lavender500,
                        unselectedIconColor = Midnight200.copy(alpha = 0.5f),
                        unselectedTextColor = Midnight200.copy(alpha = 0.5f)
                    ),
                    modifier = Modifier.testTag("nav_tab_${tab.route}")
                )
            }
        }
    }
}
