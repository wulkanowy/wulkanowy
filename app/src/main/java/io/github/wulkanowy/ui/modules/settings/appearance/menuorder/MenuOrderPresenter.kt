package io.github.wulkanowy.ui.modules.settings.appearance.menuorder

import io.github.wulkanowy.data.repositories.PreferencesRepository
import io.github.wulkanowy.data.repositories.StudentRepository
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.ui.base.ErrorHandler
import timber.log.Timber
import javax.inject.Inject

class MenuOrderPresenter @Inject constructor(
    studentRepository: StudentRepository,
    errorHandler: ErrorHandler,
    private val preferencesRepository: PreferencesRepository
) : BasePresenter<MenuOrderView>(errorHandler, studentRepository) {

    override fun onAttachView(view: MenuOrderView) {
        super.onAttachView(view)
        view.initView()
        Timber.i("Menu order view was initialized")
        loadData()
    }

    private fun loadData() {
        val defaultMenuItemList = setOf(
            MenuItem.StartMenuItem(),
            MenuItem.GradeMenuItem(),
            MenuItem.TimetableMenuItem(),
            MenuItem.AttendanceMenuItem(),
            MenuItem.ExamsMenuItem(),
            MenuItem.HomeworkMenuItem(),
            MenuItem.NoteMenuItem(),
            MenuItem.LuckyNumberMenuItem(),
            MenuItem.SchoolAnnouncementsMenuItem(),
            MenuItem.SchoolAndTeachersMenuItem(),
            MenuItem.MobileDevicesMenuItem(),
            MenuItem.ConferenceMenuItem(),
            MenuItem.MessageMenuItem()
        )

        val savedMenuItemList = (preferencesRepository.menuItemOrder ?: defaultMenuItemList)
            .sortedBy { it.order }

        view?.updateData(savedMenuItemList)
    }

    fun onDragAndDropEnd(list: List<MenuItem>) {
        val updatedList = list.mapIndexed { index, menuItem -> menuItem.apply { order = index } }

        preferencesRepository.menuItemOrder = updatedList
        view?.updateData(updatedList)
    }
}
