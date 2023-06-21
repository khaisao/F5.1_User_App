package jp.slapp.android.android.ui.review_mode.online_list

import jp.slapp.android.android.network.RMApiInterface
import javax.inject.Inject

class RMOnlineListRepository @Inject constructor(private val rmApiInterface: RMApiInterface) {

    suspend fun getBlockList() = rmApiInterface.getBlockList()

    suspend fun getDummyPerformers(params: HashMap<String, Any>) =
        rmApiInterface.getDummyPerformers(params)
}