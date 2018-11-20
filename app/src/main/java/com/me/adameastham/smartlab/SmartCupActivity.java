package com.me.adameastham.smartlab;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import io.particle.android.sdk.cloud.ParticleCloudSDK;
import io.particle.android.sdk.cloud.ParticleEvent;
import io.particle.android.sdk.cloud.ParticleEventHandler;
import io.particle.android.sdk.cloud.ParticleEventVisibility;
import io.particle.android.sdk.cloud.exceptions.ParticleCloudException;
import io.particle.android.sdk.utils.Toaster;

public class SmartCupActivity extends AppCompatActivity {

    private Button fillCupButton;
    private Button playIntake;
    private TextView txtPercentage;
    private ViewGroup cupContainer;
    private ViewGroup virtConatiner;
    private TextView five;
    private TextView four;
    private TextView three;
    private TextView two;
    private TextView one;
    private ImageView cupImage;

    private int percentage = 100;

    private HashMap<String, Integer> todaysHistory;

    private boolean playing = false;

    private DatabaseReference myRef;
    private Date timeEntered = Calendar.getInstance().getTime(); //time user entered a zone
    public static final SimpleDateFormat df = new SimpleDateFormat("dd/MM/yy - HH:mm:ss");
    public static final SimpleDateFormat mf = new SimpleDateFormat("dd/MM/yy");

