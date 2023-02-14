package jp.careapp.counseling.android.data.network

import com.google.gson.annotations.SerializedName

data class NewsResponse(
    @SerializedName("blog_id")
    val blogId: Int = 1,
    @SerializedName("body")
    var body: String = "",
    @SerializedName("category")
    val category: Int = 1,
    @SerializedName("id")
    val id: Int = 1,
    @SerializedName("link")
    val link: String = "",
    @SerializedName("start_at")
    val startAt: String = "",
    @SerializedName("subject")
    val subject: String = "",

    var isRead: Boolean = false
)
