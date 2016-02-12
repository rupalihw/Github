package anna.pedometer;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.ToggleButton;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private Sensor mySensor_Pedometer;
    private SensorManager mSensorManager;
    //private Sensor mAccelerometer;
    TextView update_text;
    boolean on_or_off;



    int flag = 0;

    // Steps counted in current session
    private int mSteps = 0;
    // Value of the step counter sensor when the listener was registered.
    // (Total steps are calculated from this value.)
    private int mCounterSteps = 0;
    // Steps counted by the step counter previously. Used to keep counter consistent across rotation
    // changes
    private int mPreviousCounterSteps = 0;


    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //initialize our text update box
        update_text = (TextView) findViewById(R.id.update_text);

        // get the sensor service
        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        // we want to make a pedometer sensor
        mySensor_Pedometer = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);



        //mSensorManager.registerListener(MainActivity.this, mySensor_Pedometer, SensorManager.SENSOR_DELAY_NORMAL);







        Switch toggle = (Switch) findViewById(R.id.pedometer_switch);
        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // The toggle is enabled
                    Log.e("You pressed toggle: ", "on");

                    // register the listener for the pedometer
                    mSensorManager.registerListener(MainActivity.this, mySensor_Pedometer, SensorManager.SENSOR_DELAY_NORMAL);

                    SharedPreferences sharedPref = getSharedPreferences("Settings_Pedometer", 0);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putBoolean("switch", true);
                    editor.apply();

                } else {
                    // The toggle is disabled
                    Log.e("You pressed toggle: ", "off");
                    mSensorManager.unregisterListener(MainActivity.this, mySensor_Pedometer);

                    SharedPreferences sharedPref = getSharedPreferences("Settings_Pedometer", 0);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putBoolean("switch", false);
                    editor.apply();

                }
            }
        });



        //mPreviousCounterSteps = mSteps;



    }

    private void set_alarm_text(String output) {
        update_text.setText(output);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    public void Today_Button(View view) {
        Intent intent = new Intent(this, Today.class);
        startActivity(intent);
    }

    public void History_Button(View view) {
        Intent intent = new Intent(this, History.class);
        startActivity(intent);
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

    protected void onResume() {
        super.onResume();
        //mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);

        SharedPreferences sharedPref = getSharedPreferences("Settings_Pedometer", 0);
        boolean on_or_off = sharedPref.getBoolean("switch", false);

        if (on_or_off) {
            mSensorManager.registerListener(MainActivity.this, mySensor_Pedometer, SensorManager.SENSOR_DELAY_NORMAL);
            Log.e("on resume: ", "on " + String.valueOf(on_or_off));
        }
        else {
            mSensorManager.unregisterListener(MainActivity.this, mySensor_Pedometer);
            Log.e("on resume: ", "off" + String.valueOf(on_or_off));
        }


    }

    protected void onPause() {
        super.onPause();
        //mSensorManager.unregisterListener(this);
        SharedPreferences sharedPref = getSharedPreferences("Settings_Pedometer", 0);
        boolean on_or_off = sharedPref.getBoolean("switch", false);

        if (on_or_off) {
            mSensorManager.registerListener(MainActivity.this, mySensor_Pedometer, SensorManager.SENSOR_DELAY_NORMAL);
            Log.e("on pause: ", "on" + String.valueOf(on_or_off));
        }
        else {
            mSensorManager.unregisterListener(MainActivity.this, mySensor_Pedometer);
            Log.e("on pause: ", "off" + String.valueOf(on_or_off));
        }
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        // initiate the sensor events
        Sensor sensor = event.sensor;

        //
        // to differentiate it between it and the accelerometer
        if (sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
            if (mCounterSteps < 1) {
                // initial value
                mCounterSteps = (int) event.values[0];
            }

            if (flag == 1) {
                // initial value
                mCounterSteps = (int) event.values[0];
                flag = 0;
            }

            // Calculate steps taken based on first counter value received.
            mSteps = (int) event.values[0] - mCounterSteps;

            // Add the number of steps previously taken, otherwise the counter would start at 0.
            // This is needed to keep the counter consistent across rotation changes.
            mSteps = mSteps + mPreviousCounterSteps;

        }




        // method that changes the update text Textbox
        set_alarm_text("You walked: " + mSteps);

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Not in use
    }
}