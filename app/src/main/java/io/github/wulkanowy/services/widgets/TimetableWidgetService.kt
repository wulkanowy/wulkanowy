package io.github.wulkanowy.services.widgets

import android.content.Intent
import android.widget.RemoteViewsService
import dagger.android.AndroidInjection
import io.github.wulkanowy.ui.widgets.timetable.TimetableWidgetFactory
import javax.inject.Inject

class TimetableWidgetService : RemoteViewsService() {

    @Inject
    lateinit var widgetFactory: TimetableWidgetFactory

    override fun onGetViewFactory(intent: Intent?): RemoteViewsFactory {
        AndroidInjection.inject(this)
        return widgetFactory
    }
}
