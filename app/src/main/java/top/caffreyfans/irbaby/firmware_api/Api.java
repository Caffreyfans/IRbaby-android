package top.caffreyfans.irbaby.firmware_api;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

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

public abstract class Api {
    abstract void send(JSONObject msg);
}
