package top.caffreyfans.irbaby.ui.devices;

import android.app.NotificationManager;
import android.content.Context;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import org.litepal.LitePal;

import java.util.ArrayList;
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

public class DeviceSelectActivity  extends AppCompatActivity implements Observer {

    private final static String TAG = DeviceSelectActivity.class.getSimpleName();
    private ListView mListView;
    private List<DeviceInfo> mDeviceInfos;
    private Context mContext;
    private DeviceAdapter mDeviceAdapter;
    private Timer mTimer;
    private IRbabyApi mCommonApi;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_device);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mContext = this;
        mListView = findViewById(R.id.device_lv);
        mDeviceInfos = new ArrayList<>();
        mTimer = new Timer();

        UdpNotifyManager.getUdpNotifyManager().addObserver(this);
        mCommonApi = new IRbabyApi(this);
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

        NotifyMsgEntity entity = (NotifyMsgEntity) arg;
        int code = (int)entity.getCode();
        if (code == UdpNotifyManager.DISCOVERY) {
            mDeviceInfos = LitePal.findAll(DeviceInfo.class);
            mDeviceAdapter = new DeviceAdapter(mContext, mDeviceInfos, 2);
            mListView.setAdapter(mDeviceAdapter);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
