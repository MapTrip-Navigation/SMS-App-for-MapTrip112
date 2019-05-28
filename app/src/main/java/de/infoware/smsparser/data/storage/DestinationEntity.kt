package de.infoware.smsparser.data.storage

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "destination")
data class DestinationEntity(
    @ColumnInfo(name = "lat") val lat: Double,
    @ColumnInfo(name = "lon") val lon: Double,
    @ColumnInfo(name = "reason") val reason: String,
    @ColumnInfo(name = "added_timestamp") var addedTimestamp: Long,
    @ColumnInfo(name = "already_shown") var alreadyShown: Boolean = false
) {
    @PrimaryKey(autoGenerate = true)
    var uid: Int = 0

}