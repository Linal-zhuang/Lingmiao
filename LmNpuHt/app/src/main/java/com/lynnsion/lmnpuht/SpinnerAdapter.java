package com.lynnsion.lmnpuht;

/**
 * Created by wizrobo on 11/16/17.
 */
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class SpinnerAdapter extends BaseAdapter {

    private Context mContext;
    private  ArrayList values=new ArrayList();


    public SpinnerAdapter(Context mContext,String mode) {
        this.mContext = mContext;

        Resources res = mContext.getResources();
        values.clear();
        //if(WizRoboNpu.isNavi)
        if(mode.equals("ActionList"))
        {
            this.values=SetTask.ActionList;
        }
        if(mode.equals("StationList")) {
            this.values = SetTask.StationList;
        }
        if(mode.equals("PathList")) {
            this.values = SetTask.PathList;
        }

        if(mode.equals("OthersList")) {
            this.values = SetTask.OthersList;
        }

    }

    @Override
    public int getCount() {
        return values.size();
    }

    @Override
    public Object getItem(int position) {
        return values.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    private class ViewHolder {
        TextView spinner_value;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.spinner_value, null);
            holder = new ViewHolder();
            holder.spinner_value = (TextView) convertView
                    .findViewById(R.id.spinner_value);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        String value = values.get(position).toString();
        holder.spinner_value.setText(value);
        if ("���ϸ�".equals(value.trim())) {
            Log.d("CheckList", "alsdjflaskdjf");
            holder.spinner_value.setTextColor(Color.RED);
        } else if ("����ȱ��".equals(value)) {
            holder.spinner_value.setTextColor(Color.GREEN);
        } else {
            holder.spinner_value.setBackgroundColor(Color.WHITE);
            holder.spinner_value.setTextColor(Color.BLACK);
        }
        return convertView;
    }

}
