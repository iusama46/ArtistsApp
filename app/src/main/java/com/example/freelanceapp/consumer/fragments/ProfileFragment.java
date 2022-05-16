package com.example.freelanceapp.consumer.fragments;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.freelanceapp.CShowProgress;
import com.example.freelanceapp.R;
import com.example.freelanceapp.Utils;
import com.example.freelanceapp.activities.EditProfileActivity;
import com.example.freelanceapp.activities.SplashActivity;
import com.example.freelanceapp.artist.adapters.WorkAdapter;
import com.example.freelanceapp.artist.models.Work;
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
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;


public class ProfileFragment extends Fragment {

    TextView bio, name, area, exp, accountNo, categoriesTv,accountNo2;
    RatingBar ratingBar;
    ImageButton payBtn;
    LinearLayout layPay, layCat, laySample, layBio, layExp;
    ArrayList<Work> workImages = new ArrayList<>();
    WorkAdapter adapter;
    FloatingActionButton uploadImg;

    RecyclerView recyclerView;

    String epAccountNo="#",jcAccountNo ="#";
    String artistName = "";
    FirebaseAuth auth;
    boolean isArtist;
    CShowProgress showProgress;
    FirebaseFirestore firestore;
    ImageView profileImg;
    StorageReference storageReference;
    boolean isProfileUpload = false;

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
            accountNo2 = view.findViewById(R.id.account2);
            layPay = view.findViewById(R.id.lay_pay);
            layCat = view.findViewById(R.id.lay_cat);
            layExp = view.findViewById(R.id.lay_rate);
            layBio = view.findViewById(R.id.lay_interest);
            categoriesTv = view.findViewById(R.id.cat);
            uploadImg = view.findViewById(R.id.uploadImg);
            laySample = view.findViewById(R.id.lay_sample);
            profileImg = view.findViewById(R.id.image_pp);
            showProgress = new CShowProgress();
            isArtist = Utils.getUserIsArtist(view.getContext());

            recyclerView = view.findViewById(R.id.sample);
            GridLayoutManager manager = new GridLayoutManager(getContext(), 3);
            recyclerView.setLayoutManager(manager);
            recyclerView.setNestedScrollingEnabled(false);

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
                    isProfileUpload = false;
                    ImagePicker.Companion.with(ProfileFragment.this)
                            //.galleryOnly()
                            .compress(512) //// Final image size will be less than 1 MB(Optional)
                            .maxResultSize(512, 512)    //Final image resolution will be less than 1080 x 1080(Optional)
                            .start();
                }
            });

            profileImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    isProfileUpload = true;
                    ImagePicker.Companion.with(ProfileFragment.this)
                            //.galleryOnly()
                            .compress(256) //// Final image size will be less than 1 MB(Optional)
                            .maxResultSize(512, 512)    //Final image resolution will be less than 1080 x 1080(Optional)
                            .start();
                }
            });

            view.findViewById(R.id.switch_acc).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(epAccountNo.equals("#") || epAccountNo.isEmpty())
                    accountNo.setText("Unavailable");
                    else
                        accountNo.setText(epAccountNo);
                }
            });

            view.findViewById(R.id.switch_acc2).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(jcAccountNo.equals("#") || jcAccountNo.isEmpty())
                        accountNo2.setText("Unavailable");
                    else
                        accountNo2.setText(jcAccountNo);
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
                laySample.setVisibility(View.GONE);
                layBio.setVisibility(View.GONE);
                layExp.setVisibility(View.GONE);
            }
            getData();
        } catch (Exception e) {
            e.printStackTrace();
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
                    //Log.d("climaa", document.ge)


                    if (isArtist) {
                        bio.setText(document.getString("bio"));
                        String experience = "Experience: " + document.getString("experience");
                        String hourlyRate = "years & \tHourly Rate: " + document.getString("hourly_rate") + "$/hour";
                        exp.setText(experience + hourlyRate);
                        float ratings = Float.parseFloat(document.getString("ratings"));
                        ratingBar.setRating(ratings);

                        if (document.contains("account_no")) {
                                //accountNo.setText(document.getString("account_no"));
                            epAccountNo=document.getString("account_no");
                        }

                        if (document.contains("account_no2")) {
                            //accountNo.setText(document.getString("account_no"));
                            jcAccountNo=document.getString("account_no2");
                        }


                        if (document.contains("categories")) {
                            List<String> categories = (List<String>) document.get("categories");
                            String strNew = categories.toString().replace("[", "");
                            categoriesTv.setText(strNew.replace("]", ""));
                        }
                    }

                    if (document.contains("img_url")) {
                        Glide
                                .with(getContext())
                                .load(document.getString("img_url"))
                                .centerCrop()
                                .placeholder(R.drawable.loading)
                                .into(profileImg);
                    }

                }
            }
        });

        if (isArtist) {
            try {
                firestore.collection("users").document(auth.getUid()).collection("portfolio").addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        Log.d("clima","hello");
                        if (value!=null) {
                            workImages.clear();
                            for (DocumentSnapshot doc : value) {
                                Work work= new Work();
                                work.setId(doc.getId());
                                work.setImageUrl(doc.getString("img_url"));
                                workImages.add(work);
                            }
                            adapter = new WorkAdapter(workImages);
                            recyclerView.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                        }
                    }
                });
            } catch (Exception e) {

            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {

            if (data != null) {
                if (isProfileUpload) {
                    uploadPPImg(data.getData());
                } else {
                    uploadData(data.getData());
                }
            }
        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(getContext(), ImagePicker.Companion.getError(data), Toast.LENGTH_SHORT).show();
        }

    }

    private void uploadPPImg(Uri imageUri) {
        showProgress.showProgress(getContext());
        String uId = auth.getCurrentUser().getUid();

        String docId = firestore.collection("users").getId();


        final StorageReference filePath1 = storageReference.child("pp_images" + "/" + uId + "/" + docId + ".png");
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


                    Map<String, Object> docData = new HashMap<>();
                    docData.put("img_url", downUri.toString());


                    Glide
                            .with(getContext())
                            .load(downUri.toString())
                            .centerCrop()
                            .placeholder(R.drawable.loading)
                            .into(profileImg);

                    firestore.collection("users").document(uId).update(docData).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            showProgress.hideProgress();

                            if (task.isSuccessful()) {
                                Toast.makeText(getContext(), "Uploaded", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
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


                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
                        LocalDateTime now = LocalDateTime.now();
                        Map<String, Object> docData = new HashMap<>();
                        docData.put("img_url", downUri.toString());
                        docData.put("date", dtf.format(now));

                        firestore.collection("users").document(uId).collection("portfolio").add(docData).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentReference> task) {
                                showProgress.hideProgress();

                                if (task.isSuccessful()) {
                                    Toast.makeText(getContext(), "Work Uploaded", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
                                }
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