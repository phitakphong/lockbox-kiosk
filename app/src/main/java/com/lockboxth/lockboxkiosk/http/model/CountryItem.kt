package com.lockboxth.lockboxkiosk.http.model

data class CountryItem(
    var name: String? = null,
    var dial_code: String? = null,
    var code: String? = null
)

data class CountryList(
    val countries: List<CountryItem>
)