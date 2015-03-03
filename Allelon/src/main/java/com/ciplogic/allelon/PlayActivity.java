package com.ciplogic.allelon;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.ciplogic.allelon.notification.StreamNotification;
import com.ciplogic.allelon.player.AMediaPlayer;
import com.ciplogic.allelon.player.AvailableStream;
import com.ciplogic.allelon.player.MediaPlayerListener;
import com.ciplogic.allelon.service.ThreadMediaPlayer;

public class PlayActivity extends Activity implements MediaPlayerListener {
    private AMediaPlayer allelonMediaPlayer = ThreadMediaPlayer.getInstance(this);

    private Button listenButton;
    private Button stopPlayButton;
    private ImageButton closeApplicationButton;

    private SeekBar volumeSeekBar;

    private boolean firstCallPassed = false;

    public static PlayActivity INSTANCE;

    private PlayerStatus playerStatus = allelonMediaPlayer.getPlayerStatus();
    private TextView statusTextView;
    private TextView currentSongTextView;

    private RadioButton contemporanRadioButton;
    private RadioButton classicRadioButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        INSTANCE = this;

        setContentView(R.layout.play_activity);

        findUiComponents();

        addEventListeners();
        updateControlsStatus();

        allelonMediaPlayer.addPlayerListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        allelonMediaPlayer.removePlayerListener(this);
    }

    private void addEventListeners() {
        contemporanRadioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectStream( AvailableStream.CONTEMPORAN );
            }
        });

        classicRadioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectStream( AvailableStream.CLASSIC );
            }
        });

        volumeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                allelonMediaPlayer.setVolume(i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        listenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                allelonMediaPlayer.startPlay(SelectedStream.getSelectedStream().getUrl());
                updateControlsStatus();
            }
        });

        stopPlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                allelonMediaPlayer.stopPlay();
                updateControlsStatus();
            }
        });

        closeApplicationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new StreamNotification().hideNotification();
                System.exit(0);
            }
        });
    }

    /**
     * Selects the given selected stream, and starts playing it.
     * @param selectedStream
     */
    private void selectStream(AvailableStream selectedStream) {
        SelectedStream.setSelectedStream(selectedStream);

        // FIXME: the very first call actually restarts the stream in some scenarios.
        // probably a better solution related to the Activity lifecycle would be in place.
        if (firstCallPassed && allelonMediaPlayer.isPlaying()) {
            allelonMediaPlayer.startPlay(SelectedStream.getSelectedStream().getUrl());
        }

        firstCallPassed = true;
    }

    private void findUiComponents() {
        contemporanRadioButton = (RadioButton) findViewById(R.id.contemporanRadioButton);
        classicRadioButton = (RadioButton) findViewById(R.id.classicRadioButton);
        listenButton = (Button) findViewById(R.id.listenButton);
        stopPlayButton = (Button) findViewById(R.id.stopPlayButton);
        closeApplicationButton = (ImageButton) findViewById(R.id.closeApplicationButton);
        statusTextView = (TextView) findViewById(R.id.statusTextView);
        currentSongTextView = (TextView) findViewById(R.id.currentSong);
        volumeSeekBar = (SeekBar) findViewById(R.id.volumeSeekBar);
    }

    @Override
    protected void onResume() {
        super.onResume();

        updateControlsStatus();
    }

    private void updateControlsStatus() {
        if (allelonMediaPlayer.isPlaying()) {
            if (findIndexOfPlayingStream() % 2 == 0) { // contemporan selected
                contemporanRadioButton.toggle();
            } else { // classical selected
                classicRadioButton.toggle();
            }
        }

        listenButton.setVisibility( allelonMediaPlayer.isPlaying() ? View.INVISIBLE : View.VISIBLE );
        stopPlayButton.setVisibility(allelonMediaPlayer.isPlaying() ? View.VISIBLE : View.INVISIBLE);
        volumeSeekBar.setProgress( allelonMediaPlayer.getVolume() );

        switch (playerStatus) {
            case STOPPED: statusTextView.setText("Stopped"); break;
            case BUFFERING: statusTextView.setText("Buffering"); break;
            case PLAYING: statusTextView.setText("Playing"); break;
        }

        currentSongTextView.setText( allelonMediaPlayer.getCurrentTitle() );
    }

    private int findIndexOfPlayingStream() {
        int index = 0;
        String url = allelonMediaPlayer.getPlayedUrl();

        for (AvailableStream stream : AvailableStream.values()) {
            if (stream.getUrl().equals(url)) {
                return index;
            }

            index++;
        }

        return -1;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.radio_listener, menu);
        return true;
    }

    @Override
    public void onStatusChange(PlayerStatus playerStatus) {
        this.playerStatus = playerStatus;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                updateControlsStatus();
            }
        });
    }

    @Override
    public void onStartStreaming() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                updateControlsStatus();
            }
        });
    }

    @Override
    public void onStopStreaming() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                updateControlsStatus();
            }
        });
    }
}
