package com.lockboxth.lockboxkiosk.http.model.pudo

import android.os.Parcel
import android.os.Parcelable

data class PudoLocationRequest(
    val generalprofile_id: Int,
    val txn: String,
    val province_id: Int?,
    val amphur_id: Int?,
)

