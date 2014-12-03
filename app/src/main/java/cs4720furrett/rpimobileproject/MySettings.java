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
    private ToggleButton toggle;
    private SensorManager sensorMan;
    private Sensor accelerometer;
    private float[] mGravity;
    private float mAccel;
    private float mAccelCurrent;
    private float mAccelLast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pref = getSharedPreferences("preferences", MODE_PRIVATE);
        editor = pref.edit();
        setContentView(R.layout.activity_my_settings);
        getActionBar().setTitle("Settings");

        toggle = (ToggleButton) findViewById(R.id.debugToggle);
        String debug = pref.getString("DEBUG", "OFF");
        toggle.setText(debug);


        SeekBar noteBar = (SeekBar) findViewById(R.id.noteSpeed_slider);
        noteBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onStopTrackingTouch(SeekBar bar) {
            }

            public void onStartTrackingTouch(SeekBar bar) {
            }

            public void onProgressChanged(SeekBar bar, int paramInt, boolean paramBoolean) {
                editor.putInt("NOTE_SPEED", paramInt);
                System.out.println(paramInt);
            }
        });
        SeekBar shakeBar = (SeekBar) findViewById(R.id.motionProtection_slider);
        shakeBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onStopTrackingTouch(SeekBar bar) {
            }

            public void onStartTrackingTouch(SeekBar bar) {
            }

            public void onProgressChanged(SeekBar bar, int paramInt, boolean paramBoolean) {
                editor.putInt("MOTION", paramInt);
                System.out.println(paramInt);
            }
        });
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
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
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
            if (mAccel > 3) {
                CharSequence text = "You shook the device!";
                Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
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
        String value = (toggle.getText() == "ON") ? "ON" : "OFF";

        editor.putString("DEBUG", (String) toggle.getText());
        editor.apply();
        editor.commit();
    }
}

