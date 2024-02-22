package com.lockboxth.lockboxkiosk

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.lockboxth.lockboxkiosk.adapter.PudoCpPrefixRecyclerAdapter
import com.lockboxth.lockboxkiosk.customdialog.CustomDialog
import com.lockboxth.lockboxkiosk.helpers.BaseActivity
import com.lockboxth.lockboxkiosk.helpers.TransactionType
import com.lockboxth.lockboxkiosk.helpers.Util
import com.lockboxth.lockboxkiosk.http.model.adidas.AdidasVerifyRequest
import com.lockboxth.lockboxkiosk.http.model.cp.CpParcelRequest
import com.lockboxth.lockboxkiosk.http.model.cp.CpPrefixRequest
import com.lockboxth.lockboxkiosk.http.model.cp.CpVerifyRequest
import com.lockboxth.lockboxkiosk.http.model.go.GoPickupVerifyRequest
import com.lockboxth.lockboxkiosk.http.model.personal.VerifyBookingFinishRequest
import com.lockboxth.lockboxkiosk.http.model.personal.VerifyBookingRequest
import com.lockboxth.lockboxkiosk.http.model.personal.VerifyFinishRequest
import com.lockboxth.lockboxkiosk.http.model.pudo.PudoReceiverVerifyRequest
import com.lockboxth.lockboxkiosk.http.model.pudo.PudoSenderVerifyRequest
import com.lockboxth.lockboxkiosk.http.model.pudo.PudoVerifyCourierRequest
import com.lockboxth.lockboxkiosk.http.repository.*
import com.otaliastudios.cameraview.CameraListener
import com.otaliastudios.cameraview.CameraView
import com.otaliastudios.cameraview.PictureResult
import kotlinx.android.synthetic.main.activity_locker_password.*
import java.io.ByteArrayOutputStream


class LockerPasswordActivity : BaseActivity() {

