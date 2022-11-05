package com.example.weatherornot;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.MyViewHolder> {

    Context c;
    ArrayList<String> planets, introductions;
    int[] images;
    private ItemClickListener clickListener;

    public MyRecyclerViewAdapter(Context c, ArrayList<String> planets, ArrayList<String> introductions, int[] images) {
        this.c = c;
        this.planets = planets;
        this.introductions = introductions;
        this.images = images;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(c);
        View view = inflater.inflate(R.layout.my_row_layout, parent, false);
        return new MyViewHolder((view));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.text1.setText(planets.get(position));
        holder.text2.setText(introductions.get(position));
        holder.image.setImageResource(images[position]);
    }

    @Override
    public int getItemCount() {
        return images.length;
    }

    public void setClickListener(ItemClickListener itemClickListener) {
        this.clickListener = itemClickListener;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView text1, text2;
        ImageView image;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            text1 = itemView.findViewById(R.id.planet);
            text2 = itemView.findViewById(R.id.introduction);
            image = itemView.findViewById(R.id.imageView);
            itemView.setOnClickListener(this);
//            image.setOnClickListener(this);
        }

        public void onClick(View itemView) {
            if (clickListener != null) clickListener.onClick(itemView, getBindingAdapterPosition());
        }
    }
}
