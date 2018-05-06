package com.example.pc.accelerometer;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends Activity implements SensorEventListener{

    private TextView xText, yText, zText, countText, updateMe, status;
    double x, y , z;
    private Button clickMe ;
    public Boolean check=false;
    private Sensor mySensor;
    private SensorManager SM;
    int count=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Create our Sensor Manager
        SM = (SensorManager)getSystemService(SENSOR_SERVICE);

        // Accelerometer Sensor
        mySensor = SM.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        // Register sensor Listener
        SM.registerListener(this, mySensor, SensorManager.SENSOR_DELAY_NORMAL);

        // Assign TextView
        //final TextView textView=
        xText = (TextView)findViewById(R.id.xText);
        yText = (TextView)findViewById(R.id.yText);
        zText = (TextView)findViewById(R.id.zText);
        status=(TextView)findViewById(R.id.status);

        status.setText("Status"+ "No connection " );
        countText = (TextView)findViewById(R.id.count);
        clickMe=(Button)findViewById(R.id.button1);

        clickMe.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(!check)
                {
                    Toast.makeText(MainActivity.this,"connections is established",Toast.LENGTH_LONG).show();
                    status.setText("Status" + "server Connected");
                    check=true;
                }else{
                    Toast.makeText(MainActivity.this,"connections is Terminated",Toast.LENGTH_LONG).show();
                    status.setText("Status"+ "Lost connection " );
                    check=false;

                }

            }
        });


            Thread t=new Thread(){
                @Override
                public void run(){
                    while(!isInterrupted()){
                        try {
                            Thread.sleep(1000);  //1000ms = 1 sec
                            runOnUiThread(new Runnable() {

                                @Override
                                public void run() {
                                    count++;
                                    countText.setText(String.valueOf(count));
                                    RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
                                    String url = "http://192.168.43.56:5000/classify";
                                    JSONObject obj = new JSONObject();
                                    try {
                                        obj.put("x", x);obj.put("y", y); obj.put("z", z);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, url, obj,
                                            new Response.Listener<JSONObject>() {
                                                @Override
                                                public void onResponse(JSONObject response) {
                                                    //Toast.makeText(login.this, response.toString(), Toast.LENGTH_LONG).show();
                                                    System.out.println("aaaaaa");

                                                }
                                            },
                                            new Response.ErrorListener() {
                                                @Override
                                                public void onErrorResponse(VolleyError error) {
                                                    Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
                                                }
                                            });
                                    queue.add(jsObjRequest);
                                }
                            });

                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                    }
                }
            };

            t.start();

        }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Not in use
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        x=event.values[0];y=event.values[1];z=event.values[2];

        xText.setText("X: " + x);
        yText.setText("Y: " + y);
        zText.setText("Z: " + z);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
