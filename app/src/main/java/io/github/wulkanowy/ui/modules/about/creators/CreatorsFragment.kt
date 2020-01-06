package io.github.wulkanowy.ui.modules.about.creators

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.text.parseAsHtml
import com.mikepenz.aboutlibraries.Libs
import dagger.Lazy
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.common.SmoothScrollLinearLayoutManager
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import io.github.wulkanowy.R
import io.github.wulkanowy.ui.base.BaseFragment
import io.github.wulkanowy.ui.modules.main.MainView
import io.github.wulkanowy.utils.openInternetBrowser
import io.github.wulkanowy.utils.setOnItemClickListener
import kotlinx.android.synthetic.main.fragment_creators.*
import org.xmlpull.v1.XmlPullParser
import javax.inject.Inject

class CreatorsFragment : BaseFragment(), CreatorsView, MainView.TitledView {

    @Inject
    lateinit var presenter: CreatorsPresenter

    @Inject
    lateinit var creatorsAdapter: FlexibleAdapter<AbstractFlexibleItem<*>>

    @Inject
    lateinit var libs: Lazy<Libs>

    override val titleStringId get() = R.string.creators_title

    companion object {
        fun newInstance() = CreatorsFragment()
    }

    override val appCreators
        get() = CreatorsXMLParser().parse(requireContext().resources.getXml(R.xml.creators))

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_creators, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        presenter.onAttachView(this)
    }

    override fun initView() {
        with(creatorsRecycler) {
            layoutManager = SmoothScrollLinearLayoutManager(context)
            adapter = creatorsAdapter
        }
        creatorsAdapter.setOnItemClickListener(presenter::onItemSelected)
    }

    override fun updateData(data: List<CreatorsItem>) {
        creatorsAdapter.updateDataSet(data)
    }

    override fun openUserGithubPage(username: String) {
        context?.openInternetBrowser("https://github.com/${username}", ::showMessage)
    }

    override fun showProgress(show: Boolean) {
        creatorsProgress.visibility = if (show) VISIBLE else GONE
    }

    override fun onDestroyView() {
        presenter.onDetachView()
        super.onDestroyView()
    }
}
