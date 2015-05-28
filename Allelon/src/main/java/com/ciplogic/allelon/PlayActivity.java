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

import com.ciplogic.allelon.eventbus.Event;
import com.ciplogic.allelon.eventbus.EventBus;
import com.ciplogic.allelon.eventbus.EventListener;
import com.ciplogic.allelon.eventbus.ObjectInstantiator;
import com.ciplogic.allelon.eventbus.events.MediaPlayerStatusEvent;
import com.ciplogic.allelon.eventbus.events.MediaPlayerTitleEvent;
import com.ciplogic.allelon.eventbus.events.RequestMediaPlayerStatusEvent;
import com.ciplogic.allelon.eventbus.events.UnknownMediaPlayerStatusEvent;
import com.ciplogic.allelon.eventbus.events.UnknownMediaPlayerTitleEvent;
import com.ciplogic.allelon.eventbus.events.uiactions.ChangeVolumeEvent;
import com.ciplogic.allelon.eventbus.events.uiactions.SelectStreamEvent;
import com.ciplogic.allelon.eventbus.events.uiactions.StartPlayEvent;
import com.ciplogic.allelon.eventbus.events.uiactions.StopPlayEvent;
import com.ciplogic.allelon.notification.StreamNotification;
import com.ciplogic.allelon.player.AvailableStream;

import java.util.ArrayList;
import java.util.List;

public class PlayActivity extends Activity implements EventListener {
    private Button listenButton;
    private Button stopPlayButton;
    private ImageButton closeApplicationButton;

    private SeekBar volumeSeekBar;

    public static PlayActivity INSTANCE;

    private TextView statusTextView;
    private TextView currentSongTextView;

    private RadioButton allelonRadioButton;
    private RadioButton allelonClassicRadioButton;
    private RadioButton allelonRoRadioButton;

    private RadioGroup availableStreamsRadioGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ObjectInstantiator.ensureInstantiated(this);

        INSTANCE = this;

        setContentView(R.layout.play_activity);

        findUiComponents();

        addEventListeners();

        updateControlsStatus(new UnknownMediaPlayerStatusEvent());
        updatePlayedTitleStatus(new UnknownMediaPlayerTitleEvent());

        EventBus.INSTANCE.registerListener(this);
        EventBus.INSTANCE.fire(new RequestMediaPlayerStatusEvent());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.INSTANCE.unregisterListener(this);
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
                EventBus.INSTANCE.fire(new ChangeVolumeEvent(i));
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
                listenButton.setEnabled(false);
                stopPlayButton.setEnabled(false);
                EventBus.INSTANCE.fire(new StartPlayEvent());
            }
        });

        stopPlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listenButton.setEnabled(false);
                stopPlayButton.setEnabled(false);
                EventBus.INSTANCE.fire(new StopPlayEvent());
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
        EventBus.INSTANCE.fire(new SelectStreamEvent(selectedStream));
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

        updateControlsStatus(new UnknownMediaPlayerStatusEvent());
        updatePlayedTitleStatus(new UnknownMediaPlayerTitleEvent());

        EventBus.INSTANCE.fire(new RequestMediaPlayerStatusEvent());
    }

    private int findIndexOfPlayingStream(AvailableStream playedStream) {
        int index = 0;

        for (AvailableStream stream : AvailableStream.values()) {
            if (stream == playedStream) {
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
    public void onEvent(final Event event) {
        if (event instanceof MediaPlayerStatusEvent) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    updateControlsStatus((MediaPlayerStatusEvent) event);
                }
            });
        } else if (event instanceof MediaPlayerTitleEvent) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    updatePlayedTitleStatus((MediaPlayerTitleEvent) event);
                }
            });
        }
    }

    @Override
    public List<Class<? extends Event>> getListenedEvents() {
        List<Class<? extends Event>> result = new ArrayList<Class<? extends Event>>();

        result.add(MediaPlayerStatusEvent.class);
        result.add(MediaPlayerTitleEvent.class);

        return result;
    }

    private void updateControlsStatus(MediaPlayerStatusEvent mediaPlayerStatus) {
        if (mediaPlayerStatus.playing) {
            int indexOfPlayingStream = findIndexOfPlayingStream(mediaPlayerStatus.playedStream);
            switch (indexOfPlayingStream % 3) {
                case 0: allelonRadioButton.toggle(); break;
                case 1: allelonRoRadioButton.toggle(); break;
                case 2: allelonClassicRadioButton.toggle(); break;
            }
        }

        listenButton.setVisibility( mediaPlayerStatus.playing ? View.INVISIBLE : View.VISIBLE );
        stopPlayButton.setVisibility(mediaPlayerStatus.playing ? View.VISIBLE : View.INVISIBLE);

        listenButton.setEnabled(true);
        stopPlayButton.setEnabled(true);

        volumeSeekBar.setProgress(mediaPlayerStatus.volume);

        switch (mediaPlayerStatus.playerStatus) {
            case STOPPED: statusTextView.setText("Stopped"); break;
            case BUFFERING: statusTextView.setText("Buffering"); break;
            case PLAYING: statusTextView.setText("Playing"); break;
        }
    }

    private void updatePlayedTitleStatus(MediaPlayerTitleEvent mediaPlayerTitleEvent) {
        currentSongTextView.setText(mediaPlayerTitleEvent.title);
    }
}
