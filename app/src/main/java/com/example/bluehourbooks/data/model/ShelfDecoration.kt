package com.example.bluehourbooks.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable
import java.util.UUID

enum class DecorationType(val label: String, val iconName: String) {
    PLANT("Plant", "spa"),
    FRAME("Photo Frame", "image"),
    QUOTE("Quote Card", "format_quote"),
    CURIO("Study Curio", "auto_awesome")
}

@Serializable
@Entity(tableName = "shelf_decorations")
data class ShelfDecoration(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val shelfIndex: Int = 0, // 0 for Shelf 1, 1 for Shelf 2, etc.
    val type: String = "PLANT", // "PLANT", "FRAME", "QUOTE", "CURIO"
    val styleKey: String = "succulent",
    val title: String = "Small Succulent",
    val subtitle: String? = null,
    val position: Int = 0, // 0 = start of shelf, 1 = middle, 2 = end
    val createdAt: Long = System.currentTimeMillis()
)

data class DecorationStylePreset(
    val styleKey: String,
    val type: String,
    val name: String,
    val description: String,
    val iconEmoji: String,
    val quoteText: String? = null,
    val quoteAuthor: String? = null
)

object DecorationPresets {
    val PLANTS = listOf(
        DecorationStylePreset(
            styleKey = "potted_succulent",
            type = "PLANT",
            name = "Cozy Succulent",
            description = "A hardy jade succulent in a ceramic terracotta pot.",
            iconEmoji = "🪴"
        ),
        DecorationStylePreset(
            styleKey = "peace_lily",
            type = "PLANT",
            name = "Peace Lily",
            description = "Elegant dark green leaves with a gentle white bloom.",
            iconEmoji = "🌿"
        ),
        DecorationStylePreset(
            styleKey = "hanging_ivy",
            type = "PLANT",
            name = "Cascading Ivy",
            description = "Lush green vine tendrils trailing over the shelf edge.",
            iconEmoji = "🍃"
        ),
        DecorationStylePreset(
            styleKey = "bonsai_pine",
            type = "PLANT",
            name = "Miniature Bonsai",
            description = "A tranquil zen pine perched on an earthen dish.",
            iconEmoji = "🌲"
        )
    )

    val FRAMES = listOf(
        DecorationStylePreset(
            styleKey = "starry_twilight",
            type = "FRAME",
            name = "Starry Twilight",
            description = "A miniature oil canvas of the blue hour night sky.",
            iconEmoji = "🌌"
        ),
        DecorationStylePreset(
            styleKey = "vintage_library",
            type = "FRAME",
            name = "Old Library Art",
            description = "Sepia etching of a historic vaulted reader's sanctuary.",
            iconEmoji = "🏛️"
        ),
        DecorationStylePreset(
            styleKey = "golden_solitude",
            type = "FRAME",
            name = "Midnight Coffee",
            description = "Warm brass framed watercolor of an evening reader's cup.",
            iconEmoji = "☕"
        ),
        DecorationStylePreset(
            styleKey = "botanical_print",
            type = "FRAME",
            name = "Botanical Fern",
            description = "Minimalist pressed fern in a sleek dark oak frame.",
            iconEmoji = "🖼️"
        )
    )

    val QUOTES = listOf(
        DecorationStylePreset(
            styleKey = "quote_bluehour",
            type = "QUOTE",
            name = "Blue Hour Tale",
            description = "Parchment card celebrating the quiet magic of reading.",
            iconEmoji = "💬",
            quoteText = "In the quiet blue hour, stories come alive.",
            quoteAuthor = "Blue Hour Books"
        ),
        DecorationStylePreset(
            styleKey = "quote_thousand_lives",
            type = "QUOTE",
            name = "A Thousand Lives",
            description = "Classic literary thought on the gift of reading.",
            iconEmoji = "📜",
            quoteText = "A reader lives a thousand lives before he dies.",
            quoteAuthor = "George R.R. Martin"
        ),
        DecorationStylePreset(
            styleKey = "quote_sanctuary",
            type = "QUOTE",
            name = "Paper Sanctuary",
            description = "Reflective words on the warmth of bookshelves.",
            iconEmoji = "✨",
            quoteText = "Between the pages of a book is a lovely place to be.",
            quoteAuthor = "Unknown"
        ),
        DecorationStylePreset(
            styleKey = "quote_infinite",
            type = "QUOTE",
            name = "Infinite Horizons",
            description = "Inspiring quote on the endless discovery of books.",
            iconEmoji = "🪐",
            quoteText = "There is no friend as loyal as a book.",
            quoteAuthor = "Ernest Hemingway"
        )
    )

    val CURIOS = listOf(
        DecorationStylePreset(
            styleKey = "brass_lamp",
            type = "CURIO",
            name = "Brass Study Lamp",
            description = "A vintage desk lamp casting an amber reading glow.",
            iconEmoji = "💡"
        ),
        DecorationStylePreset(
            styleKey = "sand_timer",
            type = "CURIO",
            name = "Hourglass Timer",
            description = "Blown glass with golden sand counting peaceful minutes.",
            iconEmoji = "⏳"
        ),
        DecorationStylePreset(
            styleKey = "amethyst_crystal",
            type = "CURIO",
            name = "Celestial Crystal",
            description = "A raw amethyst geode shimmering in twilight purple.",
            iconEmoji = "🔮"
        )
    )

    fun getPresetsForType(type: String): List<DecorationStylePreset> {
        return when (type.uppercase()) {
            "PLANT" -> PLANTS
            "FRAME" -> FRAMES
            "QUOTE" -> QUOTES
            "CURIO" -> CURIOS
            else -> PLANTS
        }
    }
}
