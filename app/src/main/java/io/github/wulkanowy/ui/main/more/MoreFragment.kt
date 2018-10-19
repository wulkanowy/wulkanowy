package io.github.wulkanowy.ui.main.more

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.common.SmoothScrollLinearLayoutManager
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import io.github.wulkanowy.R
import io.github.wulkanowy.ui.base.BaseFragment
import io.github.wulkanowy.ui.main.MainActivity
import io.github.wulkanowy.ui.main.settings.SettingsFragment
import io.github.wulkanowy.utils.setOnItemClickListener
import kotlinx.android.synthetic.main.fragment_more.*
import javax.inject.Inject

class MoreFragment : BaseFragment(), MoreView {

    @Inject
    lateinit var presenter: MorePresenter

    @Inject
    lateinit var moreAdapter: FlexibleAdapter<AbstractFlexibleItem<*>>

    companion object {
        fun newInstance() = MoreFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_more, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        presenter.onAttachView(this)
    }

    override fun initView() {
        moreAdapter.run { setOnItemClickListener { presenter.onItemSelected(getItem(it)) } }

        moreRecycler.apply {
            layoutManager = SmoothScrollLinearLayoutManager(context)
            adapter = moreAdapter
        }
    }

    override fun updateData(data: List<MoreItem>) {
        moreAdapter.updateDataSet(data)
    }

    override fun openSettingsView() {
        (activity as? MainActivity)?.pushFragment(SettingsFragment.newInstance())
    }

    override fun settingsRes(): Pair<String, Drawable?>? {
        return context?.run {
            getString(R.string.settings_title) to
                    ContextCompat.getDrawable(this, R.drawable.ic_more_settings_24dp)
        }
    }
}
