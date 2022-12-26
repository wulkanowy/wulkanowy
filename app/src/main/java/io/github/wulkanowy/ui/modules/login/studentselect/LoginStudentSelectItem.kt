package io.github.wulkanowy.ui.modules.login.studentselect

import io.github.wulkanowy.data.pojos.RegisterStudent
import io.github.wulkanowy.data.pojos.RegisterSymbol
import io.github.wulkanowy.data.pojos.RegisterTeacher
import io.github.wulkanowy.data.pojos.RegisterUnit


sealed class LoginStudentSelectItem(val type: LoginStudentSelectItemType) {

    data class EmptySymbolsHeader(
        val isExpanded: Boolean,
        val onClick: () -> Unit,
    ) : LoginStudentSelectItem(LoginStudentSelectItemType.EMPTY_SYMBOLS_HEADER)

    data class SymbolHeader(
        val symbol: RegisterSymbol,
        val humanReadableName: String?,
    ) : LoginStudentSelectItem(LoginStudentSelectItemType.SYMBOL_HEADER)

    data class SchoolHeader(
        val unit: RegisterUnit,
    ) : LoginStudentSelectItem(LoginStudentSelectItemType.SCHOOL_HEADER)

    data class Student(
        val symbol: RegisterSymbol,
        val unit: RegisterUnit,
        val student: RegisterStudent,
        val isEnabled: Boolean,
        val isSelected: Boolean,
        val onClick: (Student) -> Unit,
    ) : LoginStudentSelectItem(LoginStudentSelectItemType.STUDENT)

    data class Teacher(
        val symbol: RegisterSymbol,
        val unit: RegisterUnit,
        val teacher: RegisterTeacher,
        val isEnabled: Boolean,
        val isSelected: Boolean,
        val onClick: (Student) -> Unit,
    ) : LoginStudentSelectItem(LoginStudentSelectItemType.TEACHER)
}

enum class LoginStudentSelectItemType {
    EMPTY_SYMBOLS_HEADER,
    SYMBOL_HEADER,
    SCHOOL_HEADER,
    STUDENT,
    TEACHER,
}
