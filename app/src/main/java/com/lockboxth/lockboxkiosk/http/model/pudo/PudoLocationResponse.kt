package com.lockboxth.lockboxkiosk.http.model.pudo

import android.os.Parcel
import android.os.Parcelable

data class PudoLocationResponse(
    val province_option: ArrayList<PudoKeyVal>?,
    val amphur_option: ArrayList<PudoKeyVal>?,
    val district_option: ArrayList<PudoKeyVal>?
)
