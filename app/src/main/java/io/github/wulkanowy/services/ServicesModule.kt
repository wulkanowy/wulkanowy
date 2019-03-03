package io.github.wulkanowy.services

import android.content.Context
import androidx.core.app.NotificationManagerCompat
import com.squareup.inject.assisted.dagger2.AssistedModule
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoSet
import io.github.wulkanowy.services.sync.works.AttendanceWork
import io.github.wulkanowy.services.sync.works.GradeSummaryWork
import io.github.wulkanowy.services.sync.works.GradeWork
import io.github.wulkanowy.services.sync.works.NoteWork
import io.github.wulkanowy.services.sync.works.Work
import javax.inject.Singleton

@AssistedModule
@Module(includes = [AssistedInject_ServicesModule::class])
abstract class ServicesModule {

    @Module
    companion object {

        @JvmStatic
        @Singleton
        @Provides
        fun provideNotificationManagerCompat(context: Context) = NotificationManagerCompat.from(context)
    }

    @Binds
    @IntoSet
    abstract fun provideGradeWork(work: GradeWork): Work

    @Binds
    @IntoSet
    abstract fun provideNoteWork(work: NoteWork): Work

    @Binds
    @IntoSet
    abstract fun provideAttendanceWork(work: AttendanceWork): Work

    @Binds
    @IntoSet
    abstract fun provideGradeSummaryWork(work: GradeSummaryWork): Work
}
