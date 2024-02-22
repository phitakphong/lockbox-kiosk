package com.lockboxth.lockboxkiosk.customdialog


import android.os.Bundle
import android.os.CountDownTimer
import android.view.*
import androidx.fragment.app.DialogFragment
import com.lockboxth.lockboxkiosk.R
import com.lockboxth.lockboxkiosk.helpers.Util
import kotlinx.android.synthetic.main.dialog_confirm_cancel_pudo.*
import kotlinx.android.synthetic.main.dialog_confirm_register.*
import kotlinx.android.synthetic.main.dialog_confirm_register.btnCancel
import kotlinx.android.synthetic.main.dialog_confirm_register.btnConfirm

class ConfirmCancelPuduSendDialog : DialogFragment() {

    var onOkClickListener: (() -> Unit)? = null
    var timeoutCallback: (() -> Unit)? = null

    var hideSubtitle = false

    companion object {
        fun newInstance(): ConfirmCancelPuduSendDialog = ConfirmCancelPuduSendDialog().apply { }
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
        dialog?.setCancelable(false)
        dialog?.window?.decorView?.systemUiVisibility = View.GONE;
        return inflater.inflate(R.layout.dialog_confirm_cancel_pudo, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        tvSubTitle.visibility = if (hideSubtitle) {
            View.GONE
        } else {
            View.VISIBLE
        }
        val timeLengthMilli: Long = (15 * 1000)
        val timer = object : CountDownTimer(timeLengthMilli, 1000) {
            override fun onTick(millisUntilFinished: Long) {}
            override fun onFinish() {
                timeoutCallback?.invoke()
            }
        }
        timer.start()
        btnConfirm.setOnClickListener {
            dismiss()
            timer.cancel()
            onOkClickListener?.invoke()
        }

        btnCancel.setOnClickListener {
            timer.cancel()
            dismiss()
        }

    }


}