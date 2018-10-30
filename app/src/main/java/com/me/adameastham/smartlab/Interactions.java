package com.me.adameastham.smartlab;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.JsonReader;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import io.particle.android.sdk.cloud.ParticleCloudSDK;
import io.particle.android.sdk.cloud.ParticleEvent;
import io.particle.android.sdk.cloud.ParticleEventHandler;
import io.particle.android.sdk.cloud.exceptions.ParticleCloudException;
import io.particle.android.sdk.utils.Toaster;

public class Interactions extends AppCompatActivity {

    ListView listView;

    //DEFINING A STRING ADAPTER WHICH WILL HANDLE THE DATA OF THE LISTVIEW
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interactions);

        ParticleCloudSDK.init(this);

        listView = findViewById(R.id.LVInteractions);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("interaction");

        // Read from the database
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String[] tempList = new String[(int)dataSnapshot.getChildrenCount()];
                int count = 0;

                for (DataSnapshot singleSnapshot: dataSnapshot.getChildren()) {
                    tempList[count] = " ";
                    //Build string
                    for (DataSnapshot insideSnapshot : singleSnapshot.getChildren()) {
                        tempList[count] = insideSnapshot.getValue().toString() + "     " + tempList[count];
                    }
                    count++;
                }

                //remove empty elements and invert order.
                String[] listItems = new String[count];
                for (int i=0; i<count; i++){
                    listItems[i] = tempList[count-i-1];
                }

                //insert data into list view
                adapter=new ArrayAdapter<String>(getApplicationContext(), R.layout.list_item_layout, R.id.list_content, listItems);
                listView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("MyApp", "Failed to read value.", error.toException());
            }
        });

    }

}
