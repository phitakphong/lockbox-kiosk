package com.lockboxth.lockboxkiosk.http.model.pudo

import android.os.Parcel
import android.os.Parcelable

data class PudoCourierParcelRequest(
    val generalprofile_id: Int,
    val qrcode: String,
    val screenshot: String,
)




