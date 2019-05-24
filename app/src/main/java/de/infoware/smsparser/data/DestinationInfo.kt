package de.infoware.smsparser.data

// Data class which is transferred across layer boundaries.
data class DestinationInfo(
    val lat: Double,
    val lon: Double,
    val reason: String,
    val addedTimestamp: Long,
    var alreadyNavigated: Boolean = false,
    var uidInDataSource: Int = 0
)