package com.lockboxth.lockboxkiosk

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.lockboxth.lockboxkiosk.adapter.ServicePaymentSummaryRecyclerAdapter
import com.lockboxth.lockboxkiosk.helpers.BaseActivity
import com.lockboxth.lockboxkiosk.helpers.Util
import com.lockboxth.lockboxkiosk.http.model.pudo.PudoReceiverConfirmPaymentResponse
import kotlinx.android.synthetic.main.activity_pudo_service_fee_summary.*

class PudoServiceFeeSummaryActivity : BaseActivity() {

    companion object {
        var data: PudoReceiverConfirmPaymentResponse? = null
    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pudo_service_fee_summary)

        layoutCod.visibility = View.GONE
        val item = data!!

        tvTitleOut.text = "${getString(R.string.total_time)} ${item.time_use_hour} ${getString(R.string.hour)} ${item.time_use_min} ${getString(R.string.time_left_min)}"
        if (item.parcel_cod != null && item.parcel_cod > 0) {
            layoutCod.visibility = View.VISIBLE
            tvCodAmt.text = Util.formatMoney(item.parcel_cod)
        }

        recyclerView.layoutManager = LinearLayoutManager(this@PudoServiceFeeSummaryActivity)
        recyclerView.adapter = ServicePaymentSummaryRecyclerAdapter(this@PudoServiceFeeSummaryActivity, item.details, false)

        val totalLabel = arrayListOf<String>()
        val totalAmt = arrayListOf<String>()

        totalLabel.add("ค่าบริการทั้งหมด")
        totalAmt.add("${Util.formatMoney(item.total_amount)} บาท")

        totalLabel.add("ค่าบริการส่วนเกิน")
        totalAmt.add("${Util.formatMoney(item.service_fee)} บาท")

        tvTotalText.text = TextUtils.join("\n", totalLabel)
        tvTotalAmount.text = TextUtils.join("\n", totalAmt)

        tvAmountPay.text = "${Util.formatMoney(item.amount_pay)} บาท"

        setTimeoutMinute(5, tvCountdown) {
            onCancel()
        }

        btnBack.setOnClickListener {
            onBackPressed()
        }

        btnCancel.setOnClickListener {
            onCancel()
        }

        btnConfirm.setOnClickListener {
            val intent = Intent(this@PudoServiceFeeSummaryActivity, PaymentMethodActivity::class.java)
            startActivity(intent)
        }
    }
}