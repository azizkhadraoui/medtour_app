package com.example.namespace;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class FlightTimesActivity extends AppCompatActivity {

    private static final String TAG = "FlightTimesActivity";
    private RecyclerView recyclerView;
    private List<Flight> flightsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_available_flights);

        recyclerView = findViewById(R.id.recyclerView);
        flightsList = new ArrayList<>();
        FlightAdapter adapter = new FlightAdapter(flightsList); // Create the adapter here
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // Inside onCreate() method after initializing recyclerView and adapter

        Intent intent = getIntent();
        String flightType = intent.getStringExtra("title");

        if (flightType != null) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("flights")
                    .whereEqualTo("type", flightType)  // Only get flights of the specified type
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (DocumentSnapshot document : task.getResult()) {
                                    String date = document.getString("date");
                                    String emptySeatsString = document.getString("empty_seats");
                                    long emptySeats = Long.parseLong(emptySeatsString);
                                    String type = document.getString("type");
                                    flightsList.add(new Flight(date, emptySeats, type));
                                }
                                adapter.notifyDataSetChanged();
                            } else {
                                Log.w(TAG, "Error getting documents.", task.getException());
                            }
                        }
                    });
        }

    }

    public class Flight {
        private String date;
        private long emptySeats;
        private String type;

        public Flight(String date, long emptySeats, String type) {
            this.date = date;
            this.emptySeats = emptySeats;
            this.type = type;
        }

        public String getDate() {
            return date;
        }

        public long getEmptySeats() {
            return emptySeats;
        }

        public String getType() {
            return type;
        }
    }

    public class FlightAdapter extends RecyclerView.Adapter<FlightAdapter.FlightViewHolder> {

        private List<Flight> flightsList;

        public FlightAdapter(List<Flight> flightsList) {
            this.flightsList = flightsList;
        }

        @NonNull
        @Override
        public FlightViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_flight, parent, false);
            return new FlightViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull FlightViewHolder holder, int position) {
            Flight flight = flightsList.get(position);
            holder.bind(flight);

            // Add OnClickListener here
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Create an Intent to start the new activity
                    Intent intent = new Intent(view.getContext(), rooms.class);

                    // Put data as extras
                    intent.putExtra("date", flight.getDate());
                    intent.putExtra("emptySeats", flight.getEmptySeats());
                    intent.putExtra("type", flight.getType());

                    // Start the new activity
                    view.getContext().startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return flightsList.size();
        }

        public class FlightViewHolder extends RecyclerView.ViewHolder {

            private TextView dateTextView;
            private TextView seatsTextView;
            private TextView typeTextView;

            public FlightViewHolder(@NonNull View itemView) {
                super(itemView);
                dateTextView = itemView.findViewById(R.id.dateTextView);
                seatsTextView = itemView.findViewById(R.id.seatsTextView);
                typeTextView = itemView.findViewById(R.id.typeTextView);
            }

            public void bind(Flight flight) {
                dateTextView.setText(flight.getDate());
                seatsTextView.setText(String.valueOf(flight.getEmptySeats()));
                typeTextView.setText(flight.getType());
            }
        }
    }
}
