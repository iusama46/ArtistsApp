package com.example.freelanceapp.artist.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.freelanceapp.R;
import com.example.freelanceapp.artist.ArtistProfileActivity;
import com.example.freelanceapp.artist.models.Booking;

import java.util.ArrayList;
import java.util.Locale;

public class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.ViewHolder> {

    ArrayList<Booking> bookings;

    public BookingAdapter(ArrayList<Booking> bookings) {
        this.bookings = bookings;
    }

    @NonNull
    @Override
    public BookingAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.lay_item_booking, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookingAdapter.ViewHolder holder, int position) {
        Booking booking = bookings.get(position);

        String txt = booking.getBookedByName().toUpperCase(Locale.ROOT) + " hired you for a job.";
        holder.text.setText(txt);
        holder.date.setText(booking.getDate());
        Context context = holder.itemView.getContext();
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.startActivity(new Intent(context, ArtistProfileActivity.class).putExtra("uId", booking.getBookedById())
                        .putExtra("isArtist",false));
            }
        });
    }

    @Override
    public int getItemCount() {
        return bookings.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView text, date;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            text = itemView.findViewById(R.id.txt);
            date = itemView.findViewById(R.id.date);
        }
    }
}
