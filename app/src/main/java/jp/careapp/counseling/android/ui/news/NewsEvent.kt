package jp.careapp.counseling.android.ui.news

import jp.careapp.counseling.android.data.network.NewsResponse

interface NewsEvent {
    fun newsClick(item: NewsResponse)
}
