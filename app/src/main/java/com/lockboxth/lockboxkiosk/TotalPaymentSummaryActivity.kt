package com.lockboxth.lockboxkiosk

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.lockboxth.lockboxkiosk.adapter.ServicePaymentSummaryRecyclerAdapter
import com.lockboxth.lockboxkiosk.customdialog.AlertMessageDialog
import com.lockboxth.lockboxkiosk.customdialog.CustomDialog
import com.lockboxth.lockboxkiosk.customdialog.PaymentTimeoutDialog
import com.lockboxth.lockboxkiosk.helpers.BaseActivity
import com.lockboxth.lockboxkiosk.helpers.TransactionType
import com.lockboxth.lockboxkiosk.helpers.Util
import com.lockboxth.lockboxkiosk.http.model.locker.*
import com.lockboxth.lockboxkiosk.http.model.pudo.PudoReceiverCheckoutRequest
import com.lockboxth.lockboxkiosk.http.model.pudo.PudoReceiverPaymentConfirmRequest
import com.lockboxth.lockboxkiosk.http.repository.LockerRepository
import com.lockboxth.lockboxkiosk.http.repository.PudoRepository
import com.otaliastudios.cameraview.CameraListener
import com.otaliastudios.cameraview.CameraView
import com.otaliastudios.cameraview.PictureResult
import kotlinx.android.synthetic.main.activity_topup_method.*
import kotlinx.android.synthetic.main.activity_total_payment_summary.*
import kotlinx.android.synthetic.main.activity_total_payment_summary.btnBack
import kotlinx.android.synthetic.main.activity_total_payment_summary.btnCancel
import kotlinx.android.synthetic.main.activity_total_payment_summary.btnConfirm
import kotlinx.android.synthetic.main.activity_total_payment_summary.camera
import kotlinx.android.synthetic.main.activity_total_payment_summary.recyclerView
import kotlinx.android.synthetic.main.activity_total_payment_summary.tvCountdown
import kotlinx.android.synthetic.main.recycler_locker_item.*
import java.io.ByteArrayOutputStream

class TotalPaymentSummaryActivity : BaseActivity() {

    var calcTotal = false
    var price: Float = 0f


