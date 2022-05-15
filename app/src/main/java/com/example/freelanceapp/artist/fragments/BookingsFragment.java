package com.example.freelanceapp.artist.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.freelanceapp.R;
import com.example.freelanceapp.artist.adapters.BookingAdapter;
import com.example.freelanceapp.artist.models.Booking;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;


public class BookingsFragment extends Fragment {


    private RecyclerView recyclerView;
    private LinearLayout layNotFound;
    BookingAdapter bookingAdapter;

    ArrayList<Booking> bookings;

    FirebaseAuth auth;
    FirebaseFirestore db;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_bookings, container, false);

        bookings = new ArrayList<>();
        recyclerView = view.findViewById(R.id.rv);
        layNotFound = view.findViewById(R.id.notFoundLay);

        auth=FirebaseAuth.getInstance();
        db=FirebaseFirestore.getInstance();

        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        manager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(manager);

        getData();
        return view;
    }

    private void loadRVList() {
        bookingAdapter = new BookingAdapter(bookings);
        recyclerView.setAdapter(bookingAdapter);
        bookingAdapter.notifyDataSetChanged();
    }

    private void getData() {
        bookings.clear();
        try {
            db.collection("bookings").whereEqualTo("artist",auth.getUid()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isComplete()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {


                            Booking booking = new Booking();
                            booking.setDate(document.getString("date"));
                            booking.setBookedByName(document.getString("booked_by_name"));
                            bookings.add(booking);
                        }

                        loadRVList();

                        if(bookings.isEmpty()){
                            layNotFound.setVisibility(View.VISIBLE);
                        }else {
                            layNotFound.setVisibility(View.GONE);
                        }
                    }
                }

            });

        } catch (Exception e) {
            Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
        }
    }
}