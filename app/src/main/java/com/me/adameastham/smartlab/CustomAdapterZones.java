package com.me.adameastham.smartlab;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;



/**
 * Created by user on 10/31/2018.
 * this file is created to customise the Listview in an custom adapter
 */

public class CustomAdapterZones extends ArrayAdapter<ZoneDataModel> implements View.OnClickListener{

    private ArrayList<ZoneDataModel> dataSet;
    Context mContext;

    // View lookup cache
    private static class ViewHolder {
        TextView txttime;
        TextView txttemp;
        TextView txtambientlight;
        TextView txthumidity;
    }

    public CustomAdapterZones(ArrayList<ZoneDataModel> data, Context context) {
        super(context, R.layout.list_item_layout, data);
        this.dataSet = data;
        this.mContext=context;

    }

    @Override
    public void onClick(View v) {
    }

    private int lastPosition = -1;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        ZoneDataModel zoneDataModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.list_item_layout, parent, false);
            viewHolder.txttime = (TextView) convertView.findViewById(R.id.time);
            viewHolder.txttemp = (TextView) convertView.findViewById(R.id.temp);
            viewHolder.txtambientlight = (TextView) convertView.findViewById(R.id.light);
            viewHolder.txthumidity = (TextView) convertView.findViewById(R.id.humidity);

            result=convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }

        Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        result.startAnimation(animation);
        lastPosition = position;

        viewHolder.txttime.setText(zoneDataModel.getdate() + ("\n") + zoneDataModel.gethms());
        viewHolder.txttemp.setText(zoneDataModel.getTemp());
        viewHolder.txtambientlight.setText(zoneDataModel.getAmbientLight());
        viewHolder.txthumidity.setText(zoneDataModel.getHumidity());
        // Return the completed view to render on screen
        return convertView;
    }
}