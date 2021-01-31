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
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Observable;
import java.util.Observer;

import top.caffreyfans.irbaby.R;
import top.caffreyfans.irbaby.firmware_api.IRbabyApi;
import top.caffreyfans.irbaby.helper.ApplianceContract;
import top.caffreyfans.irbaby.helper.NotifyMsgEntity;
import top.caffreyfans.irbaby.helper.UdpNotifyManager;
import top.caffreyfans.irbaby.model.DeviceInfo;

import static top.caffreyfans.irbaby.helper.UdpNotifyManager.SAVE_CONFIG;

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
    private IRbabyApi mCommonApi;

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
            mCommonApi = new IRbabyApi(this, mDeviceInfo, null);
        }

        Button saveBtn = (Button) findViewById(R.id.device_save_btn);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean couldSave = updateValue();
                mDeviceInfo.update(mDeviceInfo.getId());
                if (mCommonApi != null && couldSave) {
                    mCommonApi.saveConfig(mDeviceInfo);
                }
            }
        });
    }

    private void fillValue() {
        mAddress.setText(mDeviceInfo.getMqttAddress());
        mPort.setText(String.valueOf(mDeviceInfo.getMqttPort()));
        mUser.setText(mDeviceInfo.getMqttUser());
        mPassword.setText(mDeviceInfo.getMqttPassword());
        mSendPin.setText(String.valueOf(mDeviceInfo.getIrSendPin()));
        mReceivePin.setText(String.valueOf(mDeviceInfo.getIrReceivePin()));
    }

    private boolean updateValue() {
        String address = mAddress.getText().toString();
        String mqttPort = mPort.getText().toString();
        String sendPin = mSendPin.getText().toString();
        String tips = "";
        boolean ret = true;
        Log.d(TAG, "updateValue: " + address);
        if (!address.isEmpty() && mqttPort.isEmpty()) {
            tips += "mqtt 端口不能为空;";
            mqttPort = "0";
            ret = false;
        }
        if (sendPin.isEmpty()) {
            tips += "发送引脚不能为空;";
            sendPin = "0";
            ret = false;
        }
        String receivePin = mReceivePin.getText().toString();
        if (receivePin.isEmpty()) {
            receivePin = "0";
        }
        if (ret) {
            mDeviceInfo.setMqttAddress(mAddress.getText().toString());
            mDeviceInfo.setMqttPort(Integer.parseInt(mqttPort));
            mDeviceInfo.setMqttUser(mUser.getText().toString());
            mDeviceInfo.setMqttPassword(mPassword.getText().toString());
            mDeviceInfo.setIrSendPin(Integer.parseInt(mSendPin.getText().toString()));
            mDeviceInfo.setIrReceivePin(Integer.parseInt(mReceivePin.getText().toString()));
        } else {
            Toast.makeText(mContext, tips, Toast.LENGTH_LONG).show();
        }
        return ret;
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
        NotifyMsgEntity entity = (NotifyMsgEntity)arg;
        int code = ((NotifyMsgEntity) arg).getCode();
        if (code == SAVE_CONFIG) {
            try {
                JSONObject object = new JSONObject((String)entity.getData());
                String message = object.getJSONObject("params").getString("message");
                Toast.makeText(mContext, message, Toast.LENGTH_LONG).show();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
