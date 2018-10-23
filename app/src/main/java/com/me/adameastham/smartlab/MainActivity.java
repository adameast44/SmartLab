package com.me.adameastham.smartlab;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import java.io.IOException;

import io.particle.android.sdk.cloud.ApiFactory;
import io.particle.android.sdk.cloud.ParticleCloudSDK;
import io.particle.android.sdk.cloud.ParticleDevice;
import io.particle.android.sdk.cloud.ParticleEvent;
import io.particle.android.sdk.cloud.ParticleEventHandler;
import io.particle.android.sdk.cloud.exceptions.ParticleCloudException;
import io.particle.android.sdk.utils.Async;
import io.particle.android.sdk.utils.Toaster;
import pl.pawelkleczkowski.customgauge.CustomGauge;

public class MainActivity extends AppCompatActivity {

    private CustomGauge zone1G1;
    private CustomGauge zone1G2;
    private CustomGauge zone1G3;
    private TextView txtZ1out;

    private CustomGauge zone2G1;
    private CustomGauge zone2G2;
    private CustomGauge zone2G3;
    private TextView txtZ2out;

    private CustomGauge zone3G1;
    private CustomGauge zone3G2;
    private CustomGauge zone3G3;
    private TextView txtZ3out;

    ParticleDevice myDevice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtZ1out = findViewById(R.id.txtZ1Out);
        zone1G1 = findViewById(R.id.zone1G1);
        zone1G2 = findViewById(R.id.zone1G2);
        zone1G3 = findViewById(R.id.zone1G3);
        zone1G1.setEndValue(100);
        zone1G2.setEndValue(100);
        zone1G3.setEndValue(100);

        txtZ2out = findViewById(R.id.txtZ2Out);
        zone2G1 = findViewById(R.id.zone2G1);
        zone2G2 = findViewById(R.id.zone2G2);
        zone2G3 = findViewById(R.id.zone2G3);
        zone2G1.setEndValue(100);
        zone2G2.setEndValue(100);
        zone2G3.setEndValue(100);

        txtZ3out = findViewById(R.id.txtZ3Out);
        zone3G1 = findViewById(R.id.zone3G1);
        zone3G2 = findViewById(R.id.zone3G2);
        zone3G3 = findViewById(R.id.zone3G3);
        zone3G1.setEndValue(100);
        zone3G2.setEndValue(100);
        zone3G3.setEndValue(100);

        ParticleCloudSDK.init(this);
        /*ParticleCloudSDK.initWithOauthCredentialsProvider(this, new ApiFactory.OauthBasicAuthCredentialsProvider() {
            public String getClientId() {
                return "smartlabapp-8404";
            }

            public String getClientSecret() {
                return "aede0f53c224078decdd86191bf1e468ab6e9cd1";
            }
        });*/
        //Login to Particle
        new AsyncTask<Void, Void, String>() {
            protected String doInBackground(Void... params) {
                //aede0f53c224078decdd86191bf1e468ab6e9cd1
                //  LOG IN TO PARTICLE
                try {
                    // Log in to Particle Cloud using username and password
                    //ParticleCloudSDK.getCloud().setAccessToken("6be1370a885dc381eede63c12d092a03073c79b2");
                    ParticleCloudSDK.getCloud().logIn("adameastham@hotmail.com", "SmartLab");
                    return "Logged in!";
                } catch (ParticleCloudException e) {
                    Log.e("My app", "Error logging in: " + e.toString());
                    return "Error logging in!";
                }
            }

            protected void onPostExecute(String msg) {
                // Show Toast containing message from doInBackground
                Toaster.s(MainActivity.this, msg);
            }
        }.execute();

        //subscribe to event
        new AsyncTask<Void, Void, String>() {
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
                                        data = new JSONObject(event1.dataPayload.toString());
                                        zone1G1.setValue((int)data.getDouble("temp"));
                                        zone1G2.setValue((int)data.getDouble("hum"));
                                        zone1G3.setValue((int)data.getDouble("ambL"));
                                        txtZ1out.setText("     "+ String.format("%.2f", data.getDouble("temp"))+"          "
                                                +String.format("%.2f", data.getDouble("hum"))+"%         "
                                                +String.format("%.2f", data.getDouble("ambL")));

                                    }catch(JSONException e){
                                        e.printStackTrace();
                                    }
                                }
                            });
                        }

                        public void onEventError(Exception e) {
                            Log.e("MyAPP", "Event error: ", e);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                }
                            });
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
                                        data = new JSONObject(event2.dataPayload.toString());
                                        zone2G1.setValue((int)data.getDouble("temp"));
                                        zone2G2.setValue((int)data.getDouble("hum"));
                                        zone2G3.setValue((int)data.getDouble("ambL"));
                                        txtZ2out.setText("     "+ String.format("%.2f", data.getDouble("temp"))+"          "+String.format("%.2f", data.getDouble("hum"))+"%         "+String.format("%.2f", data.getDouble("ambL")));

                                    }catch(JSONException e){
                                        e.printStackTrace();
                                    }
                                }
                            });
                        }

                        public void onEventError(Exception e) {
                            Log.e("MyAPP", "Event error: ", e);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                }
                            });
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
                                        data = new JSONObject(event3.dataPayload.toString());
                                        zone3G1.setValue((int)data.getDouble("temp"));
                                        zone3G2.setValue((int)data.getDouble("hum"));
                                        zone3G3.setValue((int)data.getDouble("ambL"));
                                        txtZ3out.setText("     "+ String.format("%.2f", data.getDouble("temp"))+"          "+String.format("%.2f", data.getDouble("hum"))+"%         "+String.format("%.2f", data.getDouble("ambL")));

                                    }catch(JSONException e){
                                        e.printStackTrace();
                                    }
                                }
                            });
                        }

                        public void onEventError(Exception e) {
                            Log.e("MyAPP", "Event error: ", e);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                }
                            });
                        }
                    });
                    return "Subscribed!";
                } catch (IOException e) {
                    //error subscribing
                    Log.e("MyAPP", e.toString());
                    return "Error subscribing!";
                }
            }
        // This code is run after the doInBackground code finishes
        protected void onPostExecute(String msg) {
            Toaster.s(MainActivity.this, msg);
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
}
