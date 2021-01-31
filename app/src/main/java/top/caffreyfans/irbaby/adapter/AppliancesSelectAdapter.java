package top.caffreyfans.irbaby.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import top.caffreyfans.irbaby.R;

public class AppliancesSelectAdapter extends BaseAdapter {

    private List<String> mStringList;
    private LayoutInflater mInflater;
    private final String TAG = AppliancesSelectAdapter.class.getSimpleName();

    public AppliancesSelectAdapter(Context context, List<String> list) {
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mStringList = list;
    }

    @Override
    public int getCount() {
        return mStringList.size();
    }

    @Override
    public Object getItem(int position) {
        return mStringList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = mInflater.inflate(R.layout.appliance_select_item, null);
        TextView textView = (TextView) v.findViewById(R.id.item_ip_tv);
        textView.setText(mStringList.get(position));
        return v;
    }
}
