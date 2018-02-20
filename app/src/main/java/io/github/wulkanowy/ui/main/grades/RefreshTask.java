package io.github.wulkanowy.ui.main.grades;

import android.os.AsyncTask;

public class RefreshTask extends AsyncTask<Void, Integer, Boolean> {

    private GradesContract.Presenter presenter;

    private Exception exception;

    RefreshTask(GradesContract.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        try {
            presenter.getRepository().loginCurrentUser();
            presenter.getRepository().syncSubjects();
            presenter.getRepository().syncGrades();
            return true;
        } catch (Exception e) {
            exception = e;
            return false;
        }
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        presenter.onCanceledAsync();
    }

    @Override
    protected void onPostExecute(Boolean result) {
        super.onPostExecute(result);
        presenter.onEndAsync(result, exception);
    }
}
