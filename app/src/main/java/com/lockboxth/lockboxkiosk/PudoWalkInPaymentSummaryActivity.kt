package com.lockboxth.lockboxkiosk

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import com.lockboxth.lockboxkiosk.helpers.BaseActivity
import com.lockboxth.lockboxkiosk.helpers.Util
import com.lockboxth.lockboxkiosk.http.model.locker.LockerCheckoutRequest
import com.lockboxth.lockboxkiosk.http.model.locker.LockerCheckoutResponse
import com.lockboxth.lockboxkiosk.http.model.pudo.PudoReceiverPaymentConfirmRequest
import com.lockboxth.lockboxkiosk.http.repository.PudoRepository
import com.otaliastudios.cameraview.CameraListener
import com.otaliastudios.cameraview.CameraView
import com.otaliastudios.cameraview.PictureResult
import kotlinx.android.synthetic.main.activity_pudo_walk_in_payment_summary.*
import java.io.ByteArrayOutputStream

class PudoWalkInPaymentSummaryActivity : BaseActivity() {
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pudo_walk_in_payment_summary)

        val paymentMethod = intent.getStringExtra("paymentMethod")!!
        val paymentMethodName = intent.getStringExtra("paymentMethodName")!!
        val promoCode = intent.getStringExtra("promoCode")

        layoutShiping.visibility = View.GONE
        layoutDiscount.visibility = View.GONE

        btnBack.setOnClickListener {
            onBackPressed()
        }

        btnCancel.setOnClickListener {
            onCancel()
        }

        showProgressDialog()
        PudoRepository.getInstance().walkinConfirm(
            PudoReceiverPaymentConfirmRequest(appPref.kioskInfo!!.generalprofile_id, appPref.currentTransactionId!!, paymentMethod, promoCode),
            onSuccess = { resp ->

                tvAmount.text = Util.formatMoney(resp.amount_total) + " บาท"
                layoutShiping.visibility = if (resp.fee_data.details.isNotEmpty()) {
                    View.VISIBLE
                } else {
                    View.GONE
                }
                val detailText = arrayListOf<String>()
                val detailAmt = arrayListOf<String>()
                resp.fee_data.details.forEach { d ->
                    detailText.add(d.detail)
                    detailAmt.add(Util.formatMoney(d.fee) + " บาท")
                }
                tvShipingTxt.text = TextUtils.join("\n", detailText)
                tvShipingAmt.text = TextUtils.join("\n", detailAmt)

                layoutDiscount.visibility = if (resp.fee_data.discount_list.isNotEmpty()) {
                    View.VISIBLE
                } else {
                    View.GONE
                }
                val discountText = arrayListOf<String>()
                val discountAmt = arrayListOf<String>()
                resp.fee_data.discount_list.forEach { d ->
                    discountText.add(d.detail)
                    discountAmt.add(Util.formatMoney(d.sum) + " บาท")
                }
                tvDiscountTxt.text = TextUtils.join("\n", discountText)
                tvDiscount.text = TextUtils.join("\n", discountAmt)
                tvAmountAfterDiscount.text = Util.formatMoney(resp.amount_pay) + " บาท"
                tvPaymentMethod.text = paymentMethodName

                hideProgressDialog()
            },
            onFailure = { error ->
                hideProgressDialog()
                showMessage(error.message!!) {
                    finish()
                }
            }
        )

        val camera = findViewById<CameraView>(R.id.camera)
        camera.setLifecycleOwner(this)
        camera.addCameraListener(object : CameraListener() {
            override fun onPictureTaken(result: PictureResult) {
                super.onPictureTaken(result)
                submit(result)
            }
        })

        btnConfirm.setOnClickListener {
            if (camera.isOpened) {
                showProgressDialog()
                this.camera.takePicture()
            } else {
                submit()
            }
        }

    }

    @SuppressLint("WrongThread")
    private fun submit(result: PictureResult) {

        result.toBitmap { bmp ->
            val b = Bitmap.createScaledBitmap(bmp!!, 200, 200, false)
            val stream = ByteArrayOutputStream()
            b.compress(Bitmap.CompressFormat.PNG, 90, stream)
            val image = stream.toByteArray()
            val base64 = Util.toBase64(image)
            submit(base64)
        }
    }

    private fun submit(base64: String = "") {
        val req = LockerCheckoutRequest(appPref.kioskInfo!!.generalprofile_id, appPref.currentTransactionId!!, base64)

        PudoRepository.getInstance().walkinCheckout(
            req,
            onSuccess = { resp ->
                hideProgressDialog()
                goPayment(resp)
            },
            onFailure = { error ->
                hideProgressDialog()
                showMessage(getString(R.string.cannot_payment))
            }
        )
    }

    private fun goPayment(resp: LockerCheckoutResponse) {
        val intent = Intent(this@PudoWalkInPaymentSummaryActivity, QrCodePaymentActivity::class.java)
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
    }

    override fun onResume() {
        super.onResume()
        setTimeoutMinute(5, tvCountdown) {
            onCancel()
        }
    }
}