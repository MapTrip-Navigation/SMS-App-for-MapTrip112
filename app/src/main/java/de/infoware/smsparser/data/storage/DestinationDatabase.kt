package de.infoware.smsparser.data.storage

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import de.infoware.smsparser.data.DataSource

@Database(entities = arrayOf(DestinationEntity::class), version = 2, exportSchema = false)
abstract class DestinationDatabase : RoomDatabase(), DataSource {
    abstract fun destinationDao(): DestinationDao

    companion object {
        private lateinit var instance: DestinationDatabase

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE destination ADD COLUMN blue_light_navigation INTEGER NOT NULL DEFAULT 0")
            }
        }

        fun getInstance(context: Context): DestinationDatabase {
            if (!::instance.isInitialized) {
                synchronized(DestinationDatabase::class.java) {
                    instance = Room.databaseBuilder(
                        context, DestinationDatabase::class.java, "sms-database"
                    ).addMigrations(MIGRATION_1_2).build()
                }
            }

            return instance
        }
    }
}
