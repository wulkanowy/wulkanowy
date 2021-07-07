package io.github.wulkanowy.ui.modules.grade.details

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.View.GONE
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import io.github.wulkanowy.R
import io.github.wulkanowy.data.db.entities.Grade
import io.github.wulkanowy.databinding.FragmentGradeDetailsBinding
import io.github.wulkanowy.ui.base.BaseFragment
import io.github.wulkanowy.ui.modules.grade.GradeFragment
import io.github.wulkanowy.ui.modules.grade.GradeView
import io.github.wulkanowy.ui.modules.main.MainActivity
import io.github.wulkanowy.utils.getThemeAttrColor
import java.time.LocalDate
import javax.inject.Inject

@AndroidEntryPoint
class GradeDetailsFragment :
    BaseFragment<FragmentGradeDetailsBinding>(R.layout.fragment_grade_details), GradeDetailsView,
    GradeView.GradeChildView {

    @Inject
    lateinit var presenter: GradeDetailsPresenter

    @Inject
    lateinit var gradeDetailsAdapter: GradeDetailsAdapter

    private var gradeDetailsMenu: Menu? = null

    companion object {
        fun newInstance() = GradeDetailsFragment()
    }

    override val isViewEmpty
        get() = gradeDetailsAdapter.itemCount == 0

    private val menuReadItems = listOf(
            GradeDetailsMenuReadItem(null, R.id.gradeDetailsMenuReadAll),
            GradeDetailsMenuReadItem(1, R.id.gradeDetailsMenuReadOneDay),
            GradeDetailsMenuReadItem(3, R.id.gradeDetailsMenuReadThreeDays),
            GradeDetailsMenuReadItem(7, R.id.gradeDetailsMenuReadOneWeek),
            GradeDetailsMenuReadItem(14, R.id.gradeDetailsMenuReadTwoWeeks)
        )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentGradeDetailsBinding.bind(view)
        messageContainer = binding.gradeDetailsRecycler
        presenter.onAttachView(this)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.action_menu_grade_details, menu)
        gradeDetailsMenu = menu
        presenter.updateMarkAsDoneButton()
    }

    override fun initView() {
        gradeDetailsAdapter.onClickListener = presenter::onGradeItemSelected

        with(binding) {
            with(gradeDetailsRecycler) {
                layoutManager = LinearLayoutManager(context)
                adapter = gradeDetailsAdapter
            }
            gradeDetailsSwipe.setOnRefreshListener(presenter::onSwipeRefresh)
            gradeDetailsSwipe.setColorSchemeColors(requireContext().getThemeAttrColor(R.attr.colorPrimary))
            gradeDetailsSwipe.setProgressBackgroundColorSchemeColor(requireContext().getThemeAttrColor(R.attr.colorSwipeRefresh))
            gradeDetailsErrorRetry.setOnClickListener { presenter.onRetry() }
            gradeDetailsErrorDetails.setOnClickListener { presenter.onDetailsClick() }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val menuReadItem = menuReadItems.find { item.itemId == it.id }
        if (menuReadItem != null) {
            presenter.onMarkAsReadSelected(menuReadItem.date)
            return true
        }
        return false
    }

    override fun updateData(data: List<GradeDetailsItem>, isGradeExpandable: Boolean, gradeColorTheme: String) {
        with(gradeDetailsAdapter) {
            colorTheme = gradeColorTheme
            setDataItems(data, isGradeExpandable)
            notifyDataSetChanged()
        }
        presenter.updateMarkAsDoneButton()
    }

    override fun updateItem(item: Grade, position: Int) {
        gradeDetailsAdapter.updateDetailsItem(position, item)
        presenter.updateMarkAsDoneButton()
    }

    override fun clearView() {
        with(gradeDetailsAdapter) {
            setDataItems(mutableListOf())
            notifyDataSetChanged()
        }
    }

    override fun collapseAllItems() {
        gradeDetailsAdapter.collapseAll()
    }

    override fun scrollToStart() {
        binding.gradeDetailsRecycler.smoothScrollToPosition(0)
    }

    override fun getHeaderOfItem(subject: String): GradeDetailsItem {
        return gradeDetailsAdapter.getHeaderItem(subject)
    }

    override fun updateHeaderItem(item: GradeDetailsItem) {
        gradeDetailsAdapter.updateHeaderItem(item)
    }

    override fun showProgress(show: Boolean) {
        binding.gradeDetailsProgress.visibility = if (show) VISIBLE else GONE
    }

    override fun enableSwipe(enable: Boolean) {
        binding.gradeDetailsSwipe.isEnabled = enable
    }

    override fun showContent(show: Boolean) {
        binding.gradeDetailsRecycler.visibility = if (show) VISIBLE else INVISIBLE
    }

    override fun showEmpty(show: Boolean) {
        binding.gradeDetailsEmpty.visibility = if (show) VISIBLE else INVISIBLE
    }

    override fun showErrorView(show: Boolean) {
        binding.gradeDetailsError.visibility = if (show) VISIBLE else GONE
    }

    override fun setErrorDetails(message: String) {
        binding.gradeDetailsErrorMessage.text = message
    }

    override fun showRefresh(show: Boolean) {
        binding.gradeDetailsSwipe.isRefreshing = show
    }

    override fun showGradeDialog(grade: Grade, colorScheme: String) {
        (activity as? MainActivity)?.showDialogFragment(GradeDetailsDialog.newInstance(grade, colorScheme))
    }

    override fun onParentLoadData(semesterId: Int, forceRefresh: Boolean) {
        presenter.onParentViewLoadData(semesterId, forceRefresh)
    }

    override fun onParentReselected() {
        presenter.onParentViewReselected()
    }

    override fun onParentChangeSemester() {
        presenter.onParentViewChangeSemester()
    }

    override fun notifyParentDataLoaded(semesterId: Int) {
        (parentFragment as? GradeFragment)?.onChildFragmentLoaded(semesterId)
    }

    override fun notifyParentRefresh() {
        (parentFragment as? GradeFragment)?.onChildRefresh()
    }

    override fun disableMarkAsDoneButton() {
        gradeDetailsMenu?.apply {
            findItem(R.id.gradeDetailsMenuRead).isEnabled = false
            menuReadItems.forEach {
                findItem(it.id).isEnabled = false
            }
        }
    }

    override fun enableMarkAsDoneButton(oldestUnread: LocalDate) {
        gradeDetailsMenu?.apply {
            findItem(R.id.gradeDetailsMenuRead).isEnabled = true
            menuReadItems.forEach {
                val date = it.date
                findItem(it.id).isEnabled = date == null || date >= oldestUnread
            }
        }
    }

    override fun onDestroyView() {
        presenter.onDetachView()
        super.onDestroyView()
    }
}
