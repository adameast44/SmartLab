package com.me.adameastham.smartlab;

//Written by Adam Eastham

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;

import io.particle.android.sdk.cloud.ParticleCloudSDK;
import io.particle.android.sdk.cloud.ParticleEvent;
import io.particle.android.sdk.cloud.ParticleEventHandler;
import io.particle.android.sdk.cloud.ParticleEventVisibility;
import io.particle.android.sdk.cloud.exceptions.ParticleCloudException;
import io.particle.android.sdk.utils.Toaster;
import pl.pawelkleczkowski.customgauge.CustomGauge;

public class MainActivity extends AppCompatActivity {

    private TextView txtWifi;
    private String hotSpotName;
    private ViewGroup transContainer;

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

    private TextView txtEnterName;
    private EditText txtHotspotName;
    private ToggleButton togButton;
    private ToggleButton togStartStop;


    CircularBuffer zone1Wifi = new CircularBuffer(5);
    CircularBuffer zone2Wifi = new CircularBuffer(5);
    CircularBuffer zone3Wifi = new CircularBuffer(5);

    String hotspotName;
    private Date timeEntered = Calendar.getInstance().getTime(); //time user entered a zone
    public static final SimpleDateFormat df = new SimpleDateFormat("dd/MM/yy - HH:mm:ss");

    private AsyncTask<Void, Void, String> dataThread;
    private AsyncTask<Void, Void, String> wifiThread;

