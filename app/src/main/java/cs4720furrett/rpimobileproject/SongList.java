package cs4720furrett.rpimobileproject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;


public class SongList extends Activity {
    final Context context = this;
    public String url = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_list);

        ListView mainListView = (ListView) findViewById(R.id.list);

        // Create and populate a List of planet names.
        String[] planets = new String[]{"Jingle Bells", "Deck the Halls"};
        ArrayList<String> planetList = new ArrayList<String>();
        planetList.addAll(Arrays.asList(planets));

        // Create ArrayAdapter using the planet list.
        ArrayAdapter<String> listAdapter = new ArrayAdapter<String>(this, R.layout.simplerow, planetList);

        // Add more planets. If you passed a String[] instead of a List<String>
        // into the ArrayAdapter constructor, you must not add more items.
        // Otherwise an exception will occur.
        //listAdapter.add("Ceres");


        // Set the ArrayAdapter as the ListView's adapter.
        mainListView.setAdapter(listAdapter);
        // React to user clicks on item
        mainListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parentAdapter, View view, int position,
                                    long id) {

                // We know the View is a TextView so we can cast it
                TextView clickedView = (TextView) view;
                SharedPreferences.Editor editor = getSharedPreferences("preferences", MODE_PRIVATE).edit();
                editor.putString("CLICKED_SONG", (String) clickedView.getText());

                editor.apply();
                SharedPreferences pref = getSharedPreferences("preferences", MODE_PRIVATE);
                String storedUrl = pref.getString("url", null);
                //System.out.println(storedUrl);
                if (storedUrl== null || storedUrl.equals("/rpi") ) {
                    urlAlert();
                } else {
                    Intent intent = new Intent(context, GameScreen.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

    public void urlAlert() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setMessage("You need to set the URL of your Raspberry Pi before playing. Please head back to the Main Menu and enter your URL in the Settings page.");
        alert.setTitle("Invalid URL");
        alert.setPositiveButton("OK", null);
        alert.setCancelable(true);
        alert.create().show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_song_list, menu);
        return true;
    }

    //Disable Back Button
    @Override
    public void onBackPressed() {
    }

    public void switchToMainMenu(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
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
}
