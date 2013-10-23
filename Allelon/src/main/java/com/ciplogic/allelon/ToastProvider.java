package com.ciplogic.allelon;

import android.util.Log;
import android.widget.Toast;

public class ToastProvider {
    public ToastProvider() {
    }

    public void showToast(final String message) {
        try {
            if (RadioActivity.INSTANCE == null) {
                return; // FIXME: I should probably get the intent context at this stage.
            }

            RadioActivity.INSTANCE.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(RadioActivity.INSTANCE, message, Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Error e) {
            Log.e("Allelon", e.getMessage(), e);
        }
    }
}
