package jp.careapp.counseling.android.data.model.search

import java.io.Serializable

data class SearchCategoryItem(
    var id: Int = 0,
    val name: String? = null,
    var isSelected: Boolean? = false
) : Serializable
