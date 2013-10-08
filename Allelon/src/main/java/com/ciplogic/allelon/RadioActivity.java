package com.ciplogic.allelon;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;

import com.ciplogic.allelon.notification.StreamNotification;
import com.ciplogic.allelon.player.AllelonMediaPlayer;
import com.ciplogic.allelon.player.AvailableStream;

public class RadioActivity extends Activity {

    private AllelonMediaPlayer allelonMediaPlayer = new AllelonMediaPlayer();
    private MediaPlayerNotificationListener listener;

    private Button listenButton;
    private Button closeButton;
    private Spinner availableStreamsSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_radio_listener);

        listener = new MediaPlayerNotificationListener(new StreamNotification(this));
        allelonMediaPlayer.addPlayerListener(listener);

        availableStreamsSpinner = (Spinner) findViewById(R.id.availableStreams);
        availableStreamsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (allelonMediaPlayer.isPlaying()) {
                    allelonMediaPlayer.startPlay(getSelectedStream());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        listenButton = (Button) findViewById(R.id.listenButton);
        listenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                allelonMediaPlayer.startPlay(getSelectedStream());
                updateButtonVisibility();
            }
        });

        closeButton = (Button) findViewById(R.id.closeButton);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                allelonMediaPlayer.stopPlay();
                updateButtonVisibility();
            }
        });

        updateButtonVisibility();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        allelonMediaPlayer.removePlayerListener(listener);
    }

    private String getSelectedStream() {
        AvailableStream stream = AvailableStream.valueOf(
            availableStreamsSpinner.getSelectedItem().toString().toUpperCase()
        );

        return stream.getUrl();
    }

    @Override
    protected void onResume() {
        super.onResume();

        updateButtonVisibility();
    }

    private void updateButtonVisibility() {
        listenButton.setVisibility( allelonMediaPlayer.isPlaying() ? View.INVISIBLE : View.VISIBLE );
        closeButton.setVisibility(allelonMediaPlayer.isPlaying() ? View.VISIBLE : View.INVISIBLE);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.radio_listener, menu);
        return true;
    }
    
}
