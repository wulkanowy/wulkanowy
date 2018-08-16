package io.github.wulkanowy.di

import android.content.Context
import dagger.Module
import dagger.Provides
import io.github.wulkanowy.WulkanowyApp
import io.github.wulkanowy.utils.schedulers.AppSchedulers
import io.github.wulkanowy.utils.schedulers.SchedulersManager
import io.reactivex.disposables.CompositeDisposable

@Module
internal class AppModule {

    @Provides
    fun provideContext(app: WulkanowyApp): Context = app

    @Provides
    fun provideCompositeDisposable(): CompositeDisposable = CompositeDisposable()

    @Provides
    fun provideSchedulers(): SchedulersManager = AppSchedulers()
}
