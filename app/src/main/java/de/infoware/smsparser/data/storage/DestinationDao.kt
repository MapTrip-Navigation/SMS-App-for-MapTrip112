package de.infoware.smsparser.data.storage

import androidx.room.*
import io.reactivex.Completable
import io.reactivex.Maybe
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

    /**
     * For the queries below we can not use Completable as a return type because of the room ideology.
     * Int value in Maybe stands for the number of affected rows in the database.
     */
    @Query("DELETE FROM destination")
    fun deleteAll(): Maybe<Int>

    @Query("UPDATE destination SET already_shown = :alreadyShown WHERE uid = :uid")
    fun updateNavigatedStatus(uid: Int, alreadyShown: Boolean): Maybe<Int>
}