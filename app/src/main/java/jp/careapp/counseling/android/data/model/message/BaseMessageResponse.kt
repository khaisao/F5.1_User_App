package jp.careapp.counseling.android.data.model.message

import com.google.gson.annotations.SerializedName

open class BaseMessageResponse(
    val typeMessage: Int,
    @SerializedName("send_date") open var sendDate: String = ""
)
