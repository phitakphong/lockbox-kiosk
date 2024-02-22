package com.lockboxth.lockboxkiosk.customdialog


import android.os.Bundle
import android.os.CountDownTimer
import android.view.*
import androidx.fragment.app.DialogFragment
import com.lockboxth.lockboxkiosk.R
import com.lockboxth.lockboxkiosk.helpers.Util
import kotlinx.android.synthetic.main.dialog_alert.*

class AlertMessageDialog : DialogFragment() {

    private var message: String = ""

    var onAcceptClickListener: (() -> Unit)? = null
    var timeoutCallback: (() -> Unit)? = null

    companion object {
        fun newInstance(message: String): AlertMessageDialog = AlertMessageDialog().apply {
            this.message = message
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog?.window?.setLayout(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        dialog?.window?.decorView?.systemUiVisibility = View.GONE
        dialog?.setCancelable(false)
        return inflater.inflate(R.layout.dialog_alert, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        val timeLengthMilli: Long = (15 * 1000)
        val timer = object : CountDownTimer(timeLengthMilli, 1000) {
            override fun onTick(millisUntilFinished: Long) {}
            override fun onFinish() {
                timeoutCallback?.invoke()
            }
        }
        timer.start()
        tvMessage.text = message
        btnConfirm.setOnClickListener {
            dismiss()
            timer.cancel()
            onAcceptClickListener?.invoke()
        }


    }


}