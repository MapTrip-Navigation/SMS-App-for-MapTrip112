package de.infoware.smsparser.domain

import de.infoware.smsparser.repository.DestinationRepository
import io.reactivex.Completable
import io.reactivex.schedulers.Schedulers

class DestinationEraser(private val repository: DestinationRepository) : UseCase<Any, Completable> {
    override fun execute(param: Any): Completable {
        return repository
            .deleteAll()
            .subscribeOn(Schedulers.io())
    }
}