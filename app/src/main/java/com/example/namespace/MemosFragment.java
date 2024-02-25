package com.example.namespace;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MemosFragment extends Fragment {

    private EditText memoEditText;
    private String username ;
    private ListView memoListView;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private FirebaseFirestore db;
    private ArrayAdapter<String> memoAdapter;
    private List<DocumentSnapshot> memoSnapshots;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_memos, container, false);

        // Initialize views
        memoEditText = view.findViewById(R.id.memoEditText);
        memoListView = view.findViewById(R.id.memoListView);

        // Initialize Firebase Authentication
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Set click listener for the send button
        ImageButton sendButton = view.findViewById(R.id.sendButton);
        sendButton.setOnClickListener(v -> sendMemo());

        // Initialize memoSnapshots list
        memoSnapshots = new ArrayList<>();

        // Initialize memoAdapter
        memoAdapter = new ArrayAdapter<>(
                requireContext(), android.R.layout.simple_list_item_1, new ArrayList<>());
        memoListView.setAdapter(memoAdapter);

        // Set up Firestore listener to retrieve and display memos
        db.collection("memos").addSnapshotListener((value, error) -> {
            if (error != null) {
                // Handle error
                return;
            }

            memoSnapshots = value.getDocuments();
            List<String> memoTexts = new ArrayList<>();
            for (DocumentSnapshot doc : memoSnapshots) {
                String memoText = doc.getString("text");
                memoTexts.add(memoText);
            }

            memoAdapter.clear();
            memoAdapter.addAll(memoTexts);
            memoAdapter.notifyDataSetChanged();
        });

        // Set up long click listener for deleting memos
        memoListView.setOnItemLongClickListener((parent, view1, position, id) -> {
            DocumentSnapshot selectedMemo = memoSnapshots.get(position);
            deleteMemo(selectedMemo);
            return true;
        });

        return view;
    }

    private void sendMemo() {
        String memoText = memoEditText.getText().toString();
        if (currentUser != null) {
            String userId = currentUser.getUid(); // Get user's ID

            // Get the user's email from Firebase Authentication
            String userEmail = currentUser.getEmail();

            // Use the user's email to search for their name in the "users" collection
            db.collection("users")
                    .whereEqualTo("email", userEmail)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            // Assuming there is only one matching user
                            DocumentSnapshot userSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                            username = userSnapshot.getString("name");

                            // Now you can use the 'username' variable as needed
                            // For example, you can add it to the memoData or use it in your UI

                            Map<String, Object> memoData = new HashMap<>();
                            memoData.put("text", memoText);
                            memoData.put("userId", userId); // Store user's ID with memo
                            memoData.put("userName", username);

                            db.collection("memos").add(memoData)
                                    .addOnSuccessListener(documentReference -> {
                                        // Memo added successfully
                                        // You can update the UI or perform any other action
                                    })
                                    .addOnFailureListener(e -> {
                                        // Handle error
                                    });

                            memoEditText.setText(""); // Clear the memo text field
                        } else {
                            // Handle the case where the user's email is not found in the "users" collection
                        }
                    })
                    .addOnFailureListener(e -> {
                        // Handle error
                    });
        }
    }


    private void deleteMemo(DocumentSnapshot memoSnapshot) {
        db.collection("memos").document(memoSnapshot.getId()).delete()
                .addOnSuccessListener(aVoid -> {
                    // Memo deleted successfully
                    // You can update the UI or perform any other action
                })
                .addOnFailureListener(e -> {
                    // Handle error
                });
    }
}
