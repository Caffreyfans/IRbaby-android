package top.caffreyfans.irbaby.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.BaseSwipeAdapter;
import net.irext.webapi.utils.Constants.CategoryID;
import org.litepal.LitePal;
import java.io.Serializable;
import java.util.List;
import top.caffreyfans.irbaby.R;
import top.caffreyfans.irbaby.helper.ApplianceContract;
import top.caffreyfans.irbaby.model.ApplianceInfo;
import top.caffreyfans.irbaby.ui.appliances.ACControlActivity;

public class ApplianceListAdapter extends BaseSwipeAdapter {
    private final String TAG = ApplianceListAdapter.class.getSimpleName();
    private List<ApplianceInfo> mAppliancesInfoList;
    private Context mContext;
    public ApplianceListAdapter(Context context, List<ApplianceInfo> applianceInfos) {

        mContext = context;
        mAppliancesInfoList = applianceInfos;
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.swipe;
    }

    @Override
    public View generateView(int position, ViewGroup parent) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.appliance_list_item, null);
        return v;
    }

    @Override
    public void fillValues(final int position, View convertView) {

        TextView textView = (TextView) convertView.findViewById(R.id.item_ip_tv);
        ImageView imageView = (ImageView) convertView.findViewById(R.id.item_icon_img);
        textView.setText(mAppliancesInfoList.get(position).getName());
        int i = mAppliancesInfoList.get(position).getCategory() - 1;
        CategoryID categoryID = CategoryID.values()[i];
        switch (categoryID) {
            case AIR_CONDITIONER:
                imageView.setImageResource(R.drawable.ic_air_conditioner);
                break;

            case TV:
                imageView.setImageResource(R.drawable.ic_tv);
                break;

            case FAN:
                imageView.setImageResource(R.drawable.ic_fan);
                break;
        }
        SwipeLayout swipeLayout = (SwipeLayout) convertView.findViewById(R.id.swipe);
        swipeLayout.getSurfaceView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ACControlActivity.class);
                intent.putExtra(ApplianceContract.Control.APPLIANCE_INFO, (Serializable) mAppliancesInfoList.get(position));
                mContext.startActivity(intent);
            }
        });

        ImageButton imageButton = convertView.findViewById(R.id.trash);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LitePal.delete(ApplianceInfo.class, mAppliancesInfoList.get(position).getId());
                mAppliancesInfoList.remove(position);
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getCount() {
        return mAppliancesInfoList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}