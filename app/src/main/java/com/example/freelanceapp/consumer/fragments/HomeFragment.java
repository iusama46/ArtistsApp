package com.example.freelanceapp.consumer.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.freelanceapp.R;
import com.example.freelanceapp.consumer.adapters.SearchAdapter;
import com.example.freelanceapp.consumer.models.Search;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Locale;

public class HomeFragment extends Fragment {

    private final ArrayList<Search> resultList = new ArrayList<>();
    ArrayList<Search> allSearches = new ArrayList<>();
    SearchAdapter searchAdapter;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;
    private SearchView search;
    private RecyclerView recyclerView;
    private String area = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);
        try {
            currentUser = FirebaseAuth.getInstance().getCurrentUser();
            search = view.findViewById(R.id.search);
            recyclerView = view.findViewById(R.id.search_result);

            LinearLayoutManager manager = new LinearLayoutManager(getContext());
            manager.setOrientation(RecyclerView.VERTICAL);
            recyclerView.setLayoutManager(manager);


            search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    if (query.length() > 0) {
                        handleSearch(query);

                    } else if (query.isEmpty() || query.length() == 0) {
                        loadSuggestionsList(allSearches);
                    }
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    if (newText.length() > 0) {
                        handleSearch(newText);
                    } else if (newText.isEmpty() || newText.length() == 0) {
                        loadSuggestionsList(allSearches);
                    }
                    return true;
                }
            });


            db = FirebaseFirestore.getInstance();

            loadSuggestions();
        } catch (Exception e) {
            //utils.toast(getContext(), e.getMessage(), 1);
            Log.d("E51", e.getMessage());

        }

        return view;
    }


    private void loadSuggestionsList(ArrayList<Search> searches) {
        searchAdapter = new SearchAdapter(searches);
        recyclerView.setAdapter(searchAdapter);
        searchAdapter.notifyDataSetChanged();
    }


    private void loadSuggestions() {
        allSearches.clear();
        try {
            db.collection("users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isComplete()) {
                        area = area.toLowerCase(Locale.ROOT);
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            long type = (long) document.get("type");
                            if (type == 0) {
                                Search search = new Search();
                                search.setId(document.getId());
                                search.setName(document.getString("name"));
                                search.setExperience(document.getString("experience"));
                                search.setHourlyRate(document.getString("hourly_rate"));
                                search.setArea(document.getString("area"));
                                allSearches.add(search);

                            } else {
                                if (currentUser.getUid().equals(document.getId())) {
                                    area = document.getString("area").toLowerCase();
                                }
                            }
                        }
                        loadSuggestionsList(allSearches);
                    }
                }
            });

        } catch (Exception e) {
            Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
        }
    }

    public void handleSearch(String v) {
        try {
            resultList.clear();
            v = v.toLowerCase(Locale.ROOT);
            for (Search search : allSearches) {
                if (search.getName().toLowerCase(Locale.ROOT).contains(v) || search.getArea().toLowerCase(Locale.ROOT).contains(v)) {
                    resultList.add(search);

                }
            }
            loadSuggestionsList(resultList);

        } catch (Exception e) {
            Log.d("clima", e.getLocalizedMessage());
        }
    }
}