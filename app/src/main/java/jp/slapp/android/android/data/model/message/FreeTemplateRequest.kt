package jp.slapp.android.android.data.model.message

import com.google.gson.annotations.SerializedName

data class FreeTemplateRequest(
    @SerializedName("performer_code") val performerCode: String,
    @SerializedName("template_id") val templateId: Int
)
