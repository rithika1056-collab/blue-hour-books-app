package com.example.bluehourbooks.data.remote

import com.example.bluehourbooks.data.model.BookSearchResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import okhttp3.OkHttpClient
import okhttp3.Request
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.util.concurrent.TimeUnit

data class SearchPage(
    val results: List<BookSearchResult>,
    val nextStart: Int,
    val hasMore: Boolean = false
)

class BookSearchService {
    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    companion object {
        private const val GOOGLE_BOOKS_ENDPOINT = "https://www.googleapis.com/books/v1/volumes"
        private const val OPEN_LIBRARY_ENDPOINT = "https://openlibrary.org/search.json"
        private const val PAGE_SIZE = 20
        private const val MAX_PER_SOURCE = 40
    }

    fun isIsbn(query: String): Boolean {
        val cleaned = query.replace("[-\\s]".toRegex(), "")
        return cleaned.matches("^\\d{9,13}$".toRegex()) && (cleaned.length == 10 || cleaned.length == 13)
    }

    suspend fun searchBooks(query: String, start: Int = 0): SearchPage = withContext(Dispatchers.IO) {
        val trimmed = query.trim()
        if (trimmed.isEmpty()) {
            return@withContext SearchPage(emptyList(), 0, false)
        }

        var googleResults = emptyList<BookSearchResult>()
        var googleHasMore = false

        try {
            val g = searchGoogleBooks(trimmed, start)
            googleResults = g.first
            googleHasMore = g.second
        } catch (e: Exception) {
            // Log or ignore to try Open Library
        }

        var olResults = emptyList<BookSearchResult>()
        var olHasMore = false
        try {
            val ol = searchOpenLibrary(trimmed, start)
            olResults = ol.first
            olHasMore = ol.second
        } catch (e: Exception) {
            // Ignore
        }

        if (googleResults.isEmpty() && olResults.isEmpty()) {
            return@withContext SearchPage(emptyList(), start, false)
        }

        val combined = dedupe(googleResults + olResults)
        val pageResults = combined.take(PAGE_SIZE)
        val hasMore = googleHasMore || olHasMore || combined.size > PAGE_SIZE

        SearchPage(
            results = pageResults,
            nextStart = start + pageResults.size,
            hasMore = hasMore
        )
    }

    private fun searchGoogleBooks(query: String, start: Int): Pair<List<BookSearchResult>, Boolean> {
        val queries = buildGoogleQueries(query)
        val maxResults = minOf(PAGE_SIZE, MAX_PER_SOURCE)
        val allItems = mutableListOf<BookSearchResult>()
        var hasMore = false

        for (q in queries) {
            try {
                val encodedQ = URLEncoder.encode(q, StandardCharsets.UTF_8.toString())
                val url = "$GOOGLE_BOOKS_ENDPOINT?q=$encodedQ&startIndex=$start&maxResults=$maxResults"
                val request = Request.Builder().url(url).build()
                val response = client.newCall(request).execute()
                if (!response.isSuccessful) continue
                val responseBody = response.body?.string() ?: continue

                val root = json.parseToJsonElement(responseBody).jsonObject
                val totalItems = root["totalItems"]?.jsonPrimitive?.intOrNull ?: 0
                val items = root["items"]?.jsonArray ?: JsonArray(emptyList())

                for (elem in items) {
                    val itemObj = elem.jsonObject
                    val result = normalizeGoogleItem(itemObj)
                    if (result != null) {
                        allItems.add(result)
                    }
                }

                if (start + items.size < totalItems) {
                    hasMore = true
                }
            } catch (e: Exception) {
                // Continue to next query
            }
        }

        return Pair(allItems, hasMore)
    }

    private fun buildGoogleQueries(query: String): List<String> {
        if (isIsbn(query)) {
            return listOf("isbn:${query.replace("[-\\s]".toRegex(), "")}")
        }
        val escaped = query.replace("\"", "").trim()
        val parts = escaped.split("\\s+".toRegex())
        return if (parts.size >= 2) {
            listOf(query, "inauthor:\"$escaped\"", "intitle:\"$escaped\"")
        } else {
            listOf(query)
        }
    }

    private fun normalizeGoogleItem(item: JsonObject): BookSearchResult? {
        val id = item["id"]?.jsonPrimitive?.contentOrNull ?: return null
        val volumeInfo = item["volumeInfo"]?.jsonObject ?: return null
        val title = volumeInfo["title"]?.jsonPrimitive?.contentOrNull ?: return null

        val authors = volumeInfo["authors"]?.jsonArray?.mapNotNull { it.jsonPrimitive.contentOrNull }
        val author = if (!authors.isNullOrEmpty()) authors.joinToString(", ") else null

        val industryIdentifiers = volumeInfo["industryIdentifiers"]?.jsonArray
        var isbn: String? = null
        if (industryIdentifiers != null) {
            for (idElement in industryIdentifiers) {
                val obj = idElement.jsonObject
                val type = obj["type"]?.jsonPrimitive?.contentOrNull
                val identifier = obj["identifier"]?.jsonPrimitive?.contentOrNull
                if (type == "ISBN_13") {
                    isbn = identifier
                    break
                } else if (type == "ISBN_10" && isbn == null) {
                    isbn = identifier
                }
            }
        }

        val imageLinks = volumeInfo["imageLinks"]?.jsonObject
        val thumbnail = imageLinks?.get("thumbnail")?.jsonPrimitive?.contentOrNull
            ?: imageLinks?.get("smallThumbnail")?.jsonPrimitive?.contentOrNull
            ?: imageLinks?.get("small")?.jsonPrimitive?.contentOrNull
            ?: imageLinks?.get("medium")?.jsonPrimitive?.contentOrNull
            ?: imageLinks?.get("large")?.jsonPrimitive?.contentOrNull

        val coverUrl = thumbnail?.replace("^http:".toRegex(), "https:")?.let { largeCover(it) }

        val publishedDate = volumeInfo["publishedDate"]?.jsonPrimitive?.contentOrNull
        val year = extractYear(publishedDate)
        val publisher = volumeInfo["publisher"]?.jsonPrimitive?.contentOrNull
        val description = volumeInfo["description"]?.jsonPrimitive?.contentOrNull
        val categories = volumeInfo["categories"]?.jsonArray?.mapNotNull { it.jsonPrimitive.contentOrNull }

        return BookSearchResult(
            id = "gb:$id",
            externalBookId = id,
            catalogSource = "google_books",
            title = title,
            author = author,
            cover = coverUrl,
            isbn = isbn,
            publicationYear = year,
            publisher = publisher,
            description = description,
            categories = categories
        )
    }

