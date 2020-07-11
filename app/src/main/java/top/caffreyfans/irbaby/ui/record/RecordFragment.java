package top.caffreyfans.irbaby.ui.record;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import org.litepal.LitePal;

import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Timer;
import java.util.TimerTask;

import top.caffreyfans.irbaby.R;
import top.caffreyfans.irbaby.adapter.DeviceAdapter;
import top.caffreyfans.irbaby.firmware_api.IRbabyApi;
import top.caffreyfans.irbaby.helper.NotifyMsgEntity;
import top.caffreyfans.irbaby.helper.UdpNotifyManager;
import top.caffreyfans.irbaby.model.DeviceInfo;

public class RecordFragment extends Fragment implements Observer {

    private static final String TAG = RecordFragment.class.getSimpleName();
    private ListView mListView;
    private DeviceAdapter mDeviceAdapter;
    private List<DeviceInfo> mDeviceInfos;
    private Context mContext;
    private Timer mTimer;
    private IRbabyApi mCommonApi;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_device, container, false);
        mContext = getContext();
        mListView = root.findViewById(R.id.device_lv);
        mCommonApi = new IRbabyApi(getContext().getApplicationContext());
        mTimer = new Timer();
        UdpNotifyManager.getUdpNotifyManager().addObserver(this);
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                mCommonApi.discoverDevices();
            }
        };
        if (mTimer == null) {
            mTimer = new Timer();
        }
        mTimer.schedule(timerTask, 0, 5000);
    }

    @Override
    public void onPause() {
        super.onPause();
        mTimer.cancel();
        mTimer.purge();
        mTimer = null;
    }

    @Override
    public void update(Observable o, Object arg) {
        NotifyMsgEntity entity = (NotifyMsgEntity)arg;
        int code = (int)entity.getCode();
        if (code == UdpNotifyManager.DISCOVERY) {
            mDeviceInfos = LitePal.findAll(DeviceInfo.class);
            mDeviceAdapter = new DeviceAdapter(mContext, mDeviceInfos, 3);
            mListView.setAdapter(mDeviceAdapter);
        }
    }
}