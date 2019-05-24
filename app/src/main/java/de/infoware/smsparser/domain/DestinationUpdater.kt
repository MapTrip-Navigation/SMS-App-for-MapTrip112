package de.infoware.smsparser.domain

import de.infoware.smsparser.data.DestinationInfo
import de.infoware.smsparser.repository.DestinationRepository
import io.reactivex.Maybe
import io.reactivex.schedulers.Schedulers

/**
 * UseCase for updating information regarding alreadyNavigated status.
 */
class DestinationUpdater(private val repository: DestinationRepository) : UseCase<DestinationInfo, Maybe<Int>> {
    override fun execute(param: DestinationInfo): Maybe<Int> {
        return repository
            .updateNavigatedStatus(param)
            .subscribeOn(Schedulers.io())
    }
}