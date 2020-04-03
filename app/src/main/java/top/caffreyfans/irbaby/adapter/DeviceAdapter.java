package top.caffreyfans.irbaby.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.BaseSwipeAdapter;

import net.irext.webapi.utils.Constants;

import java.util.List;
import top.caffreyfans.irbaby.R;
import top.caffreyfans.irbaby.helper.ApplianceContract;
import top.caffreyfans.irbaby.model.ApplianceInfo;
import top.caffreyfans.irbaby.model.DeviceInfo;
import top.caffreyfans.irbaby.ui.appliances.ApplianceSelectActivity;
import top.caffreyfans.irbaby.ui.devices.DeviceSelectActivity;
import top.caffreyfans.irbaby.ui.devices.DeviceSettingsActivity;

public class DeviceAdapter extends BaseSwipeAdapter {

    private List<DeviceInfo> mDeviceInfos;
    private Context mContext;
    private String mName;
    private String mIP;
    private boolean mIsSetting;

    public DeviceAdapter(Context context, List<DeviceInfo> deviceInfos, boolean isSetting) {
        mDeviceInfos = deviceInfos;
        mContext = context;
        mIsSetting = isSetting;
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.swipe;
    }

    @Override
    public View generateView(final int position, ViewGroup parent) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.device_list_item, null);
        SwipeLayout swipeLayout = v.findViewById(R.id.swipe);
        ImageButton imageButton = v.findViewById(R.id.edit_ib);
        if (mIsSetting) {
            imageButton.setVisibility(View.VISIBLE);
            imageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, DeviceSettingsActivity.class);
                    intent.putExtra(ApplianceContract.DeviceSetting.DEVICE_INFO, mDeviceInfos.get(position));
                    mContext.startActivity(intent);
                }
            });

        } else {
            imageButton.setVisibility(View.INVISIBLE);
            swipeLayout.getSurfaceView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ApplianceInfo applianceInfo = new ApplianceInfo();
                    applianceInfo.setIp(mDeviceInfos.get(position).getIp());
                    applianceInfo.setMac(mDeviceInfos.get(position).getMac());
                    Intent intent = new Intent(mContext, ApplianceSelectActivity.class);
                    intent.putExtra(ApplianceContract.Select.TITLE, mContext.getString(R.string.select_category));
                    intent.putExtra(ApplianceContract.Select.CONTENT_ID, Constants.ContentID.LIST_CATEGORIES);
                    intent.putExtra(ApplianceContract.Select.APPLIANCE_INFO, applianceInfo);
                    mContext.startActivity(intent);
                }
            });
        }
        return v;
    }

    @Override
    public void fillValues(int position, View convertView) {
        TextView macTV = (TextView) convertView.findViewById(R.id.item_mac_tv);
        TextView ipTV = (TextView) convertView.findViewById(R.id.item_ip_tv);
        mName = mDeviceInfos.get(position).getMac();
        mIP = mDeviceInfos.get(position).getIp();
        macTV.setText(mName);
        ipTV.setText(mIP);
    }

    @Override
    public int getCount() {
        return mDeviceInfos.size();
    }

    @Override
    public Object getItem(int position) {
        return mDeviceInfos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mDeviceInfos.get(position).getId();
    }
}
