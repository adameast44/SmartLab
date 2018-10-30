package com.me.adameastham.smartlab;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class Zone2 extends AppCompatActivity {

    ListView listView;

    //DEFINING A STRING ADAPTER WHICH WILL HANDLE THE DATA OF THE LISTVIEW
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zone2);

        listView = findViewById(R.id.LVZone2);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("tempTest");

        // Read from the database
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String[] tempList = new String[(int)dataSnapshot.getChildrenCount()];
                int count = 0;

                for (DataSnapshot singleSnapshot: dataSnapshot.getChildren()) {
                    //filter by zone
                    if (singleSnapshot.child("Zone").getValue().toString().equals("Zone2")) {
                        tempList[count] = " ";
                        for (DataSnapshot insideSnapshot : singleSnapshot.getChildren()) {
                            if (!insideSnapshot.getKey().toString().equals("Zone")) {
                                //build string
                                tempList[count] = insideSnapshot.getValue().toString() + " - " + tempList[count];
                                Log.i("OUTPUT" ,insideSnapshot.getValue().toString());
                            }
                        }
                        Log.i("OUTPUT", singleSnapshot.getValue().toString());
                        count++;
                    }
                }

                //remove empty elements and invert order
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
