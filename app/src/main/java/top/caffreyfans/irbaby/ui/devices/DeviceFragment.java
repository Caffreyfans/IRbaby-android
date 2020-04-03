package top.caffreyfans.irbaby.ui.devices;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import org.json.JSONException;
import org.json.JSONObject;

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

public class DeviceFragment extends Fragment implements Observer {

    private static final String TAG = DeviceFragment.class.getSimpleName();
    private ListView mListView;
    private DeviceAdapter mDeviceAdapter;
    private List<DeviceInfo> mDeviceInfos = new ArrayList<>();
    private DeviceInfo mDeviceInfo;
    private Context mContext;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_device, container, false);
        mContext = getContext();
        mListView = root.findViewById(R.id.device_lv);
        UdpNotifyManager.getUdpNotifyManager().addObserver(this);
        discoveryDevice();
        return root;
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
                            .getString("send_pin"));
                }
                if (jsonObject.getJSONObject("params").has("receive_pin")) {
                    mDeviceInfo.setIrReceivePin(jsonObject.getJSONObject("params")
                            .getString("receive_pin"));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        switch (code) {
            case UdpNotifyManager.DISCOVERY:
                if (!mDeviceInfos.contains(mDeviceInfo)) {
                    mDeviceInfos.add(mDeviceInfo);
                    mDeviceAdapter = new DeviceAdapter(mContext, mDeviceInfos, true);
                    mListView.setAdapter(mDeviceAdapter);
                }
        }
    }
}