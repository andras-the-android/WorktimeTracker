package hu.kts.wtracker.persistency

import androidx.room.Database
import androidx.room.RoomDatabase
import hu.kts.wtracker.data.SessionItem

@Database(entities = [SessionItem::class], version = 1)
abstract class WTrackerDatabase: RoomDatabase() {
    abstract fun sessionDao(): SessionDao
}