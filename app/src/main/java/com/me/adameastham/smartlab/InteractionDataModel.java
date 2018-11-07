package com.me.adameastham.smartlab;

//Written by Inkan Fung

public class InteractionDataModel {
    String time;
    String type;
    String zone;

    String date;
    String hms;


    public InteractionDataModel(String date, String hms, String type, String zone) {
        this.date = date;
        this.hms = hms;
        this.type=type;
        this.zone=zone;
    }


    public String getTime() {
        return time;
    }

    public String getType() {
        return type;
    }

    public String getZone() {
        return zone;
    }


    public String getdate() {
        return date;
    }

    public String gethms() {
        return hms;
    }
}

