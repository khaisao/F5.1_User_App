package jp.slapp.android.android.model.user_profile

import java.io.Serializable

data class PerformerSearch (
    var statusConsultant: Int = 0,
    var nameConsultant: String = "",
    var haveNumberReview: Int = 0,
    var genderCondition: Int = 0,
    var listGenresSelected: ArrayList<Int> = arrayListOf(),
    var listRankingSelected: ArrayList<Int> = arrayListOf(),
    var reviewAverageCondition: Int = 0
) : Serializable

data class ConsultantSearch (
    var statusConsultant: Int? = null,
    var nameConsultant: String? = null,
    var haveNumberReview: Int? = null,
    var genderCondition: Int? = null,
    var genresSelectCondition: String? = null,
    var listGenresSelected: ArrayList<Int>? = null,
    var listRankingSelected: ArrayList<Int>? = null,
    var reviewAverageCondition: Int? = null,
    var sort: String? = null,
    var order: String? = null,
    var sort2: String? = null,
    var order2: String? = null,
    var limit: Int? = null,
    var page: Int? = null
) : Serializable
