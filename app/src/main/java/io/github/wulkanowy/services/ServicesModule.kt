package io.github.wulkanowy.services

import com.squareup.inject.assisted.dagger2.AssistedModule
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoSet
import io.github.wulkanowy.services.sync.works.Work
import io.github.wulkanowy.services.sync.works.grade.GradeWork

@AssistedModule
@Module(includes = [AssistedInject_ServicesModule::class])
abstract class ServicesModule {

    @Binds
    @IntoSet
    abstract fun provideGradeWork(work: GradeWork): Work
}
