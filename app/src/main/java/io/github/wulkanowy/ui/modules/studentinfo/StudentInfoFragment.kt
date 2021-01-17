package io.github.wulkanowy.ui.modules.studentinfo

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import androidx.core.view.get
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import io.github.wulkanowy.R
import io.github.wulkanowy.data.db.entities.StudentInfo
import io.github.wulkanowy.databinding.FragmentStudentInfoBinding
import io.github.wulkanowy.ui.base.BaseFragment
import io.github.wulkanowy.ui.modules.main.MainActivity
import io.github.wulkanowy.ui.modules.main.MainView
import javax.inject.Inject

@AndroidEntryPoint
class StudentInfoFragment :
    BaseFragment<FragmentStudentInfoBinding>(R.layout.fragment_student_info), StudentInfoView,
    MainView.TitledView {

    @Inject
    lateinit var presenter: StudentInfoPresenter

    @Inject
    lateinit var studentInfoAdapter: StudentInfoAdapter

    override val titleStringId: Int
        get() = R.string.student_info_title

    companion object {

        private const val ARGUMENT_KEY = "info_type"

        fun newInstance(type: StudentInfoView.Type) = StudentInfoFragment().apply {
            arguments = Bundle().apply { putSerializable(ARGUMENT_KEY, type) }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentStudentInfoBinding.bind(view)
        presenter.onAttachView(
            this,
            requireArguments().getSerializable(ARGUMENT_KEY) as StudentInfoView.Type
        )
    }

    override fun initView() {
        studentInfoAdapter.onItemClickListener = presenter::onItemSelected

        with(binding.studentInfoRecycler) {
            layoutManager = LinearLayoutManager(context)
            addItemDecoration(DividerItemDecoration(context, RecyclerView.VERTICAL))
            setHasFixedSize(true)
            adapter = studentInfoAdapter
        }
    }

    override fun updateData(data: List<Pair<String, String>>) {
        with(studentInfoAdapter) {
            items = data
            notifyDataSetChanged()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu[0].isVisible = false
    }

    override fun showPersonalTypeData(studentInfo: StudentInfo) {
        updateData(
            listOf(
                "Imię" to studentInfo.firstName,
                "Drugię imię" to studentInfo.secondName,
                "Płeć" to studentInfo.gender,
                "Obywatelstwo polskie" to studentInfo.hasPolishCitizenship.toString(),
                "Nazwisko rodowe" to studentInfo.familyName,
                "Imię matki i ojca" to studentInfo.parentsNames
            )
        )
    }

    override fun showContactTypeData(studentInfo: StudentInfo) {
        updateData(
            listOf(
                "Telefon" to studentInfo.phoneNumber,
                "Telefon komórkowy" to studentInfo.cellPhoneNumber,
                "E-mail" to studentInfo.email
            )
        )
    }

    @SuppressLint("DefaultLocale")
    override fun showFamilyTypeData(studentInfo: StudentInfo) {
        updateData(
            listOf(
                studentInfo.firstGuardian.kinship.capitalize() to studentInfo.firstGuardian.fullName,
                studentInfo.secondGuardian.kinship.capitalize() to studentInfo.secondGuardian.fullName
            )
        )
    }

    override fun showAddressTypeData(studentInfo: StudentInfo) {
        updateData(
            listOf(
                "Adres zamieszkania" to studentInfo.address,
                "Adres zameldowania" to studentInfo.registeredAddress,
                "Adres korenspondencji" to studentInfo.correspondenceAddress
            )
        )
    }

    override fun showFirstGuardianTypeData(studentInfo: StudentInfo) {
        updateData(
            listOf(
                "Nazwisko i imię" to studentInfo.firstGuardian.fullName,
                "Stopień pokrewieństwa" to studentInfo.firstGuardian.kinship,
                "Adres" to studentInfo.firstGuardian.address,
                "Telefony" to studentInfo.firstGuardian.phones,
                "E-mail" to studentInfo.firstGuardian.email
            )
        )
    }

    override fun showSecondGuardianTypeData(studentInfo: StudentInfo) {
        updateData(
            listOf(
                "Nazwisko i imię" to studentInfo.secondGuardian.fullName,
                "Stopień pokrewieństwa" to studentInfo.secondGuardian.kinship,
                "Adres" to studentInfo.secondGuardian.address,
                "Telefony" to studentInfo.secondGuardian.phones,
                "E-mail" to studentInfo.secondGuardian.email
            )
        )
    }

    override fun openStudentInfoView(infoType: StudentInfoView.Type) {
        (requireActivity() as MainActivity).pushView(newInstance(infoType))
    }
}