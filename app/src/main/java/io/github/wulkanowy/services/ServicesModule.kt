package io.github.wulkanowy.services

import com.squareup.inject.assisted.dagger2.AssistedModule
import dagger.Binds
import dagger.Module
import dagger.multibindings.ClassKey
import dagger.multibindings.IntoMap
import io.github.wulkanowy.services.factory.InjectableWorkerFactory
import io.github.wulkanowy.services.sync.grade.GradeWorker

@AssistedModule
@Module(includes = [AssistedInject_ServicesModule::class])
abstract class ServicesModule {

    @Binds
    @IntoMap
    @ClassKey(GradeWorker::class)
    abstract fun bindGradeWorker(): InjectableWorkerFactory
}
