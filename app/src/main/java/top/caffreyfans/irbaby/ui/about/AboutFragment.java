package top.caffreyfans.irbaby.ui.about;

import android.app.DownloadManager;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import java.io.IOException;
import top.caffreyfans.irbaby.R;
import top.caffreyfans.irbaby.helper.GitHubReleaseHelper;
import top.caffreyfans.irbaby.helper.GitHubReleaseHelper.ReleaseInfo;

public class AboutFragment extends Fragment {
    private final static String TAG = AboutFragment.class.getSimpleName();
    private static final int REQUEST_INSTALL_UNKNOWN_APPS = 1001;
    private TextView version_tv;
    private Button check_version_btn;
    private DownloadManager mDownloadManager;
    private long mCurrentDownloadId = -1L;
    private Uri mPendingInstallUri;
    private final BroadcastReceiver mDownloadReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent == null || DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(intent.getAction()) == false) {
                return;
            }
            long downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1L);
            if (downloadId != mCurrentDownloadId) {
                return;
            }
            handleDownloadCompleted(downloadId);
        }
    };

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_about, container, false);

        version_tv = (TextView) root.findViewById(R.id.version_tv);
        check_version_btn = (Button) root.findViewById(R.id.check_version_bt);
        mDownloadManager = (DownloadManager) requireContext().getSystemService(Context.DOWNLOAD_SERVICE);
        version_tv.setText(getVersion());
        check_version_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new VersionCheck().execute();
            }
        });
        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        requireContext().registerReceiver(mDownloadReceiver,
                new android.content.IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

    @Override
    public void onStop() {
        super.onStop();
        try {
            requireContext().unregisterReceiver(mDownloadReceiver);
        } catch (IllegalArgumentException ignored) {
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mPendingInstallUri != null && canRequestPackageInstalls()) {
            installDownloadedApk(mPendingInstallUri);
        }
    }

    private String getVersion() {
        try {
            PackageManager packageManager = getContext().getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(getContext().getPackageName(), 0);
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void showUpdateDialog(final ReleaseInfo releaseInfo) {
        if (getContext() == null) {
            return;
        }
        AlertDialog.Builder inputDialog =
                new AlertDialog.Builder(getContext());
        if (releaseInfo != null) {
            inputDialog.setMessage(getString(R.string.about_update_message_with_version,
                    releaseInfo.getTagName()));
            inputDialog.setPositiveButton(R.string.about_dialog_update,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startUpdateDownload(releaseInfo);
                        }
                    });
            inputDialog.setNegativeButton(getString(R.string.dialog_cancel_button),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
        } else {
            inputDialog.setMessage(R.string.about_no_update_message);
        }
        inputDialog.show();
    }

    private void startUpdateDownload(ReleaseInfo releaseInfo) {
        if (getContext() == null || mDownloadManager == null) {
            showCheckFailed();
            return;
        }
        if (!releaseInfo.hasApkAsset()) {
            Toast.makeText(getContext(), R.string.about_no_apk_asset_message, Toast.LENGTH_SHORT).show();
            Uri uri = Uri.parse(releaseInfo.getPreferredDownloadUrl());
            startActivity(new Intent(Intent.ACTION_VIEW, uri));
            return;
        }

        String downloadUrl = releaseInfo.getApkUrl();
        String fileName = "IRbaby-" + releaseInfo.getTagName() + ".apk";
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(downloadUrl));
        request.setTitle(getString(R.string.about_download_title));
        request.setDescription(getString(R.string.about_download_description, releaseInfo.getTagName()));
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setMimeType("application/vnd.android.package-archive");
        request.setDestinationInExternalFilesDir(getContext(), Environment.DIRECTORY_DOWNLOADS, fileName);
        mCurrentDownloadId = mDownloadManager.enqueue(request);
        Toast.makeText(getContext(), R.string.about_download_started_message, Toast.LENGTH_SHORT).show();
    }

    private void handleDownloadCompleted(long downloadId) {
        if (getContext() == null || mDownloadManager == null) {
            return;
        }
        DownloadManager.Query query = new DownloadManager.Query().setFilterById(downloadId);
        Cursor cursor = mDownloadManager.query(query);
        if (cursor == null) {
            Toast.makeText(getContext(), R.string.about_download_failed_message, Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            if (!cursor.moveToFirst()) {
                Toast.makeText(getContext(), R.string.about_download_failed_message, Toast.LENGTH_SHORT).show();
                return;
            }

            int status = cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_STATUS));
            if (status != DownloadManager.STATUS_SUCCESSFUL) {
                Toast.makeText(getContext(), R.string.about_download_failed_message, Toast.LENGTH_SHORT).show();
                return;
            }

            Uri apkUri = mDownloadManager.getUriForDownloadedFile(downloadId);
            if (apkUri == null) {
                Toast.makeText(getContext(), R.string.about_download_failed_message, Toast.LENGTH_SHORT).show();
                return;
            }

            installDownloadedApk(apkUri);
        } finally {
            cursor.close();
        }
    }

    private void installDownloadedApk(Uri apkUri) {
        if (getContext() == null) {
            return;
        }
        if (!canRequestPackageInstalls()) {
            mPendingInstallUri = apkUri;
            Intent settingsIntent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES,
                    Uri.parse("package:" + requireContext().getPackageName()));
            startActivityForResult(settingsIntent, REQUEST_INSTALL_UNKNOWN_APPS);
            Toast.makeText(getContext(), R.string.about_enable_install_permission_message, Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            Intent installIntent = new Intent(Intent.ACTION_VIEW);
            installIntent.setDataAndType(apkUri, "application/vnd.android.package-archive");
            installIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            installIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(installIntent);
            mPendingInstallUri = null;
        } catch (ActivityNotFoundException e) {
            Toast.makeText(getContext(), R.string.about_install_failed_message, Toast.LENGTH_SHORT).show();
        }
    }

    private boolean canRequestPackageInstalls() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return true;
        }
        return requireContext().getPackageManager().canRequestPackageInstalls();
    }

    private void showCheckFailed() {
        if (getContext() == null) {
            return;
        }
        Toast.makeText(getContext(), R.string.about_check_failed_message, Toast.LENGTH_SHORT).show();
    }

    private class VersionCheck extends AsyncTask<Void, Void, ReleaseInfo> {

        private boolean requestFailed;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            check_version_btn.setEnabled(false);
        }

        @Override
        protected ReleaseInfo doInBackground(Void... voids) {
            try {
                return GitHubReleaseHelper.fetchLatestRelease();
            } catch (IOException e) {
                requestFailed = true;
                e.printStackTrace();
            } catch (org.json.JSONException e) {
                requestFailed = true;
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(ReleaseInfo releaseInfo) {
            super.onPostExecute(releaseInfo);
            check_version_btn.setEnabled(true);
            if (requestFailed || releaseInfo == null) {
                showCheckFailed();
                return;
            }
            try {
                PackageManager packageManager = getContext().getPackageManager();
                PackageInfo packageInfo = packageManager.getPackageInfo(getContext().getPackageName(), 0);
                boolean hasUpdate = GitHubReleaseHelper.hasNewerRelease(packageInfo.versionName,
                        releaseInfo.getTagName());
                showUpdateDialog(hasUpdate ? releaseInfo : null);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
                showCheckFailed();
            }
        }
    }
}