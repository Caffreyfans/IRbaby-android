package top.caffreyfans.irbaby.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import top.caffreyfans.irbaby.R;

public class InfoAdapter extends BaseAdapter {

    private List<String[]> mList;
    private LayoutInflater mInflater;

    public InfoAdapter(Context context, List<String[]> list) {
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mList = list;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = mInflater.inflate(R.layout.info_list_item, null);
        TextView key_tv = (TextView) v.findViewById(R.id.key_tv);
        TextView value_tv = (TextView) v.findViewById(R.id.value_tv);
        String key = mList.get(position)[0];
        String value = mList.get(position)[1];
        key_tv.setText(key);
        value_tv.setText(value);
        return v;
    }
}
