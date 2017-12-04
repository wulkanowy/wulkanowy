package io.github.wulkanowy.activity.dashboard;


import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.common.SmoothScrollLinearLayoutManager;
import eu.davidea.flexibleadapter.items.AbstractExpandableHeaderItem;
import io.github.wulkanowy.R;
import io.github.wulkanowy.activity.WulkanowyApp;
import io.github.wulkanowy.dao.entities.DaoSession;
import io.github.wulkanowy.utilities.ConnectionUtilities;

public abstract class AbstractFragment<T extends AbstractExpandableHeaderItem> extends Fragment {

    private FlexibleAdapter<T> flexibleAdapter;

    private List<T> itemList = new ArrayList<>();

    private WeakReference<Activity> activityWeakReference;

    private SwipeRefreshLayout swipeRefreshLayout;

    private RecyclerView recyclerViewLayout;

    private DaoSession daoSession;

    private long userId;

    public AbstractFragment() {
        //empty constructor for fragments
    }

    public long getUserId() {
        return userId;
    }

    public SwipeRefreshLayout getRefreshLayoutView() {
        return swipeRefreshLayout;
    }

    public DaoSession getDaoSession() {
        return daoSession;
    }

    public Activity getActivityWeakReference() {
        return activityWeakReference.get();
    }

    public void setItemList(List<T> itemList) {
        this.itemList = itemList;
    }

    public abstract int getLayoutId();

    public abstract int getRecyclerViewId();

    public abstract int getLoadingBarId();

    public abstract int getRefreshLayoutId();

    public abstract List<T> getItems();

    public abstract void onRefresh() throws Exception;

    public abstract void onPostRefresh(Boolean result);

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(getLayoutId(), container, false);
        recyclerViewLayout = view.findViewById(getRecyclerViewId());
        swipeRefreshLayout = view.findViewById(getRefreshLayoutId());
        setUpRefreshLayout(swipeRefreshLayout);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (getActivity() != null) {
            activityWeakReference = new WeakReference<Activity>(getActivity());
            daoSession = ((WulkanowyApp) getActivity().getApplication()).getDaoSession();
            userId = getActivity().getSharedPreferences("LoginData", Context.MODE_PRIVATE)
                    .getLong("userId", 0);

            if (itemList.isEmpty()) {
                setItemList(getItems());
                flexibleAdapter = onInitiationFlexibleAdapter(itemList);
                setAdapterOnRecyclerView(recyclerViewLayout);
                setLoadingBarInvisible(getView());
            } else {
                setAdapterOnRecyclerView(recyclerViewLayout);
                setLoadingBarInvisible(getView());
            }
        }
    }

    @NonNull
    protected FlexibleAdapter<T> onInitiationFlexibleAdapter(List<T> itemList) {
        return new FlexibleAdapter<>(itemList)
                .setAutoCollapseOnExpand(true)
                .setAutoScrollOnExpand(true)
                .expandItemsAtStartUp();
    }

    private void updateDataInRecyclerView() {
        flexibleAdapter.updateDataSet(itemList);
        if (getView() != null) {
            setAdapterOnRecyclerView((RecyclerView) getView().findViewById(getRecyclerViewId()));
        }
    }

    @NonNull
    private SwipeRefreshLayout.OnRefreshListener getDefaultRefreshListener() {
        return new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (ConnectionUtilities.isOnline(getContext())) {
                    new RefreshTask(AbstractFragment.this).execute();
                } else {
                    Toast.makeText(getContext(), R.string.noInternet_text, Toast.LENGTH_SHORT).show();
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        };
    }

    protected void setUpRefreshLayout(SwipeRefreshLayout swipeRefreshLayout) {
        swipeRefreshLayout.setColorSchemeResources(android.R.color.black);
        swipeRefreshLayout.setOnRefreshListener(getDefaultRefreshListener());
    }

    protected final void setLoadingBarInvisible(View mainView) {
        mainView.findViewById(getLoadingBarId()).setVisibility(View.INVISIBLE);
    }

    protected void setAdapterOnRecyclerView(RecyclerView recyclerView) {
        recyclerView.setLayoutManager(new SmoothScrollLinearLayoutManager(getActivityWeakReference()));
        recyclerView.setAdapter(flexibleAdapter);
    }

    public static class RefreshTask extends AsyncTask<Void, Void, Boolean> {

        public static final String DEBUG_TAG = "RefreshTask";

        private WeakReference<AbstractFragment> abstractFragment;

        public RefreshTask(AbstractFragment abstractFragment) {
            this.abstractFragment = new WeakReference<>(abstractFragment);
        }

        @SuppressWarnings("unchecked")
        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                abstractFragment.get().onRefresh();
                abstractFragment.get().setItemList(abstractFragment.get().getItems());
                return true;
            } catch (Exception e) {
                Log.e(DEBUG_TAG, "There was a synchronization problem", e);
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            abstractFragment.get().updateDataInRecyclerView();
            abstractFragment.get().onPostRefresh(result);
            abstractFragment.get().getRefreshLayoutView().setRefreshing(false);
        }
    }
}
