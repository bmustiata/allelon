package com.ciplogic.allelon;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.ciplogic.allelon.player.AMediaPlayer;
import com.ciplogic.allelon.player.AvailableStream;
import com.ciplogic.allelon.player.MediaPlayerListener;
import com.ciplogic.allelon.service.ThreadMediaPlayer;

public class PlayActivity extends Activity implements MediaPlayerListener {
    private AMediaPlayer allelonMediaPlayer = ThreadMediaPlayer.getInstance(this);

    private Button listenButton;
    private Button closeButton;
    private Spinner availableStreamsSpinner;

    private SeekBar volumeSeekBar;

    private boolean firstCallPassed = false;

    public static PlayActivity INSTANCE;

    private PlayerStatus playerStatus = allelonMediaPlayer.getPlayerStatus();
    private TextView statusTextView;
    private TextView currentSongTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        INSTANCE = this;

        setContentView(R.layout.activity_radio_listener);

        findUiComponents();

        fixAspectRatioForImageIfNeeded();
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
        availableStreamsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                // FIXME: the very first call actually restarts the stream in some scenarios.
                // probably a better solution related to the Activity lifecycle would be in place.
                if (firstCallPassed && allelonMediaPlayer.isPlaying()) {
                    allelonMediaPlayer.startPlay(getSelectedStream());
                }

                firstCallPassed = true;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
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
                allelonMediaPlayer.startPlay(getSelectedStream());
                updateControlsStatus();
            }
        });

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                allelonMediaPlayer.stopPlay();
                updateControlsStatus();
            }
        });
    }

    private void findUiComponents() {
        availableStreamsSpinner = (Spinner) findViewById(R.id.availableStreams);
        listenButton = (Button) findViewById(R.id.listenButton);
        closeButton = (Button) findViewById(R.id.closeButton);
        statusTextView = (TextView) findViewById(R.id.statusTextView);
        currentSongTextView = (TextView) findViewById(R.id.currentSong);
        volumeSeekBar = (SeekBar) findViewById(R.id.volumeSeekBar);
    }

    private void fixAspectRatioForImageIfNeeded() {
        float aspect;

        if (isVerticalLayout()) {
            aspect = (960.0f / 713.0f); // 713 / 960
        } else {
            aspect = (171.0f / 410.0f); // 713 / 960
        }

        resizeImageToFillScreen(aspect);
    }

    private void resizeImageToFillScreen(float aspect) {
        ImageView imageView = (ImageView) findViewById(R.id.imageView);
        imageView.setLayoutParams(new RelativeLayout.LayoutParams((int) getWidth(), (int) (aspect * getWidth())));
    }

    private boolean isVerticalLayout() {
        return this.getResources().getConfiguration().orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
    }

    public float getWidth() {
        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        return outMetrics.widthPixels;
    }

    private String getSelectedStream() {
        AvailableStream stream = AvailableStream.fromLabel(
                availableStreamsSpinner.getSelectedItem().toString()
        );

        return stream.getUrl();
    }

    @Override
    protected void onResume() {
        super.onResume();

        updateControlsStatus();
    }

    private void updateControlsStatus() {
        if (allelonMediaPlayer.isPlaying()) {
            availableStreamsSpinner.setSelection( findIndexOfPlayingStream() );
        }

        listenButton.setVisibility( allelonMediaPlayer.isPlaying() ? View.INVISIBLE : View.VISIBLE );
        closeButton.setVisibility(allelonMediaPlayer.isPlaying() ? View.VISIBLE : View.INVISIBLE);
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
