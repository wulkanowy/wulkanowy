package io.github.wulkanowy.ui.modules.mobiledevice

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import eu.davidea.flexibleadapter.common.FlexibleItemDecoration
import eu.davidea.flexibleadapter.common.SmoothScrollLinearLayoutManager
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import io.github.wulkanowy.R
import io.github.wulkanowy.ui.base.session.BaseSessionFragment
import io.github.wulkanowy.ui.modules.main.MainView
import kotlinx.android.synthetic.main.fragment_mobile_device.*
import javax.inject.Inject

class MobileDeviceFragment : BaseSessionFragment(), MobileDeviceView, MainView.TitledView {

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
        devicesAdapter.onDeviceUnregisterListener = { presenter.unregisterDevice(it) }
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

    override fun onDestroyView() {
        presenter.onDetachView()
        super.onDestroyView()
    }
}
