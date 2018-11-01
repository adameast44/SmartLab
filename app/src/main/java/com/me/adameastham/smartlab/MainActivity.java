package com.me.adameastham.smartlab;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import java.io.IOException;

import io.particle.android.sdk.cloud.ApiFactory;
import io.particle.android.sdk.cloud.ParticleCloudSDK;
import io.particle.android.sdk.cloud.ParticleDevice;
import io.particle.android.sdk.cloud.ParticleEvent;
import io.particle.android.sdk.cloud.ParticleEventHandler;
import io.particle.android.sdk.cloud.exceptions.ParticleCloudException;
import io.particle.android.sdk.utils.Async;
import io.particle.android.sdk.utils.Toaster;
import pl.pawelkleczkowski.customgauge.CustomGauge;

public class MainActivity extends AppCompatActivity {

    private TextView txtWifi;

    private CustomGauge zone1G1;
    private CustomGauge zone1G2;
    private CustomGauge zone1G3;
    private TextView txtZ1out;
    private TextView zone1T1;
    private TextView zone1T2;
    private TextView zone1T3;

    private CustomGauge zone2G1;
    private CustomGauge zone2G2;
    private CustomGauge zone2G3;
    private TextView txtZ2out;
    private TextView zone2T1;
    private TextView zone2T2;
    private TextView zone2T3;

    private CustomGauge zone3G1;
    private CustomGauge zone3G2;
    private CustomGauge zone3G3;
    private TextView txtZ3out;
    private TextView zone3T1;
    private TextView zone3T2;
    private TextView zone3T3;

    ParticleDevice myDevice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        boolean isLoggedIn = false;
        txtWifi = findViewById(R.id.txtWifi);

        //Zone 1 UI elements
        txtZ1out = findViewById(R.id.txtZ1Out);
        zone1T1 = findViewById(R.id.zone1T1);
        zone1T2 = findViewById(R.id.zone1T2);
        zone1T3 = findViewById(R.id.zone1T3);
        zone1G1 = findViewById(R.id.zone1G1);
        zone1G2 = findViewById(R.id.zone1G2);
        zone1G3 = findViewById(R.id.zone1G3);
        zone1G1.setEndValue(100);
        zone1G2.setEndValue(100);
        zone1G3.setEndValue(100);

        //Zone 2 UI elements
        txtZ2out = findViewById(R.id.txtZ2Out);
        zone2T1 = findViewById(R.id.zone2T1);
        zone2T2 = findViewById(R.id.zone2T2);
        zone2T3 = findViewById(R.id.zone2T3);
        zone2G1 = findViewById(R.id.zone2G1);
        zone2G2 = findViewById(R.id.zone2G2);
        zone2G3 = findViewById(R.id.zone2G3);
        zone2G1.setEndValue(100);
        zone2G2.setEndValue(100);
        zone2G3.setEndValue(100);

        //Zone 3 UI elements
        txtZ3out = findViewById(R.id.txtZ3Out);
        zone3T1 = findViewById(R.id.zone3T1);
        zone3T2 = findViewById(R.id.zone3T2);
        zone3T3 = findViewById(R.id.zone3T3);
        zone3G1 = findViewById(R.id.zone3G1);
        zone3G2 = findViewById(R.id.zone3G2);
        zone3G3 = findViewById(R.id.zone3G3);
        zone3G1.setEndValue(100);
        zone3G2.setEndValue(100);
        zone3G3.setEndValue(100);

        ParticleCloudSDK.init(this);
        //Login to Particle
        new AsyncTask<Void, Void, String>() {
            protected String doInBackground(Void... params) {
                //aede0f53c224078decdd86191bf1e468ab6e9cd1
                //  LOG IN TO PARTICLE
                try {
                    // Log in to Particle Cloud using username and password
                    //-----------------------------------------------------------ParticleCloudSDK.getCloud().setAccessToken("6be1370a885dc381eede63c12d092a03073c79b2");
                    ParticleCloudSDK.getCloud().logIn("adameastham@hotmail.com", "SmartLab");
                    return "Logged in!";
                } catch (ParticleCloudException e) {
                    Log.e("My app", "Error logging in: " + e.toString());
                    return "Error logging in!";
                }
            }

            protected void onPostExecute(String msg) {
                // Show Toast containing message from doInBackground
                Toaster.l(MainActivity.this, msg);
            }
        }.execute();

