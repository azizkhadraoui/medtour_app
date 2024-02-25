package com.example.namespace;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.omnilink.test.first;

import java.util.ArrayList;
import java.util.List;

public class pay extends AppCompatActivity {

    private FirebaseFirestore db;
    private String groupId;

    private RecyclerView clientsRecyclerView;
    private ClientAdapter clientAdapter;
    private List<Client> clientsList;

    private TextView flightTypeTextView;
    private TextView flightDateTextView;
    private TextView groupInfoTextView;
    private TextView totalCostTextView;
    private EditText initialPaymentEditText;
    private Button addPaymentButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Get the groupId from the previous activity
        groupId = getIntent().getStringExtra("groupId");

        // Initialize views
        clientsRecyclerView = findViewById(R.id.groupMembersRecyclerView);

        // Initialize RecyclerView and Adapter
        clientsList = new ArrayList<>();
        clientAdapter = new ClientAdapter(clientsList);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        clientsRecyclerView.setLayoutManager(layoutManager);
        clientsRecyclerView.setAdapter(clientAdapter);

        flightTypeTextView = findViewById(R.id.flightTypeTextView);
        flightDateTextView = findViewById(R.id.flightDateTextView);
        groupInfoTextView = findViewById(R.id.groupInfoTextView);
        totalCostTextView = findViewById(R.id.totalCostTextView);
        initialPaymentEditText = findViewById(R.id.initialPaymentEditText);

        // Initialize Add Payment button
        addPaymentButton = findViewById(R.id.addPaymentButton);
        addPaymentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String initialPayment = initialPaymentEditText.getText().toString();
                if (!initialPayment.isEmpty()) {
                    addPaymentToFirestore(initialPayment);
                    Intent intent = new Intent(getApplicationContext(), first.class);
                    startActivity(intent);
                    finish();
                }
            }
        });

        // Populate the layout with data
        populateLayoutWithData();
    }

    private void populateLayoutWithData() {
        if (groupId == null || groupId.isEmpty()) {
            // Handle case where groupId is empty or null
            return;
        }

        // Query Firestore for documents where 'group_id' field matches the provided value
        Query query = db.collection("clients").whereEqualTo("group_id", groupId);

        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    QuerySnapshot querySnapshot = task.getResult();
                    if (querySnapshot != null && !querySnapshot.isEmpty()) {
                        clientsList.clear(); // Clear existing data

                        for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                            String name = document.getString("firstName") + " " + document.getString("lastName");
                            String passportNumber = document.getString("passportNumber");
                            String date = document.getString("flight_date");
                            String type = document.getString("product");
                            flightTypeTextView.setText("Type choisit : "+type);
                            flightDateTextView.setText("Date de voyage : "+date);
                            fetchPriceFromFirestore(type);


                            clientsList.add(new Client(name, passportNumber));
                        }

                        clientAdapter.notifyDataSetChanged();
                    }
                } else {
                    Log.e("FirestoreDebug", "Error getting documents: ", task.getException());
                }
            }
        });
    }

    private void addPaymentToFirestore(String initialPayment) {
        Query query = db.collection("clients").whereEqualTo("group_id", groupId);

        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    QuerySnapshot querySnapshot = task.getResult();
                    if (querySnapshot != null && !querySnapshot.isEmpty()) {
                        for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                            String clientId = document.getId();
                            DocumentReference clientRef = db.collection("clients").document(clientId);

                            clientRef
                                    .update("Payment", initialPayment)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Log.d("FirestoreDebug", "Initial payment added successfully.");
                                            } else {
                                                Log.e("FirestoreDebug", "Error adding initial payment: ", task.getException());
                                            }
                                        }
                                    });
                        }
                    }
                } else {
                    Log.e("FirestoreDebug", "Error getting documents: ", task.getException());
                }
            }
        });
    }
    private void fetchPriceFromFirestore(String productName) {
        // Assuming 'sessions' is the collection containing product information
        db.collection("sessions")
                .whereEqualTo("title", productName)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            QuerySnapshot querySnapshot = task.getResult();
                            if (querySnapshot != null && !querySnapshot.isEmpty()) {
                                for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                                    String price = document.getString("price");
                                    totalCostTextView.setText("prix totale: "+price);


                                    // Now you have the price for the product with the matching title

                                    // Do something with the price here
                                }
                            } else {
                                Log.e("FirestoreDebug", "No matching documents found");
                            }
                        } else {
                            Log.e("FirestoreDebug", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

}
