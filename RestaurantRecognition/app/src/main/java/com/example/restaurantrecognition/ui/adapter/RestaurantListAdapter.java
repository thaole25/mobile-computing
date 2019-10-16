package com.example.restaurantrecognition.ui.adapter;

import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.restaurantrecognition.R;

import org.w3c.dom.Text;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class RestaurantListAdapter extends ArrayAdapter<Restaurant> {

    Context mCtx;
    int resource;
    List<Restaurant> restaurantList;

    public RestaurantListAdapter(Context mCtx, int resource, List<Restaurant> restaurantList) {

        super(mCtx, resource, restaurantList);
        this.mCtx = mCtx;
        this.resource = resource;
        this.restaurantList = restaurantList;

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.items_layout, null);

        TextView textViewName = view.findViewById(R.id.textViewName);
        TextView textViewAddress = view.findViewById(R.id.textViewAddress);
        TextView textViewRating = view.findViewById(R.id.textViewRating);
        ImageView imageView = view.findViewById(R.id.imageView);

        Restaurant restaurant = restaurantList.get(position);
        textViewName.setText(restaurant.getName());
        textViewAddress.setText(restaurant.getAddress());
        textViewRating.setText(restaurant.getRating());
       // imageView.setImageDrawable(mCtx.getResources().getDrawable(hero.getImage()));

        return view;
    }

}