    @SuppressLint("StaticFieldLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smartcup);

        virtConatiner = findViewById(R.id.virtContainer);

        cupImage = findViewById(R.id.imageView);
        txtPercentage = findViewById(R.id.txtPercentage);
        cupContainer = findViewById(R.id.cupContainer);
        five = findViewById(R.id.five);
        four = findViewById(R.id.four);
        three = findViewById(R.id.three);
        two = findViewById(R.id.two);
        one = findViewById(R.id.one);

        //connect to firebase
        myRef = FirebaseDatabase.getInstance().getReference("SmartCupData/");
        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.

                todaysHistory = new HashMap<>(); //clear hash map
                for (DataSnapshot singleSnapshot: dataSnapshot.getChildren()) {
                    if (singleSnapshot.child("ts").getValue().toString().contains(mf.format(Calendar.getInstance().getTime()))) {

                        //retrieve data
                        String time = "";
                        int percent = 0;
                        for (DataSnapshot insideSnapshot : singleSnapshot.getChildren()) {
                            if (insideSnapshot.getKey().toString().equals("ts")) {
                                time = insideSnapshot.getValue().toString().split(" - ")[1];
                            } else if (insideSnapshot.getKey().toString().equals("percentage")) {
                                percent = Integer.parseInt(insideSnapshot.getValue().toString());
                            }
                        }
                        todaysHistory.put(time, percent);
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read values
                Toaster.s(SmartCupActivity.this, "Failed to connect to database!");
            }

        };

        // Read from the database
        myRef.addValueEventListener(listener);
        playIntake = findViewById(R.id.playIntake);
        playIntake.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (playing == false){  //if currently not playing
                    playing = true;
                    TransitionManager.beginDelayedTransition(virtConatiner);
                    fillCupButton.setVisibility(View.GONE);

                    new AsyncTask<Void, Void, String>(){
                        @Override
                        protected String doInBackground(Void... voids) {
                            // This code is run after the doInBackground code finishes

                            if(todaysHistory!=null) {
                                Iterator<Map.Entry<String, Integer>> entries = todaysHistory.entrySet().iterator();
                                while (entries.hasNext()) {
                                    Map.Entry<String, Integer> entry = entries.next();
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            //update UI
                                            updateCup(entry.getValue());
                                            txtPercentage.setText("Time: " + entry.getKey());
                                        }
                                    });
                                    try {
                                        Thread.sleep(1000);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }
                                return "Done!";
                            }else{
                                return "No data for today!";
                            }
                        }
                        protected void onPostExecute (String msg){
                            //wait for previous toast to finish
                            Toaster.s(SmartCupActivity.this, msg);
                            TransitionManager.beginDelayedTransition(virtConatiner);
                            fillCupButton.setVisibility(View.VISIBLE);
                            playing = false;
                            txtPercentage.setText(""+percentage);
                        }
                    }.execute();
                }
            }
        });

        fillCupButton = findViewById(R.id.fillButton);
        fillCupButton.setVisibility(View.INVISIBLE);

        fillCupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                percentage = 100;
                five.setVisibility(View.VISIBLE);
                four.setVisibility(View.VISIBLE);
                three.setVisibility(View.VISIBLE);
                two.setVisibility(View.VISIBLE);
                one.setVisibility(View.VISIBLE);

                new AsyncTask<Void, Void, String>() {
                    @Override
                    protected String doInBackground(Void... voids) {

                        try {
                            ParticleCloudSDK.getCloud().publishEvent("FillCup", "100", ParticleEventVisibility.PRIVATE,60);
                        } catch (ParticleCloudException e) {
                            return "Failed to Send Command";
                        }
                        return "Fill Cup Command Sent";
                    }

                    // This code is run after the doInBackground code finishes
                    protected void onPostExecute(String msg) {
                        //wait for previous toast to finish
                        Toaster.s(SmartCupActivity.this, msg);
                    }
                }.execute();

                /*percentage = 0;
                five.setVisibility(View.INVISIBLE);
                four.setVisibility(View.INVISIBLE);
                three.setVisibility(View.INVISIBLE);
                two.setVisibility(View.INVISIBLE);
                one.setVisibility(View.INVISIBLE);*/

            }
        });

        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                try {
                    // Subscribe to smart cup event
                    long event = ParticleCloudSDK.getCloud().subscribeToMyDevicesEvents("SmartCup", new ParticleEventHandler() {

                        // Trigger this function when the event is received
                        public void onEvent(String eventName, ParticleEvent smartCupEvent) {

                            JSONObject data = null;
                            try {
                                //parse data to JSON
                                data = new JSONObject(smartCupEvent.dataPayload.toString());
                                int waterLevel = data.getInt("WaterLevel");
                                if(data.getString("origin").equals("cup")) {
                                    //getCupData(data);
                                } else if (data.getString("origin").equals("leapMotion")) {
                                    //getLeapData(data);
                                }

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        //update UI
                                        if (!playing){
                                            updateCup(waterLevel);
                                            //txtPercentage.setText(percentage+"%");
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

                    return "Subscribed to SmartCup";
                } catch (IOException e) {
                    //error subscribing
                    Log.e("MyAPP", e.toString());
                    return "Error subscribing!";
                }
            }

            // This code is run after the doInBackground code finishes
            protected void onPostExecute(String msg) {
                //wait for previous toast to finish
                Toaster.s(SmartCupActivity.this, msg);
                fillCupButton.setVisibility(View.VISIBLE);
            }
        }.execute();
    }
    private void getCupData(JSONObject data) throws JSONException{
        if (Math.abs(data.getDouble("accelXYZ")) > 1.9 && percentage == 100) {
            Toaster.s(SmartCupActivity.this, "Splash!");
        }

        double tilt = data.getDouble("Rotation");

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                cupImage.setRotation((percentage/100)*90);
                txtPercentage.setText(tilt+"");
            }
        });

        if (tilt > 20 && tilt <= 30) { //tilted enough to pour
            if (percentage > 80) {
                percentage = 80;
                uploadToDatabase();
            }
        } else if (tilt > 30 && tilt <= 40) {
            if (percentage > 60) {
                percentage = 60;
                uploadToDatabase();
            }
        } else if (tilt > 40 && tilt <= 50) {
            if (percentage > 40) {
                percentage = 40;
                uploadToDatabase();
            }
        } else if (tilt > 50 && tilt <= 60) {
            if (percentage > 20) {
                percentage = 20;
                uploadToDatabase();
            }
        } else if (tilt > 60) {
            if (percentage > 0) {
                percentage = 0;
                uploadToDatabase();
            }
        }
    }
    private void getLeapData(JSONObject data) throws JSONException{
        double rotation = data.getDouble("Rotation");

        if(rotation>0.12){
            if(percentage<=80){
                percentage=100;
                uploadToDatabase();
            }
        } else if(rotation>0.09 && rotation<=0.12){
            if(percentage<=60){
                percentage=80;
                uploadToDatabase();
            }
        } else if(rotation>0.06 && rotation<=0.09){
            if(percentage<=40){
                percentage=60;
                uploadToDatabase();
            }
        } else if(rotation>0.03 && rotation<=0.06){
            if(percentage<=20){
                percentage=40;
                uploadToDatabase();
            }
        } else if(rotation>0 && rotation<=0.03){ //hand rotated enough to pour back into cup
            if(percentage==0){
                percentage=20;
                uploadToDatabase();
            }
        }
    }

    private void uploadToDatabase(){
        HashMap<String, String> map = new HashMap<>();
        map.put("ts", df.format(Calendar.getInstance().getTime()));
        map.put("percentage", Integer.toString(percentage));
        myRef.push().setValue(map);
    }

    private void updateCup(int value){
        switch(value){
            case 100:
                five.setVisibility(View.VISIBLE);
                four.setVisibility(View.VISIBLE);
                three.setVisibility(View.VISIBLE);
                two.setVisibility(View.VISIBLE);
                one.setVisibility(View.VISIBLE);
                break;
            case 80:
                five.setVisibility(View.INVISIBLE);
                four.setVisibility(View.VISIBLE);
                three.setVisibility(View.VISIBLE);
                two.setVisibility(View.VISIBLE);
                one.setVisibility(View.VISIBLE);
                break;
            case 60:
                five.setVisibility(View.INVISIBLE);
                four.setVisibility(View.INVISIBLE);
                three.setVisibility(View.VISIBLE);
                two.setVisibility(View.VISIBLE);
                one.setVisibility(View.VISIBLE);
                break;
            case 40:
                five.setVisibility(View.INVISIBLE);
                four.setVisibility(View.INVISIBLE);
                three.setVisibility(View.INVISIBLE);
                two.setVisibility(View.VISIBLE);
                one.setVisibility(View.VISIBLE);
                break;
            case 20:
                five.setVisibility(View.INVISIBLE);
                four.setVisibility(View.INVISIBLE);
                three.setVisibility(View.INVISIBLE);
                two.setVisibility(View.INVISIBLE);
                one.setVisibility(View.VISIBLE);
                break;
            case 0:
                five.setVisibility(View.INVISIBLE);
                four.setVisibility(View.INVISIBLE);
                three.setVisibility(View.INVISIBLE);
                two.setVisibility(View.INVISIBLE);
                one.setVisibility(View.INVISIBLE);
                break;
            default:
                break;
        }
    }
}

