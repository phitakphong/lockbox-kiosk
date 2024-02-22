package com.lockboxth.lockboxkiosk.http.model.pudo

import android.os.Parcel
import android.os.Parcelable

data class PudoVerifyCourierRequest(
    val generalprofile_id: Int,
    val txn: String,
    val code: String
)




