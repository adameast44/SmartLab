package com.me.adameastham.smartlab;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.lang.*;
import java.util.Collections;

import io.particle.android.sdk.utils.Toaster;

//Written by Inkan Fung and Adam Eastham

public class ZoneActivity extends AppCompatActivity {

    //declaring listview and the datamodel + customadapter
    ListView listView;
    ArrayList<ZoneDataModel> zoneDataModel;
    private static CustomAdapterZones CustomAdapterZones;

    private Button zone1;
    private Button zone2;
    private Button zone3;

    private String selectedZone = "Zone1";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zone);

        listView = findViewById(R.id.LVZone1);
        zone1 = findViewById(R.id.btnZ1);
        zone2 = findViewById(R.id.btnZ2);
        zone3 = findViewById(R.id.btnZ3);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("tempTest");

        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                zoneDataModel = new ArrayList<>();

                //declare variable for method
                String Time = "";
                String AmbientLight = "";
                String Humidity = "";
                String Temp = "";
                String date = "";
                String hms = "";

                for (DataSnapshot singleSnapshot: dataSnapshot.getChildren()) {
                    //filter for zone
                    if (singleSnapshot.child("Zone").getValue().toString().equals(selectedZone)) {
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
                CustomAdapterZones = new CustomAdapterZones(zoneDataModel, getApplicationContext());
                listView.setAdapter(CustomAdapterZones);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read values
                Toaster.s(ZoneActivity.this, "Failed to connect to database!");
            }

        };

        // Read from the database
        myRef.addValueEventListener(listener);

        zone1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedZone = "Zone1";
                zone1.setBackgroundResource(R.color.md_grey_500);
                zone2.setBackgroundResource(R.color.md_grey_400);
                zone3.setBackgroundResource(R.color.md_grey_400);
                myRef.removeEventListener(listener);
                myRef.addValueEventListener(listener);
            }
        });

        zone2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedZone = "Zone2";
                zone1.setBackgroundResource(R.color.md_grey_400);
                zone2.setBackgroundResource(R.color.md_grey_500);
                zone3.setBackgroundResource(R.color.md_grey_400);
                myRef.removeEventListener(listener);
                myRef.addValueEventListener(listener);
            }
        });

        zone3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedZone = "Zone3";
                zone1.setBackgroundResource(R.color.md_grey_400);
                zone2.setBackgroundResource(R.color.md_grey_400);
                zone3.setBackgroundResource(R.color.md_grey_500);
                myRef.removeEventListener(listener);
                myRef.addValueEventListener(listener);
            }
        });
    }
}
