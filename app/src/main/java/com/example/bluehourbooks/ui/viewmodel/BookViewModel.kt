package com.example.bluehourbooks.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.bluehourbooks.data.local.AppDatabase
import com.example.bluehourbooks.data.model.Book
import com.example.bluehourbooks.data.model.BookSearchResult
import com.example.bluehourbooks.data.model.ShelfDecoration
import com.example.bluehourbooks.data.repository.BookRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class SortOption(val label: String) {
    DATE_COMPLETED("Date Completed"),
    TITLE("Title"),
    AUTHOR("Author"),
    RATING("Rating")
}

data class OnlineSearchUiState(
    val query: String = "",
    val results: List<BookSearchResult> = emptyList(),
    val isSearching: Boolean = false,
    val isLoadingMore: Boolean = false,
    val searchError: String? = null,
    val hasSearched: Boolean = false,
    val hasMore: Boolean = false,
    val nextStart: Int = 0
)

class BookViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    private val repository = BookRepository(database.bookDao(), database.decorationDao())

    val allBooks: StateFlow<List<Book>> = repository.allBooks
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val allDecorations: StateFlow<List<ShelfDecoration>> = repository.allDecorations
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _searchFilter = MutableStateFlow("")
    val searchFilter: StateFlow<String> = _searchFilter.asStateFlow()

    private val _sortOption = MutableStateFlow(SortOption.DATE_COMPLETED)
    val sortOption: StateFlow<SortOption> = _sortOption.asStateFlow()

    private val _minRatingFilter = MutableStateFlow(0.0)
    val minRatingFilter: StateFlow<Double> = _minRatingFilter.asStateFlow()

    val filteredBooks: StateFlow<List<Book>> = combine(allBooks, _searchFilter, _sortOption, _minRatingFilter) { books, query, sort, minRating ->
        val q = query.trim().lowercase()
        val textFiltered = if (q.isEmpty()) {
            books
        } else {
            books.filter { b ->
                b.title.lowercase().contains(q) ||
                (b.author?.lowercase()?.contains(q) == true) ||
                (b.isbn?.lowercase()?.contains(q) == true)
            }
        }

        val ratingFiltered = if (minRating > 0.0) {
            textFiltered.filter { it.rating >= minRating }
        } else {
            textFiltered
        }

        when (sort) {
            SortOption.TITLE -> ratingFiltered.sortedBy { it.title.lowercase() }
            SortOption.AUTHOR -> ratingFiltered.sortedBy { (it.author ?: "").lowercase() }
            SortOption.RATING -> ratingFiltered.sortedByDescending { it.rating }
            SortOption.DATE_COMPLETED -> ratingFiltered.sortedWith(
                compareByDescending<Book> { it.dateCompleted ?: "" }
                    .thenByDescending { it.createdAt }
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    private val _onlineSearchState = MutableStateFlow(OnlineSearchUiState())
    val onlineSearchState: StateFlow<OnlineSearchUiState> = _onlineSearchState.asStateFlow()

    private val _detailBook = MutableStateFlow<Book?>(null)
    val detailBook: StateFlow<Book?> = _detailBook.asStateFlow()

    private val _confirmingBookResult = MutableStateFlow<BookSearchResult?>(null)
    val confirmingBookResult: StateFlow<BookSearchResult?> = _confirmingBookResult.asStateFlow()

    init {
        // Auto-seed sample books on first launch if empty after short delay
        viewModelScope.launch {
            allBooks.collect { list ->
                // Initial check done through UI or user action
            }
        }
    }

    fun setSearchFilter(query: String) {
        _searchFilter.value = query
    }

    fun setSortOption(option: SortOption) {
        _sortOption.value = option
    }

    fun setMinRatingFilter(minRating: Double) {
        _minRatingFilter.value = minRating
    }

    fun openBookDetails(book: Book) {
        _detailBook.value = book
    }

    fun closeBookDetails() {
        _detailBook.value = null
    }

    fun selectSearchResultForConfirm(result: BookSearchResult?) {
        _confirmingBookResult.value = result
    }

    fun searchOnline(query: String) {
        val trimmed = query.trim()
        if (trimmed.isEmpty()) return

        _onlineSearchState.value = _onlineSearchState.value.copy(
            query = trimmed,
            isSearching = true,
            searchError = null,
            hasSearched = true,
            results = emptyList(),
            hasMore = false,
            nextStart = 0
        )

        viewModelScope.launch {
            try {
                val page = repository.searchOnline(trimmed, 0)
                _onlineSearchState.value = _onlineSearchState.value.copy(
                    results = page.results,
                    isSearching = false,
                    hasMore = page.hasMore,
                    nextStart = page.nextStart
                )
            } catch (e: Exception) {
                _onlineSearchState.value = _onlineSearchState.value.copy(
                    isSearching = false,
                    searchError = e.message ?: "Search failed. Please try again."
                )
            }
        }
    }

    fun loadMoreOnline() {
        val state = _onlineSearchState.value
        if (state.query.isEmpty() || state.isLoadingMore || !state.hasMore) return

        _onlineSearchState.value = state.copy(isLoadingMore = true, searchError = null)

        viewModelScope.launch {
            try {
                val page = repository.searchOnline(state.query, state.nextStart)
                val newResults = (state.results + page.results).distinctBy { it.isbn ?: "${it.title}|${it.author}" }
                _onlineSearchState.value = state.copy(
                    results = newResults,
                    isLoadingMore = false,
                    hasMore = page.hasMore,
                    nextStart = page.nextStart
                )
            } catch (e: Exception) {
                _onlineSearchState.value = state.copy(
                    isLoadingMore = false,
                    searchError = "Could not load more results."
                )
            }
        }
    }

    fun clearOnlineSearch() {
        _onlineSearchState.value = OnlineSearchUiState()
    }

    fun addBook(book: Book, onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            repository.insertBook(book)
            onComplete()
        }
    }

    fun updateBook(book: Book, onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            val updated = book.copy(updatedAt = System.currentTimeMillis())
            repository.updateBook(updated)
            _detailBook.value = updated
            onComplete()
        }
    }

    fun deleteBook(id: String, onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            repository.deleteBookById(id)
            if (_detailBook.value?.id == id) {
                _detailBook.value = null
            }
            onComplete()
        }
    }

    fun addDecoration(decoration: ShelfDecoration, onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            repository.insertDecoration(decoration)
            onComplete()
        }
    }

    fun updateDecoration(decoration: ShelfDecoration, onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            repository.updateDecoration(decoration)
            onComplete()
        }
    }

    fun deleteDecoration(id: String, onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            repository.deleteDecorationById(id)
            onComplete()
        }
    }

    fun seedSampleData() {
        viewModelScope.launch {
            repository.seedSampleBooks()
        }
    }

    fun clearAllData() {
        viewModelScope.launch {
            repository.clearAll()
            _detailBook.value = null
        }
    }

    companion object {
        fun todayIso(): String {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            return sdf.format(Date())
        }
    }
}
