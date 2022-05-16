package com.example.freelanceapp.activities;

import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.freelanceapp.R;
import com.github.chrisbanes.photoview.PhotoView;

public class ImageActivity extends AppCompatActivity {


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("Image Viewer");
        PhotoView photoView = (PhotoView) findViewById(R.id.photo_view);

        String url = getIntent().getStringExtra("img_url");

        Glide
                .with(this)
                .load(Uri.parse(url))
                .centerCrop()
                .placeholder(R.drawable.loading)
                .into(photoView);
    }
}