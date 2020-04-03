package top.caffreyfans.irbaby.ui.devices;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Observable;
import java.util.Observer;

import top.caffreyfans.irbaby.R;
import top.caffreyfans.irbaby.helper.ApplianceContract;
import top.caffreyfans.irbaby.helper.NotifyMsgEntity;
import top.caffreyfans.irbaby.helper.UdpNotifyManager;
import top.caffreyfans.irbaby.helper.UdpSendThread;
import top.caffreyfans.irbaby.model.DeviceInfo;

public class DeviceSettingsActivity extends AppCompatActivity implements Observer {

    private static final String TAG = DeviceSettingsActivity.class.getSimpleName();
    private DeviceInfo mDeviceInfo;
    private EditText mAddress;
    private EditText mPort;
    private EditText mUser;
    private EditText mPassword;
    private EditText mSendPin;
    private EditText mReceivePin;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_settings);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mContext = this;
        mAddress = (EditText) findViewById(R.id.address_et);
        mPort = (EditText) findViewById(R.id.port_et);
        mUser = (EditText) findViewById(R.id.user_et);
        mPassword = (EditText) findViewById(R.id.password_et);
        mSendPin = (EditText) findViewById(R.id.send_pin_et);
        mReceivePin = (EditText) findViewById(R.id.receive_pin_et);

        UdpNotifyManager.getUdpNotifyManager().addObserver(this);

        Intent intent = getIntent();
        if (intent.hasExtra(ApplianceContract.DeviceSetting.DEVICE_INFO)) {
            mDeviceInfo = (DeviceInfo) intent.getSerializableExtra(ApplianceContract.DeviceSetting.DEVICE_INFO);
            this.setTitle(mDeviceInfo.getMac());

            fillValue();
        }

        Button saveBtn = (Button) findViewById(R.id.device_save_btn);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDeviceInfo.update(mDeviceInfo.getId());
                JSONObject sendJson = new JSONObject();
                try {
                    JSONObject mqtt = new JSONObject();
                    mqtt.put("host", mAddress.getText().toString());
                    mqtt.put("port", Integer.parseInt(mPort.getText().toString()));
                    mqtt.put("user", mUser.getText().toString());
                    mqtt.put("password", mPassword.getText().toString());

                    JSONObject params = new JSONObject();
                    params.put("mqtt", mqtt);
                    params.put("send_pin", Integer.parseInt(mSendPin.getText().toString()));
                    params.put("receive_pin", Integer.parseInt(mReceivePin.getText().toString()));

                    sendJson.put("cmd", "config");
                    sendJson.put("params", params);

                    Log.d(TAG, "onClick: " + sendJson.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                new UdpSendThread(mDeviceInfo.getIp(), sendJson).start();
            }
        });
    }

    private void fillValue() {
        mAddress.setText(mDeviceInfo.getMqttAddress());
        mPort.setText(String.valueOf(mDeviceInfo.getMqttPort()));
        mUser.setText(mDeviceInfo.getMqttUser());
        mPassword.setText(mDeviceInfo.getMqttPassword());
        mSendPin.setText(mDeviceInfo.getIrSendPin());
        mReceivePin.setText(mDeviceInfo.getIrReceivePin());
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

    @Override
    public void update(Observable o, Object arg) {
        if (arg == null || !(arg instanceof NotifyMsgEntity)) {
            return;
        }

        NotifyMsgEntity entity = (NotifyMsgEntity) arg;

        int code = (int) entity.getCode();

        try {
            JSONObject object = new JSONObject((String)entity.getData());
            Log.d(TAG, "update: " + object.toString());
            if (object.getString("cmd").equals("return")) {
                String message = object.getJSONObject("params").getString("message");
                Toast.makeText(mContext, message, Toast.LENGTH_LONG).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
