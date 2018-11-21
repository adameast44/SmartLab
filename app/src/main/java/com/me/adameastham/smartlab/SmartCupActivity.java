package com.me.adameastham.smartlab;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
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

import io.particle.android.sdk.cloud.ParticleCloudSDK;
import io.particle.android.sdk.cloud.ParticleEvent;
import io.particle.android.sdk.cloud.ParticleEventHandler;
import io.particle.android.sdk.cloud.ParticleEventVisibility;
import io.particle.android.sdk.cloud.exceptions.ParticleCloudException;
import io.particle.android.sdk.utils.Toaster;

public class SmartCupActivity extends AppCompatActivity {

    //UI objects
    private Button fillCupButton;
    private ToggleButton playIntake;
    private TextView txtPercentage;
    private ViewGroup cupContainer;
    private ViewGroup virtConatiner;
    private TextView five;
    private TextView four;
    private TextView three;
    private TextView two;
    private TextView one;
    private ImageView cupImage;

    //data variables
    private ArrayList<SmartCupDataModel> todaysHistory;
    private boolean playing = false;
    private float cupTilt = 0;
    private AsyncTask thread;

    //database objects
    private DatabaseReference myRef;
    private Date timeEntered = Calendar.getInstance().getTime(); //time user entered a zone
    public static final SimpleDateFormat mf = new SimpleDateFormat("yyyy-MM-dd");

    @SuppressLint("StaticFieldLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smartcup);

        virtConatiner = findViewById(R.id.virtContainer);

        cupImage = findViewById(R.id.imageView);
        cupImage.setImageResource(R.drawable.cup100);

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

                todaysHistory = new ArrayList<>(); //clear array list
                for (DataSnapshot singleSnapshot: dataSnapshot.getChildren()) {
                    if (singleSnapshot.child("ts").getValue().toString().contains(mf.format(Calendar.getInstance().getTime()))) {

                        //retrieve data
                        String time = "";
                        int percent = 0;
                        for (DataSnapshot insideSnapshot : singleSnapshot.getChildren()) {
                            if (insideSnapshot.getKey().toString().equals("ts")) {
                                time = insideSnapshot.getValue().toString().split("T")[1].substring(0, 8);
                            } else if (insideSnapshot.getKey().toString().equals("percentage")) {
                                percent = Integer.parseInt(insideSnapshot.getValue().toString());
                            }
                        }
                        todaysHistory.add(new SmartCupDataModel(time, percent));
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
        playIntake.setChecked(false);
        playIntake.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) { //playing
                    playing = true;
                    cupImage.refreshDrawableState();
                    cupImage.setVisibility(View.GONE);
                    fillCupButton.setVisibility(View.GONE);
                    TransitionManager.beginDelayedTransition(virtConatiner);

                    thread = new AsyncTask<Void, Void, String>(){
                        @Override
                        protected String doInBackground(Void... voids) {
                            if(todaysHistory!=null && todaysHistory.size() > 0) {
                                for (int i = 0; i<todaysHistory.size(); i++) {
                                    if (!isCancelled()) {
                                        int level = todaysHistory.get(i).getPercentage();
                                        String time = todaysHistory.get(i).getTime();
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                //update UI
                                                cupImage.refreshDrawableState();
                                                updateCup(level);
                                                txtPercentage.setText("Time: " + time);
                                            }
                                        });
                                        try {
                                            Thread.sleep(1000);
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                    } else {
                                        return "Stopped!";
                                    }
                                }
                                return "Done!";
                            }else{
                                return "No data for today!";
                            }
                        }

                        @Override
                        protected void onCancelled() {
                            super.onCancelled();
                            Toaster.s(SmartCupActivity.this, "Stopped!");
                        }

                        protected void onPostExecute (String msg){
                            //wait for previous toast to finish
                            Toaster.s(SmartCupActivity.this, msg);
                            playIntake.setChecked(false);
                        }
                    }.execute();
                } else { //not playing
                    thread.cancel(true);
                    TransitionManager.beginDelayedTransition(virtConatiner);
                    fillCupButton.setVisibility(View.VISIBLE);
                    cupImage.setImageResource(R.drawable.cup100);
                    cupImage.setVisibility(View.VISIBLE);
                    txtPercentage.setText("");
                    playing = false;
                }
            }
        });

        fillCupButton = findViewById(R.id.fillButton);
        fillCupButton.setVisibility(View.INVISIBLE);

        fillCupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //tell photon to fill cup
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

                five.setVisibility(View.INVISIBLE);
                four.setVisibility(View.INVISIBLE);
                three.setVisibility(View.INVISIBLE);
                two.setVisibility(View.INVISIBLE);
                one.setVisibility(View.INVISIBLE);
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
                                if (!playing) {
                                    getCupData(data, waterLevel);

                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            //update UI
                                            updateCup(waterLevel);
                                            txtPercentage.setText(waterLevel + "%");
                                        }
                                    });
                                }
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
    private void getCupData(JSONObject data, int percentage) throws JSONException{
        if (Math.abs(data.getDouble("accelXYZ")) > 1.9 && percentage!=0) {
            Toaster.s(SmartCupActivity.this, "Splash!");
        }

        double tilt = data.getDouble("Rotation");

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                rotateCup((float) tilt);
            }
        });
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
    private void rotateCup(float degree) {
        final RotateAnimation rotateAnim = new RotateAnimation(-cupTilt, -degree,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f);

        rotateAnim.setDuration(0);
        rotateAnim.setFillAfter(true);
        if(!playing) {
            cupImage.startAnimation(rotateAnim);
        }
        cupTilt = degree;
    }
}

