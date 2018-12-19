package info.camposha.mrmp3player;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {

    private ListView listview;
    private String mediaPath;
    private List<String> songs = new ArrayList<String>();
    private MediaPlayer mediaPlayer = new MediaPlayer();
    private SongsLoaderAsyncTask task;
    ProgressBar songsLoadingProgressBar;
    ArrayList<String> songNames=new ArrayList<>();

    /**
     * We will scan and load all mp3 files in a background thread via asynctask.
     */
    // Use AsyncTask to read all mp3 file names
    private class SongsLoaderAsyncTask extends AsyncTask<Void, String, Void> {
        private List<String> loadedSongs = new ArrayList<String>();

        /**
         * Before our background starts
         */
        protected  void onPreExecute() {
            songsLoadingProgressBar.setVisibility(View.VISIBLE);
            songNames.clear();
            Toast.makeText(getApplicationContext(),"Scanning Songs..Please Wait.",Toast.LENGTH_LONG).show();
        }

        /**
         * Load files in background thread here
         * @param url
         * @return
         */
        protected Void doInBackground(Void... url) {
            updateSongListRecursive(new File(mediaPath));
            return null;
        }

        /**
         * Recursively Load Files From ExternalStorage
         * @param path
         */
        public void updateSongListRecursive(File path) {
            if (path.isDirectory()) {
                for (int i = 0; i < path.listFiles().length; i++) {
                    File file = path.listFiles()[i];
                    updateSongListRecursive(file);
                }
            } else {
                String songPath = path.getAbsolutePath();
                String songName=path.getName();
                publishProgress(songPath);
                if (songPath.endsWith(".mp3")) {
                    loadedSongs.add(songPath);
                    songNames.add(songName.substring(0,songName.length()-4));
                }
            }
        }

        /**
         * When our background work is over.
         * @param args
         */
        protected void onPostExecute(Void args) {
            songsLoadingProgressBar.setVisibility(View.GONE);
            ArrayAdapter<String> songList =  new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, songNames);
            listview.setAdapter(songList);
            songs = loadedSongs;
            Toast.makeText(getApplicationContext(),"Scanning Complete."+songs.size()+" Songs Found.",Toast.LENGTH_LONG).show();
        }
    }

    /*
     *OPEN PLAYER ACTIVITY PASSING THE SONG TO PLAYER
     */
    private void openPlayerActivity(int position)
    {
        Intent i=new Intent(this,PlayerActivity.class);
        i.putExtra("SONG_KEY",songs.get(position));
        startActivity(i);
    }



    /**
     * When the activity is created.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        songsLoadingProgressBar=findViewById(R.id.myProgressBar);
        listview = findViewById(R.id.mListView);
        mediaPath = Environment.getExternalStorageDirectory().getPath() + "/Music/";
        //mediaPath = Environment.getExternalStorageDirectory().getPath() + "/Download/";
		//mediaPath = Environment.getExternalStorageDirectory().getPath() + "/mnt/shared/Other/";
		//mediaPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).getPath() ;
        //mediaPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath() ;
        // itemclick listener for our listview
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                openPlayerActivity(position);
            }
        });
        //instantiate and execute our asynctask
        task = new SongsLoaderAsyncTask();
        task.execute();

    }


    /** Called when the activity is stopped */
    @Override
    public void onStop() {
        super.onStop();
        if (mediaPlayer.isPlaying()) mediaPlayer.reset();
    }



}

