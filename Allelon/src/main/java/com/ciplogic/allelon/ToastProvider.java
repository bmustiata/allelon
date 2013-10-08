package com.ciplogic.allelon;

import android.content.Context;
import android.widget.Toast;

public class ToastProvider {
    private Context context;

    public ToastProvider(Context context) {
        this.context = context;
    }

    public void showToast(String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}
