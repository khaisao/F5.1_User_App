package jp.careapp.counseling.android.data.model.labo

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class LaboResponse(
    @SerializedName("id")
    val id: Int = 0,
    @SerializedName("genre")
    val genre: Int = 1,
    @SerializedName("body")
    val body: String = "",
    @SerializedName("status")
    val status: Int = 1,
    @SerializedName("answer_count")
    val answerCount: Int = 0,
    @SerializedName("last_answered_at")
    val lastAnsweredAt: String = "",
    @SerializedName("accept_answer_end_at")
    val acceptAnswerEndAt: String = "",
    @SerializedName("best_answer_end_at")
    val bestAnswerEndAt: String = "",
    @SerializedName("created_at")
    val createdAt: String = "",
    @SerializedName("member")
    val member: Member = Member()
) : Serializable

data class Member(
    @SerializedName("code")
    val code: String = "",
    @SerializedName("counselee_name")
    val counseleeName: String = "",
    @SerializedName("age")
    val age: Int = 0,
) : Serializable