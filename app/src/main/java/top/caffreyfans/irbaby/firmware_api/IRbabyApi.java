package top.caffreyfans.irbaby.firmware_api;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import net.irext.webapi.bean.ACStatus;
import net.irext.webapi.utils.Constants;

import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.LitePal;

import java.util.List;
import java.util.Observable;
import java.util.Observer;

import top.caffreyfans.irbaby.helper.NotifyMsgEntity;
import top.caffreyfans.irbaby.helper.UdpNotifyManager;
import top.caffreyfans.irbaby.model.ApplianceInfo;
import top.caffreyfans.irbaby.model.DeviceInfo;

public class IRbabyApi implements Observer {

    private UdpApi mUdpApi;
    private MqttApi mMqttApi;
    private Api mApi;
    private Context mContext;
    private DeviceInfo mDeviceInfo;
    private ApplianceInfo mApplianceInfo;

    private final static String TAG = IRbabyApi.class.getSimpleName();

    public IRbabyApi(Context context, DeviceInfo deviceInfo, ApplianceInfo applianceInfo) {
        UdpNotifyManager.getUdpNotifyManager().addObserver(this);
        mContext = context;
        mDeviceInfo = deviceInfo;
        mApplianceInfo = applianceInfo;
        mUdpApi = new UdpApi(context, deviceInfo);
        if (mApplianceInfo != null) {
            mMqttApi = new MqttApi(context, deviceInfo, mApplianceInfo);
        }
        switchApi();
    }

    public IRbabyApi(Context context) {
        UdpNotifyManager.getUdpNotifyManager().addObserver(this);
        mContext = context;
        mUdpApi = new UdpApi(context);
        mApi = mUdpApi;
    }

    public void discoverDevices() {
        mUdpApi.discoverDevice();
    }

