package de.infoware.smsparser.repository

import de.infoware.smsparser.DestinationInfo
import de.infoware.smsparser.storage.DestinationDatabase
import de.infoware.smsparser.storage.DestinationEntity
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single

class LocalDestinationRepository(private val destinationDatabase: DestinationDatabase) : DestinationRepository {

    override fun deleteAll(): Maybe<Int> {
        return destinationDatabase
            .destinationDao()
            .deleteAll()
    }

    override fun updateNavigatedStatus(destinationInfo: DestinationInfo): Maybe<Int> {
        return destinationDatabase
            .destinationDao()
            .updateNavigatedStatus(destinationInfo.uidInDataSource, destinationInfo.alreadyNavigated)
    }

    override fun insertDestinationInfo(destinationInfo: DestinationInfo): Completable {
        return destinationDatabase
            .destinationDao()
            .insert(
                DestinationEntity(
                    destinationInfo.lat,
                    destinationInfo.lon,
                    destinationInfo.reason,
                    destinationInfo.addedTimestamp
                )
            )
    }

    override fun getAllDestinationInfo(): Single<List<DestinationInfo>> {
        return destinationDatabase
            .destinationDao()
            .getAll()
            .flatMap {
                Single.create<List<DestinationInfo>> { emitter ->
                    val result = ArrayList<DestinationInfo>()
                    it.forEach { entity ->
                        result.add(
                            DestinationInfo(
                                entity.lat,
                                entity.lon,
                                entity.reason,
                                entity.addedTimestamp,
                                entity.alreadyNavigated,
                                entity.uid
                            )
                        )
                    }
                    emitter.onSuccess(result)
                }
            }
    }
}