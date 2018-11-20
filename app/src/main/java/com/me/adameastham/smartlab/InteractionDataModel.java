package com.me.adameastham.smartlab;

//Written by Inkan Fung / Adam Eastham

public class InteractionDataModel extends BaseDataModel{
    String type;
    String zone;

    String date;
    String hms;


    public InteractionDataModel(String date, String hms, String type, String zone) {
        super(date, hms);
        this.type=type;
        this.zone=zone;
    }

    public String getType() {
        return type;
    }

    public String getZone() {
        return zone;
    }
}

