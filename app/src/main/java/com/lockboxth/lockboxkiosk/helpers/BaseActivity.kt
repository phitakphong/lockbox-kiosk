package com.lockboxth.lockboxkiosk.helpers

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application.ActivityLifecycleCallbacks
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Bundle
import android.os.CountDownTimer
import android.os.PersistableBundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.ViewGroup
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.view.Window
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.lockboxth.lockboxkiosk.*
import com.lockboxth.lockboxkiosk.customdialog.AlertMessageDialog
import com.lockboxth.lockboxkiosk.customdialog.ProgressDialog
import com.lockboxth.lockboxkiosk.http.model.kiosk.ClearTransactionRequest
import com.lockboxth.lockboxkiosk.http.model.locker.CancelTransactionRequest
import com.lockboxth.lockboxkiosk.http.repository.KioskRepository
import com.lockboxth.lockboxkiosk.http.repository.LockerRepository
import com.lockboxth.lockboxkiosk.http.repository.TopupRepository
import com.lockboxth.lockboxkiosk.service.firebase.NotificationsData
import com.lockboxth.lockboxkiosk.service.firebase.NotificationsManager
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.Locale


open class BaseActivity : AppCompatActivity() {

    val appPref: MyPrefs by lazy { MyPrefs(Contextor.getInstance().context) }

    private var timer: CountDownTimer? = null
    private var viewToUpdate: TextView? = null
    private var milliLeft: Long = 0
    private var min: Long = 0
    private var sec: Long = 0
    private var timeoutCallback: (() -> Unit)? = null

    public var allowOpenLocker = false

    var autoHideKeyboard = true

    private var openLockerSubscriber: Disposable? = null

    lateinit var progressDialog: ProgressDialog

    private val keyboardLayoutListener = OnGlobalLayoutListener {
        val heightDiff: Int = rootLayout!!.rootView.height - rootLayout!!.height
        val contentViewTop = window.findViewById<View>(Window.ID_ANDROID_CONTENT).top
        val broadcastManager = LocalBroadcastManager.getInstance(this@BaseActivity)
        if (heightDiff <= contentViewTop) {
            onHideKeyboard()
            val intent = Intent("KeyboardWillHide")
            broadcastManager.sendBroadcast(intent)
        } else {
            val keyboardHeight = heightDiff - contentViewTop
            onShowKeyboard(keyboardHeight)
            val intent = Intent("KeyboardWillShow")
            intent.putExtra("KeyboardHeight", keyboardHeight)
            broadcastManager.sendBroadcast(intent)
        }
    }
    private var keyboardListenersAttached = false
    private var rootLayout: ViewGroup? = null

    protected open fun onShowKeyboard(keyboardHeight: Int) {}
    protected open fun onHideKeyboard() {}

    protected open fun attachKeyboardListeners() {
        if (keyboardListenersAttached) {
            return
        }
        rootLayout = findViewById<View>(R.id.rootLayout) as ViewGroup
        rootLayout!!.viewTreeObserver.addOnGlobalLayoutListener(keyboardLayoutListener)
        keyboardListenersAttached = true
    }

    @SuppressLint("SourceLockedOrientationActivity", "CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        Util.fullscreen(this.window.decorView)

        super.onCreate(savedInstanceState)

        ProgressDialog.newInstance().apply { }.run {
            progressDialog = this
        }

        if (!appPref.currentTransactionId.isNullOrEmpty()) {
            Log.d("Tran", appPref.currentTransactionId!!)
        }

        if (savedInstanceState != null) {
            finish()
        }
    }


    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {

    }

