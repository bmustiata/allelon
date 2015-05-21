package com.ciplogic.allelon;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.ciplogic.allelon.notification.StreamNotification;
import com.ciplogic.allelon.player.AvailableStream;
import com.ciplogic.allelon.player.MediaPlayerListener;
import com.ciplogic.allelon.service.ThreadMediaPlayer;

public class PlayActivity extends Activity implements MediaPlayerListener {
    private ThreadMediaPlayer allelonMediaPlayer = ThreadMediaPlayer.getInstance(this);

    private Button listenButton;
    private Button stopPlayButton;
    private ImageButton closeApplicationButton;

    private SeekBar volumeSeekBar;

    public static PlayActivity INSTANCE;

    private PlayerStatusChangeEvent playerStatus = allelonMediaPlayer.getPlayerStatus();
    private TextView statusTextView;
    private TextView currentSongTextView;

    private RadioButton allelonRadioButton;
    private RadioButton allelonClassicRadioButton;
    private RadioButton allelonRoRadioButton;

    private RadioGroup availableStreamsRadioGroup;

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
        availableStreamsRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    // always use the MP3 stream, until figuring out what is wrong with AAC.
                    case R.id.allelonRadioButton:
                        selectStream(android.os.Build.VERSION.SDK_INT >= 19 ? // KitKat, Android 4.4 and up
                                AvailableStream.ALLELON :
                                AvailableStream.ALLELON);
                        break;
                    case R.id.allelonRoRadioButton:
                        selectStream(android.os.Build.VERSION.SDK_INT >= 19 ? // KitKat, Android 4.4 and up
                                AvailableStream.ALLELON_RO :
                                AvailableStream.ALLELON_RO);
                        break;
                    case R.id.allelonClassicRadioButton:
                        selectStream(android.os.Build.VERSION.SDK_INT >= 19 ? // KitKat, Android 4.4 and up
                                AvailableStream.ALLELON_CLASSIC :
                                AvailableStream.ALLELON_CLASSIC);
                        break;
                }
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
        if (allelonMediaPlayer.isPlaying()) {
            allelonMediaPlayer.startPlay(SelectedStream.getSelectedStream().getUrl());
        }
    }

    private void findUiComponents() {
        availableStreamsRadioGroup = (RadioGroup) findViewById(R.id.availableStreams);

        allelonRadioButton = (RadioButton) findViewById(R.id.allelonRadioButton);
        allelonRoRadioButton = (RadioButton) findViewById(R.id.allelonRoRadioButton);
        allelonClassicRadioButton = (RadioButton) findViewById(R.id.allelonClassicRadioButton);

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
            int indexOfPlayingStream = findIndexOfPlayingStream();
            switch (indexOfPlayingStream % 3) {
                case 0: allelonRadioButton.toggle(); break;
                case 1: allelonRoRadioButton.toggle(); break;
                case 2: allelonClassicRadioButton.toggle(); break;
            }
        }

        listenButton.setVisibility( allelonMediaPlayer.isPlaying() ? View.INVISIBLE : View.VISIBLE );
        stopPlayButton.setVisibility(allelonMediaPlayer.isPlaying() ? View.VISIBLE : View.INVISIBLE);
        volumeSeekBar.setProgress( allelonMediaPlayer.getVolume() );

        switch (playerStatus.playerStatus) {
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
    public void onStatusChange(PlayerStatusChangeEvent playerStatus) {
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
