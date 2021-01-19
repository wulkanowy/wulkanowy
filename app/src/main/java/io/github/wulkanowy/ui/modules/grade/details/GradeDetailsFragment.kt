package io.github.wulkanowy.ui.modules.grade.details

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import dagger.hilt.android.AndroidEntryPoint
import io.github.wulkanowy.R
import io.github.wulkanowy.data.db.entities.Grade
import io.github.wulkanowy.databinding.FragmentGradeDetailsBinding
import io.github.wulkanowy.ui.base.BaseFragment
import io.github.wulkanowy.ui.modules.grade.GradeFragment
import io.github.wulkanowy.ui.modules.grade.GradeView
import io.github.wulkanowy.ui.modules.main.MainActivity
import timber.log.Timber
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
            gradeDetailsSwipe.setOnRefreshListener { presenter.onSwipeRefresh() }
            gradeDetailsErrorRetry.setOnClickListener { presenter.onRetry() }
            gradeDetailsErrorDetails.setOnClickListener { presenter.onDetailsClick() }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == R.id.gradeDetailsMenuRead) presenter.onMarkAsReadSelected()
        else false
    }

    override fun updateData(data: List<GradeDetailsItem>, isGradeExpandable: Boolean, gradeColorTheme: String) {
        with(gradeDetailsAdapter) {
            colorTheme = gradeColorTheme
            setDataItems(data, isGradeExpandable)
            notifyDataSetChanged()
        }
    }

    override fun updateItem(item: Grade, position: Int) {
        gradeDetailsAdapter.updateDetailsItem(position, item)
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
        view?.findViewById<View>(R.id.gradeDetailsProgress)?.let {
            Timber.i("${this::class.java.simpleName} Show progress: $show, Current: ${it.visibility == VISIBLE}")
            it.visibility = if (show) VISIBLE else GONE
        }
    }

    override fun enableSwipe(enable: Boolean) {
        view?.findViewById<SwipeRefreshLayout>(R.id.gradeDetailsSwipe)?.let {
            Timber.i("${this::class.java.simpleName} Enable swipe: $enable, Current: ${it.isEnabled}")
            it.isEnabled = enable
        }
    }

    override fun showContent(show: Boolean) {
        view?.findViewById<View>(R.id.gradeDetailsRecycler)?.let {
            Timber.i("${this::class.java.simpleName} Show content: $show, Current: ${it.visibility == VISIBLE}")
            it.visibility = if (show) VISIBLE else GONE
        }
    }

    override fun showEmpty(show: Boolean) {
        view?.findViewById<View>(R.id.gradeDetailsEmpty)?.let {
            Timber.i("${this::class.java.simpleName} Show empty: $show, Current: ${it.visibility == VISIBLE}")
            it.visibility = if (show) VISIBLE else GONE
        }
    }

    override fun showErrorView(show: Boolean) {
        view?.findViewById<View>(R.id.gradeDetailsError)?.let {
            Timber.i("${this::class.java.simpleName} Show error view: $show, Current: ${it.visibility == VISIBLE}")
            it.visibility = if (show) VISIBLE else GONE
        }
    }

    override fun setErrorDetails(message: String) {
        view?.findViewById<TextView>(R.id.gradeDetailsErrorMessage)?.let {
            Timber.i("${this::class.java.simpleName} Show error details: $message, Current: ${it.text}")
            it.text = message
        }
    }

    override fun showRefresh(show: Boolean) {
        view?.findViewById<SwipeRefreshLayout>(R.id.gradeDetailsSwipe)?.let {
            Timber.i("${this::class.java.simpleName} Show refresh: $show, Current: ${it.isRefreshing}")
            it.isRefreshing = show
        }
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

    override fun enableMarkAsDoneButton(enable: Boolean) {
        gradeDetailsMenu?.findItem(R.id.gradeDetailsMenuRead)?.isEnabled = enable
    }

    override fun onDestroyView() {
        presenter.onDetachView()
        super.onDestroyView()
    }
}
