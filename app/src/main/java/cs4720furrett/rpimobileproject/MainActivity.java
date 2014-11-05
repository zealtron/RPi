package cs4720furrett.rpimobileproject;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
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


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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

    /* Sends post */
    public void sendPost(View view) throws IOException, JSONException {

        //get url from field
        EditText editText  = (EditText)findViewById(R.id.editText);
        String postURL = editText.getText().toString();
        if (!postURL.startsWith("http://")) postURL = "http://" + postURL;
        //String postURL = "http://cs4720.cs.virginia.edu/rpi/?username=csh7kd"; //hard coding for now
        new SendPost().execute(postURL);
    }

    private class SendPost extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            String postURL = params[0];
            String json = "{\n" +
                    "\"lights\": [\n" +
                    "\n" +
                    "{\"lightId\": 1, \"red\":255,\"green\":0,\"blue\":0, \"intensity\": 0.3}],\n" +

                    "\n" +
                    "\"propagate\": true\n" +
                    "}";

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
