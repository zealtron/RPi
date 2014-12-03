package cs4720furrett.rpimobileproject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


public class MainActivity extends Activity {

    final Context context = this;
    //The dialog that pops up


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getActionBar().setTitle("Pi Pi Revolution");

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
        startActivity(intent);
        finish();
    }

    public void switchToSettings(View view) {
        Intent intent = new Intent(this, MySettings.class);
        startActivity(intent);
        finish();
    }

    public void switchToHowToPlay(View view) {
        Intent intent = new Intent(this, HowToPlay.class);
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


    //Disable Back Button
    @Override
    public void onBackPressed() {
    }


}
