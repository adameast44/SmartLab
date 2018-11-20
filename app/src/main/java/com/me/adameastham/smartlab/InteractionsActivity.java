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

public class InteractionsActivity extends AppCompatActivity {

    ListView listView;
    ArrayList<InteractionDataModel> dataModel;
    private static CustomAdapterInteractions customAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interactions);

        listView = findViewById(R.id.LVInteractions);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("interaction");

        // Read from the database
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                int count = 0;
                dataModel = new ArrayList<>();

                //declare variable for method
                String Time = "";
                String type = "";
                String zone = "";
                String date = "";
                String hms = "";

                for (DataSnapshot singleSnapshot: dataSnapshot.getChildren()) {
                    //Build string
                    for (DataSnapshot insideSnapshot : singleSnapshot.getChildren()) {
                        if (insideSnapshot.getKey().toString().equals("ts")) {
                            Time = insideSnapshot.getValue().toString();
                            String s[] = Time.split("T");
                            date = s[0];
                            hms = s[1];
                            hms = hms.substring(0, hms.length() - 1);
                        }
                        else if(insideSnapshot.getKey().toString().equals("itemName")){
                            type = insideSnapshot.getValue().toString();
                        }
                        if (type.equals("Bin")) {
                            zone = "1";
                        } else if (type.equals("Door")) {
                            zone = "3";
                        } else if (type.equals("Fridge")) {
                            zone = "2";
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
