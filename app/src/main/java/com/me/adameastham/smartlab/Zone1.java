package com.me.adameastham.smartlab;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.lang.*;
import java.util.Collections;

//Written by Inkan Fung and Adam Eastham

public class Zone1 extends AppCompatActivity {

    //declaring listview and the datamodel + customadapter
    ListView listView;
    ArrayList<ZoneDataModel> zoneDataModel;
    private static customAdapterZones customAdapterZones;

    //declare variable for method
    private String Time;
    private String AmbientLight;
    private String Humidity;
    private String Temp;
    private String date;
    private String hms;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zone1);

        listView = findViewById(R.id.LVZone1);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("tempTest");

        // Read from the database
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                zoneDataModel = new ArrayList<>();

                for (DataSnapshot singleSnapshot: dataSnapshot.getChildren()) {
                    //filter for zone
                    if (singleSnapshot.child("Zone").getValue().toString().equals("Zone1")) {
                        for (DataSnapshot insideSnapshot : singleSnapshot.getChildren()) {
                            //build data model
                            if (insideSnapshot.getKey().toString().equals("ts")) {
                                Time = insideSnapshot.getValue().toString();
                                String s[] = Time.split("T");
                                date = s[0];
                                hms = s[1];
                                hms = hms.substring(0, hms.length() - 1);
                            }
                            else if(insideSnapshot.getKey().toString().equals("Temp")){
                                Temp = insideSnapshot.getValue().toString();
                            }
                            else if(insideSnapshot.getKey().toString().equals("Humidity")) {
                                Humidity = insideSnapshot.getValue().toString();
                            }
                            else if(insideSnapshot.getKey().toString().equals("AmbientLight")) {
                               AmbientLight = insideSnapshot.getValue().toString();
                            }
                        }
                        zoneDataModel.add(new ZoneDataModel(date, hms, AmbientLight, Humidity, Temp));
                    }
                }

                //flip order of list
                Collections.reverse(zoneDataModel);

                //insert data into list view
                customAdapterZones = new customAdapterZones(zoneDataModel, getApplicationContext());
                listView.setAdapter(customAdapterZones);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read values
                Log.w("MyApp", "Failed to read value.", error.toException());
            }

        });

    }
}
