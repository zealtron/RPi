package cs4720furrett.rpimobileproject;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;

public class ResultsScreen extends Activity {
    public static final String CONSUMER_KEY = "s8vGGyRxStg9xbt53NSKBwgSP";
    public static final String CONSUMER_SECRET = "ehSthJoZ4v0PcfcLM9VrEKSoQOyvfQaUFiZSWZhgwzCrD0Qndj";
    public static final AccessToken ACCESS_TOKEN = new AccessToken("2892540280-cpLMtt0LHg4kYJRQxKf4VznjRfLOWjfePyn6VE4", "7fLzIgludIK0t6IsUVgI28vsLIKAGWaUIPgfzqNWZ85WP");
    private String name = "";
    private String score = "";
    private String maxCombo = "";
    private String life = "";
    private String pass = "";
    private String hits = "";
    private String misses = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results_screen);
        SharedPreferences pref = getSharedPreferences("preferences", MODE_PRIVATE);

        name = pref.getString("CLICKED_SONG", "No song");
        score = getIntent().getExtras().getString("SCORE");
        maxCombo = getIntent().getExtras().getString("MAX_COMBO");
        life = getIntent().getExtras().getString("LIFE");
        hits = getIntent().getExtras().getString("HITS");
        misses = getIntent().getExtras().getString("MISSES");

        getActionBar().setTitle("Results of " + name);
        ImageView image = (ImageView) findViewById(R.id.result);
        if (life.equals("0")) {
            image.setImageResource(R.drawable.fail);
            pass = "failed";
        } else {
            image.setImageResource(R.drawable.clear);
            pass = "cleared";
        }

        ListView mainListView = (ListView) findViewById(R.id.listView);

        // Create and populate a List of results.
        String[] results = new String[]{
                "SCORE:\t\t\t" + score,
                "MAX COMBO:\t\t\t" + maxCombo,
                "NOTES HIT:\t\t\t" + hits,
                "NOTES MISSED:\t\t" + misses
        };
        ArrayList<String> resultsList = new ArrayList<String>();
        resultsList.addAll(Arrays.asList(results));

        // Create ArrayAdapter using the planet list.
        ArrayAdapter<String> listAdapter = new ArrayAdapter<String>(this, R.layout.simplerow, resultsList);

        // Set the ArrayAdapter as the ListView's adapter.
        mainListView.setAdapter(listAdapter);
    }

    //Disable Back Button
    @Override
    public void onBackPressed() {
    }

    public void switchToSongList(View view) {
        Intent intent = new Intent(this, SongList.class);
        startActivity(intent);
        finish();
    }

    public void Retry(View view) {
        Intent intent = new Intent(this, GameScreen.class);
        startActivity(intent);
        finish();
    }

    public void Tweet(View view) {
        Thread t = new Thread() {
            @Override
            public void run() {
                Twitter twitter = new TwitterFactory().getInstance();
                twitter.setOAuthConsumer(CONSUMER_KEY, CONSUMER_SECRET);
                twitter.setOAuthAccessToken(ACCESS_TOKEN);
                try {
                    twitter.updateStatus("I just " + pass + " " + name + " on Pi Pi Revolution! I got a score of " + score + " and a max combo of " + maxCombo + "!");
                } catch (TwitterException e) {
                    e.printStackTrace();
                }
            }
        };
        t.start();
    }
}
