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
import java.util.Collections;

//Written by Adam Eastham

public class Location extends AppCompatActivity {

    ListView listView;
    ArrayList<InteractionDataModel> dataModel;
    private static CustomAdapterInteractions customAdapter;

    //declare variable for method
    private String Time;
    private String type;
    private String zone;
    private String date;
    private String hms;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        listView = findViewById(R.id.LVInteractions);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("locationData");

        // Read from the database
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                int count = 0;
                dataModel = new ArrayList<>();


                for (DataSnapshot singleSnapshot: dataSnapshot.getChildren()) {
                    //Build string
                    for (DataSnapshot insideSnapshot : singleSnapshot.getChildren()) {
                        if (insideSnapshot.getKey().toString().equals("ts")) {
                            Time = insideSnapshot.getValue().toString();
                            String s[] = Time.split(" - ");
                            date = s[0];
                            hms = s[1];
                        }
                        else if(insideSnapshot.getKey().toString().equals("Name")){
                            type = insideSnapshot.getValue().toString();
                        }
                        else if(insideSnapshot.getKey().toString().equals("Location")) {
                            zone = insideSnapshot.getValue().toString();
                        }
                    }
                    dataModel.add(new InteractionDataModel(date,hms,type,zone));

                    count++;
                }

                //flip order of list
                Collections.reverse(dataModel);

                //insert data into list view
                customAdapter = new CustomAdapterInteractions(dataModel, getApplicationContext());
                listView.setAdapter(customAdapter);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("MyApp", "Failed to read value.", error.toException());
            }
        });

    }


}