    @SuppressLint("StaticFieldLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        boolean isLoggedIn = false;
        txtWifi = findViewById(R.id.txtWifi);
        transContainer = findViewById(R.id.container);
        txtEnterName = findViewById(R.id.txtEnterName);
        txtHotspotName = findViewById(R.id.txtHotspotName);

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


        transContainer.setVisibility(View.GONE);

        Random rand = new Random();

        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("locationData/");
        HashMap<String, String> map = new HashMap<>();
        map.put("ts",df.format(Calendar.getInstance().getTime()));
        map.put("Location", "Zone 2");
        map.put("HotSpotName", "Adam's iPhone");
        myRef.push().setValue(map.toString());

        togStartStop = (ToggleButton) findViewById(R.id.togStartStop);
        togButton = (ToggleButton) findViewById(R.id.togButton);
        togButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    transContainer.setVisibility(View.GONE);
                    TransitionManager.beginDelayedTransition(transContainer);
                    txtWifi.setVisibility(View.VISIBLE);
                    txtHotspotName.setVisibility(View.VISIBLE);
                    txtEnterName.setVisibility(View.VISIBLE);
                    togStartStop.setVisibility(View.VISIBLE);
                } else {
                    TransitionManager.beginDelayedTransition(transContainer);
                    txtWifi.setVisibility(View.GONE);
                    txtHotspotName.setVisibility(View.GONE);
                    txtEnterName.setVisibility(View.GONE);
                    togStartStop.setVisibility(View.GONE);
                    transContainer.setVisibility(View.VISIBLE);

                }
            }

        });
        togButton.setChecked(true);

        togStartStop.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (!txtHotspotName.getText().toString().equals("")) {
                        hotspotName = txtHotspotName.getText().toString();
                        TransitionManager.beginDelayedTransition(transContainer);
                        togButton.setVisibility(View.GONE);
                        TransitionManager.beginDelayedTransition(transContainer);
                        txtHotspotName.setVisibility(View.GONE);
                        TransitionManager.beginDelayedTransition(transContainer);
                        txtEnterName.setVisibility(View.GONE);
                    }
                    else {
                        togStartStop.setChecked(false);
                        Toaster.s(MainActivity.this, "Please Enter a Hotspot Name");
                    }
                } else {
                    TransitionManager.beginDelayedTransition(transContainer);
                    togButton.setVisibility(View.VISIBLE);
                    TransitionManager.beginDelayedTransition(transContainer);
                    txtHotspotName.setVisibility(View.VISIBLE);
                    TransitionManager.beginDelayedTransition(transContainer);
                    txtEnterName.setVisibility(View.VISIBLE);
                    TransitionManager.beginDelayedTransition(transContainer);
                }
            }

        });
        togStartStop.setChecked(false);

        ParticleCloudSDK.init(this);
        //Login to Particle
         new AsyncTask<Void, Void, String>() {
            protected String doInBackground(Void... params) {
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

        new AsyncTask<Void, Void, String>() {

            @Override
            protected String doInBackground(Void... voids) {
                try {
                    // Subscribe to zone 1 event
                    long event1 = ParticleCloudSDK.getCloud().subscribeToMyDevicesEvents("wifiData", new ParticleEventHandler() {

                        // Trigger this function when the event is received
                        public void onEvent(String eventName, ParticleEvent wifiEvent1) {

                            JSONObject data = null;
                            try {
                                int count = 0;
                                boolean found = false;
                                //parse data to JSON
                                data = new JSONObject(wifiEvent1.dataPayload.toString());
                                String zone = data.getString("zone");
                                while (!found) {
                                    try {
                                        if (data.getJSONObject(Integer.toString(count)).getString("ssid").equals(hotspotName)) {
                                            found = true;
                                            int rssi = data.getJSONObject(Integer.toString(count)).getInt("rssi");
                                            if(zone.equals("Zone1")) {
                                                zone1Wifi.add(rssi);
                                            } else if(zone.equals("Zone2")) {
                                                zone2Wifi.add(rssi);
                                            } else if(zone.equals("Zone3")) {
                                                zone3Wifi.add(rssi);
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
                        Toaster.s(MainActivity.this, msg);
                    }
                }, 2000);
            }
        }.execute();

        new AsyncTask<Void, Void, String>() {

            @Override
            protected String doInBackground(Void... voids) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                switch (getNearestZone(zone1Wifi, zone2Wifi, zone3Wifi)) {
                                    case 1:
                                        txtWifi.setText("In Zone 1");
                                        break;
                                    case 2:
                                        txtWifi.setText("In Zone 2");
                                        break;
                                    case 3:
                                        txtWifi.setText("In Zone 3");
                                        break;
                                    default:
                                        txtWifi.setText("Unable to Locate");
                                }
                            }
                        });
                    }
                }, 2000);
                return "Finished";
            }

            // This code is run after the doInBackground code finishes
            protected void onPostExecute(String msg) {
                //wait for previous toast to finish
            }
        };

        dataThread = new AsyncTask<Void, Void, String>() {
            protected String doInBackground(Void... params) {
                try {
                    // Subscribe to zone 1 event
                    long zone1Event = ParticleCloudSDK.getCloud().subscribeToMyDevicesEvents("publishZone1",
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
                                                zone1G1.setValue((int) data.getDouble("temp"));
                                                zone1G2.setValue((int) data.getDouble("hum"));
                                                zone1G3.setValue((int) data.getDouble("ambL"));
                                                //output string
                                                txtZ1out.setText("Connected");
                                                zone1T1.setText(String.format("%.2f", data.getDouble("temp")) + "°C");
                                                zone1T2.setText(String.format("%.2f", data.getDouble("hum")) + "%");
                                                zone1T3.setText(String.format("%.2f", data.getDouble("ambL")));

                                            } catch (JSONException e) {
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
                                        zone2G1.setValue((int) data.getDouble("temp"));
                                        zone2G2.setValue((int) data.getDouble("hum"));
                                        zone2G3.setValue((int) data.getDouble("ambL"));
                                        //output string
                                        txtZ2out.setText("Connected");
                                        zone2T1.setText(String.format("%.2f", data.getDouble("temp")) + "°C");
                                        zone2T2.setText(String.format("%.2f", data.getDouble("hum")) + "%");
                                        zone2T3.setText(String.format("%.2f", data.getDouble("ambL")));
                                    } catch (JSONException e) {
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
                                        zone3G1.setValue((int) data.getDouble("temp"));
                                        zone3G2.setValue((int) data.getDouble("hum"));
                                        zone3G3.setValue((int) data.getDouble("ambL"));
                                        //output string
                                        txtZ3out.setText("Connected");
                                        zone3T1.setText(String.format("%.2f", data.getDouble("temp")) + "°C");
                                        zone3T2.setText(String.format("%.2f", data.getDouble("hum")) + "%");
                                        zone3T3.setText(String.format("%.2f", data.getDouble("ambL")));
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
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
            case "Location":
                intent = new Intent(MainActivity.this,Location.class);
                MainActivity.this.startActivity(intent);
                break;
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
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
