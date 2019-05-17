package de.infoware.smsparser.storage

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = arrayOf(DestinationEntity::class), version = 1, exportSchema = false)
abstract class DestinationDatabase : RoomDatabase() {
    abstract fun destinationDao(): DestinationDao

    companion object {
        private lateinit var instance: DestinationDatabase

        fun getInstance(context: Context): DestinationDatabase {
            if (!::instance.isInitialized) {
                synchronized(DestinationDatabase::class.java) {}
                instance = Room.databaseBuilder(
                    context, DestinationDatabase::class.java, "sms-database"
                ).build()
            }

            return instance
        }
    }
}
