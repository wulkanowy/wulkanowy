package io.github.wulkanowy.ui.main.grades;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import eu.davidea.flexibleadapter.FlexibleAdapter;
import io.github.wulkanowy.R;
import io.github.wulkanowy.di.component.ActivityComponent;
import io.github.wulkanowy.ui.base.BaseFragment;

public class GradesFragment extends BaseFragment implements GradesContract.View {

    @BindView(R.id.grade_fragment_recycler)
    RecyclerView recyclerView;

    @Inject
    FlexibleAdapter<GradeHeaderItem> adapter;

    @Inject
    GradesContract.Presenter presenter;

    public GradesFragment() {
        // empty constructor for fragment
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_grades, container, false);

        ActivityComponent component = getActivityComponent();
        if (component != null) {
            component.inject(this);
            setButterKnife(ButterKnife.bind(this, view));
            presenter.onStart(this);
        }

        return view;
    }

    @Override
    protected void setUpOnViewCreated(View mainView) {

    }
}
