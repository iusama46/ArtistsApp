package com.example.freelanceapp.consumer.fragments;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.freelanceapp.CShowProgress;
import com.example.freelanceapp.R;
import com.example.freelanceapp.Utils;
import com.example.freelanceapp.activities.EditProfileActivity;
import com.example.freelanceapp.activities.SplashActivity;
import com.example.freelanceapp.consumer.ReviewsActivity;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;


public class ProfileFragment extends Fragment {

    TextView bio, name, area, exp, accountNo, categoriesTv;
    RatingBar ratingBar;
    ImageButton payBtn;
    LinearLayout layPay, layCat, laysample;

    FloatingActionButton uploadImg;

    String artistName = "";
    FirebaseAuth auth;
    boolean isArtist;
    CShowProgress showProgress;
    FirebaseFirestore firestore;
    StorageReference storageReference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);


        try {
            auth = FirebaseAuth.getInstance();
            firestore = FirebaseFirestore.getInstance();
            storageReference = FirebaseStorage.getInstance().getReference();
            bio = view.findViewById(R.id.bio);
            name = view.findViewById(R.id.name);
            //email = findViewById(R.id.email);
            area = view.findViewById(R.id.loc);
            ratingBar = view.findViewById(R.id.simpleRatingBar);
            exp = view.findViewById(R.id.exp);
            payBtn = view.findViewById(R.id.pay_img);
            accountNo = view.findViewById(R.id.account);
            layPay = view.findViewById(R.id.lay_pay);
            layCat = view.findViewById(R.id.lay_cat);
            categoriesTv = view.findViewById(R.id.cat);
            uploadImg = view.findViewById(R.id.uploadImg);
            laysample = view.findViewById(R.id.lay_sample);
            showProgress = new CShowProgress();
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

            uploadImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ImagePicker.Companion.with(ProfileFragment.this)
                            //.galleryOnly()
                            .compress(512) //// Final image size will be less than 1 MB(Optional)
                            .maxResultSize(512, 512)    //Final image resolution will be less than 1080 x 1080(Optional)
                            .start();
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
                layPay.setVisibility(View.GONE);
                layCat.setVisibility(View.GONE);
                uploadImg.setVisibility(View.GONE);
                laysample.setVisibility(View.GONE);
            }
            getData();
        } catch (Exception e) {
            Log.d("E51", e.getMessage());

        }

        return view;
    }

    private void getData() {

        firestore.collection("users").document(auth.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
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

                        if (document.contains("is_easy_paisa")) {
                            boolean isEasyPaisa = document.getBoolean("is_easy_paisa");
                            if (!isEasyPaisa) {
                                payBtn.setImageDrawable(getResources().getDrawable(R.drawable.jazzcash));
                                accountNo.setText(document.getString("account_no"));
                            }
                        }

                        if (document.contains("categories")) {
                            List<String> categories = (List<String>) document.get("categories");
                            String strNew = categories.toString().replace("[", "");
                            categoriesTv.setText(strNew.replace("]", ""));
                        }
                    }

                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {

            if (data != null) {
                uploadData(data.getData());
            }
        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(getContext(), ImagePicker.Companion.getError(data), Toast.LENGTH_SHORT).show();
        }

    }

    private void uploadData(Uri imageUri) {
        showProgress.showProgress(getContext());
        String uId = auth.getCurrentUser().getUid();

        String docId = firestore.collection("users").document(uId).collection("portfolio").document().getId();


        final StorageReference filePath1 = storageReference.child("images" + "/" + uId + "/" + docId + ".png");
        filePath1.putFile(imageUri).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw Objects.requireNonNull(task.getException());
                }
                return filePath1.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {

                    Uri downUri = task.getResult();


                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

                        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
                        LocalDateTime now = LocalDateTime.now();
                        Map<String, Object> docData = new HashMap<>();
                        docData.put("img_url", downUri.toString());
                        docData.put("date", dtf.format(now));

                        firestore.collection("users").document(uId).collection("portfolio").add(docData).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentReference> task) {
                                showProgress.hideProgress();
                            }
                        });
                    }

                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                showProgress.hideProgress();
                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }
}