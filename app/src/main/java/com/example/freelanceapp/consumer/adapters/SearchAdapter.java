package com.example.freelanceapp.consumer.adapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.freelanceapp.R;
import com.example.freelanceapp.artist.ArtistProfileActivity;
import com.example.freelanceapp.consumer.models.Search;

import java.util.ArrayList;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder>{

    ArrayList<Search> searches;

    public SearchAdapter(ArrayList<Search> searches) {
        this.searches = searches;
    }

    @NonNull
    @Override
    public SearchAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.lay_item_search, parent, false);
        return new SearchAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchAdapter.ViewHolder holder, int position) {
        //holder.name.setText(reviews.get(position).getUserName());
        Search search = searches.get(position);
        holder.name.setText(String.format("%s (%s)", search.getName(), search.getArea()));
        holder.exp.setText(search.getExperience()+" Years");
        holder.rate.setText(search.getHourlyRate()+"$");

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(holder.itemView.getContext(), ArtistProfileActivity.class);
                i.putExtra("uId", search.getId());
                holder.itemView.getContext().startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return searches.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name,exp,rate;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.name);
            exp = itemView.findViewById(R.id.exp);
            rate = itemView.findViewById(R.id.rate);
        }
    }
}
