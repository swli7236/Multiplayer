package com.example.multiplayer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Main3Activity extends AppCompatActivity {
    Button button;

    String playerName = "";
    String roomName = "";
    String role = "";
    String msg = "";
    FirebaseDatabase database;
    DatabaseReference msgRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);

        button = findViewById(R.id.button2);
        button.setEnabled(false);

        database = FirebaseDatabase.getInstance();

        SharedPreferences preferences = getSharedPreferences("PREFS",0);
        playerName = preferences.getString("playerName","");

        Bundle extras = getIntent().getExtras();
        if (extras!=null){
            roomName = extras.getString("roomName");
            role = "host";
        } else {
            role = "guest";
        }

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //send msg
                button.setEnabled(false);
                msgRef = database.getReference("rooms/"+roomName+"/message");
                msg = role + ":Poked!";
                msgRef.setValue(msg);
                addRoomEventListener();
            }
        });

//        msgRef = database.getReference("rooms/"+roomName+"/message");
//        msg = role + ":Poked!";
//        msgRef.setValue(msg);
//        addRoomEventListener();
    }

    private void addRoomEventListener() {
        msgRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //msg received
                if(role.equals("host")) {
                    if(snapshot.getValue(String.class).contains("guest:")) {
                        button.setEnabled(true);
                        Toast.makeText(Main3Activity.this, "" + snapshot.getValue(String.class).replace("guest:", ""), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    if(snapshot.getValue(String.class).contains("host:")) {
                        button.setEnabled(true);
                        Toast.makeText(Main3Activity.this, "" + snapshot.getValue(String.class).replace("host:", ""), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //error = retry
                msgRef.setValue(msg);
            }
        });

    }
}
