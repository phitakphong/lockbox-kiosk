package com.lockboxth.lockboxkiosk.http.model.pudo

import android.os.Parcel
import android.os.Parcelable

data class PudoCourierSelectResponse(
    val shipment_id: Int,
    val shipment_name: String,
    val pickup_method: String
)

