package cs4720furrett.rpimobileproject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import twitter4j.*;
import twitter4j.auth.AccessToken;

public class ResultsScreen extends Activity{
    private String name = "";
    private String score = "";
    private String maxCombo = "";

    public static final String CONSUMER_KEY = "s8vGGyRxStg9xbt53NSKBwgSP";
    public static final String CONSUMER_SECRET= "ehSthJoZ4v0PcfcLM9VrEKSoQOyvfQaUFiZSWZhgwzCrD0Qndj";
    public static final AccessToken ACCESS_TOKEN = new AccessToken("2892540280-cpLMtt0LHg4kYJRQxKf4VznjRfLOWjfePyn6VE4","7fLzIgludIK0t6IsUVgI28vsLIKAGWaUIPgfzqNWZ85WP");




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results_screen);
        name = getIntent().getExtras().getString("SONG_NAME");
        score = getIntent().getExtras().getString("SCORE");
        maxCombo = getIntent().getExtras().getString("MAX_COMBO");
        getActionBar().setTitle("Results of " + name);
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

    public void Retry(View view) {
        Intent intent = new Intent(this, GameScreen.class);
        intent.putExtra("CLICKED_SONG", name);
        intent.putExtra("DEBUG", getIntent().getExtras().getString("DEBUG"));
        startActivity(intent);
        finish();
    }

    public void Tweet(View view) {
        Thread t = new Thread(){
            @Override
            public void run() {
                Twitter twitter = new TwitterFactory().getInstance();
                twitter.setOAuthConsumer(CONSUMER_KEY, CONSUMER_SECRET);
                twitter.setOAuthAccessToken(ACCESS_TOKEN);
                try {
                    twitter.updateStatus("I just played " + name + " on Pi Pi Revolution! I got a score of " + score + " and a max combo of " + maxCombo + "!");
                }catch (TwitterException e) {
                    e.printStackTrace();
                }
            }
        };
        t.start();
    }
}
