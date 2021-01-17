package io.github.wulkanowy.ui.modules.studentinfo

import io.github.wulkanowy.data.db.entities.StudentInfo
import io.github.wulkanowy.ui.base.BaseView

interface StudentInfoView : BaseView {

    enum class Type {
        PERSONAL, ADDRESS, CONTACT, FAMILY, FIRST_GUARDIAN, SECOND_GUARDIAN
    }

    fun initView()

    fun updateData(data: List<Pair<String, String>>)

    fun showPersonalTypeData(studentInfo: StudentInfo)

    fun showContactTypeData(studentInfo: StudentInfo)

    fun showAddressTypeData(studentInfo: StudentInfo)

    fun showFamilyTypeData(studentInfo: StudentInfo)

    fun showFirstGuardianTypeData(studentInfo: StudentInfo)

    fun showSecondGuardianTypeData(studentInfo: StudentInfo)

    fun openStudentInfoView(infoType: Type)
}