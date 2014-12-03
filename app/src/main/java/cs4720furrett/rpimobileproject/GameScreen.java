package cs4720furrett.rpimobileproject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.media.AudioManager;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.FloatMath;
import android.view.View;
import android.widget.TextView;

import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;


public class GameScreen extends Activity implements SensorEventListener {
    final Context context = this;
    private final long SEED = 9001;
    private final int speed = 100;
    //Handles what is returned from the page
    ResponseHandler responseHandler;
    private volatile boolean running = true;
    private String postURL;
    private GameScreen game;
    private ArrayList<Light> lights = new ArrayList<Light>();
    private Random rng;
    private StringBuilder builder;
    private StringBuilder lightsbuilder;
    private DefaultHttpClient httpClient;
    private HttpPost httpPost;
    private int count = 0;
    private java.lang.String songFilename = "songs.json";
    private Iterator<String> striter;
    private ArrayList<String> elements = new ArrayList<String>();
    private boolean notDebug = true;
    private long sleepTime = 250;
    private volatile long valid = 0;
    private volatile String last_color = "";
    private int currentCombo = 0;
    private int maxCombo = 0;
    private int score = 0;
    private int life = 100;
    private int hits = 0;
    private int misses = 0;
    private boolean noteHit;
    private AlertDialog alertDialog;
    private SensorManager sensorMan;
    private Sensor accelerometer;
    private float[] mGravity;
    private float mAccel;
    private float mAccelCurrent;
    private float mAccelLast;
    private boolean focused = true;
    private int songID;
    private boolean musicShouldPlay = false;
    private boolean killPlayer = false;
    private SharedPreferences pref;
    private boolean motionOn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_screen);
        game = this;
        pref = getSharedPreferences("preferences", MODE_PRIVATE);
        String storedUrl = pref.getString("url", null);
        if (storedUrl != null) {
            postURL = pref.getString("url", "no url defined");
            System.out.println(postURL);
            if (!postURL.startsWith("http://")) postURL = "http://" + postURL;
        }
        String debug = pref.getString("DEBUG", "OFF");
        motionOn = pref.getBoolean("MOTION_DETECTION", false);
        if (debug.compareTo("ON") == 0) {
            notDebug = false;
        }

        if (storedUrl != null && storedUrl.compareTo("") != 0) {
            httpClient = new DefaultHttpClient();
            httpPost = new HttpPost(postURL);
            responseHandler = new BasicResponseHandler();
        } else {
            notDebug = false;
        }
        builder = new StringBuilder();
        lightsbuilder = new StringBuilder();
        rng = new Random(SEED);

        String data = pref.getString("CLICKED_SONG", null);
        if (data == null) {
            System.out.println("blank song received!");
        }

        getActionBar().setTitle("Now Playing " + data);
        System.out.println(data);
        System.out.println(debug);
        if(data.equals("Jingle Bells")){
            songID =  R.raw.jingle;
        }

//        TextView mTextView = (TextView) findViewById(R.id.fullscreen_content);
//        mTextView.setText(data);

        String songsInJSON = loadJSONFromAsset();
        System.out.println("songJSON");
        System.out.println(songsInJSON);
        System.out.println("done");
        try {
            JSONObject j = new JSONObject(songsInJSON);
            System.out.println(j.toString());
            JSONArray song = j.getJSONArray(data);
            System.out.println(song.toString());
            for (int x = 0; x < song.length(); x++) {
                JSONObject light_data = song.getJSONObject(x);
                System.out.println(light_data.toString());
                int index = light_data.getInt("index") * -1 - 32;
                String color = light_data.getString("color");
                System.out.println("\t" + index + "\t" + color);
                Light light;
                if (color.compareTo("red") == 0) {
                    System.out.println("red");
                    light = new Light(255, 0, 0, index);
                } else if (color.compareTo("green") == 0) {
                    System.out.println("green");
                    light = new Light(0, 255, 0, index);
                } else {
                    System.out.println("blue");
                    light = new Light(0, 0, 255, index);
                }

                System.out.println(light.toString());
                lights.add(light);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        setupAccelerometer();

        System.out.println(lights.toString());
//        this.initializeMusic();
        this.runThread();
    }

//    public void initializeMusic() {
//        new Thread(){
//
//            public void run() {
//                MediaPlayer player = MediaPlayer.create(GameScreen.this, songID);
//                player.start();
//                boolean musicPlaying = true;
//                while(!killPlayer) {
//                    if (musicPlaying && !musicShouldPlay){
//                        player.pause();
//                        musicPlaying = false;
//                    }
//                    if (!musicPlaying && musicShouldPlay){
//                        player.start();
//                        musicPlaying = true;
//                    }
//                }
//                player.release();
//            }
//        }.start();
//    }

    public void runThread() {

        new Thread() {
            public void run() {
                System.out.println("run: " + lights.toString());
                long startTime, endTime;
                MediaPlayer player = MediaPlayer.create(GameScreen.this, songID);
                player.start();
                boolean musicPlaying = true;
                while (running) {
                    if (focused) {
                        startTime = System.currentTimeMillis();
                        if (musicPlaying && !musicShouldPlay){
                            player.pause();
                            musicPlaying = false;
                        }
                        if (!musicPlaying && musicShouldPlay){
                            player.start();
                            musicPlaying = true;
                        }
                        //if song ends or fail
                        if (lights.size() == 0 || life <= 0) {
//                            killPlayer = true;
                            Intent intent = new Intent(game, ResultsScreen.class);
                            intent.putExtra("MAX_COMBO", "" + maxCombo);
                            intent.putExtra("SCORE", "" + score);
                            if(life < 0) life = 0;
                            intent.putExtra("LIFE", "" + life);
                            intent.putExtra("HITS", "" + hits);
                            intent.putExtra("MISSES", "" + misses);
                            startActivity(intent);
                            finish();
                            running = false;
                            focused = false;
                            player.release();
                        }
                        sendPost();
                        endTime = System.currentTimeMillis();

                        try {
                            this.sleep(sleepTime - (endTime - startTime));
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    updateValuesInLayout();
                                }
                            });
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                            break;
                        } catch (IllegalArgumentException e) {
                            System.out.print("too slow: ");
                            System.out.print(endTime - startTime);
                        }
                        System.out.println(count);
                        count++;
                    }
                }
            }
        }.start();
    }

    //Disable Back Button
    @Override
    public void onBackPressed() {
    }

    public void sendPost() {
        //zero everything
        builder.delete(0, builder.length());
        lightsbuilder.delete(0, lightsbuilder.length());
        elements.clear();
        //Create iterator and check for empty list
        Iterator<Light> iter = lights.iterator();
        if (!iter.hasNext()) {
            return;
        }
        builder.append("{\"lights\":\n[");

        //Get all elements that don't print out blanks
        while (iter.hasNext()) {
            Light l = iter.next();
            String lightString = l.next();
            if (l.index == 31) {
                if (!noteHit) { //if onBtnClicked did not detect a valid note hit on previous note,
                    currentCombo = 0; //reset combo
                    life -= 10; //subtract life
                    misses++; //account for miss
                }
                noteHit = false; //indicate new note has not been hit yet
                valid = System.currentTimeMillis() + sleepTime / 2;
                if (l.red == 255) {
                    last_color = "red";
                } else if (l.green == 255) {
                    last_color = "green";
                } else if (l.blue == 255) {
                    last_color = "blue";
                }
            }
            if (lightString.compareTo("") != 0) {
                elements.add(lightString);
                if (l.done) {
                    System.out.print("Notes left: " + lights.size());
                    iter.remove();
                }

            }
        }
        //Create the JSON
        striter = elements.iterator();
        while (striter.hasNext()) {
            lightsbuilder.append(striter.next());
            if (!striter.hasNext()) {
                break;
            }
            //only runs if there is another element THAT HAS A VALUE
            lightsbuilder.append(",");
        }


        //The lights that will be sent
        String lightstr = lightsbuilder.toString();
        //If there is nothing, we are done for now
        if (lightstr.compareTo("") == 0)
            return;
        //add the body
        builder.append(lightstr);
        //add the end
        builder.append("],\n\"propagate\":false}");
        //create the JSON string
        String json = builder.toString();


        //json object to be sent
        JSONObject holder = null;
        try {
            holder = new JSONObject(json);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (notDebug) {
            assert holder != null;
            StringEntity se = null;
            try {
                se = new StringEntity(holder.toString());
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            httpPost.setEntity(se);
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");

            try {
                httpClient.execute(httpPost, responseHandler);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public String loadJSONFromAsset() {
        String json = null;
        try {
            InputStream is = getAssets().open(songFilename);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();

            json = new String(buffer, "UTF-8");
            json = json.replaceAll("[^\\x20-\\x7e]", "");


        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    public void updateValuesInLayout() {
        TextView comboView = (TextView) findViewById(R.id.combo);
        comboView.setText("" + currentCombo);
        TextView lifeView = (TextView) findViewById(R.id.life);
        lifeView.setText("" + life);
    }

    public void onBtnClicked(View v) {
        long currentTime = System.currentTimeMillis();
        if (currentTime >= valid && currentTime <= valid + sleepTime / 2) { //if note was hit

            noteHit = true; //indicate note was hit

            //handle combo
            currentCombo++;
            if (currentCombo > maxCombo) maxCombo = currentCombo;

            //calculate multiplier based on current combo
            int multiplier = 1;
            if (currentCombo > 100) {
                multiplier = 4;
            } else if (currentCombo > 50) {
                multiplier = 3;
            } else if (currentCombo > 25) {
                multiplier = 2;
            }

            //handle score and hits
            score += 100 * multiplier;
            hits++;

            //handle life
            life += multiplier;
            if (life > 100) life = 100;

            updateValuesInLayout();

            //debug messages
            System.out.println("Within time range of a button");
            System.out.println(last_color + " " + valid + " " + currentTime);
            if (v.getId() == R.id.red && last_color == "red") {
                System.out.println("clicked: red");
            } else if (v.getId() == R.id.green && last_color == "green") {
                System.out.println("clicked: green");
            } else if (v.getId() == R.id.blue && last_color == "blue") {
                System.out.println("clicked: blue");
            }
        }
    }

    public void setupAccelerometer() {
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
        builder.setMessage("Shake protection paused the game!")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        onResume();
                    }
                });
        alertDialog = builder.create();
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
                alertDialog.show();
                onPause();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        sensorMan.registerListener((android.hardware.SensorEventListener) this, accelerometer, SensorManager.SENSOR_DELAY_UI);
        focused = true;
        musicShouldPlay = true;
        System.out.println("Resumed");
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorMan.unregisterListener((android.hardware.SensorEventListener) this);
        focused = false;
        musicShouldPlay = false;
        System.out.println("Paused");
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public class Light {
        public int red;
        public int green;
        public int blue;
        public int index;
        public boolean done;

        public Light(int r, int g, int b) {
            red = r;
            green = g;
            blue = b;
            index = 0;
            done = false;
        }

        public Light(int r, int g, int b, int i) {
            red = r;
            green = g;
            blue = b;
            index = i;
            done = false;
        }

        public String next() {
            String s = toString();
            index++;
            if (index >= 32) {
                done = true;
            }
            return s;
        }

        public String toString() {
            if (index < 0) {
                return "";
            } else {
                return String.format(
                        "{\"lightId\": %d,\n" +
                                "\"red\":%s,\"green\":%s,\"blue\":%d,\n" +
                                "\"intensity\": 0.5}",
                        index, red, green, blue);
            }
        }
    }

}
//}
