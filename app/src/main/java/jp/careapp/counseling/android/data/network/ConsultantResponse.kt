package jp.careapp.counseling.android.data.network

import com.google.gson.annotations.SerializedName
import jp.careapp.counseling.R
import jp.careapp.counseling.android.data.model.history_chat.Performer
import jp.careapp.counseling.android.utils.CallStatus.DEFAULT_CALL_STATUS
import java.io.Serializable

data class ConsultantResponse(
    @SerializedName("code") val code: String? = "",
    @SerializedName("name") val name: String? = "",
    @SerializedName("sex") val sex: Int = 3,
    @SerializedName("age") val age: Int = 0,
    @SerializedName("bust") val bust: Int = 0,
    @SerializedName("favorite_member_count") val favoriteMemberCount: Int = 0,
    @SerializedName("exists_image") val existsImage: Boolean = false,
    @SerializedName("image_url") val imageUrl: String? = "",
    @SerializedName("thumbnail_image_url") val thumbnailImageUrl: String? = "",
    @SerializedName("message") val message: String? = "",
    @SerializedName("message_of_the_day") val messageOfTheDay: String? = "",
    @SerializedName("message_notice") val messageNotice: String? = "",
    @SerializedName("genres") val genres: List<Int> = listOf(),
    @SerializedName("stage") val stage: Int = 0,
    @SerializedName("review_average") val reviewAverage: Double = 0.0,
    @SerializedName("review_score") val reviewScore: Int = 0,
    @SerializedName("review_total_number") val reviewTotalNumber: Int = 0,
    @SerializedName("review_total_score") val reviewTotalScore: Int = 0,
    @SerializedName("point_per_char") val pointPerChar: Int = 0,
    @SerializedName("login_plans_datetime") val loginPlansDatetime: String? = "",
    @SerializedName("ranking") val ranking: RankingResponse? = null,
    @SerializedName("recommend_ranking") val recommendRanking: Int = 0,
    @SerializedName("profile_pattern") val profilePattern: Int = 0,
    @SerializedName("profile_images") val profileImages: ProfileImageResponse? = null,
    @SerializedName("presence_status") val presenceStatus: Int = 0,
    @SerializedName("status") val status: Int = 0,
    @SerializedName("login_member_count") val loginMemberCount: Int = 0,
    @SerializedName("peeping_member_count") val peepingMemberCount: Int = 0,
    @SerializedName("is_favorite") val isFavorite: Boolean = false,
    @SerializedName("point_per_minute") val pointPerMinute: Int = 0,
    @SerializedName("call_status") val callStatus: Int = DEFAULT_CALL_STATUS,
    @SerializedName("chat_status") val chatStatus: Int = DEFAULT_CALL_STATUS,
    @SerializedName("call_restriction") val callRestriction: Int? = 0, // 1=possible, null=not possible
) : Serializable {
    companion object {
        const val PREVIOUS = 0
        const val WEEK = 1
        const val MONTH = 2

        fun from(performer: Performer): ConsultantResponse {
            return ConsultantResponse(
                code = performer.code,
                existsImage = performer.existsImage,
                imageUrl = performer.imageUrl,
                name = performer.name,
                presenceStatus = performer.presenceStatus,
                status = performer.status,
                thumbnailImageUrl = performer.thumbnailImageUrl
            )
        }

        fun isWaiting(callStatus: Int, chatStatus: Int): Boolean {
            if (callStatus == 1 && chatStatus == 0) {
                return true
            } else if (callStatus == 2 && chatStatus == 0) {
                return true
            }
            return false
        }

        fun isLiveStream(callStatus: Int, chatStatus: Int): Boolean {
            if (callStatus == 0 && chatStatus == 1) {
                return true
            } else if (callStatus == 0 && chatStatus == 2) {
                return true
            }
            return false
        }

        fun isPrivateLiveStream(callStatus: Int, chatStatus: Int): Boolean {
            if (callStatus == 0 && chatStatus == 3) {
                return true
            }
            return false
        }

        fun isOffline(callStatus:Int, chatStatus:Int): Boolean {
            if(!isWaiting(callStatus, chatStatus) && !isLiveStream(callStatus,chatStatus) && !isPrivateLiveStream(callStatus, chatStatus)){
                return true
            }
            return false
        }

        private fun getIconForRank(ranking: RankingResponse?): Int {
            ranking?.let {
                when (it.interval) {
                    PREVIOUS -> {
                        when (it.ranking) {
                            1 ->  return R.drawable.ic_rank_daily_1
                            2 ->  return R.drawable.ic_rank_daily_2
                            3 ->  return R.drawable.ic_rank_daily_3
                            4 ->  return R.drawable.ic_rank_daily_4
                            5 ->  return R.drawable.ic_rank_daily_5
                            6 ->  return R.drawable.ic_rank_daily_6
                            7 ->  return R.drawable.ic_rank_daily_7
                            8 ->  return R.drawable.ic_rank_daily_8
                            9 ->  return R.drawable.ic_rank_daily_9
                            10 ->  return R.drawable.ic_rank_daily_10
                            11 ->  return R.drawable.ic_rank_daily_11
                            12 ->  return R.drawable.ic_rank_daily_12
                            13 ->  return R.drawable.ic_rank_daily_13
                            14 ->  return R.drawable.ic_rank_daily_14
                            15 ->  return R.drawable.ic_rank_daily_15
                            16 ->  return R.drawable.ic_rank_daily_16
                            17 ->  return R.drawable.ic_rank_daily_17
                            18 ->  return R.drawable.ic_rank_daily_18
                            19 ->  return R.drawable.ic_rank_daily_19
                            20 ->  return R.drawable.ic_rank_daily_20
                            21 ->  return R.drawable.ic_rank_daily_21
                            22 ->  return R.drawable.ic_rank_daily_22
                            23 ->  return R.drawable.ic_rank_daily_23
                            24 ->  return R.drawable.ic_rank_daily_24
                            25 ->  return R.drawable.ic_rank_daily_25
                            26 ->  return R.drawable.ic_rank_daily_26
                            27 ->  return R.drawable.ic_rank_daily_27
                            28 ->  return R.drawable.ic_rank_daily_28
                            29 ->  return R.drawable.ic_rank_daily_29
                            else ->  return R.drawable.ic_rank_daily_30
                        }
                    }
                    WEEK -> {
                        when (it.ranking) {
                            1 ->  return R.drawable.ic_rank_weekly_1
                            2 ->  return R.drawable.ic_rank_weekly_2
                            3 -> return R.drawable.ic_rank_weekly_3
                            4 -> return R.drawable.ic_rank_weekly_4
                            5 -> return R.drawable.ic_rank_weekly_5
                            6 -> return R.drawable.ic_rank_weekly_6
                            7 -> return R.drawable.ic_rank_weekly_7
                            8 -> return R.drawable.ic_rank_weekly_8
                            9 -> return R.drawable.ic_rank_weekly_9
                            10 -> return R.drawable.ic_rank_weekly_10
                            11 -> return R.drawable.ic_rank_weekly_11
                            12 -> return R.drawable.ic_rank_weekly_12
                            13 -> return R.drawable.ic_rank_weekly_13
                            14 -> return R.drawable.ic_rank_weekly_14
                            15 -> return R.drawable.ic_rank_weekly_15
                            16 -> return R.drawable.ic_rank_weekly_16
                            17 -> return R.drawable.ic_rank_weekly_17
                            18 -> return R.drawable.ic_rank_weekly_18
                            19 -> return R.drawable.ic_rank_weekly_19
                            20 -> return R.drawable.ic_rank_weekly_20
                            21 -> return R.drawable.ic_rank_weekly_21
                            22 -> return R.drawable.ic_rank_weekly_22
                            23 -> return R.drawable.ic_rank_weekly_23
                            24 -> return R.drawable.ic_rank_weekly_24
                            25 -> return R.drawable.ic_rank_weekly_25
                            26 -> return R.drawable.ic_rank_weekly_26
                            27 -> return R.drawable.ic_rank_weekly_27
                            28 -> return R.drawable.ic_rank_weekly_28
                            29 -> return R.drawable.ic_rank_weekly_29
                            else -> return R.drawable.ic_rank_weekly_30
                        }


                    }
                    MONTH -> {
                        when (it.ranking) {
                            1 -> return R.drawable.ic_rank_monthly_1
                            2 -> return R.drawable.ic_rank_monthly_2
                            3 -> return R.drawable.ic_rank_monthly_3
                            4 -> return R.drawable.ic_rank_monthly_4
                            5 -> return R.drawable.ic_rank_monthly_5
                            6 -> return R.drawable.ic_rank_monthly_6
                            7 -> return R.drawable.ic_rank_monthly_7
                            8 -> return R.drawable.ic_rank_monthly_8
                            9 -> return R.drawable.ic_rank_monthly_9
                            10 -> return R.drawable.ic_rank_monthly_10
                            11 -> return R.drawable.ic_rank_monthly_11
                            12 -> return R.drawable.ic_rank_monthly_12
                            13 -> return R.drawable.ic_rank_monthly_13
                            14 -> return R.drawable.ic_rank_monthly_14
                            15 -> return R.drawable.ic_rank_monthly_15
                            16 -> return R.drawable.ic_rank_monthly_16
                            17 -> return R.drawable.ic_rank_monthly_17
                            18 -> return R.drawable.ic_rank_monthly_18
                            19 -> return R.drawable.ic_rank_monthly_19
                            20 -> return R.drawable.ic_rank_monthly_20
                            21 -> return R.drawable.ic_rank_monthly_21
                            22 -> return R.drawable.ic_rank_monthly_22
                            23 -> return R.drawable.ic_rank_monthly_23
                            24 -> return R.drawable.ic_rank_monthly_24
                            25 -> return R.drawable.ic_rank_monthly_25
                            26 -> return R.drawable.ic_rank_monthly_26
                            27 -> return R.drawable.ic_rank_monthly_27
                            28 -> return R.drawable.ic_rank_monthly_28
                            29 -> return R.drawable.ic_rank_monthly_29
                            else -> return R.drawable.ic_rank_monthly_30
                        }
                    }
                    else -> {
                        return 0
                    }
                }
            }
            return 0
        }
        private fun getIconForRecommend(recommendRanking: Int): Int {
            if (recommendRanking != 0) {
                when (recommendRanking) {
                    1 -> return R.drawable.ic_best_rank_1
                    2 -> return R.drawable.ic_best_rank_2
                    3 -> return R.drawable.ic_best_rank_3
                    4 -> return R.drawable.ic_best_rank_4
                    5 -> return R.drawable.ic_best_rank_5
                    6 -> return R.drawable.ic_best_rank_6
                    7 -> return R.drawable.ic_best_rank_7
                    8 -> return R.drawable.ic_best_rank_8
                    9 -> return R.drawable.ic_best_rank_9
                    10 -> return R.drawable.ic_best_rank_10
                    11 -> return R.drawable.ic_best_rank_11
                    12 -> return R.drawable.ic_best_rank_12
                    13 -> return R.drawable.ic_best_rank_13
                    14 -> return R.drawable.ic_best_rank_14
                    15 -> return R.drawable.ic_best_rank_15
                    16 -> return R.drawable.ic_best_rank_16
                    17 -> return R.drawable.ic_best_rank_17
                    18 -> return R.drawable.ic_best_rank_18
                    19 -> return R.drawable.ic_best_rank_19
                    20 -> return R.drawable.ic_best_rank_20
                    21 -> return R.drawable.ic_best_rank_21
                    22 -> return R.drawable.ic_best_rank_22
                    23 -> return R.drawable.ic_best_rank_23
                    24 -> return R.drawable.ic_best_rank_24
                    25 -> return R.drawable.ic_best_rank_25
                    26 -> return R.drawable.ic_best_rank_26
                    27 -> return R.drawable.ic_best_rank_27
                    28 -> return R.drawable.ic_best_rank_28
                    29 -> return R.drawable.ic_best_rank_29
                    else -> return R.drawable.ic_best_rank_30
                }

            }
            return 0
        }

        fun getImageViewForRank(ranking: RankingResponse?, recommendRanking: Int): Int {
            return if ((ranking?.ranking ?: 0) < recommendRanking) {
                getIconForRank(ranking)
            } else if ((ranking?.ranking ?: 0) > recommendRanking && recommendRanking != 0) {
                getIconForRecommend(recommendRanking)
            } else {
                getIconForRank(ranking)
            }
        }
    }
}
