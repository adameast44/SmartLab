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

//Written by Adam Eastham

public class customAdapterInteractions extends ArrayAdapter<InteractionDataModel> implements View.OnClickListener{

    private ArrayList<InteractionDataModel> dataSet;
    Context mContext;

    // View lookup cache
    private static class ViewHolder {
        TextView txtTime;
        TextView txtType;
        TextView txtZone;
    }

    public customAdapterInteractions(ArrayList<InteractionDataModel> data, Context context) {
        super(context, R.layout.list_item_layout_interactions, data);
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
        InteractionDataModel interactionDataModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.list_item_layout_interactions, parent, false);
            viewHolder.txtTime = (TextView) convertView.findViewById(R.id.time);
            viewHolder.txtType = (TextView) convertView.findViewById(R.id.type);
            viewHolder.txtZone = (TextView) convertView.findViewById(R.id.zone);

            result=convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }

        Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        result.startAnimation(animation);
        lastPosition = position;

        viewHolder.txtTime.setText(interactionDataModel.getdate() + ("\n") + interactionDataModel.gethms());
        viewHolder.txtType.setText(interactionDataModel.getType());
        viewHolder.txtZone.setText(interactionDataModel.getZone());
        // Return the completed view to render on screen
        return convertView;
    }
}