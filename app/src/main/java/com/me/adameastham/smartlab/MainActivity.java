package com.me.adameastham.smartlab;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.io.IOException;

import io.particle.android.sdk.cloud.ApiFactory;
import io.particle.android.sdk.cloud.ParticleCloudSDK;
import io.particle.android.sdk.cloud.ParticleDevice;
import io.particle.android.sdk.cloud.ParticleEvent;
import io.particle.android.sdk.cloud.ParticleEventHandler;
import io.particle.android.sdk.cloud.exceptions.ParticleCloudException;
import io.particle.android.sdk.utils.Async;
import io.particle.android.sdk.utils.Toaster;

public class MainActivity extends AppCompatActivity {


    TextView txtZone1Output;
    TextView txtZone3Output;

    ParticleDevice myDevice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txtZone1Output = (TextView) findViewById(R.id.txtZone1Output);
        txtZone3Output = (TextView) findViewById(R.id.txtZone3Output);
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
                    ParticleCloudSDK.getCloud().logIn("adameastham@hotmail.com", "Ipadam44");
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
                    long zone1Event = ParticleCloudSDK.getCloud().subscribeToMyDevicesEvents("photonZone1", new ParticleEventHandler() {

                                // Trigger this function when the event is received
                                public void onEvent(String eventName, ParticleEvent event1) {

                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            txtZone1Output.setText(event1.dataPayload);
                                        }
                                    });
                                }

                                public void onEventError(Exception e) {
                                    Log.e("MyAPP", "Event error: ", e);
                                }
                            });
                    // Subscribe to zone 3 event
                    long zone3Event = ParticleCloudSDK.getCloud().subscribeToMyDevicesEvents("photonZone3", new ParticleEventHandler() {

                        // Trigger this function when the event is received
                        public void onEvent(String eventName, ParticleEvent event3) {

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    txtZone3Output.setText(event3.dataPayload);
                                }
                            });
                        }

                        public void onEventError(Exception e) {
                            Log.e("MyAPP", "Event error: ", e);
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
}
