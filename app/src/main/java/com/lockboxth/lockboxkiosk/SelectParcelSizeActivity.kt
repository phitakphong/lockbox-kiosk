package com.lockboxth.lockboxkiosk

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.lockboxth.lockboxkiosk.adapter.PudoParcelSizeRecyclerAdapter
import com.lockboxth.lockboxkiosk.helpers.BaseActivity
import com.lockboxth.lockboxkiosk.http.model.pudo.PudoKeyVal
import kotlinx.android.synthetic.main.activity_select_parcel_size.*

class SelectParcelSizeActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_parcel_size)

        val items = ArrayList(PudoParcelDetailActivity.parcelSizeItems)
        items.removeAt(0)

        recyclerView.layoutManager = LinearLayoutManager(this)
        val adapter = PudoParcelSizeRecyclerAdapter(this, items)
        adapter.setOnClick { item ->
            intent.putExtra("selected", item)
            setResult(RESULT_OK, intent)
            finish()
        }
        recyclerView.adapter = adapter

    }
}