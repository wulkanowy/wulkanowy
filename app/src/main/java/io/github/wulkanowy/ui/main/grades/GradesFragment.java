package io.github.wulkanowy.ui.main.grades;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.common.SmoothScrollLinearLayoutManager;
import io.github.wulkanowy.R;
import io.github.wulkanowy.di.component.ActivityComponent;
import io.github.wulkanowy.ui.base.BaseFragment;

public class GradesFragment extends BaseFragment implements GradesContract.View {

    @BindView(R.id.grade_fragment_recycler)
    RecyclerView recyclerView;

    @BindView(R.id.grade_fragment_progress_bar)
    View progressBar;

    @BindView(R.id.grade_fragment_no_item_container)
    View noItemView;

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
    protected void setUpOnViewCreated(View fragmentView) {
        noItemView.setVisibility(View.GONE);

        adapter.setAutoCollapseOnExpand(true);
        adapter.setAutoScrollOnExpand(true);
        adapter.expandItemsAtStartUp();

        recyclerView.setLayoutManager(new SmoothScrollLinearLayoutManager(fragmentView.getContext()));
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (presenter != null) {
            presenter.onFragmentVisible(isVisibleToUser);
        }
    }

    @Override
    public void showProgressBar(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    public void updateAdapterList(List<GradeHeaderItem> headerItems) {
        adapter.addItems(0, headerItems);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroyView() {
        presenter.onDestroy();
        super.onDestroyView();
    }
}
