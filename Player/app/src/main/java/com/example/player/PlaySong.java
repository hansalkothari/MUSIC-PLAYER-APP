package com.example.player;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

public class PlaySong extends AppCompatActivity {
    TextView textView;
    ImageView play , previous , next;
    ArrayList<File> songs ;
    String textContent;
    int position;

    MediaPlayer mediaPlayer;
    Thread updateSeek;
    SeekBar seekBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_song);
        textView = findViewById(R.id.textView);
        play = findViewById(R.id.play);
        previous = findViewById(R.id.previous);
        next = findViewById(R.id.next);
        seekBar = findViewById(R.id.seekBar);

        Intent i = getIntent();
        Bundle bundle = i.getExtras();
        songs = (ArrayList) bundle.getParcelableArrayList("songlist");
        //textContent = bundle.getParcelable("currentSong");
        textContent = i.getStringExtra("currentsong");
        textView.setText(textContent);
        textView.setSelected(true);
        position = i.getIntExtra("position",0);

        Uri uri = Uri.parse(songs.get(position).toString());
        mediaPlayer = MediaPlayer.create(PlaySong.this,uri);
        mediaPlayer.start();
        seekBar.setMax(mediaPlayer.getDuration());

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.seekTo(seekBar.getProgress());
            }
        });
        updateSeek = new Thread(){
            @Override
            public void run() {
                int currentPosition = 0;
                try {
                    while(currentPosition<mediaPlayer.getDuration()){
                        currentPosition = mediaPlayer.getCurrentPosition();
                        seekBar.setProgress(currentPosition);
                        sleep(800);
                    }
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }

        };
        updateSeek.start();

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mediaPlayer.isPlaying()){
                   play.setImageResource(R.drawable.play);
                   mediaPlayer.pause();
                }
                else{
                    play.setImageResource(R.drawable.pause);
                    mediaPlayer.start();
                }
            }
        });
        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.stop();
                mediaPlayer.release();
                if(position!=0){
                    position -= 1;
                }
                else{
                    position = songs.size()-1;
                }
                Uri uri = Uri.parse(songs.get(position).toString());
                mediaPlayer = MediaPlayer.create(PlaySong.this,uri);
                mediaPlayer.start();
                play.setImageResource(R.drawable.pause);
                seekBar.setMax(mediaPlayer.getDuration());

                textContent = songs.get(position).getName().toString();
                textView.setText(textContent);

            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.stop();
                mediaPlayer.release();
                if(position!=songs.size()-1){
                    position += 1;
                }
                else{
                    position = 0;
                }
                Uri uri = Uri.parse(songs.get(position).toString());
                mediaPlayer = MediaPlayer.create(PlaySong.this,uri);
                mediaPlayer.start();
                play.setImageResource(R.drawable.pause);
                seekBar.setMax(mediaPlayer.getDuration());

                textContent = songs.get(position).getName().toString();
                textView.setText(textContent);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mediaPlayer.stop();
        mediaPlayer.release();
        updateSeek.interrupt();
    }

}