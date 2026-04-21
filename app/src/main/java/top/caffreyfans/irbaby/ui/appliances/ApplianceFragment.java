package top.caffreyfans.irbaby.ui.appliances;

import android.content.DialogInterface;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.daimajia.swipe.util.Attributes;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import org.litepal.LitePal;
import java.util.List;
import top.caffreyfans.irbaby.R;
import top.caffreyfans.irbaby.adapter.ApplianceListAdapter;
import top.caffreyfans.irbaby.firmware_api.PhoneIrApi;
import top.caffreyfans.irbaby.helper.ApplianceContract;
import top.caffreyfans.irbaby.model.ApplianceInfo;
import top.caffreyfans.irbaby.ui.devices.DeviceSelectActivity;

public class ApplianceFragment extends Fragment {
    private ListView mListView;
    private ApplianceListAdapter mApplianceAdapter;
    private View root;
    private Context mContext;

    private final String TAG = ApplianceFragment.class.getSimpleName();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        root = inflater.inflate(R.layout.fragment_appliance, container, false);
        mContext = getContext();

        mListView = (ListView) root.findViewById(R.id.appliance_lv);

        /* fab settings */
        FloatingActionButton fab = root.findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (PhoneIrApi.hasIrEmitter(mContext)) {
                    showAddApplianceMethodDialog();
                    return;
                }
                openIrbabyDeviceSelection();
            }
        });

        return root;
    }

    private void showAddApplianceMethodDialog() {
        if (getContext() == null) {
            return;
        }
        CharSequence[] options = new CharSequence[] {
                getString(R.string.add_appliance_irbaby_device),
                getString(R.string.add_appliance_phone_ir)
        };
        new AlertDialog.Builder(getContext())
                .setTitle(R.string.add_appliance_method_title)
                .setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            openIrbabyDeviceSelection();
                        } else {
                            openPhoneIrSelection();
                        }
                    }
                })
                .show();
    }

    private void openIrbabyDeviceSelection() {
        Intent intent = new Intent(getContext(), DeviceSelectActivity.class);
        startActivity(intent);
    }

    private void openPhoneIrSelection() {
        ApplianceInfo applianceInfo = new ApplianceInfo();
        Intent intent = new Intent(getContext(), ApplianceSelectActivity.class);
        intent.putExtra(ApplianceContract.Select.TITLE, getString(R.string.select_category));
        intent.putExtra(ApplianceContract.Select.CONTENT_ID, net.irext.webapi.utils.Constants.ContentID.LIST_CATEGORIES);
        intent.putExtra(ApplianceContract.Select.APPLIANCE_INFO, applianceInfo);
        startActivity(intent);
    }

    @Override
    public void onResume() {
        List<ApplianceInfo> applianceInfos = LitePal.findAll(ApplianceInfo.class);
        mApplianceAdapter = new ApplianceListAdapter(mContext, applianceInfos);
        mListView.setAdapter(mApplianceAdapter);
        super.onResume();
    }
}