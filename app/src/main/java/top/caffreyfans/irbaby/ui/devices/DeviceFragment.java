package top.caffreyfans.irbaby.ui.devices;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Timer;
import java.util.TimerTask;

import top.caffreyfans.irbaby.R;
import top.caffreyfans.irbaby.adapter.DeviceAdapter;
import top.caffreyfans.irbaby.helper.NotifyMsgEntity;
import top.caffreyfans.irbaby.helper.UdpNotifyManager;
import top.caffreyfans.irbaby.helper.UdpSendThread;
import top.caffreyfans.irbaby.model.DeviceInfo;

public class DeviceFragment extends Fragment implements Observer {

    private static final String TAG = DeviceFragment.class.getSimpleName();
    private ListView mListView;
    private DeviceAdapter mDeviceAdapter;
    private List<DeviceInfo> mDeviceInfos = new ArrayList<>();
    private DeviceInfo mDeviceInfo;
    private Context mContext;
    private Timer mTimer;
    private String mLocalIP;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_device, container, false);
        mContext = getContext();
        mListView = root.findViewById(R.id.device_lv);
        UdpNotifyManager.getUdpNotifyManager().addObserver(this);
        mLocalIP = getLocalIP();
        mTimer = new Timer();
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                discoveryDevice();
            }
        };
        if (mTimer == null) {
            mTimer = new Timer();
        }
        mTimer.schedule(timerTask, 0, 5000);
    }

    @Override
    public void onPause() {
        super.onPause();
        mTimer.cancel();
        mTimer.purge();
        mTimer = null;
    }

    private void discoveryDevice() {
        JSONObject msg = new JSONObject();
        JSONObject params = new JSONObject();
        try {
            msg.put("cmd", "discovery");
            params.put("ip", mLocalIP);
            msg.put("params", params);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        new UdpSendThread("255.255.255.255", msg).start();
    }

    private String getLocalIP(){
        String ip = new String();
        WifiManager wifiManager = (WifiManager) getActivity().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
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
    public void update(Observable o, Object arg) {

        if (arg == null || !(arg instanceof NotifyMsgEntity)) {
            return;
        }

        NotifyMsgEntity entity = (NotifyMsgEntity) arg;

        int code = (int) entity.getCode();
        try {
            JSONObject jsonObject = new JSONObject((String) entity.getData());
            if (jsonObject.getString("cmd").equals("upload")) {
                mDeviceInfo = new DeviceInfo();
                mDeviceInfo.setIp(jsonObject.getJSONObject("params").getString("ip"));
                mDeviceInfo.setMac(jsonObject.getJSONObject("params").getString("mac"));
                if (jsonObject.getJSONObject("params").has("mqtt")) {
                    JSONObject mqttObject = jsonObject.getJSONObject("params").getJSONObject("mqtt");
                    mDeviceInfo.setMqttAddress(mqttObject.getString("host"));
                    mDeviceInfo.setMqttPort(mqttObject.getInt("port"));
                    mDeviceInfo.setMqttUser(mqttObject.getString("user"));
                    mDeviceInfo.setMqttPassword(mqttObject.getString("password"));
                }
                if (jsonObject.getJSONObject("params").has("send_pin")) {
                    mDeviceInfo.setIrSendPin(jsonObject.getJSONObject("params")
                            .getInt("send_pin"));
                }
                if (jsonObject.getJSONObject("params").has("receive_pin")) {
                    mDeviceInfo.setIrReceivePin(jsonObject.getJSONObject("params")
                            .getInt("receive_pin"));
                }
                if (jsonObject.getJSONObject("params").has("version")) {
                    mDeviceInfo.setVersion(jsonObject.getJSONObject("params").getString("version"));
                }
            }
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
                mDeviceAdapter = new DeviceAdapter(mContext, deviceInfos1, true);
                mListView.setAdapter(mDeviceAdapter);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + code);
        }
    }
}