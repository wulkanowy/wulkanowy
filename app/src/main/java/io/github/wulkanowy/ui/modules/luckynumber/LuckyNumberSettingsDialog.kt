package io.github.wulkanowy.ui.modules.luckynumber

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import dagger.android.support.DaggerAppCompatDialogFragment
import io.github.wulkanowy.R
import kotlinx.android.synthetic.main.dialog_lucky_number.*
import javax.inject.Inject

class LuckyNumberSettingsDialog : DaggerAppCompatDialogFragment(), LuckyNumberSettingsView {

    @Inject
    lateinit var presenter: LuckyNumberSettingsPresenter

    companion object {
        fun newInstance() = LuckyNumberSettingsDialog()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_lucky_number, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        presenter.onAttachView(this)
    }

    override fun initView() {
        luckyNumberDialogClose.setOnClickListener { dismiss() }
        luckyNumberDialogSave.setOnClickListener { presenter.onSave() }
    }

    override fun updateData(allNotifications: Boolean, selfNotifications: Boolean, registerNumber: Int?) {
        luckyNumberSwitchAllNotifications.isChecked = allNotifications
        luckyNumberSwitchSelfNotifications.isChecked = selfNotifications
        if (registerNumber !== null) luckyNumberInputRegisterNumber.setText(registerNumber.toString())
    }

    override fun showError(text: String, error: Throwable) {
        showMessage(text)
    }

    override fun showMessage(text: String) {
        Toast.makeText(context, text, Toast.LENGTH_LONG).show()
    }
}
