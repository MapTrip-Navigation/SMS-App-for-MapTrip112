package de.infoware.smsparser.permission

/**
 * Data class containing information about granted/non granted permissions.
 */
data class PermissionResult(val requestCode: Int, val grantResults: IntArray) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PermissionResult

        if (requestCode != other.requestCode) return false
        if (!grantResults.contentEquals(other.grantResults)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = requestCode
        result = 31 * result + grantResults.contentHashCode()
        return result
    }
}