package io.github.wulkanowy.ui.login.options

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.common.SmoothScrollLinearLayoutManager
import io.github.wulkanowy.R
import io.github.wulkanowy.ui.base.BaseFragment
import io.github.wulkanowy.utils.setOnItemClickListener
import kotlinx.android.synthetic.main.fragment_login_options.*
import javax.inject.Inject

class LoginOptionsFragment : BaseFragment(), LoginOptionsView {

    @Inject
    lateinit var presenter: LoginOptionsPresenter

    @Inject
    lateinit var loginAdapter: FlexibleAdapter<LoginOptionsItem>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_login_options, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        presenter.attachView(this)
    }

    override fun initRecycler() {
        loginAdapter.setOnItemClickListener { item -> item?.let { presenter.saveStudent(it.student) } }
        loginOptionsRecycler.run {
            adapter = loginAdapter
            layoutManager = SmoothScrollLinearLayoutManager(context)
        }
    }

    fun loadData() {
        presenter.refreshData()
        (activity as AppCompatActivity?)?.supportActionBar?.run {
            title = getString(R.string.login_options_header)
            show()
        }
    }

    override fun updateData(data: List<LoginOptionsItem>) {
        loginAdapter.run {
            updateDataSet(data)
        }
    }
}
