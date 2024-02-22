package com.lockboxth.lockboxkiosk

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import com.google.gson.JsonObject
import com.lockboxth.lockboxkiosk.customdialog.CustomDialog
import com.lockboxth.lockboxkiosk.helpers.BaseActivity
import com.lockboxth.lockboxkiosk.helpers.Gsoner
import com.lockboxth.lockboxkiosk.helpers.TransactionType
import com.lockboxth.lockboxkiosk.helpers.Util
import com.lockboxth.lockboxkiosk.http.model.pudo.*
import com.lockboxth.lockboxkiosk.http.repository.PudoRepository
import com.lockboxth.lockboxkiosk.print.PrintUtil
import com.lockboxth.lockboxkiosk.service.hardware.HardwareService
import com.otaliastudios.cameraview.CameraListener
import com.otaliastudios.cameraview.CameraView
import com.otaliastudios.cameraview.PictureResult
import kotlinx.android.synthetic.main.activity_pudo_sender_order_detail.*
import java.io.ByteArrayOutputStream

class PudoSenderOrderDetailActivity : BaseActivity() {

    private val hardwareService = HardwareService.getInstance()

    companion object {
        var cmd: JsonObject? = null
        var isOpenCommand = false
        var hasData = false
        var address: PudoParcelAddressResponse? = null
    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pudo_sender_order_detail)

        layoutPrint.visibility = View.GONE
        layoutCod.visibility = View.GONE

