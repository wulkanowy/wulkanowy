package io.github.wulkanowy.services;

import android.Manifest;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;

import com.github.javiersantos.appupdater.AppUpdaterUtils;
import com.github.javiersantos.appupdater.enums.AppUpdaterError;
import com.github.javiersantos.appupdater.enums.UpdateFrom;
import com.github.javiersantos.appupdater.objects.Update;

import java.io.File;
import java.lang.ref.WeakReference;

import io.github.wulkanowy.BuildConfig;
import io.github.wulkanowy.R;

public class Updater implements ActivityCompat.OnRequestPermissionsResultCallback {

    private static final int PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE = 0;

    private static WeakReference<Activity> activity;

    private static Update update;

    private static DownloadManager downloadManager;

    private BroadcastReceiver onComplete = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            DownloadManager.Query query = new DownloadManager.Query();
            Cursor c = downloadManager.query(query);
            if (c.moveToFirst()) {
                String uriString = c.getString(c.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));

                if (uriString.substring(0, 7).matches("file://")) {
                    uriString = uriString.substring(7);
                }

                File file = new File(uriString);

                Intent install;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    install = new Intent(Intent.ACTION_INSTALL_PACKAGE);
                    install.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    install.setData(FileProvider.getUriForFile(context,
                            BuildConfig.APPLICATION_ID + ".fileprovider", file));
                } else {
                    install = new Intent(Intent.ACTION_VIEW);
                    install.setDataAndType(Uri.parse("file://" + file.getAbsolutePath()), "application/vnd.android.package-archive");
                }

                context.startActivity(install);
                activity.get().finish();
            }
        }
    };

    public Updater(Activity act) {
        activity = new WeakReference<>(act);

        downloadManager = (DownloadManager) activity.get().getSystemService(Context.DOWNLOAD_SERVICE);
        activity.get().registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

    private static void downloadUpdate() {
        if (ActivityCompat.checkSelfPermission(activity.get(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            Snackbar.make(activity.get().findViewById(R.id.container),
                    "Write permission is available. Starting download.",
                    Snackbar.LENGTH_SHORT).show();
            startDownload();
        } else {
            // Permission is missing and must be requested.
            requestWriteStoragePermission();
            downloadUpdate();
        }
    }

    private static void requestWriteStoragePermission() {
        // Permission has not been granted and must be requested.
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity.get(), Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            // Provide an additional rationale to the user if the permission was not granted
            // and the user would benefit from additional activity for the use of the permission.
            // Display a SnackBar with a button to request the missing permission.
            Snackbar.make(activity.get().findViewById(R.id.container), "External storage is is required to download update.",
                    Snackbar.LENGTH_INDEFINITE).setAction("OK", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Request the permission
                    ActivityCompat.requestPermissions(activity.get(),
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE);
                }
            }).show();

        } else {
            // Request the permission. The result will be received in onRequestPermissionResult().
            ActivityCompat.requestPermissions(activity.get(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE);
        }
    }

    private static void startDownload() {
        Snackbar.make(activity.get().findViewById(R.id.container), "Downloading started.", Snackbar.LENGTH_SHORT).show();

        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(update.getUrlToDownload().toString()));
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
        request.setAllowedOverRoaming(false);
        request.setTitle("Wulkanowy update to version " + update.getLatestVersionCode());
        request.setDescription("Downloading " + update.getLatestVersion());
        request.setVisibleInDownloadsUi(true);
        request.setMimeType("application/vnd.android.package-archive");
        request.setDestinationInExternalPublicDir(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS).toString(), "/updates/" + update.getLatestVersion() + ".apk");

        downloadManager.enqueue(request);
    }

    public void checkForUpdates() {
        new CheckTask().execute();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                downloadUpdate();
            } else {
                Snackbar.make(activity.get().findViewById(R.id.container), "Write storage permission request was denied.",
                        Snackbar.LENGTH_SHORT)
                        .show();
            }
        }
    }

    public void onDestroy(Activity activity) {
        activity.unregisterReceiver(onComplete);
    }

    private static class CheckTask extends AsyncTask<Void, Void, Void> {

        private Boolean isUpdateAvailable = false;

        private void setUpdate(Update update) {
            Updater.update = update;
        }

        private void setUpdateAvailable(Boolean updateAvailable) {
            isUpdateAvailable = updateAvailable;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            Log.d("doInBackground()", "start job");
            AppUpdaterUtils appUpdaterUtils = new AppUpdaterUtils(activity.get())
                    .setUpdateFrom(UpdateFrom.JSON)
                    .setUpdateJSON(BuildConfig.UPDATE_URL)
                    .withListener(new AppUpdaterUtils.UpdateListener() {

                        @Override
                        public void onSuccess(final Update update, Boolean isUpdateAvailable) {
                            Log.d("Latest Version", update.getLatestVersion());
                            Log.d("Latest Version Code", update.getLatestVersionCode().toString());
                            Log.d("URL", update.getUrlToDownload().toString());
                            Log.d("Is update available?", Boolean.toString(isUpdateAvailable));

                            setUpdate(update);
                            setUpdateAvailable(isUpdateAvailable);
                            onPostExecute(null); // ajajajajajaja
                        }

                        @Override
                        public void onFailed(AppUpdaterError error) {
                            Log.d("AppUpdater Error", "Something went wrong");
                            Log.d("AppUpdater", error.toString());
                        }
                    });
            appUpdaterUtils.start();

            Log.d("doInBackground()", "end job");
            return null;
        }

        @Override
        protected void onPostExecute(Void voids) {
            Log.d("onPostExecute()", "start job");
            if (isUpdateAvailable) {
                new AlertDialog.Builder(activity.get())
                        .setTitle("Update is available")
                        .setMessage("Update to version " + update.getLatestVersionCode().toString() +
                                " is available. Your version is " + BuildConfig.VERSION_CODE + ". Update?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                downloadUpdate();
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .show();
            }
            Log.d("onPostExecute()", "end job");
        }
    }
}
