package top.caffreyfans.irbaby.firmware_api;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import org.json.JSONException;
import org.json.JSONObject;

import top.caffreyfans.irbaby.helper.UdpSendThread;
import top.caffreyfans.irbaby.model.ApplianceInfo;
import top.caffreyfans.irbaby.model.DeviceInfo;

public class UdpApi extends Api {

    private final static String TAG = UdpApi.class.getSimpleName();
    private Context mContext;
    private String mLocalIP;
    private String mRemoteIP;
    private int mIP;

    public UdpApi(Context context, String remoteIP) {
        mContext = context;
        mIP = getLocalIP();
        mRemoteIP = remoteIP;
        mLocalIP = String.format(
                "%d.%d.%d.%d", (mIP & 0xff),
                (mIP >> 8 & 0xff), (mIP >> 16 & 0xff),
                (mIP >> 24 & 0xff));
    }


    public UdpApi(Context context) {
        mContext = context;
        mIP = getLocalIP();
        mLocalIP = String.format(
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
            msg.put("cmd", "query");
            params.put("ip", mLocalIP);
            params.put("type", "discovery");
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
        return mLocalIP;
    }

    @Override
    void send(JSONObject msg) {
        new UdpSendThread(mRemoteIP, msg).start();
    }
}
