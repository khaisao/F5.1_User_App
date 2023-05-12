package jp.careapp.counseling.android.utils.performer_extension

import jp.careapp.counseling.R
import jp.careapp.counseling.android.data.network.ConsultantResponse
import jp.careapp.counseling.android.data.network.RankingResponse

class PerformerRankingHandler {
    companion object {
        private fun getIconForRank(ranking: RankingResponse?): Int {
            ranking?.let {
                when (it.interval) {
                    ConsultantResponse.PREVIOUS -> {
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
                            30 ->  return R.drawable.ic_rank_daily_30
                        }
                    }
                    ConsultantResponse.WEEK -> {
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
                            30 -> return R.drawable.ic_rank_weekly_30
                            else -> {}
                        }


                    }
                    ConsultantResponse.MONTH -> {
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
                            30 -> return R.drawable.ic_rank_monthly_30
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
                    30 -> return R.drawable.ic_best_rank_30
                }

            }
            return 0
        }

        fun getImageViewForRank(ranking: RankingResponse?, recommendRanking: Int): Int {
            return if (ranking == null) {
                getIconForRecommend(recommendRanking)
            } else if (recommendRanking == 0 || ranking.ranking <= recommendRanking) {
                getIconForRank(ranking)
            } else {
                getIconForRecommend(recommendRanking)
            }
        }
    }
}

