package cs4720furrett.rpimobileproject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.FloatMath;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Toast;
import android.widget.ToggleButton;


public class MySettings extends Activity implements SensorEventListener {
    final Context context = this;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private String url = "";
    private ToggleButton debug_toggle;
    private SensorManager sensorMan;
    private Sensor accelerometer;
    private float[] mGravity;
    private float mAccel;
    private float mAccelCurrent;
    private float mAccelLast;
    private boolean motionOn;
    private ToggleButton motion_toggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pref = getSharedPreferences("preferences", MODE_PRIVATE);
        editor = pref.edit();
        setContentView(R.layout.activity_my_settings);
        getActionBar().setTitle("Settings");

        debug_toggle = (ToggleButton) findViewById(R.id.debugToggle);
        String debug = pref.getString("DEBUG", "OFF");
        debug_toggle.setText(debug);

        motionOn = pref.getBoolean("MOTION_DETECTION", false);
        motion_toggle = (ToggleButton) findViewById(R.id.motionProtection_toggle);

        motion_toggle.setChecked(motionOn);


        SeekBar shakeBar = (SeekBar) findViewById(R.id.motionProtection_slider);
        shakeBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onStopTrackingTouch(SeekBar bar) {
            }

            public void onStartTrackingTouch(SeekBar bar) {
            }

            public void onProgressChanged(SeekBar bar, int paramInt, boolean paramBoolean) {
                editor.putInt("MOTION", paramInt);
                editor.apply();
                System.out.println(paramInt);
            }
        });

        shakeBar.setProgress(pref.getInt("MOTION", 50));
        setupAccelerometer();
    }

    public void setupAccelerometer() {
        //setting up the accelerometer
        sensorMan = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = sensorMan.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mAccel = 0.00f;
        mAccelCurrent = SensorManager.GRAVITY_EARTH;
        mAccelLast = SensorManager.GRAVITY_EARTH;
        sensorMan.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER && motionOn) {
            // Shake detection
            mGravity = event.values.clone();
            float x = mGravity[0];
            float y = mGravity[1];
            float z = mGravity[2];
            mAccelLast = mAccelCurrent;
            mAccelCurrent = FloatMath.sqrt(x * x + y * y + z * z);
            float delta = mAccelCurrent - mAccelLast;
            mAccel = mAccel * 0.9f + delta;
            // Make this higher or lower according to how much
            // motion you want to detect
            double threshold = (double) pref.getInt("MOTION", 50)*-1.0 + 100.0;
            if (mAccel > 3.0 * threshold / 50.0) {
                CharSequence text = "You shook the device! (threshold = "+threshold+")";
                final Toast toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
                toast.show();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        toast.cancel();
                    }
                }, 200);
                //alertDialog.show();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        sensorMan.registerListener((android.hardware.SensorEventListener) this, accelerometer, SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorMan.unregisterListener((android.hardware.SensorEventListener) this);
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_song_list, menu);
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

    public void done(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    //Disable Back Button
    @Override
    public void onBackPressed() {
    }

    public void setURL(View view) {
        EditText urlView = (EditText) findViewById(R.id.editText);
        url = urlView.getText().toString();
        url = url + "/rpi";
        editor.putString("url", url);
        editor.commit();
        System.out.println("URL now set to: " + url);
    }

    public void setDebug(View view) {
        editor.putString("DEBUG", (String) debug_toggle.getText());
        editor.apply();
    }

    public void toggleMotion(View view) {
        motionOn = ((String) motion_toggle.getText()).compareTo("ON") == 0;

        editor.putBoolean("MOTION_DETECTION", motionOn);
        editor.apply();
    }
}

