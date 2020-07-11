package top.caffreyfans.irbaby.helper;

import java.util.Observable;

public class UdpNotifyManager extends Observable {
    public static final int MSG_HANDLE = 0;
    public static final int DISCOVERY = 1;
    public static final int SAVE_CONFIG = 2;
    public static final int RECORD_RT = 3;
    public static final int INFO_RT = 4;

    private static UdpNotifyManager mUdpNotifyManager;

    public static UdpNotifyManager getUdpNotifyManager() {
        if (mUdpNotifyManager == null) {
            mUdpNotifyManager = new UdpNotifyManager();
        }
        return mUdpNotifyManager;
    }

    /**
     * 事件发生后通知监听者
     *
     * @param code :事件代码号
     */
    public void notifyChange(int code) {
        NotifyMsgEntity msgEntity = new NotifyMsgEntity();
        msgEntity.setCode(code);
        notifyChange(msgEntity);
    }

    /**
     * 事件发生后通知监听者
     *
     * @param msgEntity 需要发送的消息数据
     */
    public void notifyChange(final NotifyMsgEntity msgEntity) {
        UIUtils.runInMainThread(new Runnable() {
            @Override
            public void run() {
                setChanged();
                notifyObservers(msgEntity);
            }
        });
    }
}
