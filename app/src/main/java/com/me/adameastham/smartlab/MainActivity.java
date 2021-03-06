package com.me.adameastham.smartlab;

//Written by Adam Eastham

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import io.particle.android.sdk.cloud.ParticleCloudSDK;
import io.particle.android.sdk.cloud.ParticleEvent;
import io.particle.android.sdk.cloud.ParticleEventHandler;
import io.particle.android.sdk.cloud.exceptions.ParticleCloudException;
import io.particle.android.sdk.utils.Toaster;
import pl.pawelkleczkowski.customgauge.CustomGauge;

public class MainActivity extends AppCompatActivity {

    private TextView txtWifi;
    private ViewGroup transContainer;
    private ViewGroup zoneContainer;

    private CustomGauge zone1G1;
    private CustomGauge zone1G2;
    private CustomGauge zone1G3;
    private TextView txtZ1out;
    private TextView zone1T1;
    private TextView zone1T2;
    private TextView zone1T3;
    private LinearLayout zone1Users;
    private String lastBin;

    private CustomGauge zone2G1;
    private CustomGauge zone2G2;
    private CustomGauge zone2G3;
    private TextView txtZ2out;
    private TextView zone2T1;
    private TextView zone2T2;
    private TextView zone2T3;
    private LinearLayout zone2Users;
    private String lastFridge;

    private CustomGauge zone3G1;
    private CustomGauge zone3G2;
    private CustomGauge zone3G3;
    private TextView txtZ3out;
    private TextView zone3T1;
    private TextView zone3T2;
    private TextView zone3T3;
    private LinearLayout zone3Users;
    private String lastDoor;

    private TextView txtEnterName;
    private EditText txtHotspotName;
    private ToggleButton togButton;
    private ToggleButton togStartStop;
    private ImageView userImg;

    private final int MOTION_TYPE = 0;
    private final int SOUND_TYPE = 1;

    private int currentZone = 4;

    CircularBuffer zone1Wifi = new CircularBuffer(6);
    CircularBuffer zone2Wifi = new CircularBuffer(6);
    CircularBuffer zone3Wifi = new CircularBuffer(6);

    String hotspotName;
    private Date timeEntered = Calendar.getInstance().getTime(); //time user entered a zone
    public static final SimpleDateFormat df = new SimpleDateFormat("dd/MM/yy - HH:mm:ss");

    ArrayList<InteractableObject> objects = new ArrayList<>();

