package top.caffreyfans.irbaby.ui.about;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import top.caffreyfans.irbaby.R;

public class AboutFragment extends Fragment {
    private final static String TAG = AboutFragment.class.getSimpleName();
    private TextView version_tv;
    private Button check_version_btn;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_about, container, false);

        version_tv = (TextView) root.findViewById(R.id.version_tv);
        check_version_btn = (Button) root.findViewById(R.id.check_version_bt);
        version_tv.setText(getVersion());
        check_version_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new VersionCheck().execute();
            }
        });
        return root;
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

    private void showUpdateDialog(boolean update) {
        AlertDialog.Builder inputDialog =
                new AlertDialog.Builder(getContext());
        if (update) {
            inputDialog.setMessage(R.string.about_update_message);
            inputDialog.setPositiveButton(R.string.about_dialog_update,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Uri uri = Uri.parse("https://irbaby.caffreyfans.top/latest/");
                            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                            startActivity(intent);
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

    private class VersionCheck extends AsyncTask<Void, Void, Integer> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Integer doInBackground(Void... voids) {
            Integer version = 0;
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url("https://irbaby.caffreyfans.top/latest/version.json")
                    .build();
            try {
                Response response = client.newCall(request).execute();
                String result = response.body().string();
                JSONObject jsonObject = new JSONObject(result);
                version = jsonObject.getInt("app");

            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return version;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            try {
                PackageManager packageManager = getContext().getPackageManager();
                PackageInfo packageInfo = packageManager.getPackageInfo(getContext().getPackageName(), 0);
                int remoteVersion = integer.intValue();
                int localVersion = packageInfo.versionCode;
                showUpdateDialog(remoteVersion > localVersion);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}