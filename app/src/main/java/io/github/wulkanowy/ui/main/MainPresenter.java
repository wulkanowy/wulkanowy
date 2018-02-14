package io.github.wulkanowy.ui.main;


import javax.inject.Inject;

import io.github.wulkanowy.data.RepositoryContract;
import io.github.wulkanowy.ui.base.BasePresenter;

public class MainPresenter extends BasePresenter<MainContract.View>
        implements MainContract.Presenter {

    @Inject
    MainPresenter(RepositoryContract repository) {
        super(repository);
    }
}
