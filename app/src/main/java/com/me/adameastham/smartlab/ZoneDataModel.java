package com.me.adameastham.smartlab;

//Written by Inkan Fung / Adam Eastham

public class ZoneDataModel extends BaseDataModel{
    private String ambientLight;
    private String humidity;
    private String temp;

    public ZoneDataModel(String date, String hms, String AmbientLight, String Humidity, String Temp) {
        super(date, hms);
        this.ambientLight =AmbientLight;
        this.humidity=Humidity;
        this.temp=Temp;
    }

    public String getAmbientLight() {
        return ambientLight;
    }

    public String getHumidity() {
        return humidity;
    }

    public String getTemp() {
        return temp;
    }

}

