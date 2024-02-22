package com.lockboxth.lockboxkiosk.customdialog


import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.*
import androidx.fragment.app.DialogFragment
import com.lockboxth.lockboxkiosk.R
import kotlinx.android.synthetic.main.dialog_pudo_courier_alert.*


class ConfirmCourierAlertDialog : DialogFragment() {

    var onAcceptClickListener: (() -> Unit)? = null

    companion object {
        fun newInstance(): ConfirmCourierAlertDialog = ConfirmCourierAlertDialog().apply {

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
        dialog?.setCancelable(false)
        dialog?.window?.decorView?.systemUiVisibility = View.GONE;
        return inflater.inflate(R.layout.dialog_pudo_courier_alert, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {

        val timer = object : CountDownTimer(10000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                Log.d("TIMER", "seconds remaining: " + millisUntilFinished / 1000)
            }

            override fun onFinish() {
                try {
                    dismiss()
                    onAcceptClickListener?.invoke()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

        }.start()

        btnAccept.setOnClickListener {
            timer.cancel()
            dismiss()
            onAcceptClickListener?.invoke()
        }


    }


}