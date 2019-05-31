package io.github.wulkanowy.ui.modules.mobiledevice

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import eu.davidea.flexibleadapter.common.FlexibleItemDecoration
import eu.davidea.flexibleadapter.common.SmoothScrollLinearLayoutManager
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import io.github.wulkanowy.R
import io.github.wulkanowy.data.db.entities.MobileDevice
import io.github.wulkanowy.ui.base.BaseFragment
import io.github.wulkanowy.ui.modules.main.MainActivity
import io.github.wulkanowy.ui.modules.main.MainView
import io.github.wulkanowy.ui.modules.mobiledevice.token.MobileDeviceTokenDialog
import kotlinx.android.synthetic.main.fragment_mobile_device.*
import javax.inject.Inject

class MobileDeviceFragment : BaseFragment(), MobileDeviceView, MainView.TitledView {

    @Inject
    lateinit var presenter: MobileDevicePresenter

    @Inject
    lateinit var devicesAdapter: MobileDeviceAdapter<AbstractFlexibleItem<*>>

    companion object {
        fun newInstance() = MobileDeviceFragment()
    }

    override val titleStringId: Int
        get() = R.string.mobile_devices_title

    override val isViewEmpty: Boolean
        get() = devicesAdapter.isEmpty

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_mobile_device, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        presenter.onAttachView(this)
    }

    override fun initView() {
        mobileDevicesRecycler.run {
            layoutManager = SmoothScrollLinearLayoutManager(context)
            adapter = devicesAdapter
            addItemDecoration(FlexibleItemDecoration(context)
                .withDefaultDivider()
                .withDrawDividerOnLastItem(false)
            )
        }
        mobileDevicesSwipe.setOnRefreshListener { presenter.onSwipeRefresh() }
        mobileDeviceAddButton.setOnClickListener { presenter.onRegisterDevice() }
        devicesAdapter.onDeviceUnregisterListener = { presenter.onUnregister(it) }
    }

    override fun showUnregisterConfirmDialog(device: MobileDevice) {
        context?.let {
            AlertDialog.Builder(it)
                .setTitle(R.string.mobile_device_removal)
                .setMessage(R.string.mobile_device_removal_confirm)
                .setPositiveButton(R.string.mobile_devices_unregister) { _, _ -> presenter.onUnregisterConfirmed(device) }
                .setNegativeButton(android.R.string.cancel) { _, _ -> }
                .show()
        }
    }

    override fun updateData(data: List<MobileDeviceItem>) {
        devicesAdapter.updateDataSet(data, true)
    }

    override fun clearData() {
        devicesAdapter.clear()
    }

    override fun showEmpty(show: Boolean) {
        mobileDevicesEmpty.visibility = if (show) VISIBLE else GONE
    }

    override fun hideRefresh() {
        mobileDevicesSwipe.isRefreshing = false
    }

    override fun showProgress(show: Boolean) {
        mobileDevicesProgress.visibility = if (show) VISIBLE else GONE
    }

    override fun enableSwipe(enable: Boolean) {
        mobileDevicesSwipe.isEnabled = enable
    }

    override fun showContent(show: Boolean) {
        mobileDevicesRecycler.visibility = if (show) VISIBLE else GONE
    }

    override fun showTokenDialog() {
        (activity as? MainActivity)?.showDialogFragment(MobileDeviceTokenDialog.newInstance())
    }

    override fun onDestroyView() {
        presenter.onDetachView()
        super.onDestroyView()
    }
}
