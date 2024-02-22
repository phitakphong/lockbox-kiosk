package com.lockboxth.lockboxkiosk.http.model.pudo

import android.os.Parcel
import android.os.Parcelable

data class PudoVerifyIdCardRequest(
    val generalprofile_id: Int,
    val txn: String,
    val personal: PudoPersonal
)

data class PudoPersonal(
    val idcard: String,
    val fullname: String,
    val address: String
)
