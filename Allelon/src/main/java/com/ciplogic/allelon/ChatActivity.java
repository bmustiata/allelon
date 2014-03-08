package com.ciplogic.allelon;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;

public class ChatActivity extends Activity {
    private WebView webView;
    private ImageButton reloadButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.chat_activity);

        findUiComponents();
        addEventListeners();

        webView.loadUrl("http://allelon.at/chat.php");
    }

    private void findUiComponents() {
        webView = (WebView) findViewById(R.id.webView);
        reloadButton = (ImageButton) findViewById(R.id.reloadButton);
    }

    private void addEventListeners() {
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return false;
            }
        });

        reloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                webView.loadUrl("http://allelon.at/chat.php");
                //webView.reload();
            }
        });
    }
}
