package hu.kts.wtracker.persistency

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import hu.kts.wtracker.data.SessionItem

@Dao
interface SessionDao {

    @Query("SELECT * FROM Session WHERE session_timestamp == :sessionTimestamp")
    suspend fun getAll(sessionTimestamp: Long): List<SessionItem>

    @Insert
    suspend fun insert(sessionItem: SessionItem)

}