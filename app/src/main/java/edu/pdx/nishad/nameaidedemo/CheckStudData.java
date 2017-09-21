package edu.pdx.nishad.nameaidedemo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

public class CheckStudData extends AppCompatActivity {

    private static final String TAG = "CheckStudDataActivity";
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
    private TextView mIndex;
    private TextView mQueueIndex;
    private TextView mStud;
    private TextView mStudSize;

    private Button mNext;
    private Button mPlay;
    private Button mPrevious;
    private Button mStart;

    private int keyCounter;
    private Map<String, Object> studentInfo;
    private Map<String, Object> classInfo;
    private Map<String, Object> mapNext;
    private Map<String, Object> mapPrev;

    private ArrayList arrayID = new ArrayList<String>();
    private ArrayList arrayFirstName = new ArrayList<String>();
    private ArrayList arrayLastName = new ArrayList<String>();
    private ArrayList arrayPhonetics = new ArrayList<String>();

    private ProgressDialog audioBuffer;
    private ProgressDialog dataFetch;
    private MediaPlayer mediaPlayer;

    private FirebaseDatabase database;
    private StorageReference storageRef;

    private int maxSize = -30;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_stud_data);

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
        mIndex = (TextView) findViewById(R.id.textViewIndex);
        mQueueIndex = (TextView) findViewById(R.id.textViewQueueIndex);
        mStud = (TextView) findViewById(R.id.textViewStud);
        mStudSize = (TextView) findViewById(R.id.textViewStudSize);

        mNext = (Button) findViewById(R.id.buttonNext);
        mPrevious = (Button) findViewById(R.id.buttonPrevious);
        mPlay = (Button) findViewById(R.id.buttonPlay);
        mStart = (Button) findViewById(R.id.buttonStart);

        mStudSize.setVisibility(View.INVISIBLE);
        mStud.setVisibility(View.INVISIBLE);
        mQueueIndex.setVisibility(View.INVISIBLE);
        mIndex.setVisibility(View.INVISIBLE);
        mPhone.setVisibility(View.INVISIBLE);
        mPrevious.setVisibility(View.INVISIBLE);
        mPlay.setVisibility(View.INVISIBLE);
        mNext.setVisibility(View.INVISIBLE);
        mTextNext.setVisibility(View.INVISIBLE);
        mTextPrevious.setVisibility(View.INVISIBLE);
        mFName.setVisibility(View.INVISIBLE);
        mLName.setVisibility(View.INVISIBLE);
        mID.setVisibility(View.INVISIBLE);
        mStart.setVisibility(View.INVISIBLE);

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        audioBuffer = new ProgressDialog(CheckStudData.this);
        audioBuffer.setMessage("Streaming Audio...Please wait!");
        audioBuffer.setCanceledOnTouchOutside(false);

        dataFetch = new ProgressDialog(CheckStudData.this);
        dataFetch.setMessage("Data is begin fetched...Please wait!");
        dataFetch.setCanceledOnTouchOutside(false);
        dataFetch.show();

        FirebaseStorage storage = FirebaseStorage.getInstance();
        // Create a storage reference from our app
        storageRef = storage.getReferenceFromUrl("gs://fir-queue-f6ccc.appspot.com/");

        database = FirebaseDatabase.getInstance();

        database.getReference().child("StudentInfo").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    Log.v(TAG, "Student Database contains = " + dataSnapshot.getChildren());
                    for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                        studentInfo = (Map<String, Object>) snapshot.getValue();
                        arrayID.add(snapshot.getKey());
                        maxSize = arrayID.size();
                        arrayFirstName.add(studentInfo.get("firstName"));
                        arrayLastName.add(studentInfo.get("lastName"));
                        arrayPhonetics.add(studentInfo.get("phonetics"));
                    }
                    dataFetch.dismiss();
                    mStart.setVisibility(View.VISIBLE);
                } else {
                    Toast.makeText(CheckStudData.this,"No entries found!",Toast.LENGTH_SHORT).show();
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
                mStudSize.setVisibility(View.VISIBLE);
                mStud.setVisibility(View.VISIBLE);
                mQueueIndex.setVisibility(View.VISIBLE);
                mIndex.setVisibility(View.VISIBLE);
                setTextFields(keyCounter);
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
                if (keyCounter == 0) {
                    mPrevious.setVisibility(View.INVISIBLE);
                }
            }
        });

        mNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPrevious.setVisibility(View.VISIBLE);
                mPlay.setVisibility(View.VISIBLE);
                mNext.setText("Next");
                keyCounter++;
                setTextFields(keyCounter);

                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                    mediaPlayer.reset();
                    mPlay.setText("Play audio");
                }

                if (keyCounter == (maxSize - 1)) {
                    mNext.setText("End of Queue");
                    mNext.setClickable(false);
                    mPrevious.setVisibility(View.VISIBLE);
                } else if (keyCounter != 0) {
                    mPrevious.setVisibility(View.VISIBLE);
                }
            }
        });

        mPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                    mediaPlayer.reset();
                    mPlay.setText("Play audio");
                } else {
                    audioBuffer.show();
                    mPlay.setText("Stop");
                    fetchAudioUrlFromFirebase(arrayID.get(keyCounter).toString());
                }
            }
        });
    }

    private void setTextFields(int infoCounter) {
        mPSUID.setText((String)arrayID.get(infoCounter));
        mFirstName.setText((String) arrayFirstName.get(infoCounter));
        mLastName.setText((String) arrayLastName.get(infoCounter));
        mPhonetics.setText((String) arrayPhonetics.get(infoCounter));
        mQueueIndex.setText(String.valueOf(infoCounter + 1));
        mStudSize.setText(String.valueOf(maxSize));
        if (infoCounter == 0) {
            mPrevText.setText("");
        } else {
            mPrevText.setText(arrayFirstName.get(infoCounter - 1) + " " + arrayLastName.get(infoCounter - 1));
        }

        if ((infoCounter + 1) == maxSize) {
            mNextText.setText("");
        } else {
            mNextText.setText(arrayFirstName.get(infoCounter + 1) + " " + arrayLastName.get(infoCounter + 1));
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
                                        Toast.makeText(CheckStudData.this, "Audio file missing", Toast.LENGTH_SHORT).show();
                                    }
                                }
        );
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        mediaPlayer.stop();
        mediaPlayer.reset();

        Intent i = new Intent(CheckStudData.this,MainActivity.class);
        startActivity(i);
        finish();
    }
}