package jp.careapp.counseling.android.data.network

import com.google.gson.annotations.SerializedName
import jp.careapp.counseling.android.data.model.labo.LaboResponse
import jp.careapp.counseling.android.data.model.labo.Member
import java.io.Serializable

data class LabDetailResponse(
    @SerializedName("id") val id: Int = 0,
    @SerializedName("genre") val genre: Int = 0,
    @SerializedName("body") val body: String? = "",
    @SerializedName("status") val status: Int = 0,
    @SerializedName("favorite_is_enabled") val favoriteIsEnabled: Boolean = false,
    @SerializedName("answer_count") val answerCount: Int = 0,
    @SerializedName("last_answered_at") val lastAnsweredAt: String? = "",
    @SerializedName("accept_answer_end_at") val acceptAnswerEndAt: String? = "",
    @SerializedName("best_answer_end_at") val bestAnswerEndAt: String? = "",
    @SerializedName("created_at") val createdAt: String? = "",
    @SerializedName("member") val member: Member = Member(),
    @SerializedName("answers") val answers: List<AnswerResponse> = ArrayList(),
) : Serializable {
    companion object {
        fun from(lab: LaboResponse): LabDetailResponse {
            return LabDetailResponse(
                id = lab.id,
                genre = lab.genre,
                body = lab.body,
                status = lab.status,
                answerCount = lab.answerCount,
                lastAnsweredAt = lab.lastAnsweredAt,
                acceptAnswerEndAt = lab.acceptAnswerEndAt,
                bestAnswerEndAt = lab.bestAnswerEndAt,
                createdAt = lab.createdAt,
                member = lab.member
            )
        }
    }
}

