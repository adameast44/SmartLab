package com.me.adameastham.smartlab;

//Written by Inkan Fung / Adam Eastham

public class InteractionDataModel extends BaseDataModel{
    private String type;
    private String zone;
    private String date;
    private String hms;


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

