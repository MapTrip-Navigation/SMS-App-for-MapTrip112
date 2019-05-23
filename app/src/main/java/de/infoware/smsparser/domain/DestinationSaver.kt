package de.infoware.smsparser.domain

import de.infoware.smsparser.DestinationInfo
import de.infoware.smsparser.repository.DestinationRepository
import io.reactivex.Completable
import io.reactivex.schedulers.Schedulers

class DestinationSaver(private val repository: DestinationRepository) : UseCase<DestinationInfo, Completable> {
    override fun execute(param: DestinationInfo): Completable {
        return repository
            .insertDestinationInfo(param)
            .subscribeOn(Schedulers.io())
    }
}