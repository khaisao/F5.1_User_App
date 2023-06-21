package jp.slapp.android.android.network.connectivity

/**
 * Interface for hiding network connectivity resolution
 */
interface ConnectivityChecker {

    fun isConnected(): Boolean
}
