package com.me.adameastham.smartlab;

//written by Adam Eastham

public abstract class BaseDataModel {
    String date;
    String hms;

    public BaseDataModel(String date, String hms) {
        this.date = date;
        this.hms = hms;
    }

    public String getdate() {
        return date;
    }

    public String gethms() {
        return hms;
    }
}
