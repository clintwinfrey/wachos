package com.example.androidbuilder;

import android.os.Bundle;
import android.webkit.WebView;
import androidx.appcompat.app.AppCompatActivity;
import gov.mil.navy.nswcdd.wachos.desktop.AndroidSession;
import tutorial.gui.WachosTutorial;

public class MainActivity extends AppCompatActivity {

    private AndroidSession session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        WebView webview = findViewById(R.id.webview); //get reference to the WebView
        AndroidSession.create(new WachosTutorial("."), webview);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //session.stop();
    }
}