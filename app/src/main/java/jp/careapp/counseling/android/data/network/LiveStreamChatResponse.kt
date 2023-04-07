package jp.careapp.counseling.android.data.network

import android.os.Parcel
import android.os.Parcelable

data class LiveStreamChatResponse(
    val id: Long = 0,
    val isPerformer: Boolean = false,
    val isWhisper: Boolean = false,
    val name: String? = "",
    val message: String? = ""
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readByte() != 0.toByte(),
        parcel.readByte() != 0.toByte(),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeByte(if (isPerformer) 1 else 0)
        parcel.writeByte(if (isWhisper) 1 else 0)
        parcel.writeString(name)
        parcel.writeString(message)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<LiveStreamChatResponse> {
        override fun createFromParcel(parcel: Parcel): LiveStreamChatResponse {
            return LiveStreamChatResponse(parcel)
        }

        override fun newArray(size: Int): Array<LiveStreamChatResponse?> {
            return arrayOfNulls(size)
        }
    }
}