    public void sendIR(ACStatus acStatus) {
        JSONObject send = new JSONObject();
        JSONObject status = new JSONObject();
        JSONObject params = new JSONObject();
        try {
            status.put("power", acStatus.acPower.getValue());
            status.put("temperature", acStatus.acTemp.getValue());
            status.put("mode", acStatus.acMode.getValue());
            status.put("swing", acStatus.acSwing.getValue());
            status.put("direction", acStatus.acWindDir.getValue());
            status.put("speed", acStatus.acWindSpeed.getValue());
            params.put("status", status);
            params.put("file", mApplianceInfo.getFile());
            send.put("cmd", "send");
            send.put("params", params);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mApi.send(send);
    }

    public void getDeviceInfo() {
        JSONObject send = new JSONObject();
        try {
            send.put("cmd", "info");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mUdpApi.send(send);
    }

    public void saveIR(String filename) {
        JSONObject send = new JSONObject();
        JSONObject params = new JSONObject();
        try {
            params.put("file", filename);
            send.put("cmd", "save");
            send.put("params", params);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ApplianceInfo applianceInfo = new ApplianceInfo();
        applianceInfo.setMac(mDeviceInfo.getMac());
        applianceInfo.setIp(mDeviceInfo.getIp());
        applianceInfo.setFile(filename);
        applianceInfo.setName(filename);
        applianceInfo.setCategory(Constants.CategoryID.DIY.getValue());
        applianceInfo.save();
        mApi.send(send);
    }

    public void sendIR(String filename) {
        JSONObject send = new JSONObject();
        JSONObject params = new JSONObject();
        try {
            params.put("file", filename);
            send.put("cmd", "send");
            send.put("params", params);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mUdpApi.send(send);
    }

    public void syncAppliance(String filename) {

    }

    public void saveConfig(DeviceInfo deviceInfo) {
        JSONObject object = new JSONObject();
        try {
            JSONObject mqtt = new JSONObject();
            mqtt.put("host", deviceInfo.getMqttAddress());
            mqtt.put("port", deviceInfo.getMqttPort());
            mqtt.put("user", deviceInfo.getMqttUser());
            mqtt.put("password", deviceInfo.getMqttPassword());

            JSONObject params = new JSONObject();
            params.put("mqtt", mqtt);
            params.put("send_pin", deviceInfo.getIrSendPin());
            params.put("receive_pin", deviceInfo.getIrReceivePin());

            object.put("cmd", "config");
            object.put("params", params);

            Log.d(TAG, "onClick: " + object.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mUdpApi.send(object);
    }

    public void receiveIR() {

        JSONObject object = new JSONObject();
        try {
            JSONObject params = new JSONObject();
            object.put("cmd", "record");
            object.put("params", mUdpApi.getStrIP());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mUdpApi.send(object);
    }

    public void free() {
        if (mMqttApi != null) {
            mMqttApi.free();
        }
    }

    public void updateFirmware() {
        JSONObject object = new JSONObject();
        try {
            String url = "http://irbaby.caffreyfans.top/latest/irbaby-4m.bin";
            JSONObject params = new JSONObject();
            params.put("url", url);
            object.put("cmd", "update");
            object.put("params", params);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mUdpApi.send(object);
    }

    private void switchApi() {
        ConnectivityManager connectivityManager = (ConnectivityManager) mContext
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
        mApi = mMqttApi;
        if (activeNetInfo != null &&
            activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI) {
            mApi = mUdpApi;
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        NotifyMsgEntity entity = (NotifyMsgEntity)arg;
        int code = (int)entity.getCode();
        if (code == UdpNotifyManager.MSG_HANDLE) {
            try {
                JSONObject jsonObject = new JSONObject((String) entity.getData());
                String cmd = jsonObject.getString("cmd");
                NotifyMsgEntity msgEntity;
                switch (cmd) {
                    case "upload":
                        mDeviceInfo = new DeviceInfo();
                        mDeviceInfo.setIp(jsonObject.getJSONObject("params")
                                .getString("ip"));
                        mDeviceInfo.setMac(jsonObject.getJSONObject("params")
                                .getString("mac"));
                        if (jsonObject.getJSONObject("params").has("mqtt")) {
                            JSONObject mqttObject = jsonObject.getJSONObject("params")
                                    .getJSONObject("mqtt");
                            if (mqttObject.has("host")) {
                                mDeviceInfo.setMqttAddress(mqttObject.getString("host"));
                            }
                            if (mqttObject.has("port")) {
                                mDeviceInfo.setMqttPort(mqttObject.getInt("port"));
                            }
                            if (mqttObject.has("user")) {
                                mDeviceInfo.setMqttUser(mqttObject.getString("user"));
                            }
                            if (mqttObject.has("password")) {
                                mDeviceInfo.setMqttPassword(mqttObject.getString("password"));
                            }
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
                            mDeviceInfo.setVersion(jsonObject.getJSONObject("params")
                                    .getString("version"));
                        }
                        List<DeviceInfo> deviceInfos = LitePal.where("mac = ?",
                                mDeviceInfo.getMac()).find(DeviceInfo.class);
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

                        msgEntity = new NotifyMsgEntity(UdpNotifyManager.DISCOVERY, null);
                        UdpNotifyManager.getUdpNotifyManager().notifyChange(msgEntity);
                        break;
                    case "return":
                        msgEntity = new NotifyMsgEntity(UdpNotifyManager.SAVE_CONFIG,
                                entity.getData());
                        UdpNotifyManager.getUdpNotifyManager().notifyChange(msgEntity);
                        break;

                    case "record_rt":
                        msgEntity = new NotifyMsgEntity(UdpNotifyManager.RECORD_RT,
                                entity.getData());
                        UdpNotifyManager.getUdpNotifyManager().notifyChange(msgEntity);
                        break;

                    case "info_rt":
                        msgEntity = new NotifyMsgEntity(UdpNotifyManager.INFO_RT,
                                entity.getData());
                        UdpNotifyManager.getUdpNotifyManager().notifyChange(msgEntity);
                        break;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
