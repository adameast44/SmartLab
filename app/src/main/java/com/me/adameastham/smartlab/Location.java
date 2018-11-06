package com.me.adameastham.smartlab;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.ToggleButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import io.particle.android.sdk.cloud.ParticleCloud;
import io.particle.android.sdk.cloud.ParticleCloudSDK;
import io.particle.android.sdk.cloud.ParticleEvent;
import io.particle.android.sdk.cloud.ParticleEventHandler;
import io.particle.android.sdk.cloud.exceptions.ParticleCloudException;
import io.particle.android.sdk.utils.Toaster;

public class Location extends AppCompatActivity {

    private TextView txtWifi;
    private TextView txtEnterName;
    private EditText txtHotspotName;
    private ViewGroup transContainer;


    private String hotspotName;
    private ToggleButton togButton;
    private boolean isMonitoring = false;
    private AsyncTask<Void, Void, String> task1;
    private AsyncTask<Void, Void, String> task2;
    private AsyncTask<Void, Void, String> task3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        txtWifi = findViewById(R.id.txtWifi);
        txtEnterName = findViewById(R.id.txtEnterName);
        txtHotspotName = findViewById(R.id.txtHotspotName);
        transContainer = findViewById(R.id.container);

        togButton = (ToggleButton) findViewById(R.id.togButton);
        togButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    isMonitoring = true;
                    if (!txtHotspotName.getText().toString().equals("")) {
                        hotspotName = txtHotspotName.getText().toString();
                        TransitionManager.beginDelayedTransition(transContainer);
                        txtHotspotName.setVisibility(View.GONE);
                        txtEnterName.setVisibility(View.GONE);
                        task1.execute();
                        //task2.execute();

                    }
                    else {
                        togButton.setChecked(false);
                    }
                } else {
                    isMonitoring = false;
                    TransitionManager.beginDelayedTransition(transContainer);
                    txtHotspotName.setVisibility(View.VISIBLE);
                    txtEnterName.setVisibility(View.VISIBLE);
                    task1.cancel(true);
                    //task2.cancel(true);
                    //task3.cancel(true);

                }
            }
        });
        togButton.setChecked(false);

        CircularBuffer zone1Wifi = new CircularBuffer(3);
        CircularBuffer zone2Wifi = new CircularBuffer(3);
        CircularBuffer zone3Wifi = new CircularBuffer(3);

        /*task1 = new AsyncTask<Void, Void, String>() {

            @Override
            protected String doInBackground(Void... voids) {
                try {
                    // Subscribe to zone 1 event
                    long event1 = ParticleCloudSDK.getCloud().subscribeToMyDevicesEvents("wifiZone1", new ParticleEventHandler() {

                        // Trigger this function when the event is received
                        public void onEvent(String eventName, ParticleEvent wifiEvent1) {

                            JSONObject data = null;
                            try {
                                int count = 0;
                                boolean found = false;
                                //parse data to JSON
                                data = new JSONObject(wifiEvent1.dataPayload.toString());
                                while (!found) {
                                    try {
                                        if (data.getJSONObject(Integer.toString(count)).getString("ssid").equals(hotspotName)) {
                                            found = true;
                                            zone1Wifi.add(data.getJSONObject(Integer.toString(count)).getInt("rssi"));
                                        }
                                        count++;
                                    } catch (JSONException e) {
                                        break;
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        public void onEventError(Exception e) {
                            Log.e("MyAPP", "Event error: ", e);
                        }
                    });

                    // Subscribe to zone 2 event
                    long event2 = ParticleCloudSDK.getCloud().subscribeToMyDevicesEvents("wifiZone2", new ParticleEventHandler() {

                        // Trigger this function when the event is received
                        public void onEvent(String eventName, ParticleEvent wifiEvent2) {

                            JSONObject data = null;
                            try {
                                int count = 0;
                                boolean found = false;
                                //parse data to JSON
                                data = new JSONObject(wifiEvent2.dataPayload.toString());
                                while (!found) {
                                    try {
                                        if (data.getJSONObject(Integer.toString(count)).getString("ssid").equals(hotspotName)) {
                                            found = true;
                                            zone2Wifi.add(data.getJSONObject(Integer.toString(count)).getInt("rssi"));
                                        }
                                        count++;
                                    } catch (JSONException e) {
                                        break;
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        public void onEventError(Exception e) {
                            Log.e("MyAPP", "Event error: ", e);
                        }
                    });

                    // Subscribe to zone 3 event
                    long event3 = ParticleCloudSDK.getCloud().subscribeToMyDevicesEvents("wifiZone3", new ParticleEventHandler() {

                        // Trigger this function when the event is received
                        public void onEvent(String eventName, ParticleEvent wifiEvent3) {

                            JSONObject data = null;
                            try {
                                int count = 0;
                                boolean found = false;
                                //parse data to JSON
                                data = new JSONObject(wifiEvent3.dataPayload.toString());
                                while (!found) {
                                    try {
                                        if (data.getJSONObject(Integer.toString(count)).getString("ssid").equals(hotspotName)) {
                                            found = true;
                                            zone3Wifi.add(data.getJSONObject(Integer.toString(count)).getInt("rssi"));

                                            if (isMonitoring) {
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        switch (getNearestZone(zone1Wifi, zone2Wifi, zone3Wifi)) {
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
                                        }
                                        count++;
                                    } catch (JSONException e) {
                                        break;
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        public void onEventError(Exception e) {
                            Log.e("MyAPP", "Event error: ", e);
                        }
                    });

                    return "Subscribed to Wifi locations";
                } catch (IOException e) {
                    //error subscribing
                    Log.e("MyAPP", e.toString());
                    return "Error subscribing!";
                }
            }
            // This code is run after the doInBackground code finishes
            protected void onPostExecute (String msg){
                //wait for previous toast to finish
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Toaster.s(Location.this, msg);
                    }
                }, 2000);
            }
        };
*/
        task1 = new AsyncTask<Void, Void, String>() {

            @Override
            protected String doInBackground(Void... voids) {
                try {
                    // Subscribe to zone 1 event
                    long event1 = ParticleCloudSDK.getCloud().subscribeToAllEvents(null, new ParticleEventHandler() {

                        // Trigger this function when the event is received
                        public void onEvent(String eventName, ParticleEvent wifiEvent) {

                            JSONObject data = null;
                            try {
                                int count = 0;
                                boolean found = false;
                                //parse data to JSON
                                data = new JSONObject(wifiEvent.dataPayload.toString());
                                while (!found) {
                                    try {
                                        if (data.getJSONObject(Integer.toString(count)).getString("ssid").equals(hotspotName)) {
                                            found = true;
                                            switch (eventName){
                                                case "wifiZone1":
                                                    zone1Wifi.add(data.getJSONObject(Integer.toString(count)).getInt("rssi"));
                                                    break;
                                                case "wifiZone2":
                                                    zone2Wifi.add(data.getJSONObject(Integer.toString(count)).getInt("rssi"));
                                                    break;
                                                case "wifiZone3":
                                                    zone3Wifi.add(data.getJSONObject(Integer.toString(count)).getInt("rssi"));
                                                    break;
                                            }
                                            if (isMonitoring) {
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        switch (getNearestZone(zone1Wifi, zone2Wifi, zone3Wifi)) {
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
                                        }
                                        count++;
                                    } catch (JSONException e) {
                                        break;
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        public void onEventError(Exception e) {
                            Log.e("MyAPP", "Event error: ", e);
                        }
                    });

                    return "Subscribed to Wifi location 1";
                } catch (IOException e) {
                    //error subscribing
                    Log.e("MyAPP", e.toString());
                    return "Error subscribing!";
                }
            }
            // This code is run after the doInBackground code finishes
            protected void onPostExecute (String msg){
                Toaster.s(Location.this, msg);
            }
        };

        task2 = new AsyncTask<Void, Void, String>() {

            @Override
            protected String doInBackground(Void... voids) {
                try {
                    // Subscribe to zone 2 event
                    long event1 = ParticleCloudSDK.getCloud().subscribeToMyDevicesEvents("wifiZone1", new ParticleEventHandler() {

                        // Trigger this function when the event is received
                        public void onEvent(String eventName, ParticleEvent wifiEvent2) {

                            JSONObject data = null;
                            try {
                                int count = 0;
                                boolean found = false;
                                //parse data to JSON
                                data = new JSONObject(wifiEvent2.dataPayload.toString());
                                while (!found) {
                                    try {
                                        if (data.getJSONObject(Integer.toString(count)).getString("ssid").equals(hotspotName)) {
                                            found = true;
                                            zone1Wifi.add(data.getJSONObject(Integer.toString(count)).getInt("rssi"));
                                        }
                                        count++;
                                    } catch (JSONException e) {
                                        break;
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        public void onEventError(Exception e) {
                            Log.e("MyAPP", "Event error: ", e);
                        }
                    });

                    // Subscribe to zone 2 event
                    long event2 = ParticleCloudSDK.getCloud().subscribeToMyDevicesEvents("wifiZone2", new ParticleEventHandler() {

                        // Trigger this function when the event is received
                        public void onEvent(String eventName, ParticleEvent wifiEvent2) {

                            JSONObject data = null;
                            try {
                                int count = 0;
                                boolean found = false;
                                //parse data to JSON
                                data = new JSONObject(wifiEvent2.dataPayload.toString());
                                while (!found) {
                                    try {
                                        if (data.getJSONObject(Integer.toString(count)).getString("ssid").equals(hotspotName)) {
                                            found = true;
                                            zone2Wifi.add(data.getJSONObject(Integer.toString(count)).getInt("rssi"));
                                        }
                                        count++;
                                    } catch (JSONException e) {
                                        break;
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        public void onEventError(Exception e) {
                            Log.e("MyAPP", "Event error: ", e);
                        }
                    });

                    // Subscribe to zone 3 event
                    long event3 = ParticleCloudSDK.getCloud().subscribeToMyDevicesEvents("wifiZone1", new ParticleEventHandler() {

                        // Trigger this function when the event is received
                        public void onEvent(String eventName, ParticleEvent wifiEvent2) {

                            JSONObject data = null;
                            try {
                                int count = 0;
                                boolean found = false;
                                //parse data to JSON
                                data = new JSONObject(wifiEvent2.dataPayload.toString());
                                while (!found) {
                                    try {
                                        if (data.getJSONObject(Integer.toString(count)).getString("ssid").equals(hotspotName)) {
                                            found = true;
                                            zone3Wifi.add(data.getJSONObject(Integer.toString(count)).getInt("rssi"));
                                            if (isMonitoring) {
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        switch (getNearestZone(zone1Wifi, zone2Wifi, zone3Wifi)) {
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
                                        }
                                        count++;
                                    } catch (JSONException e) {
                                        break;
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        public void onEventError(Exception e) {
                            Log.e("MyAPP", "Event error: ", e);
                        }
                    });

                    return "Subscribed to Wifi location 2";
                } catch (IOException e) {
                    //error subscribing
                    Log.e("MyAPP", e.toString());
                    return "Error subscribing!";
                }
            }
            // This code is run after the doInBackground code finishes
            protected void onPostExecute (String msg){
                //wait for previous toast to finish
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Toaster.s(Location.this, msg);
                    }
                }, 2000);
            }
        };
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
}
