package jp.careapp.counseling.android.utils.event

import android.os.Handler
import android.os.Looper
import jp.careapp.counseling.android.data.network.ApiException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import javax.inject.Inject

sealed class NetworkState {
    object NO_INTERNET : NetworkState()
    object CONNECTION_LOST : NetworkState()
    object FORBIDDEN : NetworkState()
    object SERVER_NOT_AVAILABLE : NetworkState()
    object UNAUTHORIZED : NetworkState()
    object INITIALIZE : NetworkState()
    object ERROR : NetworkState()
    object NOT_FOUND : NetworkState()
    object BAD_REQUEST : NetworkState()
    data class GENERIC(val exception: ApiException) : NetworkState()
}

class NetworkEvent @Inject constructor() {

    @ExperimentalCoroutinesApi
    private val events: ConflatedBroadcastChannel<NetworkState> by lazy {
        ConflatedBroadcastChannel<NetworkState>().also { channel ->
            channel.offer(NetworkState.INITIALIZE)
        }
    }

    @FlowPreview
    @ExperimentalCoroutinesApi
    val observableNetworkState: Flow<NetworkState> = events.asFlow()

    @ExperimentalCoroutinesApi
    fun publish(networkState: NetworkState) {
        Handler(Looper.getMainLooper()).post {
            events.offer(networkState)
        }
    }
}
