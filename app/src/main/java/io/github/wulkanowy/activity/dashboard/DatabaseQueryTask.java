package io.github.wulkanowy.activity.dashboard;

import android.os.AsyncTask;

import java.util.List;

public class DatabaseQueryTask extends AsyncTask<Void, Void, List<?>> {

    private AbstractFragment abstractFragment;

    public DatabaseQueryTask(AbstractFragment<?> abstractFragment) {
        this.abstractFragment = abstractFragment;
    }

    @Override
    protected List<?> doInBackground(Void... voids) {
        return abstractFragment.getItems();
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void onPostExecute(List<?> objects) {
        super.onPostExecute(objects);
        abstractFragment.onQuarryProcessFinish(objects);
    }
}
