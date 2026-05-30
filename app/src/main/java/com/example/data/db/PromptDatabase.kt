package com.example.data.db

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.RoomDatabase
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "saved_prompts")
data class SavedPrompt(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val promptContent: String,
    val originalIdea: String,
    val platform: String,
    val category: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isFavorite: Boolean = false
)

@Dao
interface SavedPromptDao {
    @Query("SELECT * FROM saved_prompts ORDER BY timestamp DESC")
    fun getAllPrompts(): Flow<List<SavedPrompt>>

    @Query("SELECT * FROM saved_prompts WHERE isFavorite = 1 ORDER BY timestamp DESC")
    fun getFavoritePrompts(): Flow<List<SavedPrompt>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPrompt(prompt: SavedPrompt)

    @Update
    suspend fun updatePrompt(prompt: SavedPrompt)

    @Delete
    suspend fun deletePrompt(prompt: SavedPrompt)

    @Query("SELECT * FROM saved_prompts WHERE id = :id")
    suspend fun getPromptById(id: Int): SavedPrompt?

    @Query("DELETE FROM saved_prompts WHERE id = :id")
    suspend fun deletePromptById(id: Int)

    @Query("SELECT * FROM saved_prompts WHERE title LIKE :query OR promptContent LIKE :query OR originalIdea LIKE :query ORDER BY timestamp DESC")
    fun searchPrompts(query: String): Flow<List<SavedPrompt>>
}

@Database(entities = [SavedPrompt::class], version = 1, exportSchema = false)
abstract class PromptDatabase : RoomDatabase() {
    abstract fun savedPromptDao(): SavedPromptDao
}
