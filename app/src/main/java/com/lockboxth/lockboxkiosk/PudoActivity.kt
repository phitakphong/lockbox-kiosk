package com.lockboxth.lockboxkiosk

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.util.Log
import androidx.core.view.drawToBitmap
import com.lockboxth.lockboxkiosk.helpers.BaseActivity
import com.lockboxth.lockboxkiosk.helpers.ParcelType
import com.lockboxth.lockboxkiosk.helpers.TransactionType
import com.lockboxth.lockboxkiosk.print.PrintUtil
import kotlinx.android.synthetic.main.activity_pudo.*

class PudoActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pudo)


        btnSender.setOnClickListener {
            appPref.currentParcelType = ParcelType.SENDER
            val intent = Intent(this@PudoActivity, SelectUserTypeActivity::class.java)
            startActivity(intent)
        }

        btnReceive.setOnClickListener {
            appPref.currentParcelType = ParcelType.RECEIVER
            val intent = Intent(this@PudoActivity, SelectUserTypeActivity::class.java)
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