package io.github.wulkanowy.ui.modules.timetable.realized

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.common.SmoothScrollLinearLayoutManager
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import io.github.wulkanowy.R
import io.github.wulkanowy.data.db.entities.Realized
import io.github.wulkanowy.ui.base.session.BaseSessionFragment
import io.github.wulkanowy.ui.modules.main.MainActivity
import io.github.wulkanowy.ui.modules.main.MainView
import io.github.wulkanowy.utils.setOnItemClickListener
import kotlinx.android.synthetic.main.fragment_timetable_realized.*
import javax.inject.Inject

class RealizedFragment : BaseSessionFragment(), RealizedView, MainView.TitledView {

    @Inject
    lateinit var presenter: RealizedPresenter

    @Inject
    lateinit var realizedAdapter: FlexibleAdapter<AbstractFlexibleItem<*>>

    companion object {
        private const val SAVED_DATE_KEY = "CURRENT_DATE"

        fun newInstance() = RealizedFragment()
    }

    override val titleStringId: Int
        get() = R.string.realized_title

    override val isViewEmpty
        get() = realizedAdapter.isEmpty

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_timetable_realized, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        messageContainer = realizedRecycler
        presenter.onAttachView(this, savedInstanceState?.getLong(SAVED_DATE_KEY))
    }

    override fun initView() {
        realizedAdapter.run {
            setOnItemClickListener { presenter.onRealizedItemSelected(it) }
        }

        realizedRecycler.run {
            layoutManager = SmoothScrollLinearLayoutManager(context)
            adapter = realizedAdapter
        }
        realizedSwipe.setOnRefreshListener { presenter.onSwipeRefresh() }
        realizedPreviousButton.setOnClickListener { presenter.onPreviousDay() }
        realizedNextButton.setOnClickListener { presenter.onNextDay() }
    }

    override fun updateData(data: List<RealizedItem>) {
        realizedAdapter.updateDataSet(data, true)
    }

    override fun clearData() {
        realizedAdapter.clear()
    }

    override fun updateNavigationDay(date: String) {
        realizedNavDate.text = date
    }

    override fun hideRefresh() {
        realizedSwipe.isRefreshing = false
    }

    override fun showEmpty(show: Boolean) {
        realizedEmpty.visibility = if (show) View.VISIBLE else View.GONE
    }

    override fun showProgress(show: Boolean) {
        realizedProgress.visibility = if (show) View.VISIBLE else View.GONE
    }

    override fun showContent(show: Boolean) {
        realizedRecycler.visibility = if (show) View.VISIBLE else View.GONE
    }

    override fun showPreButton(show: Boolean) {
        realizedPreviousButton.visibility = if (show) View.VISIBLE else View.INVISIBLE
    }

    override fun showNextButton(show: Boolean) {
        realizedNextButton.visibility = if (show) View.VISIBLE else View.INVISIBLE
    }

    override fun showRealizedDialog(realized: Realized) {
        (activity as? MainActivity)?.showDialogFragment(RealizedDialog.newInstance(realized))
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putLong(RealizedFragment.SAVED_DATE_KEY, presenter.currentDate.toEpochDay())
    }

    override fun onDestroyView() {
        presenter.onDetachView()
        super.onDestroyView()
    }
}
