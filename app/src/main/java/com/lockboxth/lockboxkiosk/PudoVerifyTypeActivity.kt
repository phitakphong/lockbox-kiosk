package com.lockboxth.lockboxkiosk

import android.content.Intent
import android.os.Bundle
import com.lockboxth.lockboxkiosk.helpers.BaseActivity
import kotlinx.android.synthetic.main.activity_pudo_verify_type.*

class PudoVerifyTypeActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pudo_verify_type)

        btnQrCode.setOnClickListener {
            val intent = Intent(this@PudoVerifyTypeActivity, QrCodeScannerActivity::class.java)
            startActivity(intent)
        }

        btnPin.setOnClickListener {
            val intent = Intent(this@PudoVerifyTypeActivity, LockerPasswordActivity::class.java)
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