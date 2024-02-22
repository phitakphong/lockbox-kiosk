package com.lockboxth.lockboxkiosk.customdialog
import android.os.Bundle
import android.view.*
import androidx.fragment.app.DialogFragment
import com.lockboxth.lockboxkiosk.R

class SelectCountryCodeDialog : DialogFragment() {

    var onOkClickListener: (() -> Unit)? = null

    companion object {
        fun newInstance(): SelectCountryCodeDialog = SelectCountryCodeDialog().apply {}
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_select_phone_number, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {

    }
}