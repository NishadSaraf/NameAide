package edu.pdx.nishad.nameaidedemo;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class AppInfo extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_info);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setIcon(R.mipmap.ic_launcher);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent i = new Intent(AppInfo.this,MainActivity.class);
        startActivity(i);
        finish();
    }
}
