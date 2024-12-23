package com.example.musicplayer;

import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private MediaPlayer mediaPlayer;
    private String[] songs = {"After Hours", "Die With A Smile","APT","KULOSA","Dancing With A Stranger","BABY I'M BACK ","Rolling in the Deep","Lose Control","Attention","Beautiful People","That's What I Like","Often"}; // Add your song names
    private String[] artists = {"The Weeknd", "Lady Gaga, Bruno Mars","ROSÉ & Bruno Mars","Oxlade, Camila Cabello","Sam Smith, Normani","The Kid LAROI","Adele","Teddy Swims","Charlie Puth","Ed Sheeran","Bruno Mars","Weeknd"}; // Add corresponding artist names
    private int[] songResIds = {R.raw.afterhours, R.raw.diewithasmile, R.raw.apt, R.raw.kulosa, R.raw.dwas, R.raw.bib, R.raw.ritd, R.raw.lc, R.raw.attention, R.raw.bp, R.raw.twil, R.raw.often}; // Add corresponding resource IDs
    private int currentSongIndex = 0; // Tracks the current song index
    private ProgressBar progressBar;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView songListView = findViewById(R.id.songListView);
        Button playButton = findViewById(R.id.playButton);
        Button pauseButton = findViewById(R.id.pauseButton);
        Button nextButton = findViewById(R.id.nextButton);
        Button prevButton = findViewById(R.id.prevButton);
        progressBar = findViewById(R.id.progressBar);

        // Set up custom adapter to show both song name and artist name
        SongAdapter adapter = new SongAdapter();
        songListView.setAdapter(adapter);

        // Handle song selection
        songListView.setOnItemClickListener((AdapterView<?> parent, View view, int position, long id) -> {
            playSong(position);
        });

        // Play button
        playButton.setOnClickListener(v -> {
            if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
                mediaPlayer.start();
                updateProgressBar();
            }
        });

        // Pause button
        pauseButton.setOnClickListener(v -> {
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
            }
        });

        // Next button
        nextButton.setOnClickListener(v -> {
            currentSongIndex = (currentSongIndex + 1) % songs.length;
            playSong(currentSongIndex);
        });

        // Previous button
        prevButton.setOnClickListener(v -> {
            currentSongIndex = (currentSongIndex - 1 + songs.length) % songs.length;
            playSong(currentSongIndex);
        });
    }

    private void playSong(int songIndex) {
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
        mediaPlayer = MediaPlayer.create(this, songResIds[songIndex]);
        mediaPlayer.start();
        currentSongIndex = songIndex;

        // Change the color of the text in the ListView based on the currently playing song
        changeSongTextColor();

        // Update the progress bar and set autoplay feature
        updateProgressBar();

        // Set autoplay to play the next song when the current one finishes
        mediaPlayer.setOnCompletionListener(mp -> {
            // Play the next song
            currentSongIndex = (currentSongIndex + 1) % songs.length;
            playSong(currentSongIndex);
        });
    }

    private void updateProgressBar() {
        if (mediaPlayer == null) return;

        progressBar.setMax(mediaPlayer.getDuration());
        Runnable progressRunnable = new Runnable() {
            @Override
            public void run() {
                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    progressBar.setProgress(mediaPlayer.getCurrentPosition());
                    handler.postDelayed(this, 500);
                }
            }
        };
        handler.post(progressRunnable);
    }

    private void changeSongTextColor() {
        ListView songListView = findViewById(R.id.songListView);
        for (int i = 0; i < songListView.getCount(); i++) {
            View item = songListView.getChildAt(i);
            if (item != null) {
                TextView songTextView = item.findViewById(R.id.songNameTextView);
                TextView artistTextView = item.findViewById(R.id.artistNameTextView);
                if (i == currentSongIndex) {
                    // Change the text color of the currently playing song
                    songTextView.setTextColor(Color.BLACK); // You can choose any color you prefer
                    artistTextView.setTextColor(Color.BLACK); // Change artist color as well
                } else {
                    // Reset text color for other songs
                    songTextView.setTextColor(Color.WHITE);
                    artistTextView.setTextColor(Color.WHITE);
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        super.onDestroy();
    }

    // Custom Adapter for ListView to display song name and artist name
    private class SongAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return songs.length;
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater inflater = LayoutInflater.from(MainActivity.this);
                convertView = inflater.inflate(R.layout.list_item_song, parent, false);
            }

            // Set the song name and artist name in the custom layout
            TextView songNameTextView = convertView.findViewById(R.id.songNameTextView);
            TextView artistNameTextView = convertView.findViewById(R.id.artistNameTextView);

            songNameTextView.setText(songs[position]);
            artistNameTextView.setText(artists[position]);

            return convertView;
        }
    }
}
