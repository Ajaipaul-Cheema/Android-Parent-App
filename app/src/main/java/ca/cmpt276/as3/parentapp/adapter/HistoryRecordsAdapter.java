package ca.cmpt276.as3.parentapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ca.cmpt276.as3.parentapp.R;
import ca.cmpt276.as3.parentapp.data.FlipRecord;

public class HistoryRecordsAdapter extends BaseAdapter {
    private Context context;
    private List<FlipRecord> list = new ArrayList<>();

    public HistoryRecordsAdapter(Context context,List<FlipRecord> objects){
        super();
        this.context = context;
        this.list = objects;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return list == null ? 0 : list.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        LayoutInflater inflater = LayoutInflater.from(context);
        view = inflater.inflate(R.layout.list_item, null);
        FlipRecord data = (FlipRecord) getItem(position);
        TextView tv_childname = (TextView) view.findViewById(R.id.tv_name);
        tv_childname.setText(data.getChildname());
        TextView tv_time = (TextView) view.findViewById(R.id.tv_time);
        tv_time.setText(data.getTime());
        TextView tv_rs = (TextView) view.findViewById(R.id.tv_rs);
        if(data.getIswin().equals("y")) {
            tv_rs.setText("Won");
        }else{
            tv_rs.setText("X");
        }
        return view;
    }
}
