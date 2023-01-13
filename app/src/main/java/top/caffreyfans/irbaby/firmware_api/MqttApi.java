package top.caffreyfans.irbaby.firmware_api;

import android.content.Context;
import android.util.Log;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONObject;

import java.util.Observable;

import top.caffreyfans.irbaby.helper.NotifyMsgEntity;
import top.caffreyfans.irbaby.model.ApplianceInfo;
import top.caffreyfans.irbaby.model.DeviceInfo;

public class MqttApi extends Api {

    private static final String TAG = MqttApi.class.getSimpleName();
    private MqttAndroidClient mMqttClient;
    private DeviceInfo mDeviceInfo;
    private ApplianceInfo mApplianceInfo;

    private MqttCallbackExtended mMqttCallback = new MqttCallbackExtended() {
        @Override
        public void connectComplete(boolean reconnect, String serverURI) {
        }

        @Override
        public void connectionLost(Throwable cause) {
        }

        @Override
        public void messageArrived(String topic, MqttMessage message) throws Exception {
            Log.d(TAG, "messageArrived: " + topic + " " + message);
        }

        @Override
        public void deliveryComplete(IMqttDeliveryToken token) {
        }
    };

    private IMqttActionListener mIMqttActionListener;

    public MqttApi(Context context, final DeviceInfo deviceInfo, final ApplianceInfo applianceInfo) {
        super();
        mDeviceInfo = deviceInfo;
        mApplianceInfo = applianceInfo;
        mIMqttActionListener = new IMqttActionListener() {

            @Override
            public void onSuccess(IMqttToken asyncActionToken) {
                String topic = "/IRbaby/" + deviceInfo.getMac()
                        + "/set/" + applianceInfo.getFile() + "/#";
                try {
                    mMqttClient.subscribe(topic, 0);
                    Log.d(TAG, "onSuccess: subscribe " + topic);
                } catch (MqttException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(IMqttToken asyncActionToken, Throwable exception) {

            }
        };

        if (deviceInfo.getMqttAddress() != null && deviceInfo.getMqttPort() != 0) {
            try {
                String host = "tcp://";
                host += deviceInfo.getMqttAddress() + ":";
                host += deviceInfo.getMqttPort();
                Log.d(TAG, "mqttInit: " + deviceInfo.getMqttAddress());
                mMqttClient = new MqttAndroidClient(context, host, "IRbaby");
                MqttConnectOptions options = new MqttConnectOptions();
                if (deviceInfo.getMqttUser() != null && !deviceInfo.getMqttUser().trim().equals("")
                        && deviceInfo.getMqttPassword() != null) {
                    options.setUserName(deviceInfo.getMqttUser());
                    options.setPassword(deviceInfo.getMqttPassword().toCharArray());
                }
                options.setConnectionTimeout(20);
                options.setAutomaticReconnect(true);
                options.setKeepAliveInterval(20);
                mMqttClient.setCallback(mMqttCallback);
                mMqttClient.connect(options, null, mIMqttActionListener);
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }
    }

    public void free() {
        if (mMqttClient != null) {
            mMqttClient.unregisterResources();
            mMqttClient.close();
            mMqttClient = null;
        }
    }

    @Override
    void send(JSONObject msg) {
        String topic = "/IRbaby/" + mApplianceInfo.getMac()
                + "/set/" + mApplianceInfo.getFile() + "/status";
        String payload = msg.toString();
        MqttMessage mqttMessage = new MqttMessage();
        mqttMessage.setQos(0);
        mqttMessage.setRetained(false);
        mqttMessage.setPayload(payload.getBytes());
        if (mMqttClient.isConnected()) {
            try {
                mMqttClient.publish(topic, mqttMessage);
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }
    }
}