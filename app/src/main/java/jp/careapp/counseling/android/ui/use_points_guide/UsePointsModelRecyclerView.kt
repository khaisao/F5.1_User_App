package jp.careapp.counseling.android.ui.use_points_guide

import jp.careapp.counseling.android.data.model.TypeField

data class UsePointsModelRecyclerView(
    val resourceImage: Int,
    val label: String,
    val points: String,
    val time: String,
    val typeField: TypeField
)
