package com.lockboxth.lockboxkiosk

import android.content.Intent
import android.os.Bundle
import com.lockboxth.lockboxkiosk.helpers.BaseActivity
import kotlinx.android.synthetic.main.activity_courier_qr_summary.btnCancel
import kotlinx.android.synthetic.main.activity_courier_qr_summary.btnConfirm
import kotlinx.android.synthetic.main.activity_courier_qr_summary.tvCountdown
import kotlinx.android.synthetic.main.activity_courier_qr_summary.tvLockerNo
import kotlinx.android.synthetic.main.activity_courier_qr_summary.tvTrackingNo

class CourierQrSummaryActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_courier_qr_summary)

        val lockerNo = intent.getStringExtra("locker_no")
        val trackingNo = intent.getStringExtra("tracking_no")
        val lockerCommand = intent.getStringExtra("locker_commands")

        setTimeoutMinute(5, tvCountdown) {
            onCancel()
        }

        tvLockerNo.text = lockerNo
        tvTrackingNo.text = trackingNo

        btnCancel.setOnClickListener {
            onCancel()
        }

        btnConfirm.setOnClickListener {
            appPref.outType = "qr"
            val intent = Intent(this@CourierQrSummaryActivity, PaymentSuccessActivity::class.java)
            intent.putExtra("event_type", "-")
            intent.putExtra("locker_no", lockerNo)
            intent.putExtra("showSubtitle", false)
            intent.putExtra("locker_commands", lockerCommand)
            startActivity(intent)
            finish()
        }

    }
}