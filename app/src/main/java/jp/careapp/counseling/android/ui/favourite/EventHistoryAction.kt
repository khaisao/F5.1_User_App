package jp.careapp.counseling.android.ui.favourite

import jp.careapp.counseling.android.data.network.HistoryResponse

interface EventHistoryAction {
    fun onclickItem(item: HistoryResponse)
    fun onClickRelease(item: HistoryResponse)
}
