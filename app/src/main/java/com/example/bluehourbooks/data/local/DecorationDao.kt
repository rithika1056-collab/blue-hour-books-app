package com.example.bluehourbooks.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.bluehourbooks.data.model.ShelfDecoration
import kotlinx.coroutines.flow.Flow

@Dao
interface DecorationDao {
    @Query("SELECT * FROM shelf_decorations ORDER BY shelfIndex ASC, position ASC, createdAt ASC")
    fun getAllDecorations(): Flow<List<ShelfDecoration>>

    @Query("SELECT * FROM shelf_decorations WHERE shelfIndex = :shelfIndex ORDER BY position ASC, createdAt ASC")
    fun getDecorationsForShelf(shelfIndex: Int): Flow<List<ShelfDecoration>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDecoration(decoration: ShelfDecoration)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDecorations(decorations: List<ShelfDecoration>)

    @Update
    suspend fun updateDecoration(decoration: ShelfDecoration)

    @Delete
    suspend fun deleteDecoration(decoration: ShelfDecoration)

    @Query("DELETE FROM shelf_decorations WHERE id = :id")
    suspend fun deleteDecorationById(id: String)

    @Query("DELETE FROM shelf_decorations")
    suspend fun clearAll()
}
