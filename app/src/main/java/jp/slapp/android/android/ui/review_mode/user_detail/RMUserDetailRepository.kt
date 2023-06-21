package jp.slapp.android.android.ui.review_mode.user_detail

import jp.slapp.android.android.network.RMApiInterface
import javax.inject.Inject

class RMUserDetailRepository @Inject constructor(
    private val rmApiInterface: RMApiInterface
) {
    suspend fun loadUserDetail(userCode: String) = rmApiInterface.loadUserDetail(userCode)

    suspend fun addFavoriteUser(userCode: String) = rmApiInterface.addFavoriteUser(userCode)

    suspend fun deleteFavoriteUser(userCode: String) = rmApiInterface.deleteFavoriteUser(userCode)

    suspend fun blockUser(userCode: String) = rmApiInterface.blockUser(userCode)
}