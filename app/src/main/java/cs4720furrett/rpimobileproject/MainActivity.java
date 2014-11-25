package cs4720furrett.rpimobileproject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.FloatMath;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;


public class MainActivity extends Activity implements SensorEventListener {

    final Context context = this;
    //The dialog that pops up
    private AlertDialog alertDialog;
    //Variables to use the accelerometer
    private SensorManager sensorMan;
    private Sensor accelerometer;
    private float[] mGravity;
    private float mAccel;
    private float mAccelCurrent;
    private float mAccelLast;

    public String url = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //setting up the accelerometer
        sensorMan = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = sensorMan.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mAccel = 0.00f;
        mAccelCurrent = SensorManager.GRAVITY_EARTH;
        mAccelLast = SensorManager.GRAVITY_EARTH;
        sensorMan.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
        buildDialog();
    }

    public void buildDialog() {
        //setting up the dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("You moved the Android device!")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // FIRE ZE MISSILES!
                    }
                });
        alertDialog = builder.create();
    }

    public void onClick(DialogInterface dialog, int which) {
// here you can add functions
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

    public void switchToSongList(View view) {
        Intent intent = new Intent(this, SongList.class);
        intent.putExtra("url", url);
        startActivity(intent);
        finish();
    }

    public void switchToSettings(View view) {
        Intent intent = new Intent(this, MySettings.class);
        startActivity(intent);
        finish();
    }

    /* Sends post */
    //Changed, moved to settings
//    public void sendPost(View view) throws IOException, JSONException {
//
//        //get url from field
//        EditText editText = (EditText) findViewById(R.id.editText);
//        String postURL = editText.getText().toString();
//        if (!postURL.startsWith("http://")) postURL = "http://" + postURL;
//        //String postURL = "http://cs4720.cs.virginia.edu/rpi/?username=csh7kd"; //hard coding for now
//        new SendPost().execute(postURL);
//    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

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
    public void onBackPressed()
    {
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
                alertDialog.show();
            }
        }
    }
//Moved to Settings
//    private class SendPost extends AsyncTask<String, Void, Void> {
//        @Override
//        protected Void doInBackground(String... params) {
//            String postURL = params[0];
//            String json = "{\n" +
//                    "\"lights\": [\n" +
//                    "\n" +
//                    "{\"lightId\": 1, \"red\":255,\"green\":0,\"blue\":0, \"intensity\": 0.3}],\n" +
//
//                    "\n" +
//                    "\"propagate\": true\n" +
//                    "}";
//
//            DefaultHttpClient httpClient = new DefaultHttpClient();
//
//            //make connection to path
//            HttpPost httpPost = new HttpPost(postURL);
//
//            //json object to be sent
//            JSONObject holder = null;
//            try {
//                holder = new JSONObject(json);
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//
//            assert holder != null;
//            StringEntity se = null;
//            try {
//                se = new StringEntity(holder.toString());
//            } catch (UnsupportedEncodingException e) {
//                e.printStackTrace();
//            }
//
//            httpPost.setEntity(se);
//            httpPost.setHeader("Accept", "application/json");
//            httpPost.setHeader("Content-type", "application/json");
//
//            //Handles what is returned from the page
//            ResponseHandler responseHandler = new BasicResponseHandler();
//            try {
//                httpClient.execute(httpPost, responseHandler);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            return null;
//        }
//    }
}
