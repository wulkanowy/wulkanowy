package io.github.wulkanowy.ui.base.contextmenu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import io.github.wulkanowy.databinding.DialogContextMenuBinding
import io.github.wulkanowy.ui.base.BaseDialogFragment
import io.github.wulkanowy.ui.widgets.DividerItemDecoration
import javax.inject.Inject

abstract class BaseContextMenu : BaseDialogFragment<DialogContextMenuBinding>(), ContextMenuView {
    @Inject
    lateinit var adapter: ContextMenuAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, 0)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return DialogContextMenuBinding.inflate(inflater).apply { binding = this }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = DialogContextMenuBinding.bind(view)
        onViewCreated()
        initView()
    }

    override fun initView() {
        with (binding.contextMenuRecycler) {
            layoutManager = LinearLayoutManager(context)
            this@BaseContextMenu.adapter.items = getItems()
            adapter = this@BaseContextMenu.adapter
            this@BaseContextMenu.adapter.onClickListener = {
                onItemClicked(it)
            }
            adapter!!.notifyDataSetChanged()

        }
    }

    override fun closeDialog() {
        dismiss()
    }

    abstract fun onViewCreated()

    abstract fun getItems(): MutableList<ContextMenuItem>

    abstract fun onItemClicked(item: ContextMenuItem)
}
