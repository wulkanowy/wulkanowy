package io.github.wulkanowy.services

import com.squareup.inject.assisted.dagger2.AssistedModule
import dagger.Binds
import dagger.Module
import dagger.multibindings.ClassKey
import dagger.multibindings.IntoMap
import io.github.wulkanowy.services.sync.base.WorkerConfiguration
import io.github.wulkanowy.services.sync.factory.InjectableWorkerFactory
import io.github.wulkanowy.services.sync.workers.attendance.AttendanceWorker
import io.github.wulkanowy.services.sync.workers.attendance.AttendanceWorkerConfiguration
import io.github.wulkanowy.services.sync.workers.grade.GradeWorker
import io.github.wulkanowy.services.sync.workers.grade.GradeWorkerConfiguration

@AssistedModule
@Module(includes = [AssistedInject_ServicesModule::class])
abstract class ServicesModule {

    @Binds
    @IntoMap
    @ClassKey(GradeWorker::class)
    abstract fun provideGradeWorkerFactory(factory: GradeWorker.Factory): InjectableWorkerFactory

    @Binds
    @IntoMap
    @ClassKey(GradeWorker::class)
    abstract fun provideGradeWorkerConfiguration(config: GradeWorkerConfiguration): WorkerConfiguration

    @Binds
    @IntoMap
    @ClassKey(AttendanceWorker::class)
    abstract fun provideAttendanceWorkerFactory(factory: AttendanceWorker.Factory): InjectableWorkerFactory

    @Binds
    @IntoMap
    @ClassKey(AttendanceWorker::class)
    abstract fun provideAttendanceWorkerConfiguration(configuration: AttendanceWorkerConfiguration): WorkerConfiguration
}
