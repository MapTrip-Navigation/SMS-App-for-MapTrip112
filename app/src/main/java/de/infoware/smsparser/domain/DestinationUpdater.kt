package de.infoware.smsparser.domain

import de.infoware.smsparser.DestinationInfo
import de.infoware.smsparser.repository.DestinationRepository
import io.reactivex.Completable
import io.reactivex.schedulers.Schedulers

class DestinationUpdater(private val repository: DestinationRepository) : UseCase<DestinationInfo, Completable> {
    override fun execute(param: DestinationInfo): Completable {
        return repository
            .updateNavigatedStatus(param)
            .subscribeOn(Schedulers.io())
    }
}