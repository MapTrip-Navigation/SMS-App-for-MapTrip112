package de.infoware.smsparser.repository

import de.infoware.smsparser.data.DestinationInfo
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single

/**
 * Interface to unify behaviour with all possible DataSources through repository pattern
 */
interface DestinationRepository {
    fun getAllDestinationInfo(): Single<List<DestinationInfo>>

    fun insertDestinationInfo(destinationInfo: DestinationInfo): Completable

    fun updateNavigatedStatus(destinationInfo: DestinationInfo): Maybe<Int>

    fun deleteAll(): Maybe<Int>
}