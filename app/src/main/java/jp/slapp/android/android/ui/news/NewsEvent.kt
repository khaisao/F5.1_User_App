package jp.slapp.android.android.ui.news

import jp.slapp.android.android.data.network.NewsResponse

interface NewsEvent {
    fun newsClick(item: NewsResponse)
}
