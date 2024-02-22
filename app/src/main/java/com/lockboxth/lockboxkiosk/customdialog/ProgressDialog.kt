package com.lockboxth.lockboxkiosk.customdialog


import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.*
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.lockboxth.lockboxkiosk.R
import com.lockboxth.lockboxkiosk.helpers.Util
import kotlinx.android.synthetic.main.dialog_loading.*

class ProgressDialog : DialogFragment() {

    var isShowing = false
    var message: String? = null

    companion object {
        fun newInstance(): ProgressDialog = ProgressDialog().apply { }
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
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )
        dialog?.window?.setBackgroundDrawable(ColorDrawable(android.graphics.Color.TRANSPARENT))
        dialog?.setCancelable(false)
        dialog?.window?.decorView?.systemUiVisibility = View.GONE;
        return inflater.inflate(R.layout.dialog_loading, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (message == null) {
            message = "ขณะนี้ระบบกำลังทำงาน กรุณารอสักครู่..."
        }
        tvMessage.text = message
    }

    fun updateMessage(message: String) {
        tvMessage?.text = message
    }


}