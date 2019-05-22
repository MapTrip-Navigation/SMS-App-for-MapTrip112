package de.infoware.smsparser.domain

import de.infoware.smsparser.DestinationInfo
import de.infoware.smsparser.storage.DestinationDatabase
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers

class DestinationLoader(val repository: DestinationDatabase) : UseCase<Any, Single<List<DestinationInfo>>> {
    override fun execute(param: Any): Single<List<DestinationInfo>> {
        return repository.destinationDao()
            .getAll()
            .subscribeOn(Schedulers.io())
            .flatMap {
                Single.create<List<DestinationInfo>> { emitter
                    ->
                    val result = ArrayList<DestinationInfo>()
                    it.forEach { entity ->
                        result.add(
                            DestinationInfo(
                                entity.lat,
                                entity.lon,
                                entity.reason,
                                entity.addedTimestamp,
                                entity.alreadyNavigated
                            )
                        )
                    }
                    emitter.onSuccess(result)
                }
            }
    }
}