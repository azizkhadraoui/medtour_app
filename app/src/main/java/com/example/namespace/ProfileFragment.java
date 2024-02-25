package com.example.namespace;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.namespace.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.omnilink.test.login;

public class ProfileFragment extends Fragment {
    FirebaseAuth auth;
    Button button;
    TextView textView;
    TextView noc;
    TextView chiffreAffaire,nom,commition,num,origin;
    FirebaseUser user;
    FirebaseFirestore db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        auth = FirebaseAuth.getInstance();
        button = view.findViewById(R.id.one);
        textView = view.findViewById(R.id.email);
        noc = view.findViewById(R.id.noc2);
        nom = view.findViewById(R.id.username);
        num = view.findViewById(R.id.num);
        commition = view.findViewById(R.id.comm);
        origin= view.findViewById(R.id.orig);
        chiffreAffaire = view.findViewById(R.id.chaff); // Add this TextView
        user = auth.getCurrentUser();
        db = FirebaseFirestore.getInstance();

        if (user == null) {
            Intent intent = new Intent(getContext(), login.class);
            startActivity(intent);
        } else {
            textView.setText(user.getEmail());

            // Get the current user's email
            String userEmail = user.getEmail();

            // Assuming you have a "users" collection
            CollectionReference usersRef = db.collection("users");

            // Query Firestore to get the count of users with matching email
            Query query = usersRef.whereEqualTo("email", userEmail);

            query.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    QuerySnapshot querySnapshot = task.getResult();


                    // Assuming you have a field "chiffre_affaire" in the user document
                    if (!querySnapshot.isEmpty()) {
                        DocumentSnapshot userDocument = querySnapshot.getDocuments().get(0);
                        String chiffreAffaireValue = userDocument.getString("chiffre_affaire");
                        String numclient = userDocument.getString("num_client");
                        String numero = userDocument.getString("phone");
                        String name = userDocument.getString("name");
                        String orig = userDocument.getString("origin");
                        chiffreAffaire.setText("Chiffre d'affaire : " + chiffreAffaireValue);
                        noc.setText("nombre de clients : "+ numclient);
                        num.setText("numero : "+ numero);
                        origin.setText("origine : "+ orig);
                        nom.setText(name);
                        commition.setText("commition : "+ Integer.parseInt(numclient)*300);
                    }
                } else {
                    // Handle the error
                }
            });
        }

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getContext(), login.class);
                startActivity(intent);
            }
        });

        return view;
    }
}
