package jp.slapp.android.android.data.model

import jp.slapp.android.android.data.network.CategoryResponse

data class LabRequest(
    var memberCode: String = "",
    var body: String = "",
    var genre: ArrayList<CategoryResponse> = arrayListOf(),
    var statuses: ArrayList<Int> = arrayListOf(),
    var sort: String = "",
    var order: String = "",
    var limit: Int = 0,
)