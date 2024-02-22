package com.lockboxth.lockboxkiosk

import android.content.Intent
import android.os.Bundle
import com.lockboxth.lockboxkiosk.helpers.BaseActivity
import com.lockboxth.lockboxkiosk.helpers.TransactionType
import kotlinx.android.synthetic.main.activity_pudo_courier_select_type.*

class PudoCourierSelectTypeActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pudo_courier_select_type)


        btnRecieve.setOnClickListener {
            appPref.currentTransactionType = TransactionType.PUDO_COURIER_PICKUP
            val intent = Intent(this@PudoCourierSelectTypeActivity, PudoSelectShipmentActivity::class.java)
            startActivity(intent)
        }

        btnSender.setOnClickListener {
            appPref.currentTransactionType = TransactionType.PUDO_COURIER_SENDER
            val intent = Intent(this@PudoCourierSelectTypeActivity, QrCodeScannerActivity::class.java)
            startActivity(intent)
        }


        btnBack.setOnClickListener {
            onBackPressed()
        }

        setTimeoutSecond(15, null) {
            onCancel()
        }
    }
}