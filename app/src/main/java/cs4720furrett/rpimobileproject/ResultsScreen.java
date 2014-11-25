package cs4720furrett.rpimobileproject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class ResultsScreen extends Activity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results_screen);
        getActionBar().setTitle("Results");
    }

    @Override
    public void onBackPressed()
    {
    }

    public void switchToSongList(View view) {
        Intent intent = new Intent(this, SongList.class);
        startActivity(intent);
        finish();
    }
}
