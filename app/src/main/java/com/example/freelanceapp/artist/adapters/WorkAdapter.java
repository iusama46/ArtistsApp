package com.example.freelanceapp.artist.adapters;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.freelanceapp.R;
import com.example.freelanceapp.activities.ImageActivity;
import com.example.freelanceapp.artist.ArtistProfileActivity;
import com.example.freelanceapp.artist.models.Work;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class WorkAdapter extends RecyclerView.Adapter<WorkAdapter.ViewHolder> {

    ArrayList<Work> images;

    public WorkAdapter(ArrayList<Work> images) {
        this.images = images;
    }

    @NonNull
    @Override
    public WorkAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.lay_work_item, parent, false);
        return new WorkAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WorkAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Glide
                .with(holder.itemView.getContext())
                .load(images.get(position).getImageUrl().toString())
                .centerCrop()
                .placeholder(R.drawable.loading)
                .into(holder.imageView);

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.itemView.getContext().startActivity(new Intent(holder.itemView.getContext(), ImageActivity.class)
                        .putExtra("img_url", images.get(position).getImageUrl().toString()));
            }
        });

        holder.imageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                new AlertDialog.Builder(holder.imageView.getContext())
                        .setTitle("Work Sample")
                        .setMessage("Do you wanna delete this work sample?")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                String uId = FirebaseAuth.getInstance().getUid();
                                FirebaseFirestore.getInstance().collection("users").document(uId).collection("portfolio").document(images.get(position).getId()).delete();
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        })
                        .show();
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.img);
        }
    }
}
