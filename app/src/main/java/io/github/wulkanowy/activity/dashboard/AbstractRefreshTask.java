package io.github.wulkanowy.activity.dashboard;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

import java.lang.ref.WeakReference;

import io.github.wulkanowy.dao.entities.DaoSession;

public abstract class AbstractRefreshTask extends AsyncTask<Void, Void, Boolean> {

    public static final String DEBUG_TAG = "RefreshTask";

    private DaoSession daoSession;

    private WeakReference<View> mainView;

    public AbstractRefreshTask(View mainView, DaoSession daoSession) {
        this.daoSession = daoSession;
        this.mainView = new WeakReference<>(mainView);
    }

    protected DaoSession getDaoSession() {
        return daoSession;
    }

    protected Context getContext() {
        return mainView.get().getContext();
    }

    protected View getMainView() {
        return mainView.get();
    }

    protected abstract void executeInBackground() throws Exception;

    @Override
    protected Boolean doInBackground(Void... voids) {
        try {
            executeInBackground();
            return true;
        }catch (Exception e){
            Log.e(DEBUG_TAG, "There was a synchronization problem", e);
            return false;
        }
    }
}
