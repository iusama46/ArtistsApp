package com.example.freelanceapp.consumer.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.freelanceapp.R;
import com.example.freelanceapp.chat.ChatActivity;
import com.example.freelanceapp.chat.adapter.ChatAdapter;
import com.example.freelanceapp.chat.configuration.FirebaseConfigurations;
import com.example.freelanceapp.chat.model.Chat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class ChatsFragment extends Fragment {

    //constants
    private static final String TAG = "ChatsFragment";
    //firebase
    private final DatabaseReference databaseReference = FirebaseConfigurations.getFirebaseDatabase();
    private final DatabaseReference chatsReference = databaseReference.child("chats");
    LinearLayout layout;
    //lists
    private ListView listView;
    private ArrayAdapter<Chat> adapter;
    private ArrayList<Chat> chats;
    private ValueEventListener valueEventListenerChats;

    public ChatsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onStart() {
        super.onStart();
        String loggedUserId = FirebaseAuth.getInstance().getUid();
        chatsReference.child(loggedUserId).addValueEventListener(valueEventListenerChats);
        Log.i(TAG, "ValueEventListener: onStart");
    }

    @Override
    public void onStop() {
        super.onStop();
        chatsReference.removeEventListener(valueEventListenerChats);
        Log.i(TAG, "ValueEventListener: onStop");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chats, container, false);
        chats = new ArrayList<>();
        listView = view.findViewById(R.id.lv_chats);
        layout = view.findViewById(R.id.llChatNotFound);

        //listView.setVisibility(View.GONE);
        layout.setVisibility(View.VISIBLE);
        adapter = new ChatAdapter(getActivity(), chats);
        listView.setAdapter(adapter);
        //retrieving contacts from Firebase
        valueEventListenerChats = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //cleaning list
                chats.clear();

                //listing contacts
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    Chat chat = data.getValue(Chat.class);
                    chats.add(chat);
                }
                if (chats.size() > 0) {
                    // listView.setVisibility(View.VISIBLE);
                    layout.setVisibility(View.GONE);
                } else {
                    //listView.setVisibility(View.GONE);
                    layout.setVisibility(View.VISIBLE);
                }
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), ChatActivity.class);
                //retrieving data
                Chat chat = chats.get(position);

                //sending data to ChatActivity
                intent.putExtra("name", chat.getName());
                //String email = Base64Custom.decodeBase64(chat.getIdUser());
                String email = chat.getIdUser();
                intent.putExtra("u_id", email);
                intent.putExtra("phone_num", email);

                startActivity(intent);
            }
        });
        return view;
    }

}
