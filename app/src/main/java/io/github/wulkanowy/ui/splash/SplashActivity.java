package io.github.wulkanowy.ui.splash;

import android.Manifest;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.github.javiersantos.appupdater.AppUpdaterUtils;
import com.github.javiersantos.appupdater.enums.AppUpdaterError;
import com.github.javiersantos.appupdater.enums.UpdateFrom;
import com.github.javiersantos.appupdater.objects.Update;

import java.io.File;

import io.github.wulkanowy.BuildConfig;
import io.github.wulkanowy.R;
import io.github.wulkanowy.services.jobs.FullSyncJob;
import io.github.wulkanowy.ui.login.LoginActivity;
import io.github.wulkanowy.ui.main.DashboardActivity;

public class SplashActivity extends AppCompatActivity
        implements ActivityCompat.OnRequestPermissionsResultCallback {

    private static final int PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE = 0;

    private View mLayout;

    private Update update;

    private DownloadManager downloadManager;

    private final static String ANDROID_PACKAGE = "application/vnd.android.package-archive";

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
                    install = new Intent(Intent.ACTION_INSTALL_PACKAGE );
                    install.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    install.setData(FileProvider.getUriForFile(context,
                            BuildConfig.APPLICATION_ID + ".fileprovider", file));
                } else {
                    install = new Intent(Intent.ACTION_VIEW );
                    install.setDataAndType(
                            Uri.parse("file://" + file.getAbsolutePath()),
                            ANDROID_PACKAGE);
                }

                startActivity(install);
                finish();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        setContentView(R.layout.activity_splash);
        mLayout = findViewById(R.id.container);

        downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

        TextView versionName = findViewById(R.id.rawText);
        versionName.setText(getString(R.string.version_text, BuildConfig.VERSION_NAME));

        new Handler().post(new Runnable() {
            @Override
            public void run() {
                executeOnRunApp();
            }
        });
    }

    private void executeOnRunApp() {
        checkAndInstallUpdates();
    }

    private void bootApp() {
        if (getSharedPreferences("LoginData", Context.MODE_PRIVATE).getLong("userId", 0) == 0) {
            Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
            startActivity(intent);
        } else {
            new FullSyncJob().scheduledJob(getApplicationContext());

            Intent intent = new Intent(SplashActivity.this, DashboardActivity.class);
            startActivity(intent);
        }
    }

    private void checkAndInstallUpdates() {
        AppUpdaterUtils appUpdaterUtils = new AppUpdaterUtils(this)
                .setUpdateFrom(UpdateFrom.JSON)
                .setUpdateJSON(BuildConfig.UPDATE_URL)
                .withListener(new AppUpdaterUtils.UpdateListener() {

                    @Override
                    public void onSuccess(final Update update, Boolean isUpdateAvailable) {
                        Log.d("Latest Version", update.getLatestVersion());
                        Log.d("Latest Version Code", update.getLatestVersionCode().toString());
                        Log.d("URL", update.getUrlToDownload().toString());
                        Log.d("Is update available?", Boolean.toString(isUpdateAvailable));

                        if (isUpdateAvailable) {
                            new AlertDialog.Builder(SplashActivity.this)
                                    .setTitle("Update is available")
                                    .setMessage("Update to version " + update.getLatestVersionCode().toString() + " is available. " +
                                            "Your version is " + BuildConfig.VERSION_CODE + ". Update?")
                                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            SplashActivity.this.update = update;
                                            downloadUpdate();
                                        }
                                    })
                                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            bootApp();
                                        }
                                    })
                                    .show();
                        }
                    }

                    @Override
                    public void onFailed(AppUpdaterError error) {
                        Log.d("AppUpdater Error", "Something went wrong");
                        bootApp();
                    }
                });
        appUpdaterUtils.start();
    }

    private void downloadUpdate() {
        if (ActivityCompat.checkSelfPermission(getBaseContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            Snackbar.make(mLayout, "Rozpocząto pobieranie.", Snackbar.LENGTH_SHORT).show();

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
        } else {
            // Permission is missing and must be requested.
            requestWriteStoragePermission();
        }
    }

    private void requestWriteStoragePermission() {
        // Permission has not been granted and must be requested.
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            // Provide an additional rationale to the user if the permission was not granted
            // and the user would benefit from additional context for the use of the permission.
            // Display a SnackBar with a button to request the missing permission.
            Snackbar.make(mLayout, "External storage is is required to download update.",
                    Snackbar.LENGTH_INDEFINITE).setAction("OK", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Request the permission
                    ActivityCompat.requestPermissions(SplashActivity.this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE);
                }
            }).show();

        } else {
            // Request the permission. The result will be received in onRequestPermissionResult().
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                downloadUpdate();
            } else {
                Toast.makeText(this, "Write storage permission request was denied", Toast.LENGTH_SHORT).show();
                bootApp();
            }
        }
    }

    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(onComplete);
    }
}
