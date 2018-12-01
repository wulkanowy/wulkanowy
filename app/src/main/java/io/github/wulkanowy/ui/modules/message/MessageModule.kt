package io.github.wulkanowy.ui.modules.message

import dagger.Module
import dagger.Provides
import dagger.android.ContributesAndroidInjector
import io.github.wulkanowy.di.scopes.PerChildFragment
import io.github.wulkanowy.di.scopes.PerFragment
import io.github.wulkanowy.ui.base.BasePagerAdapter
import io.github.wulkanowy.ui.modules.message.inbox.InboxFragment
import io.github.wulkanowy.ui.modules.message.sent.SentFragment
import io.github.wulkanowy.ui.modules.message.trash.TrashFragment

@Module
abstract class MessageModule {

    @Module
    companion object {

        @JvmStatic
        @PerFragment
        @Provides
        fun provideGradePagerAdapter(fragment: MessageFragment) = BasePagerAdapter(fragment.childFragmentManager)
    }

    @PerChildFragment
    @ContributesAndroidInjector
    abstract fun bindMessageInboxFragment(): InboxFragment

    @PerChildFragment
    @ContributesAndroidInjector
    abstract fun bindMessageSentFragment(): SentFragment

    @PerChildFragment
    @ContributesAndroidInjector
    abstract fun bindMessageTrashFragment(): TrashFragment
}
