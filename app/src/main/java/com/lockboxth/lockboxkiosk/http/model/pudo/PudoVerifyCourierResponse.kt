package com.lockboxth.lockboxkiosk.http.model.pudo

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.JsonObject
import com.lockboxth.lockboxkiosk.helpers.Gsoner

data class PudoVerifyCourierResponse(
    val timer: Int,
    val count_parcel: Int,
    val parcel_list: List<PudoParcelListItem>
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readInt(),
        parcel.createTypedArrayList(PudoParcelListItem)!!
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(timer)
        parcel.writeInt(count_parcel)
        parcel.writeTypedList(parcel_list)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PudoVerifyCourierResponse> {
        override fun createFromParcel(parcel: Parcel): PudoVerifyCourierResponse {
            return PudoVerifyCourierResponse(parcel)
        }

        override fun newArray(size: Int): Array<PudoVerifyCourierResponse?> {
            return arrayOfNulls(size)
        }
    }
}

data class PudoParcelListItem(
    val order_id: Int,
    val tracking_number: String,
    val locker_no: String,
    val locker_size: String,
    val status: String,
    val status_text: String,
    val locker_commands: JsonObject
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        Gsoner.getInstance().gson.fromJson<JsonObject>(parcel.readString()!!, JsonObject::class.java)
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(order_id)
        parcel.writeString(tracking_number)
        parcel.writeString(locker_no)
        parcel.writeString(locker_size)
        parcel.writeString(status)
        parcel.writeString(status_text)
        parcel.writeString(Gsoner.getInstance().gson.toJson(locker_commands))
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PudoParcelListItem> {
        override fun createFromParcel(parcel: Parcel): PudoParcelListItem {
            return PudoParcelListItem(parcel)
        }

        override fun newArray(size: Int): Array<PudoParcelListItem?> {
            return arrayOfNulls(size)
        }
    }
}

