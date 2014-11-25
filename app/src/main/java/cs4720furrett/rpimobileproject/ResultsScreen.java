package cs4720furrett.rpimobileproject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import twitter4j.*;
import twitter4j.auth.AccessToken;

public class ResultsScreen extends Activity{
    private String name = "";

    public static final String CONSUMER_KEY = "s8vGGyRxStg9xbt53NSKBwgSP";
    public static final String CONSUMER_SECRET= "ehSthJoZ4v0PcfcLM9VrEKSoQOyvfQaUFiZSWZhgwzCrD0Qndj";
    public static final AccessToken ACCESS_TOKEN = new AccessToken("2892540280-cpLMtt0LHg4kYJRQxKf4VznjRfLOWjfePyn6VE4","7fLzIgludIK0t6IsUVgI28vsLIKAGWaUIPgfzqNWZ85WP");
    public static final String REQUEST_URL = "http://api.twitter.com/oauth/request_token";
    public static final String ACCESS_URL = "http://api.twitter.com/oauth/access_token";
    public static final String AUTHORIZE_URL = "http://api.twitter.com/oauth/authorize";
    final public static String  CALLBACK_SCHEME = "x-latify-oauth-twitter";
    final public static String  CALLBACK_URL = CALLBACK_SCHEME + "://callback";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results_screen);
        name = getIntent().getExtras().getString("SONG_NAME");
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
                    twitter.updateStatus("Tweet!");
                }catch (TwitterException e) {
                    e.printStackTrace();
                }
            }
        };
        t.start();
    }
}
