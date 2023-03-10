package jp.careapp.counseling.android.ui.faq

import jp.careapp.counseling.android.data.model.TypeField

sealed class FAQModelRecyclerView {
    data class ItemHeader(val header: String)
    data class ItemContent(
        val title: String,
        val content: String,
        val typeField: TypeField,
        var isCollapse: Boolean = false,
        val startSpan: Int = 0,
        val endSpan: Int = 0,
        val onClick: () -> Unit
    )
}