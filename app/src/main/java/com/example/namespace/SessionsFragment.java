package com.example.namespace;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;

public class SessionsFragment extends Fragment {

    private RecyclerView recyclerView;
    private SessionAdapter adapter;
    private ArrayList<Session> sessionList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_sessions, container, false);

        recyclerView = rootView.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        adapter = new SessionAdapter(sessionList);
        recyclerView.setAdapter(adapter);

        // Retrieve data from Firestore and populate sessionList
        getSessionsFromFirestore();

        return rootView;
    }

    private void getSessionsFromFirestore() {
        CollectionReference sessionsCollection = FirebaseFirestore.getInstance().collection("sessions");

        sessionsCollection.get().addOnSuccessListener(queryDocumentSnapshots -> {
            sessionList.clear();

            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                String title = document.getString("title");
                String date = ("de: "+ document.getString("date"));
                String time = ("jusqu'a : "+ document.getString("time"));

                Session session = new Session(title, date, time);
                sessionList.add(session);
            }

            adapter.notifyDataSetChanged();
        });
    }

    private class SessionAdapter extends RecyclerView.Adapter<SessionAdapter.SessionViewHolder> {

        private ArrayList<Session> sessionList;

        public SessionAdapter(ArrayList<Session> sessionList) {
            this.sessionList = sessionList;
        }

        @Override
        public SessionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.session_item, parent, false);
            return new SessionViewHolder(view);
        }

        @Override
        public void onBindViewHolder(SessionViewHolder holder, int position) {
            Session session = sessionList.get(position);
            holder.bind(session);
        }

        @Override
        public int getItemCount() {
            return sessionList.size();
        }

        public class SessionViewHolder extends RecyclerView.ViewHolder {
            private TextView titleTextView, dateTextView, timeTextView;
            private Button previewButton, addClientButton;

            public SessionViewHolder(View itemView) {
                super(itemView);
                titleTextView = itemView.findViewById(R.id.titleTextView);
                dateTextView = itemView.findViewById(R.id.dateTextView);
                timeTextView = itemView.findViewById(R.id.timeTextView);

                previewButton = itemView.findViewById(R.id.previewButton);
                addClientButton = itemView.findViewById(R.id.addClientButton);

                previewButton.setOnClickListener(v -> {
                    // Handle Preview button click
                    Session session = sessionList.get(getAdapterPosition());
                    String clickedTitle = session.getTitle();
                    Intent intent = new Intent(itemView.getContext(), offers.class);
                    intent.putExtra("title", clickedTitle);
                    itemView.getContext().startActivity(intent);
                });

                addClientButton.setOnClickListener(v -> {
                    // Handle Add Client button click
                    Session session = sessionList.get(getAdapterPosition());
                    String clickedTitle = session.getTitle();
                    Intent intent = new Intent(itemView.getContext(), FlightTimesActivity.class);
                    intent.putExtra("title", clickedTitle);
                    itemView.getContext().startActivity(intent);
                });
            }

            public void bind(Session session) {
                titleTextView.setText(session.getTitle());
                dateTextView.setText(session.getDate());
                timeTextView.setText(session.getTime());
            }
        }
    }
}
