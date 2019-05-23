package de.infoware.smsparser.storage

import androidx.room.*
import io.reactivex.Completable
import io.reactivex.Single


@Dao
interface DestinationDao {
    @Query("SELECT * FROM destination  ORDER BY added_timestamp DESC")
    fun getAll(): Single<List<DestinationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(destinationEntity: DestinationEntity): Completable

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(products: List<DestinationEntity>): Completable

    @Delete
    fun delete(destinationEntity: DestinationEntity): Completable

    @Query("UPDATE destination SET already_navigated = :alreadyNavigated WHERE uid = :uid")
    fun updateNavigatedStatus(uid: Int, alreadyNavigated: Boolean): Completable
}