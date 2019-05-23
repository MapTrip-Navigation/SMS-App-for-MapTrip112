package de.infoware.smsparser.repository

import de.infoware.smsparser.DestinationInfo
import io.reactivex.Completable
import io.reactivex.Single

interface DestinationRepository {
    fun getAllDestinationInfo(): Single<List<DestinationInfo>>

    fun insertDestinationInfo(destinationInfo: DestinationInfo): Completable
}