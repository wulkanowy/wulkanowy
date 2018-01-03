package io.github.wulkanowy.di.component;

import javax.inject.Singleton;

import dagger.Component;
import io.github.wulkanowy.di.modules.ApplicationModule;

@Singleton
@Component(modules = ApplicationModule.class)
public interface ApplicationComponent {
}
