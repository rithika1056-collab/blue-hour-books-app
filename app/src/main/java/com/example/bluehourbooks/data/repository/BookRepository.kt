package com.example.bluehourbooks.data.repository

import com.example.bluehourbooks.data.local.BookDao
import com.example.bluehourbooks.data.local.DecorationDao
import com.example.bluehourbooks.data.model.Book
import com.example.bluehourbooks.data.model.ShelfDecoration
import com.example.bluehourbooks.data.remote.BookSearchService
import com.example.bluehourbooks.data.remote.SearchPage
import kotlinx.coroutines.flow.Flow
import java.util.UUID

class BookRepository(
    private val bookDao: BookDao,
    private val decorationDao: DecorationDao,
    private val searchService: BookSearchService = BookSearchService()
) {
    val allBooks: Flow<List<Book>> = bookDao.getAllBooks()
    val allDecorations: Flow<List<ShelfDecoration>> = decorationDao.getAllDecorations()

    suspend fun getBookById(id: String): Book? {
        return bookDao.getBookById(id)
    }

    suspend fun insertBook(book: Book) {
        bookDao.insertBook(book)
    }

    suspend fun updateBook(book: Book) {
        bookDao.updateBook(book)
    }

    suspend fun deleteBookById(id: String) {
        bookDao.deleteBookById(id)
    }

    suspend fun insertDecoration(decoration: ShelfDecoration) {
        decorationDao.insertDecoration(decoration)
    }

    suspend fun updateDecoration(decoration: ShelfDecoration) {
        decorationDao.updateDecoration(decoration)
    }

    suspend fun deleteDecorationById(id: String) {
        decorationDao.deleteDecorationById(id)
    }

    suspend fun clearAll() {
        bookDao.clearAll()
        decorationDao.clearAll()
    }

    suspend fun searchOnline(query: String, start: Int = 0): SearchPage {
        return searchService.searchBooks(query, start)
    }

    suspend fun seedSampleBooks() {
        val sampleList = listOf(
            Book(
                id = UUID.randomUUID().toString(),
                title = "The Midnight Library",
                author = "Matt Haig",
                cover = "https://covers.openlibrary.org/b/id/10389354-L.jpg",
                isbn = "9780525559474",
                publicationYear = 2020,
                publisher = "Viking",
                description = "Between life and death there is a library, and within that library, the shelves go on forever. Every book provides a chance to try another life you could have lived.",
                rating = 5.0,
                dateCompleted = "2026-08-15",
                catalogSource = "open_library",
                categories = "Fiction, Fantasy, Magical Realism"
            ),
            Book(
                id = UUID.randomUUID().toString(),
                title = "The Starless Sea",
                author = "Erin Morgenstern",
                cover = "https://covers.openlibrary.org/b/id/9323145-L.jpg",
                isbn = "9780385541213",
                publicationYear = 2019,
                publisher = "Doubleday",
                description = "A timeless love story set in a secret underground world—a place of lost cities and seas, lovers who pass notes, and stories whispered in shadows.",
                rating = 4.5,
                dateCompleted = "2026-07-28",
                catalogSource = "open_library",
                categories = "Fantasy, Mystery, Adventure"
            ),
            Book(
                id = UUID.randomUUID().toString(),
                title = "Klara and the Sun",
                author = "Kazuo Ishiguro",
                cover = "https://covers.openlibrary.org/b/id/10543292-L.jpg",
                isbn = "9780593318171",
                publicationYear = 2021,
                publisher = "Knopf",
                description = "An Artificial Friend with outstanding observational qualities watches the behavior of those who come in to browse, and of those who pass in the street outside.",
                rating = 4.0,
                dateCompleted = "2026-06-12",
                catalogSource = "open_library",
                categories = "Science Fiction, Dystopian"
            ),
            Book(
                id = UUID.randomUUID().toString(),
                title = "Before the Coffee Gets Cold",
                author = "Toshikazu Kawaguchi",
                cover = "https://covers.openlibrary.org/b/id/10398687-L.jpg",
                isbn = "9781335430991",
                publicationYear = 2019,
                publisher = "Harlequin",
                description = "In a small back alley in Tokyo, there is a cafe which has been serving carefully brewed coffee for more than one hundred years. This cafe offers its customers the unique experience of traveling back in time.",
                rating = 5.0,
                dateCompleted = "2026-05-04",
                catalogSource = "open_library",
                categories = "Magical Realism, Japanese Literature"
            )
        )
        bookDao.insertBooks(sampleList)

        // Seed cozy sample decorations
        val sampleDecorations = listOf(
            ShelfDecoration(
                id = UUID.randomUUID().toString(),
                shelfIndex = 0,
                type = "PLANT",
                styleKey = "potted_succulent",
                title = "Cozy Succulent",
                position = 0
            ),
            ShelfDecoration(
                id = UUID.randomUUID().toString(),
                shelfIndex = 0,
                type = "FRAME",
                styleKey = "starry_twilight",
                title = "Starry Twilight",
                position = 2
            ),
            ShelfDecoration(
                id = UUID.randomUUID().toString(),
                shelfIndex = 1,
                type = "QUOTE",
                styleKey = "quote_bluehour",
                title = "Blue Hour Tale",
                subtitle = "In the quiet blue hour, stories come alive.",
                position = 1
            )
        )
        decorationDao.insertDecorations(sampleDecorations)
    }
}
