package io.github.wulkanowy.ui.modules.mobiledevice.token

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import dagger.android.support.DaggerDialogFragment
import io.github.wulkanowy.R
import io.github.wulkanowy.ui.base.BaseActivity
import kotlinx.android.synthetic.main.dialog_mobile_device.*
import javax.inject.Inject

class MobileDeviceTokenDialog : DaggerDialogFragment(), MobileDeviceTokenVIew {

    @Inject
    lateinit var presenter: MobileDeviceTokenPresenter

    companion object {
        fun newInstance(): MobileDeviceTokenDialog {
            return MobileDeviceTokenDialog()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, 0)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_mobile_device, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        presenter.onAttachView(this)
    }

    override fun initView() {
        mobileDeviceDialogClose.setOnClickListener { dismiss() }
    }

    override fun updateData(token: Triple<String, String, String>) {
        mobileDeviceDialogToken.text = token.first
        mobileDeviceDialogSymbol.text = token.second
        mobileDeviceDialogPin.text = token.third
    }

    override fun hideLoading() {
        mobileDeviceDialogProgress.visibility = GONE
    }

    override fun showContent() {
        mobileDeviceDialogContent.visibility = VISIBLE
    }

    override fun showError(text: String, error: Throwable) {
        mobileDeviceDialogMessage.run {
            this.visibility = VISIBLE
            this.text = text
        }
    }

    override fun showMessage(text: String) {
        mobileDeviceDialogMessage.run {
            this.visibility = VISIBLE
            this.text = text
        }
    }

    override fun showExpiredDialog() {
        (activity as? BaseActivity<*>)?.showExpiredDialog()
    }

    override fun openClearLoginView() {
        (activity as? BaseActivity<*>)?.openClearLoginView()
    }
}