    var promoCode: String? = null
    var paymentMethod = ""
    var paymentMethodName = ""
    var rfidAmountPay: Float = 0f

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_total_payment_summary)

        calcTotal = intent.getBooleanExtra("calcTotal", false)

        btnBack.setOnClickListener {
            onBackPressed()
        }

        btnCancel.setOnClickListener {
            onCancel()
        }

        init()

        btnConfirm.setOnClickListener {
            if (calcTotal && price != 0f) {
                val intent = Intent(this@TotalPaymentSummaryActivity, PaymentMethodActivity::class.java)
                intent.putExtra("price", price.toString())
                startActivity(intent)
                return@setOnClickListener
            }
            if (paymentMethod != "rfid") {
                when (appPref.currentTransactionType) {
                    TransactionType.PUDO_RECEIVER -> {
                        submitPudo()
                    }
                    else -> {
                        showProgressDialog()
                        if (camera.isOpened) {
                            this.camera.takePicture()
                        } else {
                            submit()
                        }
                    }
                }
            } else {
                val intent = Intent(this@TotalPaymentSummaryActivity, RfidPaymentActivity::class.java)
                intent.putExtra("payment_method", paymentMethod)
                intent.putExtra("payment_method_display", paymentMethodName)
                intent.putExtra("amount_pay", Util.formatMoney(rfidAmountPay))
                startActivity(intent)
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
        LockerRepository.getInstance().checkout(
            appPref.currentTransactionType!!,
            req,
            onSuccess = { resp ->
                hideProgressDialog()

                if (resp.money_change != null && resp.money_change == 0) {
                    val intent = Intent(this@TotalPaymentSummaryActivity, PaymentSuccessActivity::class.java)
                    intent.putExtra("locker_no", resp.locker_no)
                    if (resp.locker_commands != null) {
                        intent.putExtra("locker_commands", Gson().toJson(resp.locker_commands))
                    }
                    intent.putExtra("event_type", resp.event_type)
                    startActivity(intent)
                    return@checkout
                }

                goPayment(resp)

            },
            onFailure = { error ->
                hideProgressDialog()
                showMessage("ไม่สามารถชำระเงินผ่านช่องทางนี้ได้ให้เลือกช่องทางอื่น")
            }
        )
    }

    private fun submitPudo() {
        showProgressDialog()
        PudoRepository.getInstance().receiverCheckout(
            PudoReceiverCheckoutRequest(
                appPref.kioskInfo!!.generalprofile_id,
                appPref.currentTransactionId!!
            ),
            onSuccess = { resp ->
                hideProgressDialog()
                goPayment(resp)
            },
            onFailure = { error ->
                hideProgressDialog()
                showMessage(error.message!!)
            }
        )
    }

    private fun goPayment(resp: LockerCheckoutResponse) {
        val intent = Intent(this@TotalPaymentSummaryActivity, QrCodePaymentActivity::class.java)
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

    @SuppressLint("SetTextI18n")
    private fun init() {

        tvTitleIn.visibility = View.GONE
        tvTitleOut.visibility = View.GONE
        layoutTotalIn.visibility = View.GONE
        layoutTotalOut.visibility = View.GONE

        val camera = findViewById<CameraView>(R.id.camera)

        camera.setLifecycleOwner(this)
        camera.addCameraListener(object : CameraListener() {
            override fun onPictureTaken(result: PictureResult) {
                super.onPictureTaken(result)
                submit(result)
            }
        })

        showProgressDialog()

        if (appPref.currentTransactionType == TransactionType.IN || appPref.currentTransactionType == TransactionType.GO_IN || (appPref.currentTransactionType == TransactionType.OUT && !calcTotal)) {


            layoutTotalIn.visibility = View.VISIBLE

            promoCode = intent.getStringExtra("promoCode")
            paymentMethod = intent.getStringExtra("paymentMethod")!!
            paymentMethodName = intent.getStringExtra("paymentMethodName")!!

            layoutPromoDiscount.visibility = View.GONE
            layoutMemberDiscount.visibility = View.GONE

            LockerRepository.getInstance().calcDetail(
                appPref.currentTransactionType!!,
                LockerCalculateDetailRequest(appPref.kioskInfo!!.generalprofile_id, appPref.currentTransactionId!!, "detail", paymentMethod, promoCode),
                onSuccess = { resp ->
                    initSummaryLayout(resp.service_fee.details, resp.calculation)

                    tvTitleIn.visibility = View.VISIBLE
                    tvTitleIn.text = tvTitleIn.text.toString().replace("2", resp.service_fee.time_limit.toString())

                    hideProgressDialog()
                },
                onFailure = { error ->
                    hideProgressDialog()
                    if (error.code == "DEVICE_NOT_FOUND") {
                        CustomDialog.newInstance(R.layout.dialog_require_register_payment).apply {
                            onOkClickListener = {
                                finish()
                            }
                            timeoutCallback = {
                                onCancel()
                            }
                        }.run {
                            show(supportFragmentManager, "")
                        }
                    } else if (error.code == "MONEY_ENOUGH") {
                        AlertMessageDialog.newInstance(error.message!!).apply {
                            timeoutCallback = {
                                goToMainActivity()
                            }
                            onAcceptClickListener = {
                                finish()
                            }
                        }.run {
                            show(supportFragmentManager, "")
                        }
                    } else {
                        showMessage(error.message!!) {
                            finish()
                        }
                    }
                }
            )

        } else if (appPref.currentTransactionType == TransactionType.OUT) {

            tvTitle.text = "สรุปการใช้งาน"

            val req = LockerCalculateRequest(appPref.kioskInfo!!.generalprofile_id, appPref.currentTransactionId!!, "total", false)
            LockerRepository.getInstance().calcOut(
                req,
                onSuccess = { resp ->

                    tvTitleOut.text = "${getString(R.string.total_time)} ${resp.time_use_hour} ${getString(R.string.hour)} ${resp.time_use_min} ${getString(R.string.time_left_min)}"

                    recyclerView.layoutManager = LinearLayoutManager(this@TotalPaymentSummaryActivity)
                    recyclerView.adapter = ServicePaymentSummaryRecyclerAdapter(this@TotalPaymentSummaryActivity, resp.details)

                    tvAlreadyPaid.text = "${Util.formatMoney(resp.already_paid)} ${getString(R.string.bath)}"
                    tvTotalAmount.text = "${Util.formatMoney(resp.total_amount)} ${getString(R.string.bath)}"
                    tvAmountPay.text = "${Util.formatMoney(resp.amount_pay)} ${getString(R.string.bath)}"

                    price = resp.amount_pay

                    tvTitleOut.visibility = View.VISIBLE
                    layoutTotalOut.visibility = View.VISIBLE

                    hideProgressDialog()

                },
                onFailure = { error ->
                    hideProgressDialog()
                    if (error == "DEVICE_NOT_FOUND") {
                        CustomDialog.newInstance(R.layout.dialog_require_register_payment).apply {
                            onOkClickListener = {
                                finish()
                            }
                            timeoutCallback = {
                                onCancel()
                            }
                        }.run {
                            show(supportFragmentManager, "")
                        }
                    } else {
                        showMessage(error)
                        finish()
                    }
                }
            )
        } else if (appPref.currentTransactionType == TransactionType.PUDO_RECEIVER) {

            tvTitleOut.visibility = View.VISIBLE
            layoutTotalIn.visibility = View.VISIBLE
//            layoutPromoDiscount.visibility = View.GONE
//            layoutMemberDiscount.visibility = View.GONE
            tvTitle.text = "สรุปค่าบริการที่ต้องชำระ"

            promoCode = intent.getStringExtra("promoCode")
            paymentMethod = intent.getStringExtra("paymentMethod")!!
            paymentMethodName = intent.getStringExtra("paymentMethodName")!!

            PudoRepository.getInstance().receiverPaymentConfirm(
                PudoReceiverPaymentConfirmRequest(
                    appPref.kioskInfo!!.generalprofile_id,
                    appPref.currentTransactionId!!,
                    paymentMethod,
                    promoCode
                ),
                onSuccess = { resp ->
                    tvTitleOut.text = "${getString(R.string.total_time)} ${resp.time_use_hour} ${getString(R.string.hour)} ${resp.time_use_min} ${getString(R.string.time_left_min)}"

                    recyclerView.layoutManager = LinearLayoutManager(this@TotalPaymentSummaryActivity)
                    recyclerView.adapter = ServicePaymentSummaryRecyclerAdapter(this@TotalPaymentSummaryActivity, resp.details, false)

                    tvDiscountText.text = getString(R.string.promo_dist)
                    tvDiscount.text = Util.formatMoney(resp.discount_promo) + " ${getString(R.string.bath)}"

                    tvMemberDiscountText.text = getString(R.string.mem_disc)
                    tvMemberDiscount.text = Util.formatMoney(resp.discount_member) + " ${getString(R.string.bath)}"

//                    if(resp.discount_promo > 0){
//                        tvDiscountText.text = "ส่วนลดโปรโมชั่น"
//                        tvDiscount.text = Util.formatMoney(resp.discount_promo) + " บาท"
//                        layoutPromoDiscount.visibility = View.VISIBLE
//                    }
//                    if(resp.discount_member > 0){
//                        tvMemberDiscountText.text = "ส่วนลดสมาชิก"
//                        tvMemberDiscount.text = Util.formatMoney(resp.discount_member) + " บาท"
//                        layoutMemberDiscount.visibility = View.VISIBLE
//
//                    }
                    tvAmount.text = Util.formatMoney(resp.total_amount) + " บาท"
                    tvAmountAfterDiscount.text = Util.formatMoney(resp.amount_pay) + " ${getString(R.string.bath)}"

                    tvPaymentMethod.text = paymentMethodName

                    hideProgressDialog()
                },
                onFailure = { error ->
                    hideProgressDialog()
                    showMessage(error.message!!)
                }
            )

        }
    }


    @SuppressLint("SetTextI18n")
    private fun initSummaryLayout(details: Map<String, LockerCalculateDetail>, calculation: CalculationDetail) {
        recyclerView.layoutManager = LinearLayoutManager(this@TotalPaymentSummaryActivity)
        recyclerView.adapter = ServicePaymentSummaryRecyclerAdapter(this@TotalPaymentSummaryActivity, details)

        layoutPromoDiscount.visibility = View.GONE
        tvAmount.text = calculation.amount_total.toString().replace(".0", "") + " ${getString(R.string.bath)}"
        if (calculation.promotion != null) {
            val names = arrayListOf<String>()
            val totals = arrayListOf<String>()
            calculation.promotion.forEach { p ->
                names.add(p["name"].toString())
                totals.add("${p["discount"].toString().replace(".0", "")} ${getString(R.string.bath)}")
            }
            tvDiscountText.text = names.joinToString("\n")
            tvDiscount.text = totals.joinToString("\n")

            layoutPromoDiscount.visibility = View.VISIBLE
        }

        layoutMemberDiscount.visibility = View.GONE
        if (calculation.member != null) {
            calculation.member["name"].toString().also { tvMemberDiscountText.text = it }
            calculation.member["discount"].toString().also { tvMemberDiscount.text = "${it.replace(".0", "")} บาท" }
            layoutMemberDiscount.visibility = View.VISIBLE
        }

        tvAmountAfterDiscount.text = calculation.amount_after_discount.toString().replace(".0", "") + " บาท"
        tvPaymentMethod.text = paymentMethodName
        rfidAmountPay = calculation.amount_after_discount

        if (calculation.remark == "PROMO_INVALID") {
            showMessage(getString(R.string.invalid_promo_code))
        }

    }

}