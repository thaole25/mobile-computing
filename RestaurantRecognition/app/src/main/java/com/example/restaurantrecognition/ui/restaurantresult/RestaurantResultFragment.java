package com.example.restaurantrecognition.ui.restaurantresult;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;

import com.example.restaurantrecognition.R;
import com.example.restaurantrecognition.ui.adapter.JSONAdapter;
import com.example.restaurantrecognition.ui.adapter.Restaurant;
import com.example.restaurantrecognition.ui.adapter.Review;
import com.example.restaurantrecognition.ui.adapter.ReviewListAdapter;
import com.example.restaurantrecognition.ui.searchresult.SearchResultViewModel;
import com.example.restaurantrecognition.ui.zomatoapi.ZomatoAccess;

import org.w3c.dom.Text;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class RestaurantResultFragment extends Fragment {

    private SearchResultViewModel searchResultViewModel;
    TextView viewName, viewAddress, viewRating, gMapText, allReview;
    ImageView viewImage, viewImage2, viewImage3, ratingStar;
    LinearLayout listView;


    ReviewListAdapter reviewListAdapter;

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
        ratingStar = root.findViewById(R.id.ratingStar);
        gMapText = root.findViewById(R.id.googleMapText);
        allReview = root.findViewById(R.id.allreviews);
        listView = root.findViewById(R.id.reviewlistView);

        viewName.setText(restaurant.getName());
        viewAddress.setText(restaurant.getAddress());
        viewRating.setText(restaurant.getRating());
        viewImage2.setImageResource(R.drawable.dailyicon);
        viewImage3.setImageResource(R.drawable.normalmenu);
        ratingStar.setImageResource(R.drawable.staricon);

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

                /*FragmentManager fm = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fm.beginTransaction();
                Bundle bundle = new Bundle();

                Restaurant restaurantObject = restaurant;
                bundle.putSerializable("Restaurant", restaurantObject);


                MenuFragment fragment = new MenuFragment();

                fragment.setArguments(bundle);
                fragmentTransaction.replace(R.id.fragmentContent, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();*/
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
}
