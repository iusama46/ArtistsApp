package com.example.freelanceapp.chat.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.freelanceapp.R;
import com.example.freelanceapp.chat.ChatActivity;

import com.example.freelanceapp.chat.adapter.ContactAdapter;
import com.example.freelanceapp.chat.model.Contact;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class ContactsFragment extends Fragment {
    //constants
    private static final String TAG = "ContactsFragment";
    DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("users");
    DatabaseReference ref_groups = FirebaseDatabase.getInstance().getReference().child("blood_banks");
    //CShowProgress cShowProgress = CShowProgress.getInstance();
    //lists
    private ListView listView;
    private ArrayAdapter adapter;
    private ArrayList<Contact> contacts;
    FirebaseAuth auth;

    public ContactsFragment() {
    }

    @Override
    public void onStart() {
        super.onStart();
        auth =FirebaseAuth.getInstance();
        /*if (MainActivity.mAuth.getCurrentUser() != null) {

            String uId = MainActivity.mAuth.getCurrentUser().getUid().toString();
        }*/

        Log.i(TAG, "ValueEventListener: onStart");
    }

    @Override
    public void onResume() {
        super.onResume();
        //cShowProgress.hideProgress();
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.i(TAG, "ValueEventListener: onStop");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_contacts, container, false);
        contacts = new ArrayList<>();
        listView = view.findViewById(R.id.lv_contacts);
        adapter = new ContactAdapter(getActivity(), contacts);
        listView.setAdapter(adapter);
        //cShowProgress.showProgress(getContext());
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                contacts.clear();
                String id = auth.getUid();
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    try {
                        String key = data.getKey().toString();
                        if (!key.equals(id)) {
                            String name = data.child("name").getValue().toString();
                            String phone_no = data.child("phone_num").getValue().toString();
                            //String userId = data.child("user_id").getValue().toString();
                            Contact contact = new Contact(phone_no, name, phone_no);
                            contacts.add(contact);
                            adapter.notifyDataSetChanged();
                        }
                        Log.d("clima", String.valueOf(contacts.size()));
                    } catch (Exception e) {
                        Toast.makeText(getActivity(), "error", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                //cShowProgress.hideProgress();
            }
        });

        ref_groups.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String id = auth.getUid();
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    //cShowProgress.hideProgress();
                    try {
                        String key = data.getKey().toString();
                        if (!key.equals(id)) {
                            String name = data.child("name").getValue().toString();
                            name = name + " (Blood Bank)";
                            String phone_no = data.child("phone_num").getValue().toString();
                            //String userId = data.child("user_id").getValue().toString();
                            Contact contact = new Contact(phone_no, name, phone_no);

                            contacts.add(contact);
                            adapter.notifyDataSetChanged();
                        }
                        Log.d("clima1", String.valueOf(contacts.size()));
                    } catch (Exception e) {
                        Toast.makeText(getActivity(), "error", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                //cShowProgress.hideProgress();
            }
        });


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), ChatActivity.class);
                Contact contact = contacts.get(position);
                intent.putExtra("name", contact.getName());
                intent.putExtra("phone_num", contact.getEmail());
                intent.putExtra("u_id", contact.getUserId());
                startActivity(intent);
            }
        });

        return view;
    }

}
