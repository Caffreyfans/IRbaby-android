package top.caffreyfans.irbaby.ui.devices;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import top.caffreyfans.irbaby.R;
import top.caffreyfans.irbaby.adapter.DeviceAdapter;
import top.caffreyfans.irbaby.helper.NotifyMsgEntity;
import top.caffreyfans.irbaby.helper.UdpNotifyManager;
import top.caffreyfans.irbaby.helper.UdpSendThread;
import top.caffreyfans.irbaby.model.DeviceInfo;

public class DeviceSelectActivity  extends AppCompatActivity implements Observer {

    private final static String TAG = DeviceSelectActivity.class.getSimpleName();
    private ListView mListView;
    private List<DeviceInfo> mDeviceInfos;
    private DeviceInfo mDeviceInfo;
    private Context mContext;
    private DeviceAdapter mDeviceAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_device);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mContext = this;
        mListView = findViewById(R.id.device_lv);
        mDeviceInfos = new ArrayList<>();

        UdpNotifyManager.getUdpNotifyManager().addObserver(this);
        discoveryDevice();
    }

    @Override
    public void update(Observable o, Object arg) {
        if (arg == null || !(arg instanceof NotifyMsgEntity)) {
            return;
        }

        NotifyMsgEntity entity = (NotifyMsgEntity) arg;

        int code = (int) entity.getCode();

        try {
            JSONObject jsonObject = new JSONObject((String) entity.getData());
            mDeviceInfo = new DeviceInfo();
            mDeviceInfo.setIp(jsonObject.getJSONObject("params").getString("ip"));
            mDeviceInfo.setMac(jsonObject.getJSONObject("params").getString("mac"));
            mDeviceInfo.setMqttAddress(jsonObject.getJSONObject("params")
                    .getJSONObject("mqtt").getString("host"));
            mDeviceInfo.setMqttPort(jsonObject.getJSONObject("params")
                    .getJSONObject("mqtt").getInt("port"));
            mDeviceInfo.setMqttUser(jsonObject.getJSONObject("params")
                    .getJSONObject("mqtt").getString("user"));
            mDeviceInfo.setMqttPassword(jsonObject.getJSONObject("params")
                    .getJSONObject("mqtt").getString("password"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        switch (code) {
            case UdpNotifyManager.DISCOVERY:
                List<DeviceInfo> deviceInfos = LitePal.where("mac = ?", mDeviceInfo.getMac()).find(DeviceInfo.class);
                if (deviceInfos.size() > 0) {
                    DeviceInfo origin = deviceInfos.get(0);
                    origin.setMqttAddress(mDeviceInfo.getMqttAddress());
                    origin.setMqttPassword(mDeviceInfo.getMqttPassword());
                    origin.setMqttUser(mDeviceInfo.getMqttUser());
                    origin.setMqttPort(mDeviceInfo.getMqttPort());
                    origin.setIp(mDeviceInfo.getIp());
                    origin.setIrReceivePin(mDeviceInfo.getIrReceivePin());
                    origin.setIrSendPin(mDeviceInfo.getIrSendPin());
                    origin.setVersion(mDeviceInfo.getVersion());
                    origin.save();
                } else {
                    mDeviceInfo.save();
                }
                List<DeviceInfo> deviceInfos1 = LitePal.findAll(DeviceInfo.class);
                mDeviceAdapter = new DeviceAdapter(mContext, deviceInfos1, false);
                mListView.setAdapter(mDeviceAdapter);
                break;
        }
    }

    private void discoveryDevice() {
        JSONObject msg = new JSONObject();
        JSONObject params = new JSONObject();
        try {
            msg.put("cmd", "discovery");
            params.put("ip", getLocalIP());
            msg.put("params", params);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        new UdpSendThread("255.255.255.255", msg).start();
    }

    private String getLocalIP(){
        String ip = new String();
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wifiManager.isWifiEnabled()) {
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            int ipAddress = wifiInfo.getIpAddress();
            ip = String.format(
                    "%d.%d.%d.%d", (ipAddress & 0xff),
                    (ipAddress >> 8 & 0xff), (ipAddress >> 16 & 0xff),
                    (ipAddress >> 24 & 0xff));
        }
        return ip;
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
}