        if (appPref.currentTransactionType == TransactionType.PUDO_SENDER) {
            showProgressDialog()
            PudoRepository.getInstance().senderDetail(
                PudoSenderConfirmRequest(appPref.kioskInfo!!.generalprofile_id, appPref.currentTransactionId!!),
                onSuccess = { resp ->
                    initUI(resp)
                    hideProgressDialog()
                },
                onFailure = { error ->
                    hideProgressDialog()
                    showMessage(error.message!!)
                }
            )
        } else {
            val detail = intent.getParcelableExtra<PudoParcelAddressResponse>("detail")
            if (detail != null) {
                initUI(detail)
            } else {
                address!!
            }
        }

    }

    @SuppressLint("SetTextI18n")
    private fun initUI(data: PudoParcelAddressResponse) {
        if (data.cod_amt != null) {
            layoutPrint.visibility = View.GONE
            layoutCod.visibility = View.VISIBLE
            tvCodAmt.text = Util.formatMoney(data.cod_amt.toFloat())
        }

        tvCreateDate.text = data.create_at
        tvOrderNo.text = data.order_no

        tvDropOff.text = "${data.sender.location_type}\r\n${data.sender.location_name}"
        tvPickup.text = "${data.receiver.location_type}\r\n${data.receiver.location_name}"

        tvParcelCategory.text = data.parcel_detail.category_name
        tvParcelSize.text = data.parcel_detail.size_name
        tvParcelWeight.text = "${data.parcel_detail.weight} kg."

        if (!data.payment_method.isNullOrEmpty()) {
            tvPaymentMethod.text = data.payment_method
        } else {
            layoutPayment.visibility = View.GONE
        }

        Util.loadImage(data.shipment_detail.image_url, imgShipmentLogo)

        tvSenderName.text = data.sender_address.full_name
        tvSenderTelNo.text = data.sender_address.phone
        var senderAddress = ""
        if (!data.sender_address.idcard.isNullOrEmpty()) {
            senderAddress = data.sender_address.idcard
            senderAddress += "\r\n"
        }
        senderAddress += data.sender_address.full_address
        tvSenderAddress.text = senderAddress

        tvReceiverName.text = data.receiver_address.full_name
        tvReceiverTelNo.text = data.receiver_address.phone
        tvReceiverAddress.text = data.receiver_address.full_address

        val camera = findViewById<CameraView>(R.id.camera)
        camera.setLifecycleOwner(this)
        camera.addCameraListener(object : CameraListener() {
            override fun onPictureTaken(result: PictureResult) {
                super.onPictureTaken(result)
                submitConfirm(result)
            }
        })

        btnCancel.setOnClickListener {
            onCancel()
        }

        hardwareService.setOnDataReceived { s ->
            if (!isOpenCommand) {
                hasData = true
                val openStatus: String = s.substring(7, 11)
                var arrStatus: CharArray = hardwareService.hex2binary(openStatus)!!
                arrStatus = hardwareService.reverse(arrStatus)!!
                val currentLockerNo = cmd!!.keySet().first().toInt()
                var block: Int = currentLockerNo - 1
                block %= 8
                val isOpen = arrStatus[block] == '0'
                callBack(isOpen)
            }
        }

        btnConfirm.setOnClickListener {
            when (appPref.currentTransactionType) {
                TransactionType.PUDO_COURIER_SENDER -> {
                    openLocker()
                }
                TransactionType.PUDO_SENDER -> {
                    val intent = Intent(this@PudoSenderOrderDetailActivity, PudoWalkinWaybillActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                else -> {
                    showProgressDialog()
                    if (this.camera.isOpened) {
                        this.camera.takePicture()
                    } else {
                        submitConfirm()
                    }
                }
            }
        }

        layoutPrint.setOnClickListener {
            CustomDialog.newInstance(R.layout.dialog_pudo_print2).apply {
                onOkClickListener = {

                }
                timeoutCallback = {

                }
            }.run {
                try {
                    show(supportFragmentManager, "")
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            PudoRepository.getInstance().waybill(
                PudoWayBillRequest(appPref.kioskInfo!!.generalprofile_id, appPref.currentTransactionId!!),
                onSuccess = { resp ->
                    PrintUtil.getInstance().printImage(resp.shipping_image)
                },
                onFailure = { error ->
                    showMessage(error.message!!)
                }
            )
        }
    }

    private fun openLocker() {
        showProgressDialog("กำลังเปิดตู้")
        val currentLockerNo = cmd!!.keySet().first().toInt()
        val jsonObj = cmd!![currentLockerNo.toString()].asJsonObject
        val currentOpenCommand = jsonObj["open"].toString()
        val currentInQuireCommand = jsonObj["inquire"].toString()
        isOpenCommand = true
        hardwareService.writeCommand(currentOpenCommand)
        Thread.sleep(500)
        isOpenCommand = false
        hasData = false
        hardwareService.writeCommand(currentInQuireCommand)
        Handler(Looper.getMainLooper()).postDelayed({
            if (!hasData) {
                hideProgressDialog()
                callBack(false)
            }
        }, 3000)
    }

    private fun callBack(success: Boolean) {
        showProgressDialog()
        val res = JsonObject()
        res.addProperty(cmd!!.keySet().first(), success)
        val req = PudoCourierCallbackRequest(
            appPref.kioskInfo!!.generalprofile_id,
            appPref.currentTransactionId!!,
            "pudo_courier",
            res
        )
        PudoRepository.getInstance().courierCallback(
            req,
            onSuccess = { resp ->

                if (!success) {
                    cmd = resp.locker_commands
                    openLocker()
                } else {
                    val intent = Intent(this@PudoSenderOrderDetailActivity, PaymentSuccessActivity::class.java)
                    intent.putExtra("locker_no", resp.locker_no!!)
                    startActivity(intent)
                }

            },
            onFailure = { error ->
                hideProgressDialog()
                if (error.code == "FAIL") {
                    CustomDialog.newInstance(R.layout.dialog_pudo_locker_error).apply {
                        timeoutCallback = {
                            onCancel()
                        }
                    }.run {
                        show(supportFragmentManager, "")
                    }
                } else {
                    showMessage(error.message!!)
                }
            }
        )

    }

    @SuppressLint("WrongThread")
    private fun submitConfirm(result: PictureResult) {
        result.toBitmap { bmp ->
            val b = Bitmap.createScaledBitmap(bmp!!, 200, 200, false)
            val stream = ByteArrayOutputStream()
            b.compress(Bitmap.CompressFormat.PNG, 90, stream)
            val image = stream.toByteArray()
            val base64 = Util.toBase64(image)
            submitConfirm(base64)
        }
    }

    private fun submitConfirm(base64: String = "") {
        val req = PudoReceiverConfirmRequest(
            appPref.kioskInfo!!.generalprofile_id,
            appPref.currentTransactionId!!,
            base64
        )
        PudoRepository.getInstance().receiverConfirm(
            req,
            onSuccess = { code, paymentResp, withoutPaymentResp ->
                hideProgressDialog()
                if (code == "PAYMENT") {
                    PudoServiceFeeSummaryActivity.data = paymentResp!!
                    val intent = Intent(this@PudoSenderOrderDetailActivity, PudoServiceFeeSummaryActivity::class.java)
                    startActivity(intent)
                } else {
                    val intent = Intent(this@PudoSenderOrderDetailActivity, PaymentSuccessActivity::class.java)
                    intent.putExtra("event_type", withoutPaymentResp!!.event_type)
                    intent.putExtra("locker_no", withoutPaymentResp.locker_no)
                    intent.putExtra("locker_commands", Gsoner.getInstance().gson.toJson(withoutPaymentResp.locker_commands))
                    startActivity(intent)
                }
            },
            onFailure = { error ->
                hideProgressDialog()
                showMessage(error.message!!)
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