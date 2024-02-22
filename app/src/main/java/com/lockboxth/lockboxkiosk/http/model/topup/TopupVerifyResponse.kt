package com.lockboxth.lockboxkiosk.http.model.topup

import android.os.Parcel
import android.os.Parcelable

data class TopupVerifyResponse(
    val txn: String,
    val member: TopupVerifyMember
)

data class TopupVerifyMember(
    val name: String?,
    val email: String?,
    val phone: String?
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(email)
        parcel.writeString(phone)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<TopupVerifyMember> {
        override fun createFromParcel(parcel: Parcel): TopupVerifyMember {
            return TopupVerifyMember(parcel)
        }

        override fun newArray(size: Int): Array<TopupVerifyMember?> {
            return arrayOfNulls(size)
        }
    }
}
