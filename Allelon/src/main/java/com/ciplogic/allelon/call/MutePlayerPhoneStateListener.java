package com.ciplogic.allelon.call;

import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.ciplogic.allelon.service.ThreadMediaPlayer;

/**
 * FIXME: move this to the event bus.
 */
public class MutePlayerPhoneStateListener extends PhoneStateListener {
    private final ThreadMediaPlayer allelonMediaPlayer;

    private int oldVolume;
    private boolean muted;

    public MutePlayerPhoneStateListener(ThreadMediaPlayer allelonMediaPlayer) {
        this.allelonMediaPlayer = allelonMediaPlayer;
    }

    @Override
    public void onCallStateChanged(int state, String incomingNumber) {
        switch(state){
            case TelephonyManager.CALL_STATE_RINGING:
            case TelephonyManager.CALL_STATE_OFFHOOK:
                mutePlayer();
                break;
            case TelephonyManager.CALL_STATE_IDLE:
                restorePlayerVolume();
                break;
        }
    }

    private void mutePlayer() {
        if (!muted) {
            muted = true;
            //oldVolume = allelonMediaPlayer.getVolume();
            //allelonMediaPlayer.setVolume(0);
        }
    }

    private void restorePlayerVolume() {
        //allelonMediaPlayer.setVolume(oldVolume);
        muted = false;
    }
}
