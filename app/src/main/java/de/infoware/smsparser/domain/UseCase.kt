package de.infoware.smsparser.domain

interface UseCase<PARAM, RESULT> {

    /**
     * Executes this [UseCase].
     */
    fun execute(param: PARAM): RESULT
}