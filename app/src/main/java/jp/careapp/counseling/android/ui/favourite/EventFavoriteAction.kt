package jp.careapp.counseling.android.ui.favourite

import jp.careapp.counseling.android.data.network.FavoriteResponse

interface EventFavoriteAction {
    fun onclickItem(item: FavoriteResponse)
    fun onClickRelease(item: FavoriteResponse)
}
