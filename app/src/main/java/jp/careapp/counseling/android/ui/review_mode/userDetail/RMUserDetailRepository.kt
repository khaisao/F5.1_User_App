package jp.careapp.counseling.android.ui.review_mode.userDetail

import jp.careapp.counseling.android.network.RMApiInterface
import javax.inject.Inject

class RMUserDetailRepository @Inject constructor(
    private val rmApiInterface: RMApiInterface
) {
    suspend fun loadUserDetail(userCode: String) = rmApiInterface.loadUserDetail(userCode)

    suspend fun addFavoriteUser(userCode: String) = rmApiInterface.addFavoriteUser(userCode)

    suspend fun deleteFavoriteUser(userCode: String) = rmApiInterface.deleteFavoriteUser(userCode)

    suspend fun blockUser(userCode: String) = rmApiInterface.blockUser(userCode)
}