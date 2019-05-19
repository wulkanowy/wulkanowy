package io.github.wulkanowy.ui.modules.mobiledevice

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import io.github.wulkanowy.R
import kotlinx.android.synthetic.main.dialog_mobile_device.*

class MobileDeviceDialog : DialogFragment() {

    private lateinit var token: Triple<*, *, *>

    companion object {
        private const val ARGUMENT_KEY = "Item"

        fun newInstance(token: Triple<String, String, String>): MobileDeviceDialog {
            return MobileDeviceDialog().apply {
                arguments = Bundle().apply { putSerializable(ARGUMENT_KEY, token) }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, 0)
        arguments?.run {
            token = getSerializable(ARGUMENT_KEY) as Triple<*, *, *>
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_mobile_device, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        mobileDeviceDialogPin.text = token.first as String
        mobileDeviceDialogToken.text = token.second as String
        mobileDeviceDialogSymbol.text = token.third as String
        mobileDeviceDialogClose.setOnClickListener { dismiss() }
    }
}
