package de.infoware.smsparser.tetra

/**
 * Interface provides methods required for handling TETRA session opening
 */
interface TetraSessionListener {
    fun handleSessionOpenException(ex: Exception)
    fun sessionStarted()
}
