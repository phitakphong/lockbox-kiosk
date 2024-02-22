package com.lockboxth.lockboxkiosk.http.model.pudo

import android.os.Parcel
import android.os.Parcelable

data class PudoWalkInConsentResponse(
    val parcel_category: List<PudoKeyVal>,
    val parcel_size: List<PudoKeyVal>,
    val bank_account: List<PudoBankAccount>,
    val bank_account_default: PudoBankAccount?
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.createTypedArrayList(PudoKeyVal)!!,
        parcel.createTypedArrayList(PudoKeyVal)!!,
        parcel.createTypedArrayList(PudoBankAccount)!!,
        parcel.readParcelable(PudoBankAccount::class.java.classLoader)
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeTypedList(parcel_category)
        parcel.writeTypedList(parcel_size)
        parcel.writeTypedList(bank_account)
        parcel.writeParcelable(bank_account_default, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PudoWalkInConsentResponse> {
        override fun createFromParcel(parcel: Parcel): PudoWalkInConsentResponse {
            return PudoWalkInConsentResponse(parcel)
        }

        override fun newArray(size: Int): Array<PudoWalkInConsentResponse?> {
            return arrayOfNulls(size)
        }
    }
}

data class PudoKeyVal(
    val key: Int,
    val value: String,
    val zip_code: String?
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString()!!,
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(key)
        parcel.writeString(value)
        parcel.writeString(zip_code)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PudoKeyVal> {
        override fun createFromParcel(parcel: Parcel): PudoKeyVal {
            return PudoKeyVal(parcel)
        }

        override fun newArray(size: Int): Array<PudoKeyVal?> {
            return arrayOfNulls(size)
        }
    }
}

data class PudoBankAccount(
    val bankacc_id: Int,
    val bank_name: String,
    val bank_image: String,
    val account_name: String,
    val short_account_id: String,
    val censor_account_id: String
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(bankacc_id)
        parcel.writeString(bank_name)
        parcel.writeString(bank_image)
        parcel.writeString(account_name)
        parcel.writeString(short_account_id)
        parcel.writeString(censor_account_id)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PudoBankAccount> {
        override fun createFromParcel(parcel: Parcel): PudoBankAccount {
            return PudoBankAccount(parcel)
        }

        override fun newArray(size: Int): Array<PudoBankAccount?> {
            return arrayOfNulls(size)
        }
    }
}