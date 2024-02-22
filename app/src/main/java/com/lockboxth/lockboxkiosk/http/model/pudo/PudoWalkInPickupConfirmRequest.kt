package com.lockboxth.lockboxkiosk.http.model.pudo

import android.os.Parcel
import android.os.Parcelable

data class PudoWalkInPickupConfirmRequest(
    val generalprofile_id: Int,
    val txn: String,
    val type: String,
    val pickup_generalprofile_id: Int? = null
)

