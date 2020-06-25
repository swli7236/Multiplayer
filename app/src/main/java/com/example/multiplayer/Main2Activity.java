package com.example.multiplayer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
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
import java.util.List;

public class Main2Activity extends AppCompatActivity {

    ListView listView;
    Button button;

    List<String> roomsList;

    String playerName = "";
    String roomName = "";

    FirebaseDatabase database;
    DatabaseReference roomRef;
    DatabaseReference roomsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        database = FirebaseDatabase.getInstance();

        //get player name and assign room name to player name
        SharedPreferences preferences = getSharedPreferences("PREFS",0);
        playerName = preferences.getString("playerName","");
        roomName = playerName;

        listView = findViewById(R.id.ListView);
        button = findViewById(R.id.button);

        //all existing available rooms
        roomsList = new ArrayList<>();

        button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                button.setText("CREATING ROOM");
                button.setEnabled(false);;
                roomName = playerName;
                roomRef = database.getReference("rooms/"+roomName+"/player1");
                addRoomEventListener();
                roomRef.setValue(playerName);
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //join existing room and add as player2
                roomName = roomsList.get(position);
                roomRef = database.getReference("rooms/"+roomName+"/player2");
                addRoomEventListener();
                roomRef.setValue(playerName);
            }
        });

        //show if room available
        addRoomsEventListener();
    }

    private void addRoomEventListener(){
        roomRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //join room
                button.setText("CREATE ROOM");
                button.setEnabled(true);
                Intent intent = new Intent(getApplicationContext(),Main3Activity.class);
                intent.putExtra("roomName",roomName);
                startActivity(intent);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //error
                button.setText("CREATE ROOM");
                button.setEnabled(true);;
                Toast.makeText(Main2Activity.this,"Error!", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void addRoomsEventListener(){
        roomsRef = database.getReference("rooms");
        roomsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //show list of rooms
                roomsList.clear();
                Iterable<DataSnapshot> rooms = snapshot.getChildren();
                for(DataSnapshot snap:rooms) {
                    roomsList.add(snap.getKey());

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(Main2Activity.this, android.R.layout.simple_list_item_1,roomsList);
                    listView.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //error = nothing
            }
        });
    }
}