    private var passwordText: String = ""
    private var passwordTextConfirm: String = ""
    private var prefix: String = ""
    private var prefixAdapter: PudoCpPrefixRecyclerAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_locker_password)

        btnReset.setOnClickListener {
            passwordText = ""
            displayText()
        }

        btnCancel.setOnClickListener {
            onCancel()
        }

        setTimeoutMinute(5, tvCountdown) {
            onCancel()
        }

        btnDelete.setOnClickListener {
            if (passwordText.isNotEmpty()) {
                passwordText = passwordText.substring(0, passwordText.length - 1)
            }
            displayText()
        }

        for (i in 0..9) {
            val etId = "btn${i}"
            val resID = resources.getIdentifier(etId, "id", packageName)
            val btn = findViewById<View>(resID)
            btn.setOnClickListener {
                onNumberPress(i.toString())
            }
        }

        layoutCode.visibility = View.GONE
        recyclerView.visibility = View.GONE

        when (appPref.currentTransactionType) {
            TransactionType.IN -> {
                initIn()
            }
            TransactionType.OUT -> {
                initOut()
            }
            TransactionType.PUDO_SENDER -> {
                initPudoSender()
            }
            TransactionType.PUDO_COURIER_PICKUP -> {
                initCourierReceive()
            }
            TransactionType.PUDO_RECEIVER -> {
                initReceiver()
            }
            TransactionType.PUDO_CP_PICKUP -> {
                initPartnerPickup()
            }
            TransactionType.GO_OUT -> {
                initGoOut()
            }
            TransactionType.PUDO_CP_DROP -> {
                layoutCode.visibility = View.VISIBLE
                layoutPin.visibility = View.GONE
                initPartnerDrop()
            }
            TransactionType.BOOKING -> {
                if (appPref.currentBookingId == null) {
                    initVerifyBookingPin()
                } else {
                    initBooking()
                }
            }
            TransactionType.ADIDAS_DROP -> {
                initAdidasDrop()
            }
            TransactionType.ADIDAS_PICKUP -> {
                initAdidasPickup()
            }
            else -> {}
        }

    }

    private fun initReceiver() {
        tvTitle.text = getString(R.string.type_pin)
        tvHint.text = getString(R.string.type_pin_6_1)
        btnConfirm.setOnClickListener {
            if (passwordText.length == 6) {
                processReceiver()
            }
        }
    }

    private fun initGoOut() {
        tvTitle.text = getString(R.string.type_password)
        tvHint.text = getString(R.string.type_sms_password)
        btnConfirm.setOnClickListener {
            if (passwordText.length == 6) {
                processGoOut()
            }
        }
    }

    @SuppressLint("WrongThread")
    private fun initAdidasDrop() {
        tvTitle.text = getString(R.string.type_password)
        tvHint.text = getString(R.string.return_adidas_sms)

        val camera = findViewById<CameraView>(R.id.camera)
        camera.setLifecycleOwner(this)
        camera.addCameraListener(object : CameraListener() {
            override fun onPictureTaken(result: PictureResult) {
                super.onPictureTaken(result)
                result.toBitmap { bmp ->
                    val b = Bitmap.createScaledBitmap(bmp!!, 200, 200, false)
                    val stream = ByteArrayOutputStream()
                    b.compress(Bitmap.CompressFormat.PNG, 90, stream)
                    val image = stream.toByteArray()
                    val base64 = Util.toBase64(image)
                    processAdidasDrop(base64)
                }
            }
        })

        btnConfirm.setOnClickListener {
            if (passwordText.length == 6) {
                showProgressDialog()
                if (this.camera.isOpened) {
                    this.camera.takePicture()
                } else {
                    processAdidasDrop()
                }
            }
        }
    }

    private fun initAdidasPickup() {
        tvTitle.text = getString(R.string.type_password)
        tvHint.text = getString(R.string.get_adidas_sms)

        val camera = findViewById<CameraView>(R.id.camera)
        camera.setLifecycleOwner(this)
        camera.addCameraListener(object : CameraListener() {
            override fun onPictureTaken(result: PictureResult) {
                super.onPictureTaken(result)
                result.toBitmap { bmp ->
                    val b = Bitmap.createScaledBitmap(bmp!!, 200, 200, false)
                    val stream = ByteArrayOutputStream()
                    b.compress(Bitmap.CompressFormat.PNG, 90, stream)
                    val image = stream.toByteArray()
                    val base64 = Util.toBase64(image)
                    processAdidasPickup(base64)
                }
            }
        })

        btnConfirm.setOnClickListener {
            if (passwordText.length == 6) {
                showProgressDialog()
                if (this.camera.isOpened) {
                    this.camera.takePicture()
                } else {
                    processAdidasPickup()
                }
            }
        }
    }

    private fun initPartnerPickup() {
        tvTitle.text = getString(R.string.type_pin)
        tvHint.text = getString(R.string.type_pin_6_1)
        btnConfirm.setOnClickListener {
            if (passwordText.length == 6) {
                processPartnerReceiver()
            }
        }
    }

    @SuppressLint("WrongThread")
    private fun initPartnerDrop() {
        tvTitle.text = getString(R.string.type_number)
        tvHint.text = getString(R.string.type_parcel_number)
        recyclerView.visibility = View.VISIBLE

        val camera = findViewById<CameraView>(R.id.camera)
        camera.setLifecycleOwner(this)
        camera.addCameraListener(object : CameraListener() {

            override fun onPictureTaken(result: PictureResult) {
                super.onPictureTaken(result)
                result.toBitmap { bmp ->
                    val b = Bitmap.createScaledBitmap(bmp!!, 200, 200, false)
                    val stream = ByteArrayOutputStream()
                    b.compress(Bitmap.CompressFormat.PNG, 90, stream)
                    val image = stream.toByteArray()
                    val base64 = Util.toBase64(image)
                    processPartnerDrop(base64)
                }
            }
        })

        btnConfirm.setOnClickListener {
            if (!prefixAdapter?.selectedItem.isNullOrEmpty() && passwordText.isNotEmpty()) {
                showProgressDialog()
                if (this.camera.isOpened) {
                    this.camera.takePicture()
                } else {
                    processPartnerDrop()
                }
            }
        }

        showProgressDialog()
        CpRepository.getInstance().prefix(
            CpPrefixRequest(appPref.kioskInfo!!.generalprofile_id, appPref.currentLanguage),
            onSuccess = { resp ->
                recyclerView.layoutManager = LinearLayoutManager(this@LockerPasswordActivity, LinearLayoutManager.HORIZONTAL, false)
                prefixAdapter = PudoCpPrefixRecyclerAdapter(this@LockerPasswordActivity, resp.prefix) { p ->
                    prefix = p
                    tvPrefix.text = prefix
                }
                recyclerView.adapter = prefixAdapter
                hideProgressDialog()
            },
            onFailure = { error ->
                hideProgressDialog()
                handleError(error)
            }
        )

    }


    private fun initIn() {
        btnConfirm.setOnClickListener {
            if (passwordText.length == 6) {
                if (passwordTextConfirm.isNotEmpty()) {
                    if (passwordTextConfirm != passwordText) {
                        showMessage(getString(R.string.passwd_miss_match))
                        return@setOnClickListener
                    }
                    processConfirmIn()
                } else {
                    passwordTextConfirm = passwordText
                    passwordText = ""
                    displayText()
                    tvHint.text = getString(R.string.type_pin_6_2)
                }
            }
        }

    }

    private fun initOut() {
        tvHint.text = getString(R.string.type_pin_6_1)
        btnConfirm.setOnClickListener {
            if (passwordText.length == 6) {
                processVerifyOut()
            }
        }
    }

    private fun initCourierReceive() {
        tvTitle.text = getString(R.string.input_code)
        tvHint.text = getString(R.string.type_pin_6_1)
        btnConfirm.setOnClickListener {
            if (passwordText.length == 6) {
                processCourierReceive()
            }
        }
    }

    private fun initVerifyBookingPin() {

        tvTitle.text = getString(R.string.pin)
        tvHint.text = getString(R.string.type_otp)
        btnConfirm.setOnClickListener {
            if (passwordText.length == 6) {
                val req = VerifyBookingRequest(appPref.kioskInfo!!.generalprofile_id, "pin", passwordText)
                PersonalRepository.getInstance().verifyBooking(
                    req,
                    onSuccess = { resp ->
                        appPref.currentBookingId = resp.booking_id
                        appPref.currentTransactionId = resp.txn
                        hideProgressDialog()

                        val intent = Intent(this@LockerPasswordActivity, RegisterActivity::class.java)
                        startActivity(intent)
                        finish()
                    },
                    onFailure = { error ->
                        hideProgressDialog()
                        handleError(error)
                    }
                )
            }
        }
    }

    @SuppressLint("WrongThread")
    private fun submitBookingPin(result: PictureResult) {

        result.toBitmap { bmp ->
            val b = Bitmap.createScaledBitmap(bmp!!, 200, 200, false)
            val stream = ByteArrayOutputStream()
            b.compress(Bitmap.CompressFormat.PNG, 90, stream)
            val image = stream.toByteArray()
            val base64 = Util.toBase64(image)
            submitBookingPin(base64)
        }

    }

    private fun submitBookingPin(base64: String = "") {
        val req = VerifyBookingFinishRequest(appPref.kioskInfo!!.generalprofile_id, appPref.currentTransactionId!!, passwordText, base64)
        PersonalRepository.getInstance().bookingFinish(
            req,
            onSuccess = { resp ->
                hideProgressDialog()
                openLocker(resp.locker_no, resp.event_type, Gson().toJson(resp.locker_commands))
            },
            onFailure = { error ->
                hideProgressDialog()
                handleError(error)
            }
        )
    }

    private fun initBooking() {
        val camera = findViewById<CameraView>(R.id.camera)
        camera.setLifecycleOwner(this)
        camera.addCameraListener(object : CameraListener() {
            override fun onPictureTaken(result: PictureResult) {
                super.onPictureTaken(result)
                submitBookingPin(result)
            }
        })
        btnConfirm.setOnClickListener {
            if (passwordText.length == 6) {
                if (passwordTextConfirm.isNotEmpty()) {
                    if (passwordTextConfirm != passwordText) {
                        showMessage("รหัสผ่านไม่ตรงกัน")
                        return@setOnClickListener
                    }
                    showProgressDialog()
                    if (this.camera.isOpened) {
                        this.camera.takePicture()
                    } else {
                        submitBookingPin()
                    }
                } else {
                    passwordTextConfirm = passwordText
                    passwordText = ""
                    displayText()
                    tvHint.text = getString(R.string.type_pin_6_2)
                }
            }
        }
    }

    private fun onNumberPress(number: String) {
        if (appPref.currentTransactionType != TransactionType.PUDO_CP_DROP && passwordText.length >= 6) {
            return
        }
        passwordText += number
        displayText()

    }

    private fun displayText() {
        if (appPref.currentTransactionType != TransactionType.PUDO_CP_DROP) {
            val password = passwordText.toCharArray()
            for (i in 1..6) {
                val etId = "et${i}"
                val resID = resources.getIdentifier(etId, "id", packageName)
                val et = findViewById<EditText>(resID)
                et.setText("")
                if (password.count() >= i) {
                    et.setText("•")
                }
                println(i)
            }
        } else {
            tvCode.text = passwordText
        }
    }

    private fun processConfirmIn() {
        showProgressDialog()
        val req = VerifyFinishRequest(appPref.kioskInfo!!.generalprofile_id, appPref.currentTransactionId!!, passwordText)
        PersonalRepository.getInstance().verifyFinish(
            req,
            onSuccess = {
                hideProgressDialog()
                val intent = Intent(this@LockerPasswordActivity, ServiceFeeSummaryActivity::class.java)
                startActivity(intent)
                finish()
            },
            onFailure = { error ->
                hideProgressDialog()
                handleError(error)
            }
        )
    }

    private fun processVerifyOut() {
        showProgressDialog()
        val req = VerifyFinishRequest(appPref.kioskInfo!!.generalprofile_id, appPref.currentTransactionId!!, passwordText)
        PersonalRepository.getInstance().verifyOut(
            req,
            onSuccess = {
                hideProgressDialog()
                val intent = Intent(this@LockerPasswordActivity, SelectLockBoxActivity::class.java)
                startActivity(intent)
                finish()
            },
            onFailure = { error ->
                handleError(error)
                hideProgressDialog()
            }
        )
    }

    private fun initPudoSender() {
        tvTitle.text = getString(R.string.pin)
        tvHint.text = getString(R.string.type_pin_6_1)
        btnConfirm.setOnClickListener {
            if (passwordText.length == 6) {
                processVerifyPudoSender()
            }
        }
    }

    private fun processVerifyPudoSender() {
        showProgressDialog()
        val req = PudoSenderVerifyRequest(
            appPref.kioskInfo!!.generalprofile_id,
            "pin",
            passwordText,
            appPref.currentLanguage
        )
        PudoRepository.getInstance().senderVerify(
            req,
            onSuccess = { resp ->

                appPref.currentBookingId = resp.booking_id
                appPref.currentTransactionId = resp.txn

                val intent = if (resp.has_pdpa) {
                    Intent(this@LockerPasswordActivity, PudoSenderOrderDetailActivity::class.java)
                } else {
                    Intent(this@LockerPasswordActivity, ConsentActivity::class.java)
                }
                startActivity(intent)
                finish()

            },
            onFailure = { error ->
                hideProgressDialog()
                CustomDialog.newInstance(R.layout.dialog_invalid_pin).apply {
                    message = error.message!!
                    timeoutCallback = {
                        onCancel()
                    }
                }.run {
                    show(supportFragmentManager, "")
                }
            }
        )
    }

    private fun processCourierReceive() {
        showProgressDialog()
        val req = PudoVerifyCourierRequest(
            appPref.kioskInfo!!.generalprofile_id,
            appPref.currentTransactionId!!,
            passwordText
        )
        PudoRepository.getInstance().verifyCourier(
            req,
            onSuccess = { resp ->
                hideProgressDialog()
                val intent = Intent(this@LockerPasswordActivity, PudoPickupActivity::class.java)
                intent.putExtra("data", resp)
                startActivity(intent)
                finish()
            },
            onFailure = { error ->
                hideProgressDialog()
                CustomDialog.newInstance(R.layout.dialog_invalid_pin).apply {
                    message = error.message!!
                    timeoutCallback = {
                        onCancel()
                    }
                }.run {
                    show(supportFragmentManager, "")
                }
            }
        )
    }

    private fun processReceiver() {
        showProgressDialog()
        val req = PudoReceiverVerifyRequest(
            appPref.kioskInfo!!.generalprofile_id,
            "pin",
            passwordText,
            appPref.currentLanguage
        )
        PudoRepository.getInstance().receiverVerify(
            req,
            onSuccess = { resp ->
                hideProgressDialog()
                appPref.currentTransactionId = resp.txn
                PudoSenderOrderDetailActivity.address = resp.order
                val intent = if (!resp.has_pdpa) {
                    Intent(this@LockerPasswordActivity, ConsentActivity::class.java)
                } else {
                    Intent(this@LockerPasswordActivity, PudoSenderOrderDetailActivity::class.java)
                }
                startActivity(intent)
                finish()
            },
            onFailure = { error ->
                hideProgressDialog()
                CustomDialog.newInstance(R.layout.dialog_invalid_pin).apply {
                    message = error.message!!
                    timeoutCallback = {
                        onCancel()
                    }
                }.run {
                    show(supportFragmentManager, "")
                }
            }
        )
    }

    private fun processGoOut() {
        showProgressDialog()
        GoRepository.getInstance().goPickupVerify(
            GoPickupVerifyRequest(appPref.kioskInfo!!.generalprofile_id, passwordText, appPref.currentLanguage),
            onSuccess = { resp ->
                hideProgressDialog()
                appPref.currentTransactionId = resp.txn
                ServiceFeeSummaryActivity.goOutSummary = resp
                val intent = Intent(this@LockerPasswordActivity, ServiceFeeSummaryActivity::class.java)
                startActivity(intent)
                finish()
            },
            onFailure = { error ->
                hideProgressDialog()
                CustomDialog.newInstance(R.layout.dialog_invalid_pin).apply {
                    message = error
                    timeoutCallback = {
                        onCancel()
                    }
                }.run {
                    show(supportFragmentManager, "")
                }
            }
        )
    }

    private fun processPartnerReceiver() {
        showProgressDialog()
        val req = CpVerifyRequest(
            appPref.kioskInfo!!.generalprofile_id,
            "pin",
            passwordText,
            appPref.currentLanguage
        )
        CpRepository.getInstance().receiverVerify(
            req,
            onSuccess = { resp ->
                hideProgressDialog()
                appPref.currentTransactionId = resp.txn
                val cmdJson = Gson().toJson(resp.locker_commands)
                val intent = Intent(this@LockerPasswordActivity, CpConfirmOpenActivity::class.java)
                intent.putExtra("locker_no", resp.locker_no)
                intent.putExtra("locker_commands", cmdJson)
                intent.putExtra("event_type", resp.event_type)
                startActivity(intent)
                finish()
            },
            onFailure = { error ->
                hideProgressDialog()
                CustomDialog.newInstance(R.layout.dialog_invalid_pin).apply {
                    message = error.message!!
                    timeoutCallback = {
                        onCancel()
                    }
                }.run {
                    show(supportFragmentManager, "")
                }
            }
        )
    }

    private fun processPartnerDrop(base64: String = "") {
        val req = CpParcelRequest(
            generalprofile_id = appPref.kioskInfo!!.generalprofile_id,
            qrcode = prefix + passwordText,
            screenshot = base64,
            lang = appPref.currentLanguage
        )
        CpRepository.getInstance().cpParcel(
            req,
            onSuccess = { resp ->
                hideProgressDialog()
                appPref.currentTransactionId = resp.txn
                val cmd = Gson().fromJson(resp.locker_commands, JsonObject::class.java)
                PudoSenderOrderConfirmActivity.currentLockerName = resp.locker_no
                PudoSenderOrderConfirmActivity.currentLockerNo = cmd.keySet().first().toInt()
                val jsonObj = cmd[PudoSenderOrderConfirmActivity.currentLockerNo.toString()].asJsonObject
                PudoSenderOrderConfirmActivity.currentOpenCommand = jsonObj["open"].toString()
                PudoSenderOrderConfirmActivity.currentInQuireCommand = jsonObj["inquire"].toString()
                val intent = Intent(this@LockerPasswordActivity, PudoSenderOrderConfirmActivity::class.java)
                startActivity(intent)
                finish()
            },
            onFailure = { error ->
                hideProgressDialog()
                handleError(error)
            }
        )
    }

    private fun processAdidasDrop(base64: String = "") {
        val req = AdidasVerifyRequest(
            generalprofile_id = appPref.kioskInfo!!.generalprofile_id,
            pin = passwordText,
            screenshot = base64,
            lang = appPref.currentLanguage
        )
        AdidasRepository.getInstance().dropVerify(
            req,
            onSuccess = { resp ->
                appPref.currentTransactionId = resp.txn
                val cmd = Gson().fromJson(resp.locker_commands, JsonObject::class.java)
                PudoSenderOrderConfirmActivity.currentLockerName = resp.locker_no
                PudoSenderOrderConfirmActivity.currentLockerNo = cmd.keySet().first().toInt()
                val jsonObj = cmd[PudoSenderOrderConfirmActivity.currentLockerNo.toString()].asJsonObject
                PudoSenderOrderConfirmActivity.currentOpenCommand = jsonObj["open"].toString()
                PudoSenderOrderConfirmActivity.currentInQuireCommand = jsonObj["inquire"].toString()
                val intent = Intent(this@LockerPasswordActivity, PudoSenderOrderConfirmActivity::class.java)
                hideProgressDialog()
                startActivity(intent)
                finish()
            },
            onFailure = { error ->
                hideProgressDialog()
                handleError(error.message!!)
            }
        )
    }

    private fun processAdidasPickup(base64: String = "") {
        val req = AdidasVerifyRequest(
            generalprofile_id = appPref.kioskInfo!!.generalprofile_id,
            pin = passwordText,
            screenshot = base64,
            lang = appPref.currentLanguage
        )
        AdidasRepository.getInstance().pickupVerify(
            req,
            onSuccess = { resp ->
                appPref.currentTransactionId = resp.txn
                val cmdJson = Gson().toJson(resp.locker_commands)
                val intent = Intent(this@LockerPasswordActivity, CpConfirmOpenActivity::class.java)
                intent.putExtra("locker_no", resp.locker_no)
                intent.putExtra("locker_commands", cmdJson)
                intent.putExtra("event_type", resp.event_type)
                startActivity(intent)
                finish()
            },
            onFailure = { error ->
                hideProgressDialog()
                handleError(error.message!!)
            }
        )
    }

    private fun handleError(error: String) {
        when (error) {
            "BOKKING_NOT_FOUND",
            "BOOKING_NOT_FOUND",
            "REQUEST_INVALID",
            "PIN_FAIL" -> {
                CustomDialog.newInstance(R.layout.dialog_invalid_pin).apply {
                    timeoutCallback = {
                        onCancel()
                    }
                }.run {
                    show(supportFragmentManager, "")
                }
            }
            else -> {
                showMessage(error)
            }
        }
    }


}