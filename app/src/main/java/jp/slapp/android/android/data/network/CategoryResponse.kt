package jp.slapp.android.android.data.network

import androidx.recyclerview.widget.DiffUtil
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class CategoryResponse(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("default_content") val defaultContent: String,
    @SerializedName("regist_enable") val registEnable: Boolean = false
) : Serializable

object CategoryDiffCallBack: DiffUtil.ItemCallback<CategoryResponse>() {

    override fun areItemsTheSame(oldItem: CategoryResponse, newItem: CategoryResponse): Boolean = oldItem.id == newItem.id
    override fun areContentsTheSame(oldItem: CategoryResponse, newItem: CategoryResponse): Boolean = oldItem == newItem
}