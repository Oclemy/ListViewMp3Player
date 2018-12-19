package info.camposha.mrmp3player;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;

import java.io.IOException;


public class PlayerActivity extends AppCompatActivity {

    private MediaPlayer mediaPlayer = new MediaPlayer();

    TextView songTxt;
    LottieAnimationView rippleAnimation;
    String songToPlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        initializeViews();
        receiveSong();
        playSong();
    }

    /**
     * Initialize widgets
     */
    private void initializeViews(){
        songTxt=findViewById(R.id.songTxt);
        rippleAnimation = findViewById(R.id.lottieloading);

    }
    private void receiveSong(){

        //receive passed song via intent
        Intent i=this.getIntent();
        songToPlay=i.getStringExtra("SONG_KEY");
    }

    private void playSong(){
        //TextView to display the song name
        songTxt.setText(songToPlay);

        if(TextUtils.isEmpty(songToPlay))
        {
            Toast.makeText(this, "Hey PlayerActivity has received a null or empty song.", Toast.LENGTH_LONG).show();
        }
        else{
            try {
                mediaPlayer.reset();
                // in recursive version
                mediaPlayer.setDataSource(songToPlay);
                mediaPlayer.prepare();
                mediaPlayer.start();

            } catch(IOException e) {
                Toast.makeText(getBaseContext(), "Cannot Play Song!", Toast.LENGTH_SHORT).show();

            }
        }
        showSplash();
    }

    public void showSplash() {
        if (mediaPlayer.isPlaying()) {
            rippleAnimation.setAnimation(R.raw.ripple);
            rippleAnimation.playAnimation();
        } else {
            rippleAnimation.pauseAnimation();
        }
    }

    /** Called when the activity is stopped */
    @Override
    public void onStop() {
        super.onStop();
        //if you want to stop playing when this activity is closed then uncomment this.
//        if (mediaPlayer.isPlaying()){
//            mediaPlayer.reset();
//            rippleAnimation.pauseAnimation();
//        }
    }


}
