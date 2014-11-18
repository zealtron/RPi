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
import java.io.UnsupportedEncodingException;
import java.util.Random;


public class GameScreen extends Activity {


    private MainThread thread;
    private Random rng;
    private final long SEED = 9001;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_screen);

        rng = new Random(SEED);
        String data = getIntent().getExtras().getString("CLICKED_SONG");
        System.out.println(data);
        TextView mTextView = (TextView) findViewById(R.id.fullscreen_content);
        mTextView.setText(data);
        thread = new MainThread();
        thread.setRunning(true);
        thread.start();
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
            while (running) {
                if (count % 1000 == 0) {
                    sendPost("http://10.0.0.60/rpi");
                }
                count++;
                System.out.println("Sent");
                // update game state
                // render state to the screen
            }
        }
        public void sendPost(String postURL) {
            int red =   rng.nextInt(11)*25;
            int blue =  rng.nextInt(11)*25;
            int green = rng.nextInt(11)*25;
            String json = "{\n" +
                    "\"lights\": [\n" +
                    "\n" +
                    "{\"lightId\": 1, \"red\":"+red+",\"green\":"+green+",\"blue\":"+blue+", \"intensity\": 0.3}],\n" +
                    "\n" +
                    "\"propagate\": true\n" +
                    "}";
            System.out.println(red + "\t" + green + "\t" + blue + "\t");
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
        }
    }

}
