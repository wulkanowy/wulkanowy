package io.github.wulkanowy.ui.modules.login.studentselect

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil.ItemCallback
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import io.github.wulkanowy.R
import io.github.wulkanowy.databinding.*
import javax.inject.Inject

@SuppressLint("SetTextI18n")
class LoginStudentSelectAdapter @Inject constructor() :
    ListAdapter<LoginStudentSelectItem, RecyclerView.ViewHolder>(Differ) {

    override fun getItemViewType(position: Int): Int = getItem(position).type.ordinal

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (LoginStudentSelectItemType.values()[viewType]) {
            LoginStudentSelectItemType.SYMBOL_HEADER -> SymbolsHeaderViewHolder(
                ItemLoginStudentSelectHeaderSymbolBinding.inflate(inflater, parent, false)
            )
            LoginStudentSelectItemType.SCHOOL_HEADER -> SchoolHeaderViewHolder(
                ItemLoginStudentSelectHeaderSchoolBinding.inflate(inflater, parent, false)
            )
            LoginStudentSelectItemType.STUDENT -> StudentViewHolder(
                ItemLoginStudentSelectStudentBinding.inflate(inflater, parent, false)
            )
            LoginStudentSelectItemType.TEACHER -> TeacherViewHolder(
                ItemLoginStudentSelectTeacherBinding.inflate(inflater, parent, false)
            )
            LoginStudentSelectItemType.EMPTY_SYMBOLS_HEADER -> EmptySymbolsHeaderViewHolder(
                ItemLoginStudentSelectEmptySymbolHeaderBinding.inflate(inflater, parent, false),
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is EmptySymbolsHeaderViewHolder -> holder.bind(getItem(position) as LoginStudentSelectItem.EmptySymbolsHeader)
            is SymbolsHeaderViewHolder -> holder.bind(getItem(position) as LoginStudentSelectItem.SymbolHeader)
            is SchoolHeaderViewHolder -> holder.bind(getItem(position) as LoginStudentSelectItem.SchoolHeader)
            is TeacherViewHolder -> holder.bind(getItem(position) as LoginStudentSelectItem.Teacher)
            is StudentViewHolder -> holder.bind(getItem(position) as LoginStudentSelectItem.Student)
        }
    }

    private class EmptySymbolsHeaderViewHolder(
        private val binding: ItemLoginStudentSelectEmptySymbolHeaderBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: LoginStudentSelectItem.EmptySymbolsHeader) {
            with(binding) {
                loginStudentSelectEmptySymbolChevron.rotation = if (item.isExpanded) 270f else 90f
                root.setOnClickListener { item.onClick() }
            }
        }
    }

    private class SymbolsHeaderViewHolder(
        private val binding: ItemLoginStudentSelectHeaderSymbolBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: LoginStudentSelectItem.SymbolHeader) {
            with(binding) {
                loginStudentSelectHeaderSymbolValue.text = item.symbol.symbol
                loginStudentSelectHeaderSymbolUsername.text = item.symbol.userName
                loginStudentSelectHeaderSymbolUsername.isVisible = item.symbol.userName.isNotBlank()
                loginStudentSelectHeaderSymbolError.text = item.symbol.error?.message
                loginStudentSelectHeaderSymbolError.isVisible = item.symbol.error != null
            }
        }
    }

    private class SchoolHeaderViewHolder(
        private val binding: ItemLoginStudentSelectHeaderSchoolBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: LoginStudentSelectItem.SchoolHeader) {
            with(binding) {
                loginStudentSelectHeaderSchoolName.text = buildString {
                    append(item.unit.schoolName) // todo: add trim in sdk
                    append(" (")
                    append(item.unit.schoolShortName)
                    append(")")
                }
                loginStudentSelectHeaderSchoolDetails.text = buildString {
                    if (item.unit.subjects.isEmpty()) {
                        append("Brak uczniów możliwych do dodania")
                    }
                }
                with(loginStudentSelectHeaderSchoolDetails) {
                    isVisible = text.isNotBlank()
                }
                loginStudentSelectHeaderSchoolError.text = item.unit.error?.message
                loginStudentSelectHeaderSchoolError.isVisible = item.unit.error != null
            }
        }
    }

    private class StudentViewHolder(
        private val binding: ItemLoginStudentSelectStudentBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: LoginStudentSelectItem.Student) {
            val student = item.student
            val semesters = student.semesters
            val diary = semesters.maxByOrNull { it.semesterId }

            with(binding) {
                loginItemName.text = "${student.studentName} ${student.studentSurname}"
                loginItemName.isEnabled = item.isEnabled
                loginItemSignedIn.text = if (!item.isEnabled) {
                    root.context.getString(R.string.login_signed_in)
                } else diary?.diaryName

                with(loginItemCheck) {
                    isEnabled = item.isEnabled
                    keyListener = null
                    isChecked = item.isSelected
                }

                root.isEnabled = item.isEnabled
                root.setOnClickListener {
                    item.onClick(item)
                }
            }
        }
    }

    private class TeacherViewHolder(
        private val binding: ItemLoginStudentSelectTeacherBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: LoginStudentSelectItem.Teacher) {
            with(binding) {
                loginItemName.text = item.teacher.employeeName
            }
        }
    }

    private object Differ : ItemCallback<LoginStudentSelectItem>() {

        override fun areItemsTheSame(
            oldItem: LoginStudentSelectItem, newItem: LoginStudentSelectItem
        ): Boolean = when {
            oldItem is LoginStudentSelectItem.EmptySymbolsHeader && newItem is LoginStudentSelectItem.EmptySymbolsHeader -> true
            oldItem is LoginStudentSelectItem.Student && newItem is LoginStudentSelectItem.Student -> {
                oldItem.student == newItem.student
            }
            else -> oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: LoginStudentSelectItem, newItem: LoginStudentSelectItem
        ): Boolean = oldItem == newItem
    }
}
