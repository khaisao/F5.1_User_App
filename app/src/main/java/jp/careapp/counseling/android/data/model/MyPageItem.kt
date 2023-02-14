package jp.careapp.counseling.android.data.model

import android.graphics.drawable.Drawable
import jp.careapp.counseling.android.ui.mypage.Destination

data class MyPageItem(
    val checked: Boolean,
    var bage: Int,
    val icon: Drawable,
    val title: String,
    val destination: Destination = Destination.SHEET,
    val isShowWarning: Boolean = false
)
