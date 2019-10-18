package com.example.restaurantrecognition.ui.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.restaurantrecognition.R;

import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.ExecutionException;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class RestaurantListAdapter extends ArrayAdapter<Restaurant> {

    Context mCtx;
    int resource;
    List<Restaurant> restaurantList;
    TextView textViewName;
    TextView textViewAddress;
    TextView textViewRating;
    ImageView imageView;
    ImageView starIcon;

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

        textViewName = view.findViewById(R.id.textViewName);
        textViewAddress = view.findViewById(R.id.textViewAddress);
        textViewRating = view.findViewById(R.id.textViewRating);
        imageView = view.findViewById(R.id.imageView);
        starIcon = view.findViewById(R.id.starIcon);

        Restaurant restaurant = restaurantList.get(position);
        textViewName.setText(restaurant.getName());
        textViewAddress.setText(restaurant.getAddress());
        textViewRating.setText(restaurant.getRating());
        starIcon.setImageResource(R.drawable.staricon);
        URL url = null;
        try {
            url = new URL(restaurant.getImage());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        GetImageAsync getImageAsync = new GetImageAsync();
        try {
            imageView.setImageBitmap(getImageAsync.execute(url).get());
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        return view;
    }

    private class GetImageAsync extends AsyncTask<URL, Void, Bitmap> {

        Bitmap bmp = null;

        @Override
        protected Bitmap doInBackground(URL... urls) {

            try {
                bmp = BitmapFactory.decodeStream(urls[0].openConnection().getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }

            return bmp;
        }

    }

}
