package com.lockboxth.lockboxkiosk

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.lockboxth.lockboxkiosk.adapter.AddressRecyclerAdapter
import com.lockboxth.lockboxkiosk.helpers.BaseActivity
import com.lockboxth.lockboxkiosk.helpers.Util
import com.lockboxth.lockboxkiosk.http.model.pudo.PudoKeyVal
import com.lockboxth.lockboxkiosk.http.model.pudo.PudoReceiverAddress
import com.lockboxth.lockboxkiosk.http.model.pudo.PudoRevieverResponse
import kotlinx.android.synthetic.main.activity_pickup_location.*
import kotlinx.android.synthetic.main.activity_pickup_location.tvCountdown

class PickupLocationActivity : BaseActivity() {

    private var selected: PudoReceiverAddress? = null
    private var data: PudoRevieverResponse? = null
    private var timeouted = false
    private var firstResume = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pickup_location)

        data = intent.getParcelableExtra("data")!!
        bindUI()

        setTimeoutMinute(10, tvCountdown) {
            timeouted = true
            if (recyclerView != null) {
                onCancel()
            }
        }

        btnAdd.setOnClickListener {
            val intent = Intent(this@PickupLocationActivity, PudoAddReceiverActivity::class.java)
            selectAddressResultLauncher.launch(intent)
        }

        btnConfirm.setOnClickListener {
            if (selected == null) {
                showMessage("โปรดเลือกที่อยู่")
                return@setOnClickListener
            }

            val intent = Intent(this@PickupLocationActivity, PudoPickupPointActivity::class.java)
            intent.putExtra("address_id", selected!!.address_id)
            startActivity(intent)
        }

        btnCancel.setOnClickListener {
            onCancel()
        }


    }

    private var selectAddressResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent = result.data!!
            this@PickupLocationActivity.data = data.getParcelableExtra("data")!!
            bindUI()
        }
    }

    private fun bindUI() {
        recyclerView.layoutManager = LinearLayoutManager(this)
        val adapter = AddressRecyclerAdapter(this, data!!.receiver_address)
        if (data?.receiver_selected != null) {
            selected = data!!.receiver_address.find { a -> a.address_id == data!!.receiver_selected }
            adapter.setSelected(selected!!)
        }
        adapter.onClick { a ->
            selected = a
        }
        recyclerView.adapter = adapter
    }

    override fun onResume() {
        super.onResume()
        if (!firstResume) {
            timerResume()
        }
        firstResume = false
        if (timeouted) {
            onCancel()
            return
        }
        Util.fullscreen(this.window.decorView)
    }
}