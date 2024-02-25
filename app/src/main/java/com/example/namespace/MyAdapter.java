package com.example.namespace;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class MyAdapter extends FirestoreRecyclerAdapter<MyDataModel, MyAdapter.ViewHolder> {
    public interface OnItemClickListener {
        void onItemClick(MyDataModel model);
    }
    private final OnItemClickListener listener;


    public MyAdapter(@NonNull FirestoreRecyclerOptions<MyDataModel> options, OnItemClickListener listener) {
        super(options);
        this.listener = listener;
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull MyDataModel model) {
        holder.bind(model);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onItemClick(model);
            }
        });
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_client, parent, false);
        return new ViewHolder(view);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView firstNameTextView, lastNameTextView, passportTextView, productTextView, statusTextView;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            firstNameTextView = itemView.findViewById(R.id.firstNameTextView);
            lastNameTextView = itemView.findViewById(R.id.lastNameTextView);
            passportTextView = itemView.findViewById(R.id.passportTextView);
            productTextView = itemView.findViewById(R.id.productTextView);
            statusTextView = itemView.findViewById(R.id.statusTextView);
        }

        void bind(MyDataModel data) {
            firstNameTextView.setText(data.getFirstName());
            lastNameTextView.setText(data.getLastName());
            passportTextView.setText(data.getPassportNumber());
            productTextView.setText(data.getProduct());
            statusTextView.setText(data.getStatus());
        }
    }
}
