package top.caffreyfans.irbaby.firmware_api;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import net.irext.webapi.bean.ACStatus;

import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.LitePal;

import java.util.List;
import java.util.Observable;

import top.caffreyfans.irbaby.helper.NotifyMsgEntity;
import top.caffreyfans.irbaby.helper.UdpNotifyManager;
import top.caffreyfans.irbaby.helper.UdpSendThread;
import top.caffreyfans.irbaby.model.ApplianceInfo;
import top.caffreyfans.irbaby.model.DeviceInfo;

public class UdpApi extends Api {

    private final static String TAG = UdpApi.class.getSimpleName();
    private Context mContext;
    private DeviceInfo mDeviceInfo;
    private int mIP;
    private String mStrIP;

    public UdpApi(Context context, DeviceInfo deviceInfo) {
        mContext = context;
        mDeviceInfo = deviceInfo;
        mIP = getLocalIP();
        mStrIP = String.format(
                "%d.%d.%d.%d", (mIP & 0xff),
                (mIP >> 8 & 0xff), (mIP >> 16 & 0xff),
                (mIP >> 24 & 0xff));
    }

    public UdpApi(Context context) {
        mContext = context;
        mIP = getLocalIP();
        mStrIP = String.format(
                "%d.%d.%d.%d", (mIP & 0xff),
                (mIP >> 8 & 0xff), (mIP >> 16 & 0xff),
                (mIP >> 24 & 0xff));
    }

    public void discoverDevice() {
        JSONObject msg = new JSONObject();
        JSONObject params = new JSONObject();
        String broadcast = String.format(
                "%d.%d.%d.%d", (mIP & 0xff),
                (mIP >> 8 & 0xff), (mIP >> 16 & 0xff),
                (0xff));
        try {
            msg.put("cmd", "discovery");
            params.put("ip", mStrIP);
            msg.put("params", params);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        new UdpSendThread(broadcast, msg).start();
    }

    public int getLocalIP(){
        WifiManager wifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
        if (wifiManager.isWifiEnabled()) {
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            int ipAddress = wifiInfo.getIpAddress();
            return ipAddress;
        }
        return 0;
    }

    public String getStrIP() {
        return mStrIP;
    }

    public void receiveIR() {

    }

    @Override
    void send(JSONObject msg) {
        new UdpSendThread(mDeviceInfo.getIp(), msg).start();
    }
}
