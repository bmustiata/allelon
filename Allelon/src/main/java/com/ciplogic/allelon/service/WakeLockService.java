package com.ciplogic.allelon.service;

import android.content.Context;
import android.os.PowerManager;

import com.ciplogic.allelon.eventbus.Event;
import com.ciplogic.allelon.eventbus.EventBus;
import com.ciplogic.allelon.eventbus.EventListener;
import com.ciplogic.allelon.eventbus.events.MediaPlayerComponentStatusEvent;

import java.util.Collections;
import java.util.List;

/**
 * This class keeps the wake lock on the CPU for as long as the player
 * plays some song.
 *
 * When the application exits, it will also release the lock, but will
 * receive only the StopPlay event, even if the the player was not
 * playing. That's why we manually keep track if the lock is held.
 */
public class WakeLockService implements EventListener {
    private PowerManager.WakeLock wakeLock;
    private final PowerManager powerManager;
    private int lockHeld; // the value is an int just for asserts.

    public WakeLockService(Context context) {
        this.powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);

        EventBus.INSTANCE.registerListener(this);
    }

    /**
     * This locks and unlocks the CPU, by looking at the media player internal state. It will
     * lock the CPU when the player component will start preparing, and remove it when the player
     * will start destroying.
     *
     * The reason is that the playing state might not be reached, if an event occurs that
     * transitions the player from PREPARING straight to DESTROYING (change stream, or user played
     * stop, before the component had time to go up)
     *
     * @param event
     */
    @Override
    public void onEvent(Event event) {
        MediaPlayerComponentStatusEvent componentStatus = (MediaPlayerComponentStatusEvent) event;
        if (componentStatus.playerState == PlayerState.PREPARING) {
            if (lockHeld > 0) {
                throw new Error("The lock is already taken: " + lockHeld);
            }
            lockHeld++;

            wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "AllelonWakeLock");
            wakeLock.acquire();
        }

        if (componentStatus.playerState == PlayerState.DESTROYING) {
            if (lockHeld <= 0) {
                throw new Error("There is currently no lock: " + lockHeld);
            }

            lockHeld--;
            wakeLock.release();
            wakeLock = null;
        }
    }

    @Override
    public List<Class<? extends Event>> getListenedEvents() {
        return (List) Collections.singletonList(MediaPlayerComponentStatusEvent.class);
    }
}
