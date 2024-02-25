package com.example.namespace;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class ClientsFragment extends Fragment implements MyAdapter.OnItemClickListener {

    private RecyclerView recyclerView;
    private FirestoreRecyclerAdapter<MyDataModel, MyAdapter.ViewHolder> adapter;
    private FirebaseFirestore firestore;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_clients, container, false);

        recyclerView = view.findViewById(R.id.clientRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        firestore = FirebaseFirestore.getInstance();

        FirestoreRecyclerOptions<MyDataModel> options = new FirestoreRecyclerOptions.Builder<MyDataModel>()
                .setQuery(firestore.collection("clients").orderBy("firstName", Query.Direction.ASCENDING), MyDataModel.class)
                .build();

        adapter = new MyAdapter(options, this);

        recyclerView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    @Override
    public void onItemClick(MyDataModel model) {
        Intent intent = new Intent(getActivity(), client_profile.class);
        intent.putExtra("clickedItem", model);
        startActivity(intent);
    }
}
