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
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.ciplogic.allelon.player.AMediaPlayer;
import com.ciplogic.allelon.player.AvailableStream;
import com.ciplogic.allelon.service.ThreadMediaPlayer;

public class RadioActivity extends Activity {
    private AMediaPlayer allelonMediaPlayer = ThreadMediaPlayer.getInstance(this);

    private Button listenButton;
    private Button closeButton;
    private Spinner availableStreamsSpinner;

    private boolean firstCallPassed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_radio_listener);

        findUiComponents();

        fixAspectRatioForImageIfNeeded();
        addEventListeners();
        updateButtonVisibility();
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

        listenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                allelonMediaPlayer.startPlay(getSelectedStream());
                updateButtonVisibility();
            }
        });

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                allelonMediaPlayer.stopPlay();
                updateButtonVisibility();
            }
        });
    }

    private void findUiComponents() {
        availableStreamsSpinner = (Spinner) findViewById(R.id.availableStreams);
        listenButton = (Button) findViewById(R.id.listenButton);
        closeButton = (Button) findViewById(R.id.closeButton);
    }

    private void fixAspectRatioForImageIfNeeded() {
        if (isVerticalLayout()) {
            ImageView imageView = (ImageView) findViewById(R.id.imageView);
            float aspect = (171.0f / 410.0f);
            imageView.setLayoutParams(new LinearLayout.LayoutParams((int) getWidth(), (int) (aspect * getWidth())));
        }
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
