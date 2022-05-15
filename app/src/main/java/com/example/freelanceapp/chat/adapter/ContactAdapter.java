package com.example.freelanceapp.chat.adapter;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.freelanceapp.R;
import com.example.freelanceapp.chat.model.Contact;

import java.util.ArrayList;





public class ContactAdapter extends ArrayAdapter<Contact> {
    private ArrayList<Contact> contacts;
    private Context context;

    public ContactAdapter(@NonNull Context context, @NonNull ArrayList<Contact> objects) {
        super(context, 0, objects);
        this.contacts = objects;
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = null;

        //verifying if list is empty
        if (contacts != null) {
            //inicializing object for building the view
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);

            //building view from XML
            view = inflater.inflate(R.layout.contact_list, parent, false);

            //retrieving element for exibition
            TextView contactName = view.findViewById(R.id.tv_name);
            TextView contactEmail = view.findViewById(R.id.tv_email);

            Contact contact = contacts.get(position);
            contactName.setText(contact.getName());
            contactEmail.setText(contact.getEmail());

        }

        return view;
    }
}
