package com.lockboxth.lockboxkiosk.http.model.pudo

import android.os.Parcel
import android.os.Parcelable

data class PudoWalkInParcelConfirmRequest(
    val generalprofile_id: Int,
    val txn: String,
    val parcel_category_id: Int,
    val parcel_size_id: Int,
    val weight: Float,
    val has_cod: Boolean,
    var width: Float? = null,
    var height: Float? = null,
    var length: Float? = null,
    var cod: Float? = null,
    var bankacc_id: Int? = null
)
