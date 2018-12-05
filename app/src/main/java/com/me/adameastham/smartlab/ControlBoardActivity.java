package com.me.adameastham.smartlab;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.ToggleButton;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;

public class ControlBoardActivity extends AppCompatActivity {

    private ViewGroup mainCont;
    private ViewGroup ketTemps;
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
    private Button ketWarm;

    private ToggleButton fanButton;

    private TextView txtOut;

    private Date lastTimeColourChanged1 = Calendar.getInstance().getTime(); //the last time the colour of light bulb 1 was changed
    private Date lastTimeColourChanged2 = Calendar.getInstance().getTime(); //the last time the colour of a light bulb 2 was changed


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control_board);

        mainCont = findViewById(R.id.mainCont);
        ketTemps = findViewById(R.id.kettleTemps);
        ketTemps.setVisibility(View.GONE);

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
        ketWarm = findViewById(R.id.ketWarm);
        ketWarm.setVisibility(View.GONE);

        colour1 = findViewById(R.id.colour1);
        colour1.setVisibility(View.GONE);
        colour2 = findViewById(R.id.colour2);
        colour2.setVisibility(View.GONE);
        brightness1 = findViewById(R.id.brightness1);
        brightness1.setVisibility(View.GONE);
        brightness2 = findViewById(R.id.brightness2);
        brightness2.setVisibility(View.GONE);
        txtOut = findViewById(R.id.txtOutput);

        fanButton = findViewById(R.id.togStartStop);
        fanButton.setChecked(false);

        fanButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    String url = "http://192.168.0.109/cgi-bin/on.cgi";
                    commandFan(url);
                } else {
                    String url = "http://192.168.0.109/cgi-bin/off.cgi";
                    commandFan(url);
                }
            }
        });

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
                    ketTemps.setVisibility(View.VISIBLE);
                    ketWarm.setVisibility(View.VISIBLE);
                    JSONObject data = new JSONObject();
                    try {
                        data.put("zone", 10);
                        data.put("command", "turn");
                        data.put("value", "on");
                        sendData(data);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    TransitionManager.beginDelayedTransition(mainCont);
                    ketTemps.setVisibility(View.GONE);
                    ketWarm.setVisibility(View.GONE);
                    JSONObject data = new JSONObject();
                    try {
                        data.put("zone", 10);
                        data.put("command", "turn");
                        data.put("value", "off");
                        sendData(data);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        ket65.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JSONObject data = new JSONObject();
                try {
                    data.put("zone", 10);
                    data.put("command", "temp");
                    data.put("value", 65);
                    sendData(data);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        ket80.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JSONObject data = new JSONObject();
                try {
                    data.put("zone", 10);
                    data.put("command", "temp");
                    data.put("value", 80);
                    sendData(data);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        ket95.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JSONObject data = new JSONObject();
                try {
                    data.put("zone", 10);
                    data.put("command", "temp");
                    data.put("value", 95);
                    sendData(data);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        ket100.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JSONObject data = new JSONObject();
                try {
                    data.put("zone", 10);
                    data.put("command", "temp");
                    data.put("value", 100);
                    sendData(data);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        ketWarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JSONObject data = new JSONObject();
                try {
                    data.put("zone", 10);
                    data.put("command", "warm");
                    data.put("value", 0);
                    sendData(data);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        colour1.setColorSeeds(R.array.text_colors);
        colour1.setMaxPosition(265);
        colour1.setShowAlphaBar(false);
        colour1.setThumbHeight(50);
        colour1.setBarHeight(12);
        colour1.setOnColorChangeListener(new ColorSeekBar.OnColorChangeListener() {
            @Override
            public void onColorChangeListener(int colorBarPosition, int alphaBarPosition, int color) {
                if ((Calendar.getInstance().getTime().getTime()-lastTimeColourChanged1.getTime())>1000) { //don't send data more than every second
                    lastTimeColourChanged1 = Calendar.getInstance().getTime();
                    JSONObject data = new JSONObject();
                    try {
                        if (all.isChecked()) {
                            data.put("zone", 0);
                        } else {
                            data.put("zone", 2);
                        }
                        data.put("command", "colour");
                        if(colorBarPosition>255)colorBarPosition=-1; //send white command
                        data.put("value", colorBarPosition);
                        sendData(data);
                        txtOut.setText("colour: " + colorBarPosition);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        colour2.setColorSeeds(R.array.text_colors);
        colour2.setMaxPosition(265);
        colour2.setShowAlphaBar(false);
        colour2.setThumbHeight(50);
        colour2.setBarHeight(12);
        colour2.setOnColorChangeListener(new ColorSeekBar.OnColorChangeListener() {
            @Override
            public void onColorChangeListener(int colorBarPosition, int alphaBarPosition, int color) {
                if ((Calendar.getInstance().getTime().getTime()-lastTimeColourChanged2.getTime())>1000) { //don't send data more than every second
                    lastTimeColourChanged2 = Calendar.getInstance().getTime();
                    JSONObject data = new JSONObject();
                    try {
                        data.put("zone", 3);
                        data.put("command", "colour");
                        if(colorBarPosition>255)colorBarPosition=-1; //send white command
                        data.put("value", colorBarPosition);
                        sendData(data);
                        txtOut.setText("colour: " + colorBarPosition);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
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

    private static void sendData(JSONObject json) {
        Thread dataThread = new Thread(new Runnable() {
            @Override
            public void run() {
                String response = "";
                BufferedReader reader = null;
                HttpURLConnection conn = null;
                String url = "http://10.42.72.161:1234/";
                try {
                    URL urlObj = new URL(url);
                    conn = (HttpURLConnection) urlObj.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setDoOutput(true);
                    OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
                    wr.write(json.toString());
                    wr.flush();

                    Log.d("response", conn.getResponseCode() + "");

                } catch (Exception e) {
                    Log.e("responseError", e.toString());
                } finally {
                    try {
                        reader.close();
                        if (conn != null) {
                            conn.disconnect();
                        }
                    } catch (Exception ex) {

                    }
                }
            }
        });
        dataThread.start();
    }

    private void commandFan(String url){
        Thread dataThread = new Thread(new Runnable() {
            @Override
            public void run() {
                BufferedReader reader = null;
                HttpURLConnection conn = null;
                try {
                    URL urlObj = new URL(url);
                    conn = (HttpURLConnection) urlObj.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setDoOutput(true);

                    Log.d("response", conn.getResponseCode() + "");

                } catch (Exception e) {
                    Log.e("responseError", e.toString());
                } finally {
                    try {
                        reader.close();
                        if (conn != null) {
                            conn.disconnect();
                        }
                    } catch (Exception ex) {

                    }
                }
            }
        });
        dataThread.start();
    }
}