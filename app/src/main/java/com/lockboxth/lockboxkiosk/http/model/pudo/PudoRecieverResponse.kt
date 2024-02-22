package com.lockboxth.lockboxkiosk.http.model.pudo

import android.os.Parcel
import android.os.Parcelable

data class PudoRevieverResponse(
    val receiver_selected: Int?,
    val receiver_address: List<PudoReceiverAddress>
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.createTypedArrayList(PudoReceiverAddress)!!
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(receiver_selected)
        parcel.writeTypedList(receiver_address)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PudoRevieverResponse> {
        override fun createFromParcel(parcel: Parcel): PudoRevieverResponse {
            return PudoRevieverResponse(parcel)
        }

        override fun newArray(size: Int): Array<PudoRevieverResponse?> {
            return arrayOfNulls(size)
        }
    }
}

data class PudoReceiverAddress(
    val address_id: Int,
    val full_name: String,
    val phone: String,
    val full_address: String
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(address_id)
        parcel.writeString(full_name)
        parcel.writeString(phone)
        parcel.writeString(full_address)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PudoReceiverAddress> {
        override fun createFromParcel(parcel: Parcel): PudoReceiverAddress {
            return PudoReceiverAddress(parcel)
        }

        override fun newArray(size: Int): Array<PudoReceiverAddress?> {
            return arrayOfNulls(size)
        }
    }
}