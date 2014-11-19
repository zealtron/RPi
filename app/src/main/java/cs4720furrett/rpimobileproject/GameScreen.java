package cs4720furrett.rpimobileproject;

import android.app.Activity;
import android.os.AsyncTask;
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
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;


public class GameScreen extends Activity {

    private final long SEED = 9001;
    private ArrayList<Light> lights;
    private MainThread thread;
    private Random rng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_screen);

        rng = new Random(SEED);
        lights = new ArrayList<Light>();
        lights.add(new Light(255, 0, 0, -100));
        String data = getIntent().getExtras().getString("CLICKED_SONG");
        System.out.println(data);
        TextView mTextView = (TextView) findViewById(R.id.fullscreen_content);
        mTextView.setText(data);
        thread = new MainThread();
        thread.setRunning(true);
        thread.start();
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
            }
            return String.format(
                    "{\"lightId\": %d,\n" +
                            "\"red\":%s,\"green\":%s,\"blue\":%d,\n" +
                            "\"intensity\": 0.5}",
                    index, red, green, blue);
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
            int count = 0;
            int modval = 10000;

            while (running) {
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    break;
                }

                System.out.println(count);
                if (Math.random() > .9) {
                    int red = rng.nextInt(11) * 25;
                    int blue = rng.nextInt(11) * 25;
                    int green = rng.nextInt(11) * 25;
                    Light l = new Light(red, green, blue);
                    System.out.println(l);
                    lights.add(l);
                }
                String postURL = "http://10.0.0.60/rpi";
                new SendPost().execute(postURL);
                count++;
            }

            // update game state
            // render state to the screen
        }
    }

    private class SendPost extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            String postURL = params[0];
            StringBuilder builder = new StringBuilder();
            builder.append("{\"lights\":\n[");
            Iterator<Light> iter = lights.iterator();
            while (iter.hasNext()) {
                builder.append(iter.next().next());
                if (!iter.hasNext()) {
                    break;
                }
                builder.append(",");
            }
            builder.append("],\n\"propagate\":");
            builder.append("false}");
            String json = builder.toString();
            DefaultHttpClient httpClient = new DefaultHttpClient();

            //make connection to path
            HttpPost httpPost = new HttpPost(postURL);

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

            //Handles what is returned from the page
            ResponseHandler responseHandler = new BasicResponseHandler();
            try {
                httpClient.execute(httpPost, responseHandler);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
