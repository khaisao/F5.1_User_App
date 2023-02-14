package jp.careapp.counseling.android.network.connectivity

/**
 * Interface for hiding network connectivity resolution
 */
interface ConnectivityChecker {

    fun isConnected(): Boolean
}