    @SuppressLint("StaticFieldLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        objects.add(new InteractableObject("Bin",findViewById(R.id.intBin)));
        objects.add(new InteractableObject("Fridge", findViewById(R.id.intFridge)));
        objects.add(new InteractableObject("Door", findViewById(R.id.intDoor)));

        boolean isLoggedIn = false;
        transContainer = findViewById(R.id.container);
        zoneContainer = findViewById(R.id.zoneContainer);
        txtEnterName = findViewById(R.id.txtEnterName);
        txtHotspotName = findViewById(R.id.txtHotspotName);

        userImg = new ImageView(this);
        userImg.setImageResource(R.drawable.user);
        userImg.setColorFilter(ContextCompat.getColor(userImg.getContext(), R.color.md_red_500));

        //Zone 1 UI elements
        zone1T1 = findViewById(R.id.zone1T1);
        zone1T2 = findViewById(R.id.zone1T2);
        zone1T3 = findViewById(R.id.zone1T3);
        zone1G1 = findViewById(R.id.zone1G1);
        zone1G2 = findViewById(R.id.zone1G2);
        zone1G3 = findViewById(R.id.zone1G3);
        zone1Users = findViewById(R.id.relativeZone1);
        zone1G1.setEndValue(100);
        zone1G2.setEndValue(100);
        zone1G3.setEndValue(100);
        lastBin = "NaN";

        //Zone 2 UI elements
        zone2T1 = findViewById(R.id.zone2T1);
        zone2T2 = findViewById(R.id.zone2T2);
        zone2T3 = findViewById(R.id.zone2T3);
        zone2G1 = findViewById(R.id.zone2G1);
        zone2G2 = findViewById(R.id.zone2G2);
        zone2G3 = findViewById(R.id.zone2G3);
        zone2Users = findViewById(R.id.relativeZone2);
        zone2G1.setEndValue(100);
        zone2G2.setEndValue(100);
        zone2G3.setEndValue(100);
        lastFridge = "NaN";

        //Zone 3 UI elements
        zone3T1 = findViewById(R.id.zone3T1);
        zone3T2 = findViewById(R.id.zone3T2);
        zone3T3 = findViewById(R.id.zone3T3);
        zone3G1 = findViewById(R.id.zone3G1);
        zone3G2 = findViewById(R.id.zone3G2);
        zone3G3 = findViewById(R.id.zone3G3);
        zone3Users = findViewById(R.id.relativeZone3);
        zone3G1.setEndValue(100);
        zone3G2.setEndValue(100);
        zone3G3.setEndValue(100);
        lastDoor = "NaN";

        //arange UI
        transContainer.setVisibility(View.GONE);

        //connect to firebase
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("locationData/");

        togStartStop = (ToggleButton) findViewById(R.id.togStartStop);
        togButton = (ToggleButton) findViewById(R.id.togButton);

        togButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    //Change UI view
                    transContainer.setVisibility(View.GONE);
                    TransitionManager.beginDelayedTransition(transContainer);
                    txtHotspotName.setVisibility(View.VISIBLE);
                    txtEnterName.setVisibility(View.VISIBLE);
                    togStartStop.setVisibility(View.VISIBLE);
                    InputMethodManager imm = (InputMethodManager) getSystemService(MainActivity.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);

                } else {
                    //Change UI view
                    TransitionManager.beginDelayedTransition(transContainer);
                    txtHotspotName.setVisibility(View.GONE);
                    txtEnterName.setVisibility(View.GONE);
                    togStartStop.setVisibility(View.GONE);
                    transContainer.setVisibility(View.VISIBLE);
                    //hide keyboard
                    InputMethodManager imm = (InputMethodManager) getSystemService(MainActivity.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                }
            }

        });
        togButton.setChecked(true);
        togButton.setChecked(false);

        togStartStop.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) { //tracking
                    if (!txtHotspotName.getText().toString().equals("")) {
                        hotspotName = txtHotspotName.getText().toString();
                        togButton.setChecked(false);
                    }
                    else { //not tracking
                        togStartStop.setChecked(false);
                        Toaster.s(MainActivity.this, "Please Enter a Hotspot Name");
                    }
                } else { //not tracking
                    //Change UI view
                    TransitionManager.beginDelayedTransition(zoneContainer);
                    togButton.setVisibility(View.VISIBLE);
                    txtHotspotName.setVisibility(View.VISIBLE);
                    txtEnterName.setVisibility(View.VISIBLE);
                    //empty buffers
                    zone1Wifi.empty();
                    zone2Wifi.empty();
                    zone3Wifi.empty();
                }
            }

        });
        togStartStop.setChecked(false);

        DatabaseReference interRef = FirebaseDatabase.getInstance().getReference("interaction/");
        interRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                for (DataSnapshot singleSnapshot: dataSnapshot.getChildren()) {
                    //Build string

                    String itemName = "";
                    String ts = "NaN";
                    String date;
                    String hms;
                    for (DataSnapshot insideSnapshot : singleSnapshot.getChildren()) {
                        if (insideSnapshot.getKey().toString().equals("itemName")){
                            itemName = insideSnapshot.getValue().toString();
                        } else if(insideSnapshot.getKey().toString().equals("ts")){
                            ts = insideSnapshot.getValue().toString();
                            String s[] = ts.split("T");
                            date = s[0];
                            hms = s[1];
                            hms = hms.substring(0, hms.length() - 5);
                            ts = date + "\n" + hms;
                        }
                    }
                    switch (itemName){
                        case "Bin":
                            lastBin = ts;
                            break;
                        case "Fridge":
                            lastFridge = ts;
                            break;
                        case "Door":
                            lastDoor = ts;
                            break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Failed to read value
                Log.w("MyApp", "Failed to read value.", databaseError.toException());
            }
        });

        for(InteractableObject intObj: objects){
            intObj.getImageView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String msg = "";
                    switch (intObj.getName()){
                        case "Bin":
                            msg = "Bin accessed:\n" + lastBin;
                            break;
                        case "Fridge":
                            msg = "Fridge accessed:\n" + lastFridge;
                            break;
                        case "Door":
                            msg = "Door accessed:\n" + lastDoor;
                            break;
                    }
                    Toaster.s(MainActivity.this, msg);
                }
            });
        }


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
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        int nearest = getNearestZone(zone1Wifi, zone2Wifi, zone3Wifi);

                                        if (nearest != currentZone && togStartStop.isChecked() && zone1Wifi.peak(zone1Wifi.size()-1)!=0){ //zone switch
                                            currentZone = nearest;
                                            if (currentZone!=4) {
                                                togButton.setText("In Zone " + currentZone);
                                                HashMap<String, String> map = new HashMap<>();
                                                map.put("Name", hotspotName);
                                                map.put("ts", df.format(Calendar.getInstance().getTime()));
                                                map.put("Location", "Zone " + currentZone);
                                                myRef.push().setValue(map);
                                            }
                                            else{
                                                togButton.setText("Unkown Location");
                                            }

                                            //remove the user from its current zone
                                            if(userImg.getParent() != null) ((LinearLayout) userImg.getParent()).removeView(userImg);

                                            switch(currentZone){
                                                case 1:
                                                    zone1Users.addView(userImg);
                                                    break;
                                                case 2:
                                                    zone2Users.addView(userImg);
                                                    break;
                                                case 3:
                                                    zone3Users.addView(userImg);
                                                    break;
                                                default:

                                                    break;
                                            }
                                        }
                                    }
                                });
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

        //subscribe to live data
        new AsyncTask<Void, Void, String>() {
            protected String doInBackground(Void... params) {
                try {
                    // Subscribe to zone 1 event
                    long zone1Event = ParticleCloudSDK.getCloud().subscribeToMyDevicesEvents("zoneData",
                            new ParticleEventHandler() {

                                // Trigger this function when the event is received
                                public void onEvent(String eventName, ParticleEvent zoneEvent) {

                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            JSONObject data = null;
                                            try {
                                                //parse data to JSON
                                                data = new JSONObject(zoneEvent.dataPayload.toString());
                                                String zoneName = data.getString("zone");
                                                double temp = data.getDouble("temp");
                                                double hum = data.getDouble("hum");
                                                double ambL = data.getDouble("ambL");
                                                switch (zoneName){
                                                    case "Zone1":
                                                        //output to gauges
                                                        zone1G1.setValue((int) temp);
                                                        zone1G2.setValue((int) hum);
                                                        zone1G3.setValue((int) ambL);
                                                        //output string
                                                        zone1T1.setText(String.format("%.2f", temp) + "°C");
                                                        zone1T2.setText(String.format("%.2f", hum) + "%");
                                                        zone1T3.setText(String.format("%.2f", ambL) + "lx");
                                                        break;
                                                    case "Zone2":
                                                        //output to gauges
                                                        zone2G1.setValue((int) temp);
                                                        zone2G2.setValue((int) hum);
                                                        zone2G3.setValue((int) ambL);
                                                        //output string
                                                        zone2T1.setText(String.format("%.2f", temp) + "°C");
                                                        zone2T2.setText(String.format("%.2f", hum) + "%");
                                                        zone2T3.setText(String.format("%.2f", ambL) + "lx");
                                                        break;
                                                    case "Zone3":
                                                        //output to gauges
                                                        zone3G1.setValue((int) temp);
                                                        zone3G2.setValue((int) hum);
                                                        zone3G3.setValue((int) ambL);
                                                        //output string
                                                        zone3T1.setText(String.format("%.2f", temp) + "°C");
                                                        zone3T2.setText(String.format("%.2f", hum) + "%");
                                                        zone3T3.setText(String.format("%.2f", ambL) + "lx");
                                                        break;
                                                }


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

        //sound and motion
        new AsyncTask<Void, Void, String>() {

            @Override
            protected String doInBackground(Void... voids) {
                try {
                    // Subscribe to zone 1 event
                    long event1 = ParticleCloudSDK.getCloud().subscribeToMyDevicesEvents("detectionData", new ParticleEventHandler() {

                        // Trigger this function when the event is received
                        public void onEvent(String eventName, ParticleEvent interactionEvent) {

                            JSONObject data = null;
                            try {
                                //parse data to JSON
                                data = new JSONObject(interactionEvent.dataPayload.toString());
                                String type = data.getString("type");
                                String zoneName = data.getString("zone");
                                String state = data.getString("state");

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        toggleIcon(zoneName,type,state.equals("on"));
                                    }
                                });
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        public void onEventError(Exception e) {
                            Log.e("MyAPP", "Event error: ", e);
                        }
                    });

                    return "Subscribed to detectionData";
                } catch (IOException e) {
                    //error subscribing
                    Log.e("MyAPP", e.toString());
                    return "Error subscribing!";
                }
            }
        }.execute();

        //interactions
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                try {
                    // Subscribe to zone 1 event
                    long event1 = ParticleCloudSDK.getCloud().subscribeToMyDevicesEvents("interactionLive", new ParticleEventHandler() {

                        // Trigger this function when the event is received
                        public void onEvent(String eventName, ParticleEvent interactionEvent) {

                            JSONObject data = null;
                            try {
                                //parse data to JSON
                                data = new JSONObject(interactionEvent.dataPayload.toString());
                                String itemName = data.getString("item");
                                String state = data.getString("state");

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        for(InteractableObject object: objects){
                                            if(object.getName().equals(itemName)){
                                                if(state.equals("start")) {
                                                    object.setLight(true);
                                                } else {
                                                    object.setLight(false);
                                                }
                                            }
                                        }
                                    }
                                });
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        public void onEventError(Exception e) {
                            Log.e("MyAPP", "Event error: ", e);
                        }
                    });

                    return "Subscribed to InteractionsData";
                } catch (IOException e) {
                    //error subscribing
                    Log.e("MyAPP", e.toString());
                    return "Error subscribing!";
                }
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
            case "Control":
                intent = new Intent(MainActivity.this,ControlBoardActivity.class);
                MainActivity.this.startActivity(intent);
                break;
            case "SmartCup":
                intent = new Intent(MainActivity.this,SmartCupActivity.class);
                MainActivity.this.startActivity(intent);
                break;
            case "Location":
                intent = new Intent(MainActivity.this,Location.class);
                MainActivity.this.startActivity(intent);
                break;
            case "Zone":
                intent = new Intent(MainActivity.this,ZoneActivity.class);
                MainActivity.this.startActivity(intent);
                break;
            case "Interactions":
                intent = new Intent(MainActivity.this,InteractionsActivity.class);
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

    private void toggleIcon(String zoneName, String type, boolean on){
        ImageView icon;
        int zoneNum;
        String state = on ? "on" : "off";

        if(!type.equals("motion") && !type.equals("sound")){
            return;
        }

        switch (zoneName){
            case "Zone1":
                zoneNum = 1;
                break;
            case "Zone2":
                zoneNum = 2;
                break;
            case "Zone3":
                zoneNum = 3;
                break;
            default:
                return;

        }
        icon = findViewById(getResources().getIdentifier(type + zoneNum,"id",getPackageName()));
        icon.setImageResource(getResources().getIdentifier(type + "_" + state,"drawable",getPackageName()));
    }
}
