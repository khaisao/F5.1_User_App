package jp.slapp.android.android.data.model.search

import java.io.Serializable

data class SearchOptionObject(
    var listCategory: ArrayList<SearchCategoryItem>? = arrayListOf(),
    var listGender: ArrayList<SearchCategoryItem>? = arrayListOf(),
    var listRanking: ArrayList<SearchCategoryItem>? = arrayListOf(),
    var listReviewAverage: ArrayList<SearchCategoryItem>? = arrayListOf()
) : Serializable
