package top.caffreyfans.irbaby.helper;

import android.util.Log;

import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class UdpReceiveThread extends Thread {
    private static final String TAG = UdpReceiveThread.class.getSimpleName();
    private int mPort = 4210;
    private static byte[] buffer = new byte[2048];
    private DatagramSocket ds;
    public UdpReceiveThread() {
        try {
            ds = new DatagramSocket(mPort);
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while (true) {
            DatagramPacket dp = new DatagramPacket(buffer, buffer.length);
            try {
                if (ds != null) {
                    ds.receive(dp);
                }
                if (dp.getLength() > 0){
                    String msg = new String(dp.getData(), 0, dp.getLength());
                    Log.d(TAG, "run: " + msg);
                    NotifyMsgEntity msgEntity;
                    msgEntity = new NotifyMsgEntity(UdpNotifyManager.MSG_HANDLE, msg);
                    UdpNotifyManager.getUdpNotifyManager().notifyChange(msgEntity);
                }
            } catch (IOException e){
                e.printStackTrace();
            }
        }
    }
}
