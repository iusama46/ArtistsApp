package com.example.freelanceapp.consumer.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.freelanceapp.R;
import com.example.freelanceapp.Utils;
import com.example.freelanceapp.activities.EditProfileActivity;
import com.example.freelanceapp.activities.SplashActivity;
import com.example.freelanceapp.consumer.ReviewsActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


public class ProfileFragment extends Fragment {

    TextView bio, name, area, exp;
    RatingBar ratingBar;


    String artistName = "";
    FirebaseAuth auth;
    boolean isArtist;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);


        try {
            auth = FirebaseAuth.getInstance();

            bio = view.findViewById(R.id.bio);
            name = view.findViewById(R.id.name);
            //email = findViewById(R.id.email);
            area = view.findViewById(R.id.loc);
            ratingBar = view.findViewById(R.id.simpleRatingBar);
            exp = view.findViewById(R.id.exp);

            isArtist = Utils.getUserIsArtist(view.getContext());
            FloatingActionButton editBtn = view.findViewById(R.id.edit);
            editBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getContext(), EditProfileActivity.class));
                }
            });

            view.findViewById(R.id.logout).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    auth.signOut();
                    Utils.clearDb(view.getContext());
                    startActivity(new Intent(getContext(), SplashActivity.class));
                    getActivity().finish();
                }
            });


            TextView viewReview = view.findViewById(R.id.reviews);
            viewReview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(getContext(), ReviewsActivity.class).putExtra("uId", auth.getUid()));
                }
            });
            if (!isArtist) {
                viewReview.setVisibility(View.GONE);
                bio.setVisibility(View.GONE);
                exp.setVisibility(View.GONE);
            }
            getData();
        } catch (Exception e) {
            Log.d("E51", e.getMessage());

        }

        return view;
    }

    private void getData() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(auth.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot document) {
                if (document.exists()) {
                    name.setText(document.getString("name") + " | " + document.getString("email"));
                    area.setText(document.getString("area"));
                    artistName = document.getString("name");

                    if (isArtist) {
                        bio.setText(document.getString("bio"));
                        String experience = "Experience: " + document.getString("experience");
                        String hourlyRate = "years & \tHourly Rate: " + document.getString("hourly_rate") + "$/hour";
                        exp.setText(experience + hourlyRate);
                        float ratings = Float.parseFloat(document.getString("ratings"));
                        ratingBar.setRating(ratings);
                    }

                }
            }
        });
    }
}