package de.infoware.smsparser.domain

import de.infoware.smsparser.DestinationInfo
import de.infoware.smsparser.repository.DestinationRepository
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers

class DestinationLoader(private val repository: DestinationRepository) : UseCase<Any, Single<List<DestinationInfo>>> {
    override fun execute(param: Any): Single<List<DestinationInfo>> {
        return repository
            .getAllDestinationInfo()
            .subscribeOn(Schedulers.io())
    }
}