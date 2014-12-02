package cs4720furrett.rpimobileproject;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.ToggleButton;


public class MySettings extends Activity {
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private String url = "";
    private ToggleButton toggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pref = getSharedPreferences("preferences", MODE_PRIVATE);
        editor = pref.edit();
        setContentView(R.layout.activity_my_settings);
        getActionBar().setTitle("Settings");

        toggle = (ToggleButton) findViewById(R.id.debugToggle);
        String debug = pref.getString("DEBUG", "OFF");
        toggle.setText(debug);


        SeekBar noteBar = (SeekBar) findViewById(R.id.noteSpeed_slider);
        noteBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onStopTrackingTouch(SeekBar bar) {
            }

            public void onStartTrackingTouch(SeekBar bar) {
            }

            public void onProgressChanged(SeekBar bar, int paramInt, boolean paramBoolean) {
                editor.putInt("NOTE_SPEED", paramInt);
                System.out.println(paramInt);
            }
        });
        SeekBar shakeBar = (SeekBar) findViewById(R.id.motionProtection_slider);
        shakeBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onStopTrackingTouch(SeekBar bar) {
            }

            public void onStartTrackingTouch(SeekBar bar) {
            }

            public void onProgressChanged(SeekBar bar, int paramInt, boolean paramBoolean) {
                editor.putInt("MOTION", paramInt);
                System.out.println(paramInt);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_song_list, menu);
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

    public void done(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
    }

    public void setURL(View view) {
        EditText urlView = (EditText) findViewById(R.id.editText);
        url = urlView.getText().toString();

        editor.putString("url", url);
        editor.commit();
        System.out.println("URL now set to: " + url);
    }

    public void setDebug(View view) {
        String value = (toggle.getText() == "ON") ? "ON" : "OFF";

        editor.putString("DEBUG", (String) toggle.getText());
        editor.apply();
        editor.commit();
    }
}