        //subscribe to live data events
        new AsyncTask<Void, Void, String>() {
            protected String doInBackground(Void... params) {
                try {
                    // Subscribe to zone 1 event
                    /*long zone1Event = ParticleCloudSDK.getCloud().subscribeToMyDevicesEvents("publishZone1",
                            new ParticleEventHandler() {

                        // Trigger this function when the event is received
                        public void onEvent(String eventName, ParticleEvent event1) {

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    JSONObject data = null;
                                    try {
                                        //parse data to JSON
                                        data = new JSONObject(event1.dataPayload.toString());
                                        //output to gauges
                                        zone1G1.setValue((int)data.getDouble("temp"));
                                        zone1G2.setValue((int)data.getDouble("hum"));
                                        zone1G3.setValue((int)data.getDouble("ambL"));
                                        //output string
                                        txtZ1out.setText("Connected");
                                        zone1T1.setText(String.format("%.2f", data.getDouble("temp"))+"°C");
                                        zone1T2.setText(String.format("%.2f", data.getDouble("hum"))+"%");
                                        zone1T3.setText(String.format("%.2f", data.getDouble("ambL")));

                                    }catch(JSONException e){
                                        e.printStackTrace();
                                    }
                                }
                            });
                        }

                        public void onEventError(Exception e) {
                            Log.e("MyAPP", "Event error: ", e);
                        }
                    });
                    // Subscribe to zone 2 event
                    long zone2Event = ParticleCloudSDK.getCloud().subscribeToMyDevicesEvents("publishZone2", new ParticleEventHandler() {

                        // Trigger this function when the event is received
                        public void onEvent(String eventName, ParticleEvent event2) {

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    JSONObject data = null;
                                    try {
                                        //parse data to JSON
                                        data = new JSONObject(event2.dataPayload.toString());
                                        //output to gauges
                                        zone2G1.setValue((int)data.getDouble("temp"));
                                        zone2G2.setValue((int)data.getDouble("hum"));
                                        zone2G3.setValue((int)data.getDouble("ambL"));
                                        //output string
                                        txtZ2out.setText("Connected");
                                        zone2T1.setText(String.format("%.2f", data.getDouble("temp"))+"°C");
                                        zone2T2.setText(String.format("%.2f", data.getDouble("hum"))+"%");
                                        zone2T3.setText(String.format("%.2f", data.getDouble("ambL")));
                                    }catch(JSONException e){
                                        e.printStackTrace();
                                    }
                                }
                            });
                        }

                        public void onEventError(Exception e) {
                            Log.e("MyAPP", "Event error: ", e);
                        }
                    });
                    // Subscribe to zone 3 event
                    long zone3Event = ParticleCloudSDK.getCloud().subscribeToMyDevicesEvents("publishZone3", new ParticleEventHandler() {

                        // Trigger this function when the event is received
                        public void onEvent(String eventName, ParticleEvent event3) {

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    JSONObject data = null;
                                    try {
                                        //parse data to JSON
                                        data = new JSONObject(event3.dataPayload.toString());
                                        //output to gauges
                                        zone3G1.setValue((int)data.getDouble("temp"));
                                        zone3G2.setValue((int)data.getDouble("hum"));
                                        zone3G3.setValue((int)data.getDouble("ambL"));
                                        //output string
                                        txtZ3out.setText("Connected");
                                        zone3T1.setText(String.format("%.2f", data.getDouble("temp"))+"°C");
                                        zone3T2.setText(String.format("%.2f", data.getDouble("hum"))+"%");
                                        zone3T3.setText(String.format("%.2f", data.getDouble("ambL")));
                                    }catch(JSONException e){
                                        e.printStackTrace();
                                    }
                                }
                            });
                        }

                        public void onEventError(Exception e) {
                            Log.e("MyAPP", "Event error: ", e);
                        }
                    });*/

                    CircularBuffer zone1Wifi = new CircularBuffer(3);
                    CircularBuffer zone2Wifi = new CircularBuffer(3);
                    CircularBuffer zone3Wifi = new CircularBuffer(3);

                    // Subscribe to zone 1 event
                    long zone1WifiEvent = ParticleCloudSDK.getCloud().subscribeToMyDevicesEvents("wifiZone1", new ParticleEventHandler() {

                        // Trigger this function when the event is received
                        public void onEvent(String eventName, ParticleEvent wifiEvent1) {

                            JSONObject data = null;
                            try {
                                int count = 0;
                                boolean found = false;
                                //parse data to JSON
                                data = new JSONObject(wifiEvent1.dataPayload.toString());
                                while(!found){
                                    try {
                                        if (data.getJSONObject(Integer.toString(count)).getString("ssid").equals("Adam's iPhone")) {
                                            found = true;
                                            zone1Wifi.add(data.getJSONObject(Integer.toString(count)).getInt("rssi"));
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                }
                                            });
                                        }
                                        count++;
                                    }catch(JSONException e){
                                        break;
                                    }
                                }
                            }catch(JSONException e){
                                e.printStackTrace();
                            }
                        }

                        public void onEventError(Exception e) {
                            Log.e("MyAPP", "Event error: ", e);
                        }
                    });
                    // Subscribe to zone 2 event
                    long zone2WifiEvent = ParticleCloudSDK.getCloud().subscribeToMyDevicesEvents("wifiZone2", new ParticleEventHandler() {

                        // Trigger this function when the event is received
                        public void onEvent(String eventName, ParticleEvent wifiEvent2) {

                            JSONObject data = null;
                            try {
                                int count = 0;
                                boolean found = false;
                                //parse data to JSON
                                data = new JSONObject(wifiEvent2.dataPayload.toString());
                                while(!found){
                                    try {
                                        if (data.getJSONObject(Integer.toString(count)).getString("ssid").equals("Adam's iPhone")) {
                                            found = true;
                                            zone2Wifi.add(data.getJSONObject(Integer.toString(count)).getInt("rssi"));
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                }
                                            });
                                        }
                                        count++;
                                    }catch(JSONException e){
                                        break;
                                    }
                                }
                            }catch(JSONException e){
                                e.printStackTrace();
                            }
                        }

                        public void onEventError(Exception e) {
                            Log.e("MyAPP", "Event error: ", e);
                        }
                    });
                    // Subscribe to zone 3 event
                    long zone3WifiEvent = ParticleCloudSDK.getCloud().subscribeToMyDevicesEvents("wifiZone3", new ParticleEventHandler() {

                        // Trigger this function when the event is received
                        public void onEvent(String eventName, ParticleEvent wifiEvent3) {

                            JSONObject data = null;
                            try {
                                int count = 0;
                                boolean found = false;
                                //parse data to JSON
                                data = new JSONObject(wifiEvent3.dataPayload.toString());
                                while(!found){
                                    try {
                                        if (data.getJSONObject(Integer.toString(count)).getString("ssid").equals("Adam's iPhone")) {
                                            found = true;
                                            zone3Wifi.add(data.getJSONObject(Integer.toString(count)).getInt("rssi"));

                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    switch (getNearestZone(zone1Wifi, zone2Wifi, zone3Wifi)){
                                                        case 1:
                                                            txtWifi.setText("In zone 1");
                                                            break;
                                                        case 2:
                                                            txtWifi.setText("In Zone 2");
                                                            break;
                                                        case 3:
                                                            txtWifi.setText("In Zone 3");
                                                            break;
                                                        default:
                                                            txtWifi.setText("Unable to locate!");
                                                    }
                                                }
                                            });
                                        }
                                        count++;
                                    }catch(JSONException e){
                                        break;
                                    }
                                }
                            }catch(JSONException e){
                                e.printStackTrace();
                            }
                        }

                        public void onEventError(Exception e) {
                            Log.e("MyAPP", "Event error: ", e);
                        }
                    });

                    return "Subscribed to data!";
                } catch (IOException e) {
                    //error subscribing
                    Log.e("MyAPP", e.toString());
                    return "Error subscribing!";
                }
            }
        // This code is run after the doInBackground code finishes
        protected void onPostExecute(String msg) {
                //wait for previous toast to finish
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Toaster.s(MainActivity.this, msg);
                }
            }, 2000);
        }
        }.execute();
    }

    private int getNearestZone(CircularBuffer zone1Wifi, CircularBuffer zone2Wifi, CircularBuffer zone3Wifi){
        double dist1 = getAverageDist(zone1Wifi);
        double dist2 = getAverageDist(zone2Wifi);
        double dist3 = getAverageDist(zone3Wifi);

        if(dist1>dist2&&dist1>dist3){
            return 1;
        }
        else if(dist2>dist1&&dist2>dist3){
            return 2;
        }
        else if(dist3>dist2&&dist3>dist1){
            return 3;
        }
        else{
            return 4;
        }

    }

    private double getAverageDist(CircularBuffer buffer){
        double total = 0;
        for (int i=0; i<buffer.size(); i++){
            total+=buffer.peak(i);
        }
        return total/buffer.size();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Add items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        Intent intent;

        //change activity
        switch ((String)item.getTitle()){
            case "Zone 1":
                intent = new Intent(MainActivity.this,Zone1.class);
                MainActivity.this.startActivity(intent);
                break;
            case "Zone 2":
                intent = new Intent(MainActivity.this,Zone2.class);
                MainActivity.this.startActivity(intent);
                break;
            case "Zone 3":
                intent = new Intent(MainActivity.this,Zone3.class);
                MainActivity.this.startActivity(intent);
                break;
            case "Interactions":
                intent = new Intent(MainActivity.this,Interactions.class);
                MainActivity.this.startActivity(intent);
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
