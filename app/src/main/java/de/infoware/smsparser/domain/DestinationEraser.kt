package de.infoware.smsparser.domain

import de.infoware.smsparser.repository.DestinationRepository
import io.reactivex.Maybe
import io.reactivex.schedulers.Schedulers

/**
 * UseCase for deleting all the entries in the DataSource.
 */
class DestinationEraser(private val repository: DestinationRepository) : UseCase<Any, Maybe<Int>> {
    override fun execute(param: Any): Maybe<Int> {
        return repository
            .deleteAll()
            .subscribeOn(Schedulers.io())
    }
}