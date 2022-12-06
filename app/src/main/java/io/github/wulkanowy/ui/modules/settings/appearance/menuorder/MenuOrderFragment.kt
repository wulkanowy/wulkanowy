package io.github.wulkanowy.ui.modules.settings.appearance.menuorder

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import io.github.wulkanowy.R
import io.github.wulkanowy.databinding.FragmentMenuOrderBinding
import io.github.wulkanowy.ui.base.BaseFragment
import io.github.wulkanowy.ui.modules.main.MainView
import io.github.wulkanowy.ui.widgets.DividerItemDecoration
import javax.inject.Inject

@AndroidEntryPoint
class MenuOrderFragment : BaseFragment<FragmentMenuOrderBinding>(R.layout.fragment_menu_order),
    MenuOrderView, MainView.TitledView {

    @Inject
    lateinit var presenter: MenuOrderPresenter

    @Inject
    lateinit var menuOrderAdapter: MenuOrderAdapter

    override val titleStringId = R.string.menu_order_title

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentMenuOrderBinding.bind(view)
        presenter.onAttachView(this)
    }

    override fun initView() {
        val itemTouchHelper = ItemTouchHelper(
            MenuItemMoveCallback(menuOrderAdapter, presenter::onDragAndDropEnd)
        )

        itemTouchHelper.attachToRecyclerView(binding.menuOrderRecycler)

        with(binding.menuOrderRecycler) {
            layoutManager = LinearLayoutManager(context)
            adapter = menuOrderAdapter
            addItemDecoration(DividerItemDecoration(context))
        }
    }

    override fun updateData(data: List<MenuItem>) {
        menuOrderAdapter.submitList(data)
    }
}
