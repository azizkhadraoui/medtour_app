package com.example.namespace;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Date;
import java.util.Random;

public class rooms extends AppCompatActivity {
    private Button singleButton;
    private Button doubleButton;
    private Button familyButton;
    private Button selectRoomButton;
    private String groupId;
    private int numberOfClientsToAdd = 0;
    private int clientsAdded = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rooms);

        singleButton = findViewById(R.id.singleButton);
        doubleButton = findViewById(R.id.doubleButton);
        familyButton = findViewById(R.id.familyButton);

        singleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleButtonClick("single");
            }
        });

        doubleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleButtonClick("double");
            }
        });

        familyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleButtonClick("family of 3");
            }
        });


    }

    private void handleButtonClick(String selectedRoomType) {
        groupId = generateUniqueId(selectedRoomType);
        numberOfClientsToAdd = getNumberOfClients(selectedRoomType);

        clientsAdded = 0;  // Reset clientsAdded counter
        startAddClientActivity(selectedRoomType);
    }

    private void startAddClientActivity(String selectedRoomType) {
        Intent intent = new Intent(rooms.this, add_client.class);
        intent.putExtra("groupId", groupId);
        intent.putExtra("numberOfClientsToAdd", numberOfClientsToAdd);
        intent.putExtra("clientsAdded", clientsAdded);
        Intent intent2 = getIntent();
        intent.putExtra("date", intent2.getStringExtra("date"));
        intent.putExtra("emptySeats", intent2.getStringExtra("emptySeats"));
        intent.putExtra("type", intent2.getStringExtra("type"));

        startActivity(intent);
    }

    private String generateUniqueId(String selectedRoomType) {
        Date currentDate = new Date();
        long timestamp = System.currentTimeMillis();

        Random random = new Random();
        int randomNumber = random.nextInt(1000);

        return selectedRoomType + "_" + currentDate + "_" + timestamp + "_" + randomNumber;
    }

    private int getNumberOfClients(String roomType) {
        switch (roomType) {
            case "double":
                return 2;
            case "family of 3":
                return 3;
            default:
                return 0;
        }
    }
}
