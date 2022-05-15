package com.example.freelanceapp.artist;

import android.os.Bundle;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.freelanceapp.R;
import com.example.freelanceapp.artist.fragments.BookingsFragment;
import com.example.freelanceapp.consumer.fragments.ChatsFragment;
import com.example.freelanceapp.consumer.fragments.HomeFragment;
import com.example.freelanceapp.consumer.fragments.ProfileFragment;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;

public class ArtistMainActivity extends AppCompatActivity {
    public static ChipNavigationBar chipNavigationBar;
    FrameLayout frameLayout;

    private void setFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(frameLayout.getId(), fragment);
        transaction.commit();
    }

    private void bottomMenu() {
        chipNavigationBar.setOnItemSelectedListener(new ChipNavigationBar.OnItemSelectedListener() {
            @Override
            public void onItemSelected(int i) {
                if (R.id.home == i) {
                    setFragment(new BookingsFragment());
                } else if (i == R.id.chats) {
                    setFragment(new ChatsFragment());
                } else if (i == R.id.profile) {
                    setFragment(new ProfileFragment());
                }
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artist_main);

        frameLayout = findViewById(R.id.frame_lay);

        setFragment(new BookingsFragment());
        chipNavigationBar = findViewById(R.id.bottom_nav_menu);
        chipNavigationBar.setItemSelected(R.id.home, true);
        bottomMenu();
    }
}