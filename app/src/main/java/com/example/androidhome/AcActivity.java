package com.example.androidhome;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class AcActivity extends AppCompatActivity {
    private Button on_btn;
    private Button off_btn;
    private Button home_btn;
    private SeekBar ac_controls;
    private StorageReference mStorageRef;
    private FirebaseAuth mAuth;
    private TextView ACtext;
    DatabaseReference ACref;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ac);

        mStorageRef = FirebaseStorage.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        ACtext = (TextView) findViewById(R.id.customAC);
        on_btn = (Button) findViewById(R.id.btn_on);

        off_btn = (Button) findViewById(R.id.btn_off);
        home_btn = (Button) findViewById(R.id.btn_home);
        ac_controls = (SeekBar) findViewById(R.id.AC_controls);



        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference acControls = database.getReference("AC");

        on_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                acControls.setValue(0);
                Toast.makeText(getBaseContext(), "AC Turned ON", Toast.LENGTH_SHORT).show();
                on_btn.setBackgroundColor(getResources().getColor(android.R.color.white));
               on_btn.setTextColor(getResources().getColor(android.R.color.black));
                off_btn.setBackgroundColor(getResources().getColor(android.R.color.black));
                off_btn.setTextColor(getResources().getColor(android.R.color.white));

            }

        });

        off_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                acControls.setValue(70);
                Toast.makeText(getBaseContext(), "AC Turned OFF", Toast.LENGTH_SHORT).show();
                off_btn.setBackgroundColor(getResources().getColor(android.R.color.white));
                off_btn.setTextColor(getResources().getColor(android.R.color.black));
                on_btn.setBackgroundColor(getResources().getColor(android.R.color.black));
                on_btn.setTextColor(getResources().getColor(android.R.color.white));


            }

        });


        ac_controls.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int result = 70 - progress;
                ACtext.setText(progress + "%");
                acControls.setValue(result);
                on_btn.setBackgroundColor(getResources().getColor(android.R.color.black));
                on_btn.setTextColor(getResources().getColor(android.R.color.white));
                off_btn.setBackgroundColor(getResources().getColor(android.R.color.black));
                off_btn.setTextColor(getResources().getColor(android.R.color.white));

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        home_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendToHome();
            }
        });


    }
    public void sendToLogin() {
        Intent intent = new Intent(AcActivity.this, LoginActivity.class);
        startActivity(intent); //bring up login screen
        finish(); //not allow user to go back by pressing back button
    }
    public void sendToHome(){
      Intent intent = new Intent(AcActivity.this,MainActivity.class);
        startActivity(intent); //bring up login screen
        finish(); //not allow user to go back by pressing back button
   }


    @Override
    protected void onStart(){
        super.onStart();
        //sync DB/APP states
        //db connection
        ACref = FirebaseDatabase.getInstance().getReference();
        ACref.addValueEventListener(new ValueEventListener() {
            Integer loadCount = 1;   //to stop interference with user decisions

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                while(loadCount == 1){
                    String ACvalue = dataSnapshot.child("AC").getValue().toString();
                    int recalc = Integer.parseInt(ACvalue);
                    int result = recalc - 70;
                    ac_controls.setProgress(result);
                    ACtext.setText(recalc + "%");
                    if (ACvalue.equals("0")) {

                        //change colour of buttons to match state
                        on_btn.setBackgroundColor(getResources().getColor(android.R.color.white));
                        on_btn.setTextColor(getResources().getColor(android.R.color.black));
                        off_btn.setBackgroundColor(getResources().getColor(android.R.color.black));
                        off_btn.setTextColor(getResources().getColor(android.R.color.white));
                    }
                    if (ACvalue.equals("70")) {

                        off_btn.setBackgroundColor(getResources().getColor(android.R.color.white));
                        off_btn.setTextColor(getResources().getColor(android.R.color.black));
                        on_btn.setBackgroundColor(getResources().getColor(android.R.color.black));
                        on_btn.setTextColor(getResources().getColor(android.R.color.white));
                    }

                    loadCount = 2;
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        FirebaseUser currentUser = FirebaseAuth.getInstance() .getCurrentUser();
        //if user is not logged in
        if(currentUser == null){
            sendToLogin();
        }
    }


    }

