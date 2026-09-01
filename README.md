# Blue Hour Books (Android)

A native Android application built with **Kotlin** and **Jetpack Compose** for tracking finished books in a cozy, twilight-inspired personal library.

## Features

- **Home Dashboard**: View reading statistics, welcome banner with interactive twinkling starfield, and recently added books.
- **Library Grid**: Browse and search your personal book collection by title, author, or ISBN. Sort by Date Completed, Title, Author, or Star Rating.
- **Visual Bookshelf**: Realistic wooden shelf view with dynamically rendered vertical book spines showing unique heights, widths, and twilight gradients.
- **Book Search & Discovery**: Search millions of books via Google Books API and Open Library API with cover previews, automatic metadata extraction, and deduplication.
- **Book Journal Details**: Rate books (1–5 stars), track completion dates, view publisher & ISBN metadata, edit entries, and manage records.
- **Manual Book Entry**: Full support for adding offline/custom books manually.
- **Local Persistence**: Powered by Room Database for fast, offline-first reading logs.
- **Reader Profile**: Track reading milestones, 5-star favorites, and manage library data.

## Tech Stack

- **UI**: Jetpack Compose & Material Design 3
- **Language**: Kotlin
- **Architecture**: MVVM + Repository Pattern
- **Persistence**: Room Database (SQLite) + Kotlin Flow
- **Networking**: OkHttp + Kotlinx Serialization (Google Books & Open Library APIs)
- **Image Loading**: Coil 3 Compose
