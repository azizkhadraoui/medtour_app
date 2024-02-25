package com.example.namespace;

import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class client_profile extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_profile);

        MyDataModel myDataModel = getIntent().getParcelableExtra("clickedItem");

        if (myDataModel != null) {
            String firstName = myDataModel.getFirstName();
            String lastName = myDataModel.getLastName();
            String passportNumber = myDataModel.getPassportNumber();
            String product = myDataModel.getProduct();
            String status = myDataModel.getStatus();
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference().child("images/image/" +passportNumber+".jpg");
            ImageView userImage = findViewById(R.id.user_image);
            userImage.setClipToOutline(true);
            storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Picasso.get().load(uri.toString()).into(userImage);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    // Handle any errors
                }
            });
            // Retrieve the groupId based on passport number
            retrieveGroupId(passportNumber, firstName, lastName, product, status);
        }
    }

    private void populateLayout(String firstName, String lastName, String passportNumber, String product, String status, String groupId, String companions) {
        TextView passportDetails = findViewById(R.id.passport_details);
        TextView userName = findViewById(R.id.user_name);
        TextView flightInfoText = findViewById(R.id.flight_info);
        TextView paymentDetailsText = findViewById(R.id.payment_details);
        TextView companionDetailsText = findViewById(R.id.companion_details);

        passportDetails.setText("Passport: " + passportNumber);
        userName.setText(firstName + " " + lastName);
        flightInfoText.setText("Type: " + product);
        paymentDetailsText.setText("montant payer: " + status);

        // Filter out the client's name from companions
        String companionsWithoutClient = companions.replaceAll(firstName + " " + lastName + ", ", "");

        companionDetailsText.setText("Avec: " + companionsWithoutClient);
    }

    private void retrieveGroupId(String passportNumber, String firstName, String lastName, String product, String status) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Query Firestore to retrieve groupId based on passport number
        db.collection("clients")
                .whereEqualTo("passportNumber", passportNumber)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        // Assume that there is only one result
                        DocumentSnapshot document = task.getResult().getDocuments().get(0);

                        String groupId = document.getString("group_id");
                        String pay = document.getString("Payment");

                        // Now that you have the groupId, retrieve companion names
                        retrieveCompanionNames(passportNumber, firstName, lastName, product, pay, groupId);
                    }
                });
    }

    private void retrieveCompanionNames(String passportNumber, String firstName, String lastName, String product, String status, String groupId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Query Firestore to retrieve clients with the same groupId
        db.collection("clients")
                .whereEqualTo("group_id", groupId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        StringBuilder companionNames = new StringBuilder();

                        for (DocumentSnapshot document : task.getResult()) {
                            String name = document.getString("firstName") + " " + document.getString("lastName");
                            // Exclude the client's own name from companions
                            if (!name.equals(firstName + " " + lastName)) {
                                companionNames.append(name).append(", ");
                            }
                        }

                        // Remove trailing comma and space if any
                        String companions = companionNames.toString().replaceAll(", $", "");

                        // Populate the layout with the retrieved data
                        populateLayout(firstName, lastName, passportNumber, product, status, groupId, companions);
                    }
                });
    }
}
