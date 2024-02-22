package com.lockboxth.lockboxkiosk.http.model.payment

import android.os.Parcel
import android.os.Parcelable

data class PaymentMethodResponse(
    val name: String,
    val value: String,
    val discount_amount: String,
    val discount_unit: String,
    val img_dat: String,
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(value)
        parcel.writeString(discount_amount)
        parcel.writeString(discount_unit)
        parcel.writeString(img_dat)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PaymentMethodResponse> {
        override fun createFromParcel(parcel: Parcel): PaymentMethodResponse {
            return PaymentMethodResponse(parcel)
        }

        override fun newArray(size: Int): Array<PaymentMethodResponse?> {
            return arrayOfNulls(size)
        }
    }
}
