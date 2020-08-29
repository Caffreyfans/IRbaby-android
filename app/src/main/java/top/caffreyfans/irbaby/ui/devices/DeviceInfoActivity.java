package top.caffreyfans.irbaby.ui.devices;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.LitePal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import top.caffreyfans.irbaby.R;
import top.caffreyfans.irbaby.adapter.AppliancesSelectAdapter;
import top.caffreyfans.irbaby.adapter.DeviceAdapter;
import top.caffreyfans.irbaby.adapter.InfoAdapter;
import top.caffreyfans.irbaby.firmware_api.IRbabyApi;
import top.caffreyfans.irbaby.helper.ApplianceContract;
import top.caffreyfans.irbaby.helper.NotifyMsgEntity;
import top.caffreyfans.irbaby.helper.UdpNotifyManager;
import top.caffreyfans.irbaby.model.DeviceInfo;

public class DeviceInfoActivity extends AppCompatActivity implements Observer {

    private static final String TAG = DeviceInfoActivity.class.getSimpleName();
    private IRbabyApi mIRbabyApi;
    private DeviceInfo mDeviceInfo;
    private JSONObject contentJSON;
    private ListView mListView;
    private Button update_check_btn;
    private int FlashSize;
    private List<String[]> stringList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_info);

        UdpNotifyManager.getUdpNotifyManager().addObserver(this);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        Intent intent = getIntent();
        if (intent.hasExtra(ApplianceContract.DeviceSetting.DEVICE_INFO)) {
            mDeviceInfo = (DeviceInfo) intent.getSerializableExtra(ApplianceContract.DeviceSetting.DEVICE_INFO);
            this.setTitle(mDeviceInfo.getMac());
            mIRbabyApi = new IRbabyApi(this, mDeviceInfo, null);
            mIRbabyApi.getDeviceInfo();
        }

        mListView = findViewById(R.id.info_list);
        update_check_btn = findViewById(R.id.update_check_btn);
        update_check_btn.setVisibility(View.INVISIBLE);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showInfo() throws JSONException {
        if (contentJSON != null) {
            stringList.clear();
            Iterator<String> sIterator = contentJSON.keys();
            while (sIterator.hasNext()) {
                String[] strings = new String[2];
                String key = sIterator.next();
                String value = contentJSON.getString(key);
                strings[0] = key;
                strings[1] = value;
                stringList.add(strings);
                if (key.equals("flash_size")) {
                    value = value.replaceAll("KB", "");
                    FlashSize = Integer.parseInt(value);
                }
            }
            InfoAdapter infoAdapter = new InfoAdapter(this, stringList);
            mListView.setAdapter(infoAdapter);
            update_check_btn.setVisibility(View.
                    VISIBLE);
            update_check_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new VersionCheck().execute();
                }
            });
        }
    }

    private void showUpdateDialog(boolean update) {
        AlertDialog.Builder inputDialog =
                new AlertDialog.Builder(this);
        if (update) {
            inputDialog.setMessage(R.string.about_update_message);
            inputDialog.setPositiveButton(R.string.about_dialog_update,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mIRbabyApi.updateFirmware(FlashSize);
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

    @Override
    public void update(Observable o, Object arg) {
        NotifyMsgEntity entity = (NotifyMsgEntity)arg;
        int code = (int)entity.getCode();
        if (code == UdpNotifyManager.INFO_RT) {
            String msg = (String)entity.getData();
            Log.d(TAG, "update: " + msg);
            try {
                contentJSON = new JSONObject(msg).getJSONObject("params");
                showInfo();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private class VersionCheck extends AsyncTask<Void, Void, Integer> {

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
                version = jsonObject.getInt("firmware");

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
            int remote_version = integer.intValue();
            try {
                int local_version = contentJSON.getInt("version_code");
                showUpdateDialog(remote_version > local_version);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
}