    private fun subscribeOpenLocker() {
        openLockerSubscriber = NotificationsManager.instance!!.getNotificationObservable().observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io()).subscribe { data ->
            Log.d("OpenLocker", "getNotificationObservable")
            val d = Gson().fromJson(Gson().toJson(data), NotificationsData::class.java)
            if (appPref.currentTransactionType != TransactionType.PUDO_SENDER_WALKIN) {
                if (d.event_type == "topup") {
                    val intent = Intent(this@BaseActivity, TopupSuccessActivity::class.java)
                    startActivity(intent)
                } else {
                    openLocker(d.locker_no ?: "", d.event_type!!, d.locker_commands!!, d.txn)
                }
            } else {
                val cmd = Gson().fromJson(d.locker_commands, JsonObject::class.java)
                PudoSenderOrderConfirmActivity.currentLockerName = d.locker_no!!
                PudoSenderOrderConfirmActivity.currentLockerNo = cmd.keySet().first().toInt()
                val jsonObj = cmd[PudoSenderOrderConfirmActivity.currentLockerNo.toString()].asJsonObject
                PudoSenderOrderConfirmActivity.currentOpenCommand = jsonObj["open"].toString()
                PudoSenderOrderConfirmActivity.currentInQuireCommand = jsonObj["inquire"].toString()
                val intent = Intent(this@BaseActivity, PudoSenderOrderConfirmActivity::class.java)
                startActivity(intent)
            }
        }
    }

    fun openLocker(lockerNo: String, eventType: String, commands: String, txn: String? = null) {
        val intent = Intent(this@BaseActivity, PaymentSuccessActivity::class.java)
        intent.putExtra("locker_no", lockerNo)
        intent.putExtra("event_type", eventType)
        intent.putExtra("locker_commands", commands)
        intent.putExtra("txn", txn)
        startActivity(intent)
    }

    override fun onUserInteraction() {
        super.onUserInteraction()
        if (autoHideKeyboard) {
            hideKeyboard()
        }
    }

    fun hideKeyboard() {
        this.currentFocus?.let { view ->
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            imm?.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }


    fun setTimeoutMinute(timeout: Int, viewToUpdate: TextView?, timeoutCallback: () -> Unit) {
        this.viewToUpdate = viewToUpdate
        this.timeoutCallback = timeoutCallback
        timerStart((timeout.toLong() * 1000) * 60)
    }

    fun setTimeoutSecond(timeout: Int, viewToUpdate: TextView?, timeoutCallback: () -> Unit) {
        this.viewToUpdate = viewToUpdate
        this.timeoutCallback = timeoutCallback
        timerStart((timeout.toLong() * 1000))
    }

    private fun timerStart(timeLengthMilli: Long) {
        timer = object : CountDownTimer(timeLengthMilli, 1000) {
            @SuppressLint("SetTextI18n")
            override fun onTick(millisUntilFinished: Long) {
                milliLeft = millisUntilFinished;
                min = (millisUntilFinished / (1000 * 60));
                sec = ((millisUntilFinished / 1000) - min * 60);
                var minStr = ""
                if (min < 10) {
                    minStr = "0"
                }
                minStr += min.toString()
                var secStr = ""
                if (sec < 10) {
                    secStr = "0"
                }
                secStr += sec.toString()
                viewToUpdate?.text = "$minStr:$secStr"
            }

            override fun onFinish() {
                Log.d("timerx", "onFinish")
                timeoutCallback!!.invoke()
            }
        }
        timer!!.start()
    }

    fun timerPause() {
        timer?.cancel()
    }

    fun timerResume() {
        timerStart(milliLeft)
    }

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
    }

    fun showMessage(message: String, timer: Boolean = true, allowCancel: Boolean = true, onAcceptClick: (() -> Unit)? = null) {
        try {
            AlertMessageDialog.newInstance(message).apply {
                timeoutCallback = {
                    if (timer && allowCancel) {
                        onCancel()
                    }
                }
                onAcceptClickListener = {
                    onAcceptClick?.invoke()
                }
            }.run {
                show(supportFragmentManager, "")
            }
        } catch (ex: Exception) {
            Log.e("showMessage", ex.message ?: "")
        }
    }

    fun showProgressDialog(message: String? = null) {
        try {
            if (!progressDialog.isShowing) {
                progressDialog.run {
                    progressDialog.message = message
                    progressDialog.isShowing = true
                    progressDialog.show(supportFragmentManager, "")
                }
            } else {
                if (!message.isNullOrEmpty()) {
                    progressDialog.run {
                        progressDialog.updateMessage(message)
                    }
                }
            }
        } catch (ex: Exception) {
            Log.e("showProgressDialog", ex.message ?: "")
        }
    }

    fun hideProgressDialog() {
        try {
            if (progressDialog.isShowing) {
                progressDialog.run {
                    this.isShowing = false
                    message = null
                    dismiss()
                }
            }
        } catch (ex: Exception) {
            Log.e("BaseActivity", "hideProgressDialog")
        }
    }

    fun onBack(type: TransactionType, event: String = "back", successCallback: () -> Unit) {
        showProgressDialog()
        val req = CancelTransactionRequest(appPref.kioskInfo!!.generalprofile_id, appPref.currentTransactionId!!, event)
        if (type == TransactionType.IN || type == TransactionType.OUT) {
            LockerRepository.getInstance().cancel(type, req, onSuccess = { resp ->
                hideProgressDialog()
                successCallback.invoke()
            }, onFailure = { error ->
                hideProgressDialog()
                showMessage(error)
            })
        } else {
            TopupRepository.getInstance().cancel(req, onSuccess = { resp ->
                hideProgressDialog()
                successCallback.invoke()
            }, onFailure = { error ->
                hideProgressDialog()
                showMessage(error)
            })
        }
    }

    fun onCancel() {
        processCancelTransaction()
    }

    private fun processCancelTransaction() {
        if (appPref.currentTransactionId == null) {
            goToMainActivity()
            return
        }
        showProgressDialog()
        val req = ClearTransactionRequest(appPref.kioskInfo!!.generalprofile_id, appPref.currentTransactionId!!)
        KioskRepository.getInstance().clearTransaction(req, onSuccess = {
            hideProgressDialog()
            appPref.currentTransactionId = null
            goToMainActivity()
        }, onFailure = { error ->
            hideProgressDialog()
            showMessage(error)
        })
    }

    fun goToMainActivity() {
        val intent = Intent(this@BaseActivity, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    override fun onPause() {
        if (timer != null) {
            timerPause()
        }
        if (openLockerSubscriber != null && !openLockerSubscriber!!.isDisposed) {
            openLockerSubscriber!!.dispose()
        }
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        if (allowOpenLocker) {
            subscribeOpenLocker()
        }

    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        Log.d("BaseActivity", "onAttachedToWindow")
        val viewGroup = (findViewById<View>(android.R.id.content) as ViewGroup).getChildAt(0) as ViewGroup
        viewGroup.setOnTouchListener(object : OnTouchListener {
            override fun onTouch(p0: View?, p1: MotionEvent?): Boolean {
                hideKeyboard()
                return true
            }

        })
    }

    override fun onDestroy() {
        if (timer != null) {
            timerPause()
        }
        if (keyboardListenersAttached) {
            rootLayout?.viewTreeObserver?.removeGlobalOnLayoutListener(keyboardLayoutListener)
        }
        super.onDestroy()
    }

    fun setLocale(languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val resources: Resources = this.resources
        val config: Configuration = resources.configuration
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)
    }

}


class AppLifecycleTracker : ActivityLifecycleCallbacks {
    private var numStarted = 0
    val isAppInForeground: Boolean
        get() = numStarted > 0


    override fun onActivityCreated(p0: Activity, p1: Bundle?) {

    }

    override fun onActivityStarted(p0: Activity) {
        numStarted++
    }

    override fun onActivityResumed(p0: Activity) {

    }

    override fun onActivityPaused(p0: Activity) {

    }

    override fun onActivityStopped(p0: Activity) {
        numStarted--
    }

    override fun onActivitySaveInstanceState(p0: Activity, p1: Bundle) {

    }

    override fun onActivityDestroyed(p0: Activity) {

    }


}