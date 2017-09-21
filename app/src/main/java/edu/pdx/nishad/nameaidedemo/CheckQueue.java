package edu.pdx.nishad.nameaidedemo;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class CheckQueue extends AppCompatActivity {

    private static final String TAG = "CheckQueueActivity";

    private Button mClear;
    private ListView mQueue;

    private ArrayList<String> arrayID = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_queue);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setIcon(R.mipmap.ic_launcher);

        mClear = (Button) findViewById(R.id.buttonClear);
        mQueue = (ListView) findViewById(R.id.listViewQueue);

        mClear.setVisibility(View.INVISIBLE);

        final FirebaseDatabase database = FirebaseDatabase.getInstance();

        database.getReference().child("NishadSaraf").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()){
                    Toast.makeText(CheckQueue.this,"Queue is empty",Toast.LENGTH_SHORT).show();
                    mQueue.setAdapter(null);
                } else {
                    arrayID = (ArrayList<String>) dataSnapshot.getValue();
                    mClear.setVisibility(View.VISIBLE);
                    mQueue.setAdapter(new ArrayAdapter<String>(CheckQueue.this, android.R.layout.simple_list_item_1,arrayID));
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.v(TAG, "Database not found");
            }
        });
        mClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                database.getReference().child("NishadSaraf").removeValue(new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        Toast.makeText(CheckQueue.this,"Success!",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent i = new Intent(CheckQueue.this,MainActivity.class);
        startActivity(i);
        finish();
    }
}
