package jp.careapp.counseling.android.ui.review_mode.block_list

import jp.careapp.counseling.android.network.RMApiInterface
import javax.inject.Inject

class RMBlockListRepository @Inject constructor(private val rmApiInterface: RMApiInterface) {

    suspend fun getBlockList() = rmApiInterface.getBlockList()

    suspend fun deleteBlock(userCode: String) = rmApiInterface.deleteBlock(userCode)
}