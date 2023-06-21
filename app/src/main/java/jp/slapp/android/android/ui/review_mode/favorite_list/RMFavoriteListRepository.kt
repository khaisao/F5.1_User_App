package jp.slapp.android.android.ui.review_mode.favorite_list

import jp.slapp.android.android.network.RMApiInterface
import javax.inject.Inject

class RMFavoriteListRepository @Inject constructor(private val rmApiInterface: RMApiInterface) {

    suspend fun getBlockList() = rmApiInterface.getBlockList()

    suspend fun getFavoriteList() = rmApiInterface.getFavorites()

    suspend fun deleteFavoriteUser(userCode: String) = rmApiInterface.deleteFavoriteUser(userCode)
}