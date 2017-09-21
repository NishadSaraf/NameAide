package edu.pdx.nishad.nameaidedemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private Button mGradCeremony;
    private Button mCheckStudDatabase;
    private Button mCheckQueue;
    private FloatingActionButton mfab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setIcon(R.mipmap.ic_launcher);

        mfab = (FloatingActionButton) findViewById(R.id.fab);

        mGradCeremony = (Button) findViewById(R.id.buttonStart);
        mCheckQueue = (Button) findViewById(R.id.buttonCheckQueue);
        mCheckStudDatabase = (Button) findViewById(R.id.buttonCheckStudData);

        mfab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,AppInfo.class));
            }
        });
        mGradCeremony.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, DisplayInfo.class);
                startActivity(intent);
            }
        });

        mCheckStudDatabase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CheckStudData.class);
                startActivity(intent);
            }
        });

        mCheckQueue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CheckQueue.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
