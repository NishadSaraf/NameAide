package edu.pdx.nishad.nameaidedemo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

public class DisplayInfo extends AppCompatActivity{

    private static final String TAG = "DisplayInfoActivity";
    private TextView mPSUID;
    private TextView mFirstName;
    private TextView mLastName;
    private TextView mPhonetics;
    private TextView mNextText;
    private TextView mPrevText;
    private TextView mID;
    private TextView mFName;
    private TextView mLName;
    private TextView mPhone;
    private TextView mTextNext;
    private TextView mTextPrevious;
    private TextView mQueueIndex;
    private TextView mQueueSize;
    private TextView mStudDataSize;
    private TextView mTextIndex;
    private TextView mTextQueue;
    private TextView mTextStud;

    private Button mNext;
    private Button mPlay;
    private Button mPrevious;
    private Button mStart;

    private int keyCounter;
    private Map<String, Object> studentInfo;
    private Map<String, Object> classInfo;
    private Map<String, Object> mapNext;
    private Map<String, Object> mapPrev;

    private ArrayList<String> arrayID = new ArrayList<String>();

    private MediaPlayer mediaPlayer;

    private FirebaseDatabase database;
    private StorageReference storageRef;

    private ProgressDialog audioBuffer;
    private ProgressDialog dataFetch;
    private int maxSize = -30;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_info);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setIcon(R.mipmap.ic_launcher);

        mPSUID = (TextView) findViewById(R.id.textViewPSUID);
        mFirstName = (TextView) findViewById(R.id.textViewFirstName);
        mLastName = (TextView) findViewById(R.id.textViewLastName);
        mPhonetics = (TextView) findViewById(R.id.textViewPhonetics);
        mNextText = (TextView) findViewById(R.id.textViewUpNext);
        mPrevText = (TextView) findViewById(R.id.textViewDownPrev);
        mID = (TextView) findViewById(R.id.textViewID);
        mFName = (TextView) findViewById(R.id.textViewFName);
        mLName = (TextView) findViewById(R.id.textViewLName);
        mTextNext = (TextView) findViewById(R.id.textViewUp);
        mTextPrevious = (TextView) findViewById(R.id.textViewDown);
        mPhone = (TextView) findViewById(R.id.textViewPhone);
        mQueueSize =(TextView) findViewById(R.id.textViewQueueSize);
        mQueueIndex = (TextView) findViewById(R.id.textViewQueueIndex);
        mStudDataSize = (TextView) findViewById(R.id.textViewStudSize);
        mTextStud = (TextView) findViewById(R.id.textViewStud);
        mTextQueue = (TextView)findViewById(R.id.textViewqueue);
        mTextIndex = (TextView) findViewById(R.id.textViewIndex);

        mNext = (Button) findViewById(R.id.buttonNext);
        mPrevious = (Button) findViewById(R.id.buttonPrevious);
        mPlay = (Button) findViewById(R.id.buttonPlay);
        mStart = (Button) findViewById(R.id.buttonStart);

        mPhone.setVisibility(View.INVISIBLE);
        mPrevious.setVisibility(View.INVISIBLE);
        mPlay.setVisibility(View.INVISIBLE);
        mNext.setVisibility(View.INVISIBLE);
        mTextNext.setVisibility(View.INVISIBLE);
        mTextPrevious.setVisibility(View.INVISIBLE);
        mFName.setVisibility(View.INVISIBLE);
        mLName.setVisibility(View.INVISIBLE);
        mID.setVisibility(View.INVISIBLE);
        mStart.setVisibility(View.VISIBLE);
        mQueueIndex.setVisibility(View.INVISIBLE);
        mStudDataSize.setVisibility(View.INVISIBLE);
        mQueueSize.setVisibility(View.INVISIBLE);
        mTextIndex.setVisibility(View.INVISIBLE);
        mTextStud.setVisibility(View.INVISIBLE);
        mTextQueue.setVisibility(View.INVISIBLE);

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);


        audioBuffer = new ProgressDialog(DisplayInfo.this);
        audioBuffer.setMessage("Streaming Audio...Please wait!");
        audioBuffer.setCanceledOnTouchOutside(false);
        dataFetch = new ProgressDialog(DisplayInfo.this);
        dataFetch.setMessage("Data is begin fetched...Please wait!");
        dataFetch.setCanceledOnTouchOutside(false);
        dataFetch.show();

        FirebaseStorage storage = FirebaseStorage.getInstance();
        // Create a storage reference from our app
        storageRef = storage.getReferenceFromUrl("gs://fir-queue-f6ccc.appspot.com/");

        database = FirebaseDatabase.getInstance();

        database.getReference().child("NishadSaraf").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(!dataSnapshot.exists()){
                    Toast.makeText(DisplayInfo.this,"Queue is empty",Toast.LENGTH_SHORT).show();

                    mPhone.setVisibility(View.INVISIBLE);
                    mPrevious.setVisibility(View.INVISIBLE);
                    mPlay.setVisibility(View.INVISIBLE);
                    mNext.setVisibility(View.INVISIBLE);
                    mTextNext.setVisibility(View.INVISIBLE);
                    mTextPrevious.setVisibility(View.INVISIBLE);
                    mFName.setVisibility(View.INVISIBLE);
                    mLName.setVisibility(View.INVISIBLE);
                    mID.setVisibility(View.INVISIBLE);
                    mStart.setVisibility(View.VISIBLE);

                    mPSUID.setText(" ");
                    mFirstName.setText(" ");
                    mLastName.setText(" ");
                    mPhonetics.setText(" ");

                    keyCounter = 0;
                    maxSize = 0;

                    Intent i = new Intent(DisplayInfo.this,MainActivity.class);
                    startActivity(i);
                    finish();
                } else {
//                    mStart.setVisibility(View.VISIBLE);
//                    Log.v(TAG,dataSnapshot.getValue().toString());
                    arrayID = (ArrayList<String>) dataSnapshot.getValue();
                    maxSize = arrayID.size();
                    mNext.setText("Next");
                    mQueueSize.setText(String.valueOf(maxSize));
                    mQueueIndex.setText(String.valueOf(maxSize - keyCounter - 1));
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.v(TAG, "Database not found");
            }
        });

        database.getReference().child("StudentInfo").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
            if(dataSnapshot.exists()){
                Log.v(TAG, "Student Database contains = " + dataSnapshot.getValue());
                classInfo = (Map<String, Object>) dataSnapshot.getValue();
                mStudDataSize.setText(String.valueOf(classInfo.size()));
                dataFetch.dismiss();
            } else{
                Toast.makeText(DisplayInfo.this,"No entries found",Toast.LENGTH_SHORT).show();
            }

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.v(TAG, "Database not found");
            }
        });

        mStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mStart.setVisibility(View.INVISIBLE);

                mNext.setVisibility(View.VISIBLE);
                mID.setVisibility(View.VISIBLE);
                mPhone.setVisibility(View.VISIBLE);
                mPlay.setVisibility(View.VISIBLE);
                mTextNext.setVisibility(View.VISIBLE);
                mTextPrevious.setVisibility(View.VISIBLE);
                mFName.setVisibility(View.VISIBLE);
                mLName.setVisibility(View.VISIBLE);
                mQueueIndex.setVisibility(View.VISIBLE);
                mStudDataSize.setVisibility(View.VISIBLE);
                mQueueSize.setVisibility(View.VISIBLE);
                mTextIndex.setVisibility(View.VISIBLE);
                mTextStud.setVisibility(View.VISIBLE);
                mTextQueue.setVisibility(View.VISIBLE);

                if(maxSize != 0){
                    setTextFields(keyCounter);
                }
            }
        });

        mPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                    mediaPlayer.reset();
                    mPlay.setText("Play audio");
                }

                keyCounter--;
                setTextFields(keyCounter);
                mNext.setClickable(true);
                mNext.setText("Next");
                if(keyCounter == 0){
                    mPrevious.setVisibility(View.INVISIBLE);
                }
            }
        });

        mNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPlay.setVisibility(View.VISIBLE);
                mPrevious.setVisibility(View.VISIBLE);
                mNext.setText("Next");
                keyCounter++;
                setTextFields(keyCounter);

                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                    mediaPlayer.reset();
                    mPlay.setText("Play audio");
                }

                if(keyCounter == (maxSize - 1)) {
                    mNext.setText("End of Queue");
                    mNext.setClickable(false);
                } else if(keyCounter != 0){
                    mPrevious.setVisibility(View.VISIBLE);
                }
            }
        });

        mPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mediaPlayer.isPlaying()){
                    mediaPlayer.stop();
                    mediaPlayer.reset();
                    mPlay.setText("Play audio");
                } else {
                    mPlay.setText("Stop");
                    audioBuffer.show();
                    fetchAudioUrlFromFirebase(arrayID.get(keyCounter));
                }
            }
        });
    }

    private void setTextFields(int infoCounter) {
        mPSUID.setText(arrayID.get(infoCounter));
        mQueueIndex.setText(String.valueOf(maxSize - infoCounter - 1));

        studentInfo = (Map<String, Object>) classInfo.get(arrayID.get(infoCounter));

        if(studentInfo == null){
            Toast.makeText(DisplayInfo.this, "Entry corresponding to PSU ID was not found in database", Toast.LENGTH_SHORT).show();
            mFirstName.setText(" ");
            mLastName.setText(" ");
            mPhonetics.setText(" ");
        } else {
            mFirstName.setText((String) studentInfo.get("firstName"));
            mLastName.setText((String) studentInfo.get("lastName"));
            mPhonetics.setText((String) studentInfo.get("phonetics"));
            Log.v(TAG,"THis is what you wanted = " + (maxSize - infoCounter));
        }

        if(infoCounter == 0){
            mPrevText.setText("");
        } else {
            mapPrev = (Map<String, Object>) classInfo.get(arrayID.get(infoCounter - 1));
            if(mapPrev == null){
                mPrevText.setText(" ");
            } else {
                mPrevText.setText(mapPrev.get("firstName") + " " + mapPrev.get("lastName"));
            }
        }

        if((infoCounter) == (maxSize - 1)){
            mNextText.setText("");
        } else {
            mapNext = (Map<String, Object>) classInfo.get(arrayID.get(infoCounter + 1));
            if(mapNext == null){
                mNextText.setText("");
            } else{
                mNextText.setText(mapNext.get("firstName") + " " + mapNext.get("lastName"));
            }
        }
    }

    private void fetchAudioUrlFromFirebase(String audioSample) {
        storageRef.child("Audio/" + audioSample + ".mp3").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                try {
                    // Download url of file
                    final String url = uri.toString();
                    mediaPlayer.setDataSource(url);
                    // wait for media player to get prepare
                    mediaPlayer.prepareAsync();
                    mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mp) {
                            audioBuffer.dismiss();
                            mp.start();
                        }
                    });
                    mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            mPlay.setText("Play audio");
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i("TAG", e.getMessage());
                mPlay.setText("Play audio");
                audioBuffer.dismiss();
                Toast.makeText(DisplayInfo.this,"Audio file missing",Toast.LENGTH_SHORT).show();
            }}
        );
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        mediaPlayer.stop();
        mediaPlayer.reset();

        Intent i = new Intent(DisplayInfo.this,MainActivity.class);
        startActivity(i);
        finish();
    }
}
