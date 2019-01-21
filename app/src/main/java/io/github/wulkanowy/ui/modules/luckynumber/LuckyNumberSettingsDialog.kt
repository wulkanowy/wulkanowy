package io.github.wulkanowy.ui.modules.luckynumber

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import dagger.android.support.DaggerAppCompatDialogFragment
import io.github.wulkanowy.R
import kotlinx.android.synthetic.main.dialog_lucky_number.*

class LuckyNumberSettingsDialog : DaggerAppCompatDialogFragment() {

    companion object {
        fun newInstance() = LuckyNumberSettingsDialog()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_lucky_number, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        luckyNumberDialogClose.setOnClickListener { dismiss() }
    }
}
