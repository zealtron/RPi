package cs4720furrett.rpimobileproject;

import android.app.Activity;
import android.os.Bundle;
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
import java.util.Vector;


public class GameScreen extends Activity {

    private final long SEED = 9001;
    private final String postURL = "http://192.168.2.14/rpi";
    private final int speed = 100;
    //Handles what is returned from the page
    ResponseHandler responseHandler;
    private ArrayList<Light> lights = new ArrayList<Light>();
    private MainThread thread;
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
        lights.add(new Light(255, 0, 0, -100));
        String data = getIntent().getExtras().getString("CLICKED_SONG");
        System.out.println(data);
        TextView mTextView = (TextView) findViewById(R.id.fullscreen_content);
        mTextView.setText(data);

        String songsInJSON = loadJSONFromAsset();
        System.out.println("songJSON");
        System.out.println(songsInJSON);
        System.out.println("done");
        try {
            JSONObject j = new JSONObject(songsInJSON);
            System.out.println(j.toString());
            JSONArray song = j.getJSONArray(data);
            System.out.println(song.toString());
            for(int x = 0; x < song.length(); x++) {
                JSONObject light_data = song.getJSONObject(x);
                System.out.println(light_data.toString());
                int index = light_data.getInt("index")*-1 - 32;
                String color = light_data.getString("color");
                System.out.println("\t"+index+"\t"+ color);
                Light light;
                if(color.compareTo("red") == 0){
                    System.out.println("red");
                    light = new Light(255, 0 , 0, index);
                }
                else if(color.compareTo("green") == 0){
                    System.out.println("green");
                    light = new Light(0, 255 , 0, index);
                }
                else {
                    System.out.println("blue");
                    light = new Light(0, 0 , 255, index);
                }

                System.out.println(light.toString());
                lights.add(light);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        System.out.println(lights.toString());
        thread = new MainThread();
        thread.setRunning(true);
        thread.start();
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
            if (lightString.compareTo("") != 0) {
                elements.add(lightString);
                if (l.done) {
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
        System.out.println(lightstr);
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
        if(notDebug) {
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
            System.out.println("run: "+lights.toString());
            long startTime, endTime;
            while (running) {
                startTime = System.currentTimeMillis();
                if (Math.random() > 2) {
                    lights.add(new Light(rng.nextInt(11) * 25, rng.nextInt(11) * 25, rng.nextInt(11) * 25));
                }
                sendPost();
                System.out.println(count);
                endTime = System.currentTimeMillis();

                try {
                    this.sleep(200 - (endTime - startTime));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    break;
                } catch (IllegalArgumentException e) {
                    System.out.print("too slow");
                    System.out.print(endTime - startTime);
                }
                count++;
            }
        }
    }
    public void onBtnClicked(View v){
        if(v.getId() == R.id.red){
           System.out.println("clicked: red");
        }
        else if(v.getId() == R.id.green){
            System.out.println("clicked: green");
        }
        else if(v.getId() == R.id.blue){
            System.out.println("clicked: blue");
        }
    }

}
//}
