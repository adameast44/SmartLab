package com.me.adameastham.smartlab;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.transition.TransitionManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class ControlBoardActivity extends AppCompatActivity {

    private ViewGroup mainCont;
    private ViewGroup ketButtons;
    private Switch bulb1;
    private Switch bulb2;
    private Switch kettle;
    private Switch all;
    private ColorSeekBar colour1;
    private ColorSeekBar colour2;
    private SeekBar brightness1;
    private SeekBar brightness2;
    private Button ket65;
    private Button ket80;
    private Button ket95;
    private Button ket100;

    private TextView txtOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control_board);

        mainCont = findViewById(R.id.mainCont);
        ketButtons = findViewById(R.id.kettleButtons);
        ketButtons.setVisibility(View.GONE);

        bulb1 = findViewById(R.id.switchB1);
        bulb1.setChecked(false);
        bulb2 = findViewById(R.id.switchB2);
        bulb2.setChecked(false);
        kettle = findViewById(R.id.switchKet);
        kettle.setChecked(false);
        all = findViewById(R.id.switchAll);
        all.setChecked(false);

        ket65 = findViewById(R.id.ket65);
        ket80 = findViewById(R.id.ket80);
        ket95 = findViewById(R.id.ket95);
        ket100 = findViewById(R.id.ket100);

        colour1 = findViewById(R.id.colour1);
        colour1.setVisibility(View.GONE);
        colour2 = findViewById(R.id.colour2);
        colour2.setVisibility(View.GONE);
        brightness1 = findViewById(R.id.brightness1);
        brightness1.setVisibility(View.GONE);
        brightness2 = findViewById(R.id.brightness2);
        brightness2.setVisibility(View.GONE);

        txtOut = findViewById(R.id.txtOutput);

        bulb1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if (bulb1.isChecked()){
                    TransitionManager.beginDelayedTransition(mainCont);
                    colour1.setVisibility(View.VISIBLE);
                    brightness1.setVisibility(View.VISIBLE);
                } else {
                    TransitionManager.beginDelayedTransition(mainCont);
                    colour1.setVisibility(View.GONE);
                    brightness1.setVisibility(View.GONE);
                    if(!bulb2.isChecked()){

                    }
                }
            }
        });

        bulb2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if (bulb2.isChecked()){
                    TransitionManager.beginDelayedTransition(mainCont);
                    colour2.setVisibility(View.VISIBLE);
                    brightness2.setVisibility(View.VISIBLE);


                } else {
                    TransitionManager.beginDelayedTransition(mainCont);
                    colour2.setVisibility(View.GONE);
                    brightness2.setVisibility(View.GONE);
                    if(!bulb1.isChecked()){

                    }
                }
            }
        });

        all.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if (all.isChecked()){
                    TransitionManager.beginDelayedTransition(mainCont);
                    bulb1.setChecked(false);
                    bulb2.setChecked(false);
                    bulb1.setVisibility(View.GONE);
                    bulb2.setVisibility(View.GONE);
                    colour1.setVisibility(View.VISIBLE);
                    brightness1.setVisibility(View.VISIBLE);
                    JSONObject data = new JSONObject();
                    try {
                        data.put("zone", "0");
                        data.put("command", "on/off");
                        data.put("value", "on");
                        sendData(data);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    TransitionManager.beginDelayedTransition(mainCont);
                    colour1.setVisibility(View.GONE);
                    brightness1.setVisibility(View.GONE);
                    bulb1.setVisibility(View.VISIBLE);
                    bulb2.setVisibility(View.VISIBLE);
                    JSONObject data = new JSONObject();
                    try {
                        data.put("zone", "0");
                        data.put("command", "on/off");
                        data.put("value", "off");
                        sendData(data);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        kettle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if (kettle.isChecked()){
                    TransitionManager.beginDelayedTransition(mainCont);
                    ketButtons.setVisibility(View.VISIBLE);

                } else {
                    TransitionManager.beginDelayedTransition(mainCont);
                    ketButtons.setVisibility(View.GONE);
                }
            }
        });

        ket65.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        ket80.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        ket95.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        ket100.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        colour1.setColorSeeds(R.array.text_colors);
        colour1.setMaxPosition(100);
        colour1.setShowAlphaBar(false);
        colour1.setThumbHeight(40);
        colour1.setBarHeight(12);
        colour1.setOnColorChangeListener(new ColorSeekBar.OnColorChangeListener() {
            @Override
            public void onColorChangeListener(int colorBarPosition, int alphaBarPosition, int color) {

            }
        });

        colour2.setColorSeeds(R.array.text_colors);
        colour2.setMaxPosition(100);
        colour2.setShowAlphaBar(false);
        colour2.setThumbHeight(40);
        colour2.setBarHeight(12);
        colour2.setOnColorChangeListener(new ColorSeekBar.OnColorChangeListener() {
            @Override
            public void onColorChangeListener(int colorBarPosition, int alphaBarPosition, int color) {

            }
        });

        brightness1.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        brightness2.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }

    private void sendData(JSONObject json){
        RequestQueue MyRequestQueue = Volley.newRequestQueue(this);
        String url = "http://10.42.72.161:1234";
        StringRequest MyStringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //This code is executed if the server responds, whether or not the response contains data.
                //The String 'response' contains the server's response.
                txtOut.setText(response);
            }
        }, new Response.ErrorListener() { //Create an error listener to handle errors appropriately.
            @Override
            public void onErrorResponse(VolleyError error) {
                //This code is executed if there is an error.
                txtOut.setText(error.getMessage());
            }
        }) {
            protected Map<String, String> getParams() {
                Map<String, String> MyData = new HashMap<String, String>();

                try {
                    MyData.put("zone", json.getString("zone"));
                    MyData.put("command", json.getString("command"));
                    MyData.put("value", json.getString("value"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                txtOut.setText(MyData.toString());
                return MyData;
            }
        };
        MyRequestQueue.add(MyStringRequest);
    }

}
