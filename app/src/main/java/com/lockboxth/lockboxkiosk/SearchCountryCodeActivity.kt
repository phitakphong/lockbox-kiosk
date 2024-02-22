package com.lockboxth.lockboxkiosk

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import com.lockboxth.lockboxkiosk.adapter.CountryRecyclerAdapter
import com.lockboxth.lockboxkiosk.helpers.BaseActivity
import com.lockboxth.lockboxkiosk.http.model.CountryItem
import com.lockboxth.lockboxkiosk.http.model.CountryList
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_search_country_code.*
import java.io.InputStream


class SearchCountryCodeActivity : BaseActivity() {

    private var selectedText: String? = null
    private var filterText: String? = null
    private var selectedCountry: CountryItem? = null

    private var countryList: ArrayList<CountryItem> = arrayListOf()
    private var _countryList: ArrayList<CountryItem> = arrayListOf()

    lateinit var adapter: CountryRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_country_code)

        adapter = CountryRecyclerAdapter(this@SearchCountryCodeActivity, ArrayList())

        val ims: InputStream = assets.open("country.json")
        val json = ims.bufferedReader().use { it.readText() }
        countryList = ArrayList(Gson().fromJson(json, CountryList::class.java).countries)

        val selectedCountryCode = intent.getStringExtra("dial_code")!!
        selectedCountry = countryList.first { c -> c.dial_code == selectedCountryCode }
        selectedText = selectedCountry!!.name!!.first().toString().uppercase()

        recyclerView.layoutManager = LinearLayoutManager(this@SearchCountryCodeActivity)
        adapter.onClick { c ->
            selectedCountry = c
        }
        adapter.setSelectedCountry(selectedCountry!!)
        recyclerView.adapter = adapter

        buildFirstCharFilter()
        etTextFilter.addTextChangedListener {
            filterText = it.toString()
            filter()
        }

        this.filter()

        btnBack.setOnClickListener {
            this.onBackPressed()
        }

        btnConfirm.setOnClickListener {
            val intent = Intent()
            intent.putExtra("dial_code", selectedCountry!!.dial_code)
            intent.putExtra("code", selectedCountry!!.code)
            setResult(RESULT_OK, intent)
            finish()
        }

    }


    private fun buildFirstCharFilter() {
        var c: Char = 'A'
        while (c <= 'Z') {
            val tv = TextView(this@SearchCountryCodeActivity)
            tv.tag = c.toString()
            if (c == 'A') {
                tv.setPadding(0, 0, 4, 0)
            } else {
                tv.setPadding(4, 0, 4, 0)
            }

            tv.setTextColor(ContextCompat.getColor(this@SearchCountryCodeActivity, R.color.white_3))
            tv.text = c.toString()
            if (selectedText != null && selectedText!!.uppercase() == c.toString()) {
                tv.setTypeface(null, Typeface.BOLD)
                tv.setTextColor(ContextCompat.getColor(this@SearchCountryCodeActivity, R.color.black))
            }

            tv.setOnClickListener {
                if (!selectedText.isNullOrEmpty()) {
                    val lastSelected = filterContainer.findViewWithTag<TextView>(selectedText!!)
                    lastSelected.setTypeface(null, Typeface.NORMAL)
                    lastSelected.setTextColor(ContextCompat.getColor(this@SearchCountryCodeActivity, R.color.white_3))
                }
                tv.setTypeface(null, Typeface.BOLD)
                tv.setTextColor(ContextCompat.getColor(this@SearchCountryCodeActivity, R.color.black))
                selectedText = tv.tag.toString().uppercase()
                this.filter()
            }

            filterContainer.addView(tv)
            ++c
        }

    }

    private fun filter() {
        _countryList = countryList
        if (selectedText != null) {
            _countryList = ArrayList(_countryList.filter { c -> c.name!!.first().toString().uppercase() == selectedText!!.uppercase() })
        }
        if (!filterText.isNullOrEmpty()) {
            _countryList = ArrayList(_countryList.filter { c -> c.name!!.uppercase().contains(filterText!!.uppercase()) })
        }
        adapter.setItems(_countryList)
    }
}