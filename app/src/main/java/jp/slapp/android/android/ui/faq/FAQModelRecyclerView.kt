package jp.slapp.android.android.ui.faq

import jp.slapp.android.android.data.model.NMTypeField

sealed class FAQModelRecyclerView {
    data class ItemHeader(val header: String) : FAQModelRecyclerView()
    data class ItemContent(
        val title: String,
        val content: String,
        val typeField: NMTypeField,
        val startSpan: Int = 0,
        val endSpan: Int = 0,
        var isCollapse: Boolean = true,
        val onClick: () -> Unit = {}
    ) : FAQModelRecyclerView()
}