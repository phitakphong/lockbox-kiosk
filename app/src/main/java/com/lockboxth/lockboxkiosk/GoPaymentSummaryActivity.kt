package com.lockboxth.lockboxkiosk

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.lockboxth.lockboxkiosk.adapter.GoPaymentSummaryRecyclerAdapter
import com.lockboxth.lockboxkiosk.helpers.BaseActivity
import com.lockboxth.lockboxkiosk.helpers.Util
import com.lockboxth.lockboxkiosk.http.model.go.GoPaymentCancelRequest
import com.lockboxth.lockboxkiosk.http.model.go.GoPaymentConfirmRequest

import com.lockboxth.lockboxkiosk.http.model.locker.LockerCheckoutResponse
import com.lockboxth.lockboxkiosk.http.model.pudo.PudoReceiverCheckoutRequest
import com.lockboxth.lockboxkiosk.http.repository.GoRepository
import kotlinx.android.synthetic.main.activity_go_payment_summary.*

class GoPaymentSummaryActivity : BaseActivity() {
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_go_payment_summary)

        val paymentMethod = intent.getStringExtra("paymentMethod")!!
        val paymentMethodName = intent.getStringExtra("paymentMethodName")!!
        val promoCode = intent.getStringExtra("promoCode")

        layoutDiscount.visibility = View.GONE

        btnBack.setOnClickListener {
            onBackPressed()
        }

        btnCancel.setOnClickListener {
            showProgressDialog()
            GoRepository.getInstance().pickupCancel(
                GoPaymentCancelRequest(appPref.kioskInfo!!.generalprofile_id, appPref.currentTransactionId!!, "cancel"),
                onSuccess = {
                    hideProgressDialog()
                    goToMainActivity()
                },
                onFailure = { error ->
                    hideProgressDialog()
                    showMessage(error) {
                        goToMainActivity()
                    }
                }
            )
        }

        showProgressDialog()
        GoRepository.getInstance().pickupPaymentConfirm(
            GoPaymentConfirmRequest(appPref.kioskInfo!!.generalprofile_id, appPref.currentTransactionId!!, paymentMethod, promoCode),
            onSuccess = { resp ->

                tvAmount.text = Util.formatMoney(resp.total_amount) + " บาท"
                tvTitle.text = getString(R.string.go_service_summary).replace("XXX", Util.formatMoney(resp.time_limit))

                layoutDiscount.visibility = if (resp.discount_list.isEmpty()) {
                    View.GONE
                } else {
                    View.VISIBLE
                }
                val discountText = arrayListOf<String>()
                val discountAmt = arrayListOf<String>()
                resp.discount_list.forEach { d ->
                    discountText.add(d.detail)
                    discountAmt.add(Util.formatMoney(d.sum) + " บาท")
                }
                tvDiscountTxt.text = TextUtils.join("\n", discountText)
                tvDiscount.text = TextUtils.join("\n", discountAmt)
                tvAmountAfterDiscount.text = Util.formatMoney(resp.amount_pay) + " บาท"
                tvPaymentMethod.text = paymentMethodName

                recyclerView.layoutManager = LinearLayoutManager(this@GoPaymentSummaryActivity)
                recyclerView.adapter = GoPaymentSummaryRecyclerAdapter(this@GoPaymentSummaryActivity, resp.details)

                hideProgressDialog()
            },
            onFailure = { error ->
                hideProgressDialog()
                showMessage(error) {
                    goToMainActivity()
                }
            }
        )

        btnConfirm.setOnClickListener {
            submit()
        }

    }

    private fun submit() {
        showProgressDialog()
        GoRepository.getInstance().pickupCheckout(
            PudoReceiverCheckoutRequest(appPref.kioskInfo!!.generalprofile_id, appPref.currentTransactionId!!),
            onSuccess = { code, resp ->
                when (code) {
                    "OPEN" -> {
                        val cmdJson = Gson().toJson(resp.locker_commands)
                        val intent = Intent(this@GoPaymentSummaryActivity, CpConfirmOpenActivity::class.java)
                        intent.putExtra("locker_no", resp.locker_no)
                        intent.putExtra("locker_commands", cmdJson)
                        intent.putExtra("event_type", resp.event_type)
                        startActivity(intent)
                        finish()
                    }
                    "CHECKOUT" -> {
                        goPayment(resp)
                    }
                    else -> {}
                }
                hideProgressDialog()
            },
            onFailure = { error ->
                hideProgressDialog()
                showMessage(error) {
                    goToMainActivity()
                }
            }
        )
    }

    private fun goPayment(resp: LockerCheckoutResponse) {
        val intent = Intent(this@GoPaymentSummaryActivity, QrCodePaymentActivity::class.java)
        if (resp.timer != null) {
            intent.putExtra("timer", resp.timer)
        }
        if (resp.time_limit != null) {
            intent.putExtra("time_limit", resp.time_limit)
        }
        if (resp.payment_method != null) {
            intent.putExtra("payment_method", resp.payment_method)
        }
        if (resp.payment_method_display != null) {
            intent.putExtra("payment_method_display", resp.payment_method_display)
        }
        if (resp.amount_pay != null) {
            intent.putExtra("amount_pay", Util.formatMoney(resp.amount_pay))
        }
        if (resp.payment_vendor != null) {
            if (resp.payment_vendor["mch_order_no"] != null) {
                intent.putExtra("mch_order_no", resp.payment_vendor["mch_order_no"].toString())
            }
            if (resp.payment_vendor["ksher_order_no"] != null) {
                intent.putExtra("ksher_order_no", resp.payment_vendor["ksher_order_no"].toString())
            }
            if (resp.payment_vendor["send_sms"] != null) {
                intent.putExtra("send_sms", resp.payment_vendor["send_sms"].toString())
            }
            if (resp.payment_vendor["qrcode"] != null) {
                intent.putExtra("qrcode", resp.payment_vendor["qrcode"].toString())
            }
            if (resp.payment_vendor["order_id"] != null) {
                intent.putExtra("order_id", resp.payment_vendor["order_id"].toString())
            }
            if (resp.payment_vendor["qrcode"] != null) {
                intent.putExtra("qrcode", resp.payment_vendor["qrcode"].toString())
            }
            if (resp.payment_vendor["gateway_order_id"] != null) {
                intent.putExtra("gateway_order_id", resp.payment_vendor["gateway_order_id"].toString())
            }
            if (resp.payment_vendor["wallet_invoice"] != null) {
                intent.putExtra("wallet_invoice", resp.payment_vendor["wallet_invoice"].toString())
            }
            if (resp.payment_vendor["invoice"] != null) {
                intent.putExtra("invoice", resp.payment_vendor["invoice"].toString())
            }
            if (resp.payment_vendor["partner_txn"] != null) {
                intent.putExtra("partner_txn", resp.payment_vendor["partner_txn"].toString())
            }
        }
        if (resp.action != null) {
            intent.putExtra("action", resp.action)
        }
        if (resp.locker_no != null) {
            intent.putExtra("locker_no", resp.locker_no)
        }
        if (resp.money_change != null) {
            intent.putExtra("money_change", resp.money_change)
        }
        if (resp.has_slip != null) {
            intent.putExtra("has_slip", resp.has_slip)
        }
        startActivity(intent)
        finish()
    }

    override fun onResume() {
        super.onResume()
        setTimeoutMinute(5, tvCountdown) {
            btnCancel.performClick()
        }
    }
}