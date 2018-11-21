package com.me.adameastham.smartlab;

//Written by Adam Eastham

public class SmartCupDataModel{
    private String time;
    private int percantage;

    public SmartCupDataModel(String time, int percentage) {
        this.time = time;
        this.percantage = percentage;
    }

    public String getTime() {
        return time;
    }

    public int getPercentage() {
        return percantage;
    }

}

