package cs4720furrett.rpimobileproject;

import cs4720furrett.rpimobileproject.util.SystemUiHider;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.MenuItem;
import android.support.v4.app.NavUtils;
import android.widget.TextView;



public class GameScreen extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_screen);

        String data = getIntent().getExtras().getString("CLICKED_SONG");
        System.out.println(data);
        TextView mTextView = (TextView) findViewById(R.id.fullscreen_content);
        mTextView.setText(data);
    }
}
