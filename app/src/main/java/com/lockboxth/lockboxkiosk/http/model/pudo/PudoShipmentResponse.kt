package com.lockboxth.lockboxkiosk.http.model.pudo

import android.os.Parcel
import android.os.Parcelable

data class PudoShipmentResponse(
    val txn: String,
    val shipments: List<PudoShipmentItem>
)


data class PudoShipmentItem(
    val shipment_id: Int,
    val name: String,
    val image_url: String,
    val fee: Int
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readInt()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(shipment_id)
        parcel.writeString(name)
        parcel.writeString(image_url)
        parcel.writeInt(fee)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PudoShipmentItem> {
        override fun createFromParcel(parcel: Parcel): PudoShipmentItem {
            return PudoShipmentItem(parcel)
        }

        override fun newArray(size: Int): Array<PudoShipmentItem?> {
            return arrayOfNulls(size)
        }
    }
}




