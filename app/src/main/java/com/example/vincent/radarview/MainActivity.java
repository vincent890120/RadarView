package com.example.vincent.radarview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private RadarView radarView;
    private Button buttonStart;
    private Button buttonStop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        radarView = (RadarView) findViewById(R.id.radarview);
        radarView.setTime(4);

        buttonStart = (Button) findViewById(R.id.start);
        buttonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                radarView.start();
                buttonStart.setClickable(false);
                buttonStop.setClickable(true);

            }
        });
        buttonStop = (Button) findViewById(R.id.stop);
        buttonStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                radarView.stop();
                buttonStop.setClickable(false);
                buttonStart.setClickable(true);
            }
        });

    }
}