    private fun searchOpenLibrary(query: String, start: Int): Pair<List<BookSearchResult>, Boolean> {
        val limit = minOf(PAGE_SIZE, MAX_PER_SOURCE)
        val offset = start
        val q = if (isIsbn(query)) "isbn:${query.replace("[-\\s]".toRegex(), "")}" else query
        val encodedQ = URLEncoder.encode(q, StandardCharsets.UTF_8.toString())
        val fields = "key,title,author_name,cover_i,first_publish_year,publisher,isbn,description,subject"
        val url = "$OPEN_LIBRARY_ENDPOINT?q=$encodedQ&limit=$limit&offset=$offset&fields=$fields"

        val request = Request.Builder().url(url).build()
        val response = client.newCall(request).execute()
        if (!response.isSuccessful) return Pair(emptyList(), false)
        val responseBody = response.body?.string() ?: return Pair(emptyList(), false)

        val root = json.parseToJsonElement(responseBody).jsonObject
        val numFound = root["numFound"]?.jsonPrimitive?.intOrNull ?: 0
        val docs = root["docs"]?.jsonArray ?: return Pair(emptyList(), false)

        val results = mutableListOf<BookSearchResult>()
        for (docElem in docs) {
            val doc = docElem.jsonObject
            val item = normalizeOpenLibraryDoc(doc)
            if (item != null) {
                results.add(item)
            }
        }

        val hasMore = start + docs.size < numFound && results.isNotEmpty()
        return Pair(results, hasMore)
    }

    private fun normalizeOpenLibraryDoc(doc: JsonObject): BookSearchResult? {
        val title = doc["title"]?.jsonPrimitive?.contentOrNull ?: return null
        val key = doc["key"]?.jsonPrimitive?.contentOrNull ?: return null

        val authors = doc["author_name"]?.jsonArray?.mapNotNull { it.jsonPrimitive.contentOrNull }
        val author = authors?.firstOrNull()

        val isbns = doc["isbn"]?.jsonArray?.mapNotNull { it.jsonPrimitive.contentOrNull }
        val isbn = isbns?.firstOrNull()

        val coverI = doc["cover_i"]?.jsonPrimitive?.intOrNull
        val coverUrl = coverI?.let { "https://covers.openlibrary.org/b/id/$it-L.jpg" }

        val firstPublishYear = doc["first_publish_year"]?.jsonPrimitive?.intOrNull
        val publishers = doc["publisher"]?.jsonArray?.mapNotNull { it.jsonPrimitive.contentOrNull }
        val publisher = publishers?.firstOrNull()

        val descElement = doc["description"]
        val description = when {
            descElement is JsonObject -> descElement["value"]?.jsonPrimitive?.contentOrNull
            descElement != null -> descElement.jsonPrimitive.contentOrNull
            else -> null
        }

        val subjects = doc["subject"]?.jsonArray?.mapNotNull { it.jsonPrimitive.contentOrNull }?.take(6)

        return BookSearchResult(
            id = "ol:$key",
            externalBookId = key,
            catalogSource = "open_library",
            title = title,
            author = author,
            cover = coverUrl,
            isbn = isbn,
            publicationYear = firstPublishYear,
            publisher = publisher,
            description = description,
            categories = subjects
        )
    }

    private fun dedupe(results: List<BookSearchResult>): List<BookSearchResult> {
        val byIsbn = mutableMapOf<String, BookSearchResult>()
        val bySig = mutableMapOf<String, BookSearchResult>()
        val out = mutableListOf<BookSearchResult>()

        for (r in results) {
            val isbn = r.isbn
            if (!isbn.isNullOrBlank()) {
                val key = isbn.replace("[-\\s]".toRegex(), "")
                if (byIsbn.containsKey(key)) continue
                byIsbn[key] = r
                out.add(r)
            } else {
                val sig = "${r.title.lowercase()}|${(r.author ?: "").lowercase()}"
                if (bySig.containsKey(sig)) continue
                bySig[sig] = r
                out.add(r)
            }
        }
        return out
    }

    private fun extractYear(dateStr: String?): Int? {
        if (dateStr == null) return null
        val match = "(\\d{4})".toRegex().find(dateStr)
        return match?.groupValues?.get(1)?.toIntOrNull()
    }

    fun largeCover(coverUrl: String?): String? {
        if (coverUrl == null) return null
        if (coverUrl.contains("covers.openlibrary.org")) {
            return coverUrl.replace("/M.jpg", "/L.jpg")
        }
        if (coverUrl.contains("googleusercontent.com") || coverUrl.contains("books.google.com")) {
            return coverUrl.replace("&zoom=\\d".toRegex(), "&zoom=0")
        }
        return coverUrl
    }
}
