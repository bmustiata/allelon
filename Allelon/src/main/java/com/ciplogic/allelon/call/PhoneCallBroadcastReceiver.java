package com.ciplogic.allelon.call;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

import com.ciplogic.allelon.service.ThreadMediaPlayer;

public class PhoneCallBroadcastReceiver extends BroadcastReceiver {
    private static MutePlayerPhoneStateListener mutePlayerPhoneStateListener;

    @Override
    public void onReceive(Context context, Intent intent) {
        ThreadMediaPlayer allelonMediaPlayer = ThreadMediaPlayer.getInstance(context);

        TelephonyManager telephony = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
        MutePlayerPhoneStateListener customPhoneListener = getMutePlayerPhoneStateListener(allelonMediaPlayer);

        telephony.listen(customPhoneListener, PhoneStateListener.LISTEN_CALL_STATE);
    }

    private MutePlayerPhoneStateListener getMutePlayerPhoneStateListener(ThreadMediaPlayer allelonMediaPlayer) {
        if (mutePlayerPhoneStateListener == null) {
            mutePlayerPhoneStateListener = new MutePlayerPhoneStateListener(allelonMediaPlayer);
        }

        return mutePlayerPhoneStateListener;
    }
}
