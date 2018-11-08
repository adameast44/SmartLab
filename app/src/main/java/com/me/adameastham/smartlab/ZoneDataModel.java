package com.me.adameastham.smartlab;

//Written by Inkan Fung

public class ZoneDataModel {
   String Time;
    String AmbientLight;
    String Humidity;
    String Temp;

    String date;
    String hms;


    public ZoneDataModel(String date, String hms, String AmbientLight, String Humidity, String Temp) {
        this.date = date;
        this.hms = hms;
        this.AmbientLight=AmbientLight;
        this.Humidity=Humidity;
        this.Temp=Temp;
    }


    public String getTime() {
        return Time;
    }

    public String getAmbientLight() {
        return AmbientLight;
    }

    public String getHumidity() {
        return Humidity;
    }

    public String getTemp() {
        return Temp;
    }

    public String getdate() {

        return date;

    }

    public String gethms() {

        return hms;
    }
}

