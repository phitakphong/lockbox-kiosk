package com.lockboxth.lockboxkiosk.http.model.pudo

import android.os.Parcel
import android.os.Parcelable

data class PudoCourierSelectRequest(
    val generalprofile_id: Int,
    val txn: String,
    val shipment_id: Int
)

