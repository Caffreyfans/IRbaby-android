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
        if (deviceInfo != null) {
            mUdpApi = new UdpApi(context, deviceInfo.getIp());
        } else {
            mUdpApi = new UdpApi(context, applianceInfo.getIp());
        }
        if (mApplianceInfo != null && deviceInfo != null) {
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

    public void sendSignal(ACStatus acStatus) {
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
            params.put("signal", "IR");
            params.put("type", "status");
            send.put("cmd", "send");
            send.put("params", params);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        mApi.send(send);
    }

    public void sendSignal(String file, String signal, String type) {
        JSONObject send = new JSONObject();
        JSONObject params = new JSONObject();
        try {
            params.put("file", file);
            params.put("signal", signal);
            params.put("type", type);
            send.put("cmd", "send");
            send.put("params", params);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mUdpApi.send(send);
    }

    public void getDeviceInfo() {
        JSONObject send = new JSONObject();
        try {
            send.put("cmd", "query");
            JSONObject params = new JSONObject();
            params.put("type", "info");
            send.put("params", params);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mUdpApi.send(send);
    }

    public void saveSignal(String file, String signal) {
        JSONObject send = new JSONObject();
        JSONObject params = new JSONObject();
        try {
            params.put("file", file);
            params.put("signal", signal);
            params.put("type", "save_signal");
            send.put("cmd", "set");
            send.put("params", params);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ApplianceInfo applianceInfo = new ApplianceInfo();
        applianceInfo.setSignal(signal);
        applianceInfo.setMac(mDeviceInfo.getMac());
        applianceInfo.setIp(mDeviceInfo.getIp());
        applianceInfo.setFile(file);
        applianceInfo.setName(file);
        applianceInfo.setCategory(Constants.CategoryID.DIY.getValue());
        applianceInfo.save();
        mApi.send(send);
    }

    public void syncAppliance(String filename) {

    }

    public void saveConfig(DeviceInfo deviceInfo) {
        JSONObject send = new JSONObject();
        try {
            JSONObject mqtt = new JSONObject();
            mqtt.put("host", deviceInfo.getMqttAddress());
            mqtt.put("port", deviceInfo.getMqttPort());
            mqtt.put("user", deviceInfo.getMqttUser());
            mqtt.put("password", deviceInfo.getMqttPassword());

            JSONObject pin = new JSONObject();
            pin.put("ir_send", deviceInfo.getIrSendPin());
            pin.put("ir_receive", deviceInfo.getIrReceivePin());

            JSONObject params = new JSONObject();
            params.put("mqtt", mqtt);
            params.put("type", "config");
            params.put("pin", pin);
            send.put("cmd", "set");
            send.put("params", params);

            Log.d(TAG, "onClick: " + send.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mUdpApi.send(send);
    }

    public void enableSignal() {

        JSONObject send = new JSONObject();
        try {
            JSONObject params = new JSONObject();
            params.put("type", "record");
            params.put("ip", mUdpApi.getStrIP());
            send.put("cmd", "set");
            send.put("params", params);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mUdpApi.send(send);
    }

    public void disableRecord() {
        JSONObject send = new JSONObject();
        try {
            JSONObject params = new JSONObject();
            params.put("type", "disable_record");
            send.put("cmd", "set");
            send.put("params", params);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mUdpApi.send(send);
    }

    public void free() {
        if (mMqttApi != null) {
            mMqttApi.free();
        }
    }

    public void updateFirmware(int flash_size) {
        JSONObject send = new JSONObject();
        try {
            int size = flash_size / 1024;
            String url = String.format("http://irbaby.caffreyfans.top/latest/IRbaby%dm.bin", size);
            Log.d(TAG, "updateFirmware: url = " + url);
            JSONObject params = new JSONObject();
            params.put("url", url);
            params.put("type", "update");
            send.put("cmd", "set");
            send.put("params", params);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mUdpApi.send(send);
    }

    public void registerDevice(String file, Constants.CategoryID device_type, boolean exist) {
        JSONObject send = new JSONObject();
        try {
            JSONObject params = new JSONObject();
            params.put("type", "device");
            params.put("file", file);
            params.put("device_type", device_type.getValue());
            params.put("exist", exist);
            send.put("cmd", "set");
            send.put("params", params);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mApi.send(send);
    }

    private void switchApi() {
//        ConnectivityManager connectivityManager = (ConnectivityManager) mContext
//                .getSystemService(Context.CONNECTIVITY_SERVICE);
//        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
//        mApi = mMqttApi;
//        if (activeNetInfo != null &&
//            activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI) {
//            mApi = mUdpApi;
//        }
        mApi = mUdpApi;
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
                    case "query_discovery":
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
                        if (jsonObject.getJSONObject("params").has("pin")) {
                            JSONObject pinObject = jsonObject.getJSONObject("params")
                                    .getJSONObject("pin");
                            if (pinObject.has("ir_send")) {
                                mDeviceInfo.setIrSendPin(pinObject.getInt("ir_send"));
                            }
                            if (pinObject.has("ir_receive")) {
                                mDeviceInfo.setIrReceivePin(pinObject.getInt("ir_receive"));
                            }
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

                    case "query_info":
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
