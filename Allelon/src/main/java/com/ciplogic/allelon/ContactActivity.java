package com.ciplogic.allelon;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class ContactActivity extends Activity {
    private Button websiteButton;
    private Button contactButton;
    private Button phoneButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.contact_activity);

        findUiComponents();
        addEventListeners();
    }

    private void findUiComponents() {
        websiteButton = (Button) findViewById(R.id.websiteButton);
        contactButton = (Button) findViewById(R.id.contactButton);
        phoneButton = (Button) findViewById(R.id.phoneButton);
    }

    private void addEventListeners() {
        websiteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                Uri data = Uri.parse("http://allelon.at");
                intent.setData(data);
                startActivity(intent);
            }
        });

        contactButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                Uri data = Uri.parse("mailto:contact@allelon.at?subject=Contact&body=");
                intent.setData(data);
                startActivity(intent);
            }
        });

        phoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_CALL);
                Uri data = Uri.parse("tel:+436603797281");
                intent.setData(data);
                startActivity(intent);
            }
        });
    }
}
