package com.ciplogic.allelon.call;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

import com.ciplogic.allelon.eventbus.EventBus;
import com.ciplogic.allelon.eventbus.ObjectInstantiator;
import com.ciplogic.allelon.eventbus.events.PhoneCallStatusEvent;

public class PhoneCallBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        ObjectInstantiator.ensureInstantiated(context);

        TelephonyManager telephony = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);

        telephony.listen(new PhoneStateListener() {
            @Override
            public void onCallStateChanged(int state, String incomingNumber) {
                EventBus.INSTANCE.fire(new PhoneCallStatusEvent(state));
            }
        }, PhoneStateListener.LISTEN_CALL_STATE);
    }
}
