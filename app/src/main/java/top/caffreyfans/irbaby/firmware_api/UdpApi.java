package top.caffreyfans.irbaby.firmware_api;

import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.Inet4Address;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.LinkedHashSet;
import java.util.Enumeration;
import java.util.Set;

import top.caffreyfans.irbaby.helper.UdpSendThread;
import top.caffreyfans.irbaby.model.ApplianceInfo;
import top.caffreyfans.irbaby.model.DeviceInfo;

public class UdpApi extends Api {

    private final static String TAG = UdpApi.class.getSimpleName();
    private static final String DEFAULT_BROADCAST_IP = "255.255.255.255";
    private static final int MAX_UNICAST_SCAN_HOSTS = 1024;
    private Context mContext;
    private String mLocalIP;
    private String mRemoteIP;
    private int mIP;

    public UdpApi(Context context, String remoteIP) {
        mContext = context;
        mIP = getLocalIP();
        mRemoteIP = remoteIP;
        mLocalIP = toIpString(mIP);
    }


    public UdpApi(Context context) {
        mContext = context;
        mIP = getLocalIP();
        mLocalIP = toIpString(mIP);
    }

    private String toIpString(int ip) {
        return String.format(
                "%d.%d.%d.%d", (ip & 0xff),
                (ip >> 8 & 0xff), (ip >> 16 & 0xff),
                (ip >> 24 & 0xff));
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
                        if (interfaceAddress.getBroadcast() != null) {
                            return interfaceAddress.getBroadcast().getHostAddress();
                        }
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return DEFAULT_BROADCAST_IP;
    }

    private void appendSubnetTargets(Set<String> targets) {
        WifiManager wifiManager = (WifiManager) mContext.getApplicationContext()
                .getSystemService(Context.WIFI_SERVICE);
        if (wifiManager == null) {
            return;
        }
        DhcpInfo dhcpInfo = wifiManager.getDhcpInfo();
        if (dhcpInfo == null || dhcpInfo.ipAddress == 0 || dhcpInfo.netmask == 0) {
            return;
        }

        long ip = dhcpInfo.ipAddress & 0xffffffffL;
        long netmask = dhcpInfo.netmask & 0xffffffffL;
        long network = ip & netmask;
        long broadcast = network | (~netmask & 0xffffffffL);
        long hostCount = broadcast - network - 1;

        targets.add(toIpString((int) broadcast));
        if (dhcpInfo.gateway != 0) {
            targets.add(toIpString(dhcpInfo.gateway));
        }
        if (hostCount <= 0 || hostCount > MAX_UNICAST_SCAN_HOSTS) {
            return;
        }

        for (long current = network + 1; current < broadcast; current++) {
            if (current == ip) {
                continue;
            }
            targets.add(toIpString((int) current));
        }
    }

    private Set<String> getDiscoveryTargets() {
        LinkedHashSet<String> targets = new LinkedHashSet<>();
        if (mIP != 0) {
            targets.add(fetchBroadcastAddressByIP(mLocalIP));
            appendSubnetTargets(targets);
        }
        targets.add(DEFAULT_BROADCAST_IP);
        return targets;
    }

    public void discoverDevice() {
        mIP = getLocalIP();
        mLocalIP = toIpString(mIP);
        JSONObject msg = new JSONObject();
        JSONObject params = new JSONObject();
        try {
            msg.put("cmd", "query");
            params.put("ip", mLocalIP);
            params.put("type", "discovery");
            msg.put("params", params);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final Set<String> targets = getDiscoveryTargets();
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (String target : targets) {
                    new UdpSendThread(target, msg).run();
                }
            }
        }).start();
    }

    public int getLocalIP(){
        WifiManager wifiManager = (WifiManager) mContext.getApplicationContext()
                .getSystemService(Context.WIFI_SERVICE);
        if (wifiManager != null && wifiManager.isWifiEnabled()) {
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            if (wifiInfo != null) {
                return wifiInfo.getIpAddress();
            }
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
