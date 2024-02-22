package com.lockboxth.lockboxkiosk.customdialog


import android.os.Bundle
import android.os.CountDownTimer
import android.view.*
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.lockboxth.lockboxkiosk.R
import com.lockboxth.lockboxkiosk.helpers.Util

class CustomDialog : DialogFragment() {

    var viewId: Int = 0
    var onOkClickListener: (() -> Unit)? = null
    var timeoutCallback: (() -> Unit)? = null

    var message: String? = null

    companion object {
        fun newInstance(viewId: Int): CustomDialog = CustomDialog().apply {
            this.viewId = viewId
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
        return inflater.inflate(this.viewId, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init(view)
    }

    private fun init(view: View) {
        val timeLengthMilli: Long = (15 * 1000)
        val timer = object : CountDownTimer(timeLengthMilli, 1000) {
            override fun onTick(millisUntilFinished: Long) {}
            override fun onFinish() {
                try {
                    dismiss()
                    timeoutCallback?.invoke()
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }
        }
        timer.start()

        val btnAccept = view.findViewById<View>(R.id.btnAccept)
        btnAccept?.setOnClickListener {
            dismiss()
            timer.cancel()
            onOkClickListener?.invoke()
        }
        val tvMessage = view.findViewById<TextView>(R.id.tvMessage)
        if (tvMessage != null && message != null) {
            tvMessage.text = message
        }
    }


}