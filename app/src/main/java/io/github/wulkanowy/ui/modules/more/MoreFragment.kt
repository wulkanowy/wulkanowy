package io.github.wulkanowy.ui.modules.more

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import io.github.wulkanowy.R
import io.github.wulkanowy.databinding.FragmentMoreBinding
import io.github.wulkanowy.ui.base.BaseFragment
import io.github.wulkanowy.ui.modules.Destination
import io.github.wulkanowy.ui.modules.dashboard.DashboardItem
import io.github.wulkanowy.ui.modules.main.MainActivity
import io.github.wulkanowy.ui.modules.main.MainView
import io.github.wulkanowy.ui.modules.message.MessageFragment
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class MoreFragment : BaseFragment<FragmentMoreBinding>(R.layout.fragment_more), MoreView,
    MainView.TitledView, MainView.MainChildView {

    @Inject
    lateinit var presenter: MorePresenter

    @Inject
    lateinit var moreAdapter: MoreAdapter

    companion object {
        fun newInstance() = MoreFragment()
    }

    override val titleStringId: Int
        get() = R.string.more_title

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentMoreBinding.bind(view)
        presenter.onAttachView(this)
    }

    override fun initView() {
        moreAdapter.onClickListener = presenter::onItemSelected
        moreAdapter.onLongClickListener = presenter::onItemHold

        with(binding.moreRecycler) {
            layoutManager = LinearLayoutManager(context)
            adapter = moreAdapter
        }
    }

    override fun onFragmentReselected() {
        if (::presenter.isInitialized) presenter.onViewReselected()
    }

    override fun onFragmentChanged() {
        (parentFragmentManager.fragments.find { it is MessageFragment } as MessageFragment?)
            ?.onFragmentChanged()
    }

    override fun updateData(data: List<MoreItem>) {
        with(moreAdapter) {
            items = data
            notifyDataSetChanged()
        }
    }

    override fun popView(depth: Int) {
        (activity as? MainActivity)?.popView(depth)
    }

    override fun openView(destination: Destination) {
        (activity as? MainActivity)?.pushView(destination.destinationFragment)
    }

    override fun onDestroyView() {
        presenter.onDetachView()
        super.onDestroyView()
    }

    override fun showHiddenSettings(data: List<DashboardItem.HiddenSettingTile>) {
        val entries = requireContext().resources.getStringArray(R.array.hidden_settings_entries)
        val values = requireContext().resources.getStringArray(R.array.hidden_settings_values)
        val selectedItemsState = values.map { value -> data.any { it.name == value } }

        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.pref_hidden_settings_title)
            .setMultiChoiceItems(entries, selectedItemsState.toBooleanArray()) { _, _, _ -> }
            .setPositiveButton(android.R.string.ok) { dialog, _ ->
                val selectedState = (dialog as AlertDialog).listView.checkedItemPositions
                val selectedValues = values.filterIndexed { index, _ -> selectedState[index] }

                Timber.i("Selected hidden settings: $selectedValues")
                presenter.onHiddenSettingsSelected(selectedValues)
            }
            .setNegativeButton(android.R.string.cancel) { _, _ -> }
            .show()
    }

    override fun restartApp() {
        startActivity(MainActivity.getStartIntent(requireContext()))
        requireActivity().finishAffinity()
    }
}
