package top.caffreyfans.irbaby.firmware_api;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.Inet4Address;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

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

    public String fetchBroadcastAddressByIP(String ip) {
        try {
            Enumeration networkInterfaceEnumeration = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaceEnumeration.hasMoreElements()) {
                NetworkInterface networkInterface = (NetworkInterface) networkInterfaceEnumeration.nextElement();
                if (!networkInterface.isUp()) {
                    continue;
                }
                for (InterfaceAddress interfaceAddress : networkInterface.getInterfaceAddresses()) {
                    if (interfaceAddress.getAddress() instanceof Inet4Address
                            && interfaceAddress.getAddress().getHostAddress().equals(ip)
                    ) {
                        return interfaceAddress.getBroadcast().getHostAddress();
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        Toast.makeText(mContext, "没有找到合适的广播地址", Toast.LENGTH_SHORT).show();
        return "0.0.0.0";
    }

    public void discoverDevice() {
        JSONObject msg = new JSONObject();
        JSONObject params = new JSONObject();
        String broadcast = fetchBroadcastAddressByIP(mLocalIP);
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
