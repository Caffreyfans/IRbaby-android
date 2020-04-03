package top.caffreyfans.irbaby.helper;

import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class UdpReceiveThread extends Thread {
    private static final String TAG = UdpReceiveThread.class.getSimpleName();
    private int mPort = 4210;

    public UdpReceiveThread() {
    }

    @Override
    public void run() {
        byte[] buffer = new byte[512];
        DatagramSocket ds = null;
        try {
            ds = new DatagramSocket(mPort);
        } catch (SocketException e) {
            e.printStackTrace();
        }

        while (true) {
            DatagramPacket dp = new DatagramPacket(buffer, buffer.length);
            try {
                if (ds != null) {
                    ds.receive(dp);
                }
                if (dp.getLength() > 0){
                    String msg = new String(dp.getData(), 0, dp.getLength());
                    NotifyMsgEntity msgEntity = new NotifyMsgEntity(UdpNotifyManager.DISCOVERY, msg);
                    Log.d(TAG, "receiveUDP: " + msg);
                    if (msg.contains("upload")) {
                        UdpNotifyManager.getUdpNotifyManager().notifyChange(msgEntity);
                    }
                    if (msg.contains("return")) {
                        UdpNotifyManager.getUdpNotifyManager().notifyChange(msgEntity);
                    }
                }
            } catch (IOException e){
                e.printStackTrace();
            }
        }
    }
}
