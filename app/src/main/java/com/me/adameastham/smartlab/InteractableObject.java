package com.me.adameastham.smartlab;

import android.support.v4.content.ContextCompat;
import android.widget.ImageView;

public class InteractableObject {
    String name;
    ImageView imageView;

    InteractableObject(String name, ImageView iv){
        this.name = name;
        this.imageView = iv;
    }

    void setLight(boolean state){
        if(state){
            imageView.setColorFilter(ContextCompat.getColor(imageView.getContext(), R.color.md_green_400));
        } else {
            imageView.clearColorFilter();
        }
    }

    public String getName() {
        return name;
    }

    public ImageView getImageView() {
        return imageView;
    }
}