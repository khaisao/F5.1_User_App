package jp.careapp.counseling.android.data.network
import com.google.gson.annotations.SerializedName


data class BlogResponse(
    @SerializedName("blog_category_id")
    val blogCategoryId: Int?,
    @SerializedName("content")
    val content: String?,
    @SerializedName("id")
    val id: Int?,
    @SerializedName("image")
    val image: String?,
    @SerializedName("imp")
    val imp: Int?,
    @SerializedName("title")
    val title: String?
)