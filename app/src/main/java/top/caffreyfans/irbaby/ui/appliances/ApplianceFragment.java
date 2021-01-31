package top.caffreyfans.irbaby.ui.appliances;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.daimajia.swipe.util.Attributes;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import org.litepal.LitePal;
import java.util.List;
import top.caffreyfans.irbaby.R;
import top.caffreyfans.irbaby.adapter.ApplianceListAdapter;
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
//                Snackbar.make(view, "In ApplianceFragment", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
                Intent intent = new Intent(getContext(), DeviceSelectActivity.class);
                startActivity(intent);
            }
        });

        return root;
    }

    @Override
    public void onResume() {
        List<ApplianceInfo> applianceInfos = LitePal.findAll(ApplianceInfo.class);
        mApplianceAdapter = new ApplianceListAdapter(mContext, applianceInfos);
        mListView.setAdapter(mApplianceAdapter);
        super.onResume();
    }
}