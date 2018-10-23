package io.github.wulkanowy.services.widgets

import android.content.Intent
import android.widget.RemoteViewsService
import io.github.wulkanowy.ui.widgets.timetable.TimetableWidgetFactory

class TimetableWidgetService : RemoteViewsService() {

    override fun onGetViewFactory(intent: Intent?) = TimetableWidgetFactory()
}
