package jp.slapp.android.android.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ParamsUpdateMember(
    @SerializedName("name")
    var name: String = " ",
    @SerializedName("sex")
    var gender: Int = 0,
    @SerializedName("birth")
    var birth: String = "",
) : Parcelable
