package com.me.adameastham.smartlab;

//Written by Adam Eastham

public class SmartCupDataModel{
    private String time;
    private int percentage;
    private int temp;

    public SmartCupDataModel(String time, int percentage, int temp) {
        this.time = time;
        this.percentage = percentage;
        this.temp = temp;
    }

    public String getTime() {
        return time;
    }

    public int getPercentage() {
        return percentage;
    }

    public int getTemp() {
        return temp;
    }
}

