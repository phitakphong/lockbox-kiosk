package com.lockboxth.lockboxkiosk.http.model.pudo

import android.os.Parcel
import android.os.Parcelable

data class PudoParcelAddressResponse(
    val order_no: String,
    val create_at: String,
    val cod_amt: Int?,
    val sender: PudoLocation,
    val receiver: PudoLocation,
    val parcel_detail: PudoParcelDetail,
    val shipment_detail: PudoShipmentDetail,
    val payment_method: String?,
    val sender_address: PudoAddress,
    val receiver_address: PudoAddress
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readParcelable(PudoLocation::class.java.classLoader)!!,
        parcel.readParcelable(PudoLocation::class.java.classLoader)!!,
        parcel.readParcelable(PudoParcelDetail::class.java.classLoader)!!,
        parcel.readParcelable(PudoShipmentDetail::class.java.classLoader)!!,
        parcel.readString(),
        parcel.readParcelable(PudoAddress::class.java.classLoader)!!,
        parcel.readParcelable(PudoAddress::class.java.classLoader)!!
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(order_no)
        parcel.writeString(create_at)
        parcel.writeValue(cod_amt)
        parcel.writeParcelable(sender, flags)
        parcel.writeParcelable(receiver, flags)
        parcel.writeParcelable(parcel_detail, flags)
        parcel.writeParcelable(shipment_detail, flags)
        parcel.writeString(payment_method)
        parcel.writeParcelable(sender_address, flags)
        parcel.writeParcelable(receiver_address, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PudoParcelAddressResponse> {
        override fun createFromParcel(parcel: Parcel): PudoParcelAddressResponse {
            return PudoParcelAddressResponse(parcel)
        }

        override fun newArray(size: Int): Array<PudoParcelAddressResponse?> {
            return arrayOfNulls(size)
        }
    }
}

data class PudoLocation(
    val location_type: String,
    val location_name: String
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(location_type)
        parcel.writeString(location_name)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PudoLocation> {
        override fun createFromParcel(parcel: Parcel): PudoLocation {
            return PudoLocation(parcel)
        }

        override fun newArray(size: Int): Array<PudoLocation?> {
            return arrayOfNulls(size)
        }
    }
}

data class PudoParcelDetail(
    val category_name: String,
    val size_name: String,
    val weight: Float
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readFloat()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(category_name)
        parcel.writeString(size_name)
        parcel.writeFloat(weight)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PudoParcelDetail> {
        override fun createFromParcel(parcel: Parcel): PudoParcelDetail {
            return PudoParcelDetail(parcel)
        }

        override fun newArray(size: Int): Array<PudoParcelDetail?> {
            return arrayOfNulls(size)
        }
    }
}

data class PudoShipmentDetail(
    val name: String,
    val image_url: String
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(image_url)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PudoShipmentDetail> {
        override fun createFromParcel(parcel: Parcel): PudoShipmentDetail {
            return PudoShipmentDetail(parcel)
        }

        override fun newArray(size: Int): Array<PudoShipmentDetail?> {
            return arrayOfNulls(size)
        }
    }
}

data class PudoAddress(
    val full_name: String,
    val phone: String,
    val idcard: String?,
    val full_address: String
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString(),
        parcel.readString()!!
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(full_name)
        parcel.writeString(phone)
        parcel.writeString(idcard)
        parcel.writeString(full_address)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PudoAddress> {
        override fun createFromParcel(parcel: Parcel): PudoAddress {
            return PudoAddress(parcel)
        }

        override fun newArray(size: Int): Array<PudoAddress?> {
            return arrayOfNulls(size)
        }
    }
}
