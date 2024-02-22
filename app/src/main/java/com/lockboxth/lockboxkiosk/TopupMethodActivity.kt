package com.lockboxth.lockboxkiosk

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import com.lockboxth.lockboxkiosk.adapter.TopupMethodRecyclerAdapter
import com.lockboxth.lockboxkiosk.helpers.BaseActivity
import com.lockboxth.lockboxkiosk.helpers.Util
import com.lockboxth.lockboxkiosk.http.model.topup.TopupCheckoutRequest
import com.lockboxth.lockboxkiosk.http.model.topup.TopupConfirmRequest
import com.lockboxth.lockboxkiosk.http.repository.TopupRepository
import com.otaliastudios.cameraview.CameraListener
import com.otaliastudios.cameraview.CameraView
import com.otaliastudios.cameraview.PictureResult
import kotlinx.android.synthetic.main.activity_topup_method.*
import kotlinx.android.synthetic.main.activity_topup_method.btnBack
import kotlinx.android.synthetic.main.activity_topup_method.btnCancel
import kotlinx.android.synthetic.main.activity_topup_method.btnConfirm
import kotlinx.android.synthetic.main.activity_topup_method.camera
import kotlinx.android.synthetic.main.activity_topup_method.recyclerView
import kotlinx.android.synthetic.main.activity_topup_method.tvCountdown
import kotlinx.android.synthetic.main.activity_total_payment_summary.*
import java.io.ByteArrayOutputStream
import java.util.Base64


class TopupMethodActivity : BaseActivity() {

    private var adapter: TopupMethodRecyclerAdapter? = null

//
//    private var camera: Camera? = null
//    private var preview: CameraPreview? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_topup_method)

        val total = intent.getIntExtra("total", 0)

        btnBack.setOnClickListener {
            onBackPressed()
        }

        showProgressDialog()
        TopupRepository.getInstance().confirm(
            TopupConfirmRequest(appPref.kioskInfo!!.generalprofile_id, appPref.currentTransactionId!!, total),
            onSuccess = { resp ->
                tvPrice.text = Util.formatMoney(resp.amount)
                recyclerView.layoutManager = GridLayoutManager(this@TopupMethodActivity, 5)
                adapter = TopupMethodRecyclerAdapter(this@TopupMethodActivity, resp.payment_access)
                recyclerView.adapter = adapter
                hideProgressDialog()
            },
            onFailure = { error ->
                hideProgressDialog()
                showMessage(error)
            }
        )

        btnCancel.setOnClickListener {
            onCancel()
        }

        btnConfirm.setOnClickListener {
            if (adapter?.selectedItem == null) {
                showMessage(getString(R.string.select_topup_method))
                return@setOnClickListener
            }
            showProgressDialog()
            if (this.camera.isOpened) {
                this.camera.takePicture()
            } else {
                submit()
            }

        }

        val camera = findViewById<CameraView>(R.id.camera)
        camera.setLifecycleOwner(this)
        camera.addCameraListener(object : CameraListener() {
            @SuppressLint("WrongThread")
            override fun onPictureTaken(result: PictureResult) {
                super.onPictureTaken(result)

                result.toBitmap { bmp ->
                    val b = Bitmap.createScaledBitmap(bmp!!, 200, 200, false)
                    val stream = ByteArrayOutputStream()
                    b.compress(Bitmap.CompressFormat.PNG, 90, stream)
                    val image = stream.toByteArray()
                    val base64 = Util.toBase64(image)
                    submit(base64)
                }
            }
        })
    }

    private fun submit(base64: String = "") {
        val req = TopupCheckoutRequest(appPref.currentTransactionId!!, appPref.kioskInfo!!.generalprofile_id, adapter!!.selectedItem!!.value, base64)
        TopupRepository.getInstance().checkout(
            req,
            onSuccess = { resp ->

                hideProgressDialog()

                val intent = Intent(this@TopupMethodActivity, QrCodePaymentActivity::class.java)
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
            },
            onFailure = { error ->
                hideProgressDialog()
                showMessage(getString(R.string.cannot_topup))
            }
        )
    }

    override fun onResume() {
        super.onResume()
        setTimeoutMinute(5, tvCountdown) {
            onCancel()
        }
    }

}