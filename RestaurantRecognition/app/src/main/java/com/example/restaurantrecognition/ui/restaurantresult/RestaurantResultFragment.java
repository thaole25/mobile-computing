package com.example.restaurantrecognition.ui.restaurantresult;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.restaurantrecognition.R;
import com.example.restaurantrecognition.ui.adapter.Restaurant;
import com.example.restaurantrecognition.ui.adapter.RestaurantListAdapter;
import com.example.restaurantrecognition.ui.searchresult.SearchResultViewModel;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutionException;

public class RestaurantResultFragment extends Fragment {

    private SearchResultViewModel searchResultViewModel;
    TextView viewName, viewAddress, viewRating;
    ImageView viewImage, viewImage2, viewImage3;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        searchResultViewModel =
                ViewModelProviders.of(this).get(SearchResultViewModel.class);
        View root = inflater.inflate(R.layout.restaurant_information, container, false);

        Bundle bundle = this.getArguments();

        Restaurant restaurant = (Restaurant)bundle.get("Restaurant");
        viewName = root.findViewById(R.id.restaurantName);
        viewAddress = root.findViewById(R.id.addressContent);
        viewRating = root.findViewById(R.id.ratingNumber);
        viewImage = root.findViewById(R.id.imageView2);
        viewImage2 = root.findViewById(R.id.imageView3);
        viewImage3 = root.findViewById(R.id.imageView4);

        viewName.setText(restaurant.getName());
        viewAddress.setText(restaurant.getAddress());
        viewRating.setText(restaurant.getRating());
        viewImage2.setImageResource(R.drawable.dailymenu);
        viewImage3.setImageResource(R.drawable.normalmenu);

        viewImage2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // your code here
            }
        });
        viewImage3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // your code here
            }
        });

        URL url = null;
        try {
            url = new URL(restaurant.getImage());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        GetImageAsync getImageAsync = new GetImageAsync();
        try {
            viewImage.setImageBitmap(getImageAsync.execute(url).get());
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return root;
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
