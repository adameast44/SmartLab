package com.me.adameastham.smartlab;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
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
                    JSONObject data = new JSONObject();
                    try {
                        data.put("zone", 2);
                        data.put("command", "turn");
                        data.put("value", "on");
                        sendData(data);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    TransitionManager.beginDelayedTransition(mainCont);
                    colour1.setVisibility(View.GONE);
                    brightness1.setVisibility(View.GONE);
                    JSONObject data = new JSONObject();
                    try {
                        data.put("zone", 2);
                        data.put("command", "turn");
                        data.put("value", "off");
                        sendData(data);
                    } catch (Exception e) {
                        e.printStackTrace();
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
                    JSONObject data = new JSONObject();
                    try {
                        data.put("zone", 3);
                        data.put("command", "turn");
                        data.put("value", "on");
                        sendData(data);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else {
                    TransitionManager.beginDelayedTransition(mainCont);
                    colour2.setVisibility(View.GONE);
                    brightness2.setVisibility(View.GONE);
                    JSONObject data = new JSONObject();
                    try {
                        data.put("zone", 3);
                        data.put("command", "turn");
                        data.put("value", "off");
                        sendData(data);
                    } catch (Exception e) {
                        e.printStackTrace();
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
                        data.put("zone", 0);
                        data.put("command", "turn");
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
                        data.put("zone", 0);
                        data.put("command", "turn");
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
        colour1.setMaxPosition(255);
        colour1.setShowAlphaBar(false);
        colour1.setThumbHeight(40);
        colour1.setBarHeight(12);
        colour1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }


        });
        colour1.setOnColorChangeListener(new ColorSeekBar.OnColorChangeListener() {
            @Override
            public void onColorChangeListener(int colorBarPosition, int alphaBarPosition, int color) {
                JSONObject data = new JSONObject();
                try {
                    if (all.isChecked()){
                        data.put("zone", 0);
                    } else {
                        data.put("zone", 2);
                    }
                    data.put("command", "colour");
                    data.put("value", colorBarPosition);
                    sendData(data);
                    txtOut.setText("colour: " + colorBarPosition);
                } catch (Exception e) {
                    e.printStackTrace();
                }
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
                JSONObject data = new JSONObject();
                try {
                    if(all.isChecked()) {
                        data.put("zone", 0);
                    } else {
                        data.put("zone", 2);
                    }
                    data.put("command", "brightness");
                    int brightness = (seekBar.getProgress()/4) + 1;
                    if (brightness>24) brightness = 24;
                    data.put("value", brightness);
                    sendData(data);
                } catch (Exception e) {
                    e.printStackTrace();
                }
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
                JSONObject data = new JSONObject();
                try {
                    data.put("zone", 3);
                    data.put("command", "brightness");
                    int brightness = (seekBar.getProgress()/4) + 1;
                    if (brightness>24) brightness = 24;
                    data.put("value", brightness);
                    sendData(data);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    private static void sendData(JSONObject json){
        Thread dataThread = new Thread(new Runnable() {
            @Override
            public void run() {
                String response = "";
                BufferedReader reader = null;
                HttpURLConnection conn = null;
                String url = "http://10.42.72.161:1234/";
                try{
                    URL urlObj = new URL(url);
                    conn = (HttpURLConnection) urlObj.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setDoOutput(true);
                    OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
                    wr.write(json.toString());
                    wr.flush();

                    Log.d("response",conn.getResponseCode() + "");

                }  catch (Exception e) {
                    Log.e("responseError", e.toString());
                } finally {
                    try {
                        reader.close();
                        if(conn != null){
                            conn.disconnect();
                        }
                    } catch (Exception ex){

                    }
                }
            }
        });
        dataThread.start();
    }

}