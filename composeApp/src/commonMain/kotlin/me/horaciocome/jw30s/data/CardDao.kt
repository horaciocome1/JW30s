package me.horaciocome.jw30s.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface CardDao {
    @Query("SELECT * FROM cards WHERE hasBeenShown = 0 ORDER BY RANDOM() LIMIT 1")
    suspend fun getRandomUnshownCard(): Card?

    @Query("UPDATE cards SET hasBeenShown = 1 WHERE id = :cardId")
    suspend fun markAsShown(cardId: String)

    @Query("UPDATE cards SET hasBeenShown = 0")
    suspend fun resetAllCards()

    @Query("SELECT COUNT(*) FROM cards WHERE hasBeenShown = 0")
    suspend fun getUnshownCount(): Int

    @Query("SELECT COUNT(*) FROM cards")
    suspend fun getTotalCount(): Int

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(cards: List<Card>)
}