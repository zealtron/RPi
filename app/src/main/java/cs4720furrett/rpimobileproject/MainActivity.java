package cs4720furrett.rpimobileproject;

import android.app.Activity;
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

        EditText editText  = (EditText)findViewById(R.id.editText);
        String postURL = editText.getText().toString();

        String json = "{\n" +
                "\"lights\": [\n" +
                "\n" +
                "{\"lightId\": 1, \"red\":255,\"green\":0,\"blue\":0, \"intensity\": 0.3},\n" +
                "{\"lightId\": 10, \"red\":0,\"green\":0,\"blue\":255, \"intensity\": 0.5},\n" +
                "{\"lightId\": 15, \"red\":255,\"green\":0,\"blue\":255, \"intensity\": 0.5},\n" +
                "{\"lightId\": 20, \"red\":0,\"green\":255,\"blue\":0, \"intensity\": 0.7}],\n" +
                "\n" +
                "\"propagate\": true\n" +
                "}";

        DefaultHttpClient httpClient = new DefaultHttpClient();

        //make connection to path
        HttpPost httpPost = new HttpPost(postURL);

        //json object to be sent
        JSONObject holder = new JSONObject(json);

        StringEntity se = new StringEntity(holder.toString());

        httpPost.setEntity(se);
        httpPost.setHeader("Accept", "application/json");
        httpPost.setHeader("Content-type", "application/json");

        //Handles what is returned from the page
        ResponseHandler responseHandler = new BasicResponseHandler();
        httpClient.execute(httpPost, responseHandler);
    }
}
