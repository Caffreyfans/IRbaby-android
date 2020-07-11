package top.caffreyfans.irbaby.helper;

import org.json.JSONObject;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UdpSendThread extends Thread {

    private int mPort = 4210;
    private String mAddr;
    private JSONObject mSendMsg;

    public UdpSendThread(String addr, JSONObject sendMsg) {
        mAddr = addr;
        mSendMsg = sendMsg;
    }

    @Override
    public void run() {

        DatagramSocket mSocket;
        try {
            InetAddress ip = InetAddress.getByName(mAddr);
            mSocket = new DatagramSocket();
            byte[] tmp = mSendMsg.toString().getBytes();
            mSocket.send(new DatagramPacket(tmp, tmp.length, ip, mPort));
            mSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
