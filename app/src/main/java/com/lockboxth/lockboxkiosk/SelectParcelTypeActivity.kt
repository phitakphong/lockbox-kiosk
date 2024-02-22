package com.lockboxth.lockboxkiosk

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.lockboxth.lockboxkiosk.adapter.PudoParcelTypeRecyclerAdapter
import com.lockboxth.lockboxkiosk.helpers.BaseActivity
import com.lockboxth.lockboxkiosk.http.model.pudo.PudoKeyVal
import kotlinx.android.synthetic.main.activity_select_parcel_type.*

class SelectParcelTypeActivity : BaseActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_parcel_type)

        val items = ArrayList(PudoParcelDetailActivity.parcelTypeItems)
        items.removeAt(0)

        recyclerView.layoutManager = LinearLayoutManager(this)
        val adapter = PudoParcelTypeRecyclerAdapter(this, items)
        adapter.setOnClick { item ->
            intent.putExtra("selected", item)
            setResult(RESULT_OK, intent)
            finish()
        }
        recyclerView.adapter = adapter

    }
}