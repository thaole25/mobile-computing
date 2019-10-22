package com.example.restaurantrecognition.ui.restaurantresult;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;

import com.example.restaurantrecognition.R;
import com.example.restaurantrecognition.ui.adapter.JSONAdapter;
import com.example.restaurantrecognition.ui.adapter.Restaurant;
import com.example.restaurantrecognition.ui.adapter.RestaurantListAdapter;
import com.example.restaurantrecognition.ui.adapter.Review;
import com.example.restaurantrecognition.ui.adapter.ReviewListAdapter;
import com.example.restaurantrecognition.ui.searchresult.SearchResultFragment;
import com.example.restaurantrecognition.ui.searchresult.SearchResultViewModel;
import com.example.restaurantrecognition.ui.zomatoapi.ZomatoAccess;

import org.w3c.dom.Text;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.zip.Inflater;

public class RestaurantResultFragment extends Fragment {

    private SearchResultViewModel searchResultViewModel;
    TextView viewName, viewAddress, viewRating, gMapText, allReview;
    ImageView viewImage, viewImage2, viewImage3, ratingStar;
    Button button;
    LinearLayout listView;

    String name, address;
    double lat, lon;

    ProgressDialog progressDialog;
    ReviewListAdapter reviewListAdapter;
    Restaurant restaurant;
    List<Restaurant> restaurants;

    View root;
    LayoutInflater inflater;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        searchResultViewModel =
                ViewModelProviders.of(this).get(SearchResultViewModel.class);
        root = inflater.inflate(R.layout.restaurant_information, container, false);
        this.inflater = inflater;

        Bundle bundle = this.getArguments();

        this.name = bundle.getString("Name");
        this.address = bundle.getString("Address");
        this.lat = bundle.getDouble("Latitude");
        this.lon = bundle.getDouble("Longitude");
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setTitle("Loading Restaurant Data...");
        progressDialog.setMessage("Please Wait...");
        progressDialog.show();
        ZomatoAsync zomatoAsync = new ZomatoAsync();

        zomatoAsync.execute();

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

    private class GetReviewAsync extends AsyncTask<Restaurant, Void, List<Review>>{

        List<Review> reviewList = new ArrayList<>();

        @Override
        protected List<Review> doInBackground(Restaurant... restaurants) {

            ZomatoAccess zomatoAccess = new ZomatoAccess();
            JSONAdapter jsonAdapter = new JSONAdapter();

            //GET Reviews
            String reviews = zomatoAccess.getReview(restaurants[0].getId(),0);

            //GET RESTAURANT LIST
            reviewList = jsonAdapter.getReviews(reviews);

            return reviewList;
        }

    }

    private class ZomatoAsync extends AsyncTask<Void, Void, List<Restaurant>> {

        @Override
        protected List<Restaurant> doInBackground(Void... voids) {

            List<Restaurant> restaurantList;

            ZomatoAccess zomatoAccess = new ZomatoAccess();
            JSONAdapter jsonAdapter = new JSONAdapter();
            //GET LOCATION
            String res = zomatoAccess.findNearbyLocation(lat, lon, address);
            int city_id = jsonAdapter.getLocationId(res);
            //GET RESTAURANT LIST
            String resList = zomatoAccess.findMatchingRestaurants(name, city_id, lat, lon);
            restaurantList = jsonAdapter.getRestaurantList(resList);

            return restaurantList;
        }

        @Override
        protected void onPostExecute(List<Restaurant> newRestaurants) {

            restaurant = newRestaurants.get(0);
            restaurants = newRestaurants;

            setupViewElements(root);
            setupOnClickListener();

            retrieveRestaurantImage();
            retrieveRestaurantReviews(inflater);

            progressDialog.dismiss();
        }

    }

    private void setupViewElements(View root) {

        viewName = root.findViewById(R.id.restaurantName);
        viewAddress = root.findViewById(R.id.addressContent);
        viewRating = root.findViewById(R.id.ratingNumber);
        viewImage = root.findViewById(R.id.imageView2);
        viewImage2 = root.findViewById(R.id.imageView3);
        viewImage3 = root.findViewById(R.id.imageView4);
        ratingStar = root.findViewById(R.id.ratingStar);
        gMapText = root.findViewById(R.id.googleMapText);
        allReview = root.findViewById(R.id.allreviews);
        listView = root.findViewById(R.id.reviewlistView);
        button = root.findViewById(R.id.button);

        viewName.setText(restaurant.getName());
        viewAddress.setText(restaurant.getAddress());
        viewRating.setText(restaurant.getRating());
        viewImage2.setImageResource(R.drawable.dailyicon);
        viewImage3.setImageResource(R.drawable.normalmenu);
        ratingStar.setImageResource(R.drawable.staricon);

    }

    public void setupOnClickListener(){

        viewImage2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // your code here
            }
        });
        viewImage3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                String url = restaurant.getMenuURL();
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(browserIntent);

            }
        });
        allReview.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                FragmentManager fm = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fm.beginTransaction();
                Bundle bundle = new Bundle();

                Restaurant restaurantObject = restaurant;
                bundle.putSerializable("Restaurant", restaurantObject);


                ReviewResultFragment fragment = new ReviewResultFragment();

                fragment.setArguments(bundle);
                fragmentTransaction.replace(R.id.fragmentContent, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });
        gMapText.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String query = "geo:";
                query=query+restaurant.getLat()+","+restaurant.getLon()+"?q=";
                String restaurantBuffer = restaurant.getName().replaceAll(" ","+");
                query=query+restaurantBuffer;
                Uri gmmIntentUri = Uri.parse(query);
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                FragmentManager fm = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fm.beginTransaction();
                Bundle bundle = new Bundle();

                int sizeCount = 0;
                while (sizeCount<restaurants.size()) {
                    bundle.putSerializable("Restaurant"+sizeCount, restaurants.get(sizeCount));
                    sizeCount++;
                }
                bundle.putInt("Count",restaurants.size());

                SearchResultFragment fragment = new SearchResultFragment();

                fragment.setArguments(bundle);
                fragmentTransaction.replace(R.id.fragmentContent, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

    }

    public void retrieveRestaurantImage() {
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
    }

    public void retrieveRestaurantReviews(LayoutInflater inflater) {
        GetReviewAsync getReviewAsync = new GetReviewAsync();
        List<Review> reviewList;
        try {
            reviewList = getReviewAsync.execute(restaurant).get();

            for (int i=0; i<reviewList.size(); i++) {
                Review review = reviewList.get(i);
                View vi = inflater.inflate(R.layout.review_layout, null);
                TextView reviewerName = vi.findViewById(R.id.reviewerName);
                TextView reviewerRating = vi.findViewById(R.id.reviewerRating);
                TextView reviewContent = vi.findViewById(R.id.reviewContent);
                TextView reviewTime = vi.findViewById(R.id.reviewTime);
                ImageView reviewStar = vi.findViewById(R.id.reviewStar);

                reviewerName.setText(review.getUser_name());
                reviewerRating.setText(review.getRating());
                reviewContent.setText(review.getReview_text());
                reviewTime.setText(review.getReview_time());
                reviewStar.setImageResource(R.drawable.staricon);

                listView.addView(vi);
            }
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
