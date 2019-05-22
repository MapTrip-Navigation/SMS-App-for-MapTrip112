package de.infoware.smsparser.domain

import de.infoware.smsparser.DestinationInfo
import de.infoware.smsparser.storage.DestinationDatabase
import de.infoware.smsparser.storage.DestinationEntity
import io.reactivex.Completable
import io.reactivex.schedulers.Schedulers

class DestinationSaver(val repository: DestinationDatabase) : UseCase<DestinationInfo, Completable> {
    override fun execute(param: DestinationInfo): Completable {
        return repository.destinationDao()
            .insert(DestinationEntity(param.lat, param.lon, param.reason, param.addedTimestamp))
            .subscribeOn(Schedulers.io())
    }
}