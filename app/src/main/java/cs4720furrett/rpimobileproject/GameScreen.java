package cs4720furrett.rpimobileproject;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;


public class GameScreen extends Activity {

    private final long SEED = 9001;
    private final String postURL = "http://10.0.0.60/rpi";
    private final int speed = 100;
    //Handles what is returned from the page
    ResponseHandler responseHandler;
    private ArrayList<Light> lights;
    private MainThread thread;
    private Random rng;
    private StringBuilder builder;
    private StringBuilder lightsbuilder;
    private DefaultHttpClient httpClient;
    private HttpPost httpPost;
    private int count = 0;
    private java.lang.String songFilename = "songs.json";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_screen);

        httpClient = new DefaultHttpClient();
        httpPost = new HttpPost(postURL);
        responseHandler = new BasicResponseHandler();
        builder = new StringBuilder();
        lightsbuilder = new StringBuilder();
        rng = new Random(SEED);
        lights = new ArrayList<Light>();
        lights.add(new Light(255, 0, 0, -100));
        String data = getIntent().getExtras().getString("CLICKED_SONG");
        System.out.println(data);
        TextView mTextView = (TextView) findViewById(R.id.fullscreen_content);
        mTextView.setText(data);

        String songsInJSON = loadJSONFromAsset();
        System.out.println(songsInJSON);

        thread = new MainThread();
        thread.setRunning(true);
        thread.start();
    }

    public void sendPost() {
        builder.delete(0, builder.length());
        lightsbuilder.delete(0, lightsbuilder.length());
        Iterator<Light> iter = lights.iterator();
        if (!iter.hasNext()) {
            return;
        }
        builder.append("{\"lights\":\n[");
        while (iter.hasNext()) {
            Light l = iter.next();
            if (l.index >= 0) {
                lightsbuilder.append(l.next());
                if (l.done) {
                    iter.remove();
                }
                if (!iter.hasNext()) {
                    break;
                }
                lightsbuilder.append(",");
            }
        }
        String lightstr = lightsbuilder.toString();

        if (lightstr.compareTo("") == 0)
            return;
        builder.append(lightstr);
        builder.append("],\n\"propagate\":false}");
        String json = builder.toString();
        //clear builder


        //json object to be sent
        JSONObject holder = null;
        try {
            holder = new JSONObject(json);
        } catch (JSONException e) {
            e.printStackTrace();
        }

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

    public class MainThread extends Thread {

        // flag to hold game state
        private boolean running;

        public void setRunning(boolean running) {
            this.running = running;
        }

        @Override
        public void run() {

            long startTime, endTime;
            while (running) {
                startTime = System.currentTimeMillis();
                if (Math.random() > .9) {
                    lights.add(new Light(rng.nextInt(11) * 25, rng.nextInt(11) * 25, rng.nextInt(11) * 25));
                }
                sendPost();
                endTime = System.currentTimeMillis();

                try {
                    Thread.sleep(200 - (endTime - startTime));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    break;
                } catch (IllegalArgumentException e) {
                    System.out.print("too slow");
                    System.out.print(endTime - startTime);
                }
                count++;
            }

            // update game state
            // render state to the screen
        }
    }
}
//}
