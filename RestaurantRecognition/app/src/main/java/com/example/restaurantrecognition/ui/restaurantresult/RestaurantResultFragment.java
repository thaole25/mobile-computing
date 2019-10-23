package com.example.restaurantrecognition.ui.restaurantresult;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.example.restaurantrecognition.ui.adapter.DailyMenu;
import com.example.restaurantrecognition.ui.adapter.Food;
import com.example.restaurantrecognition.ui.adapter.JSONAdapter;
import com.example.restaurantrecognition.ui.adapter.Restaurant;
import com.example.restaurantrecognition.ui.adapter.Review;
import com.example.restaurantrecognition.ui.adapter.ReviewListAdapter;
import com.example.restaurantrecognition.ui.searchresult.SearchResultFragment;
import com.example.restaurantrecognition.ui.searchresult.SearchResultViewModel;
import com.example.restaurantrecognition.ui.zomatoapi.ZomatoAccess;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class RestaurantResultFragment extends Fragment {

    private SearchResultViewModel searchResultViewModel;
    private TextView viewName, viewAddress, viewRating, gMapText, allReview, normalMenu,
            currency, price;
    private ImageView viewImage, ratingStar;
    private Button buttonNotRight, buttonCheapest;
    private LinearLayout listView, menuView;

    private String name, address;
    private double lat, lon;
    private int zomatoId;

    private ProgressDialog progressDialog;
    private Restaurant restaurant;
    private List<Restaurant> restaurants;

    private View root;
    private LayoutInflater inflater;

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
        this.zomatoId = bundle.getInt("ZomatoId");

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

    private class GetDailyAsync extends AsyncTask<Restaurant, Void, List<DailyMenu>>{

        List<DailyMenu> dailyList = new ArrayList<>();

        @Override
        protected List<DailyMenu> doInBackground(Restaurant... restaurants) {

            ZomatoAccess zomatoAccess = new ZomatoAccess();
            JSONAdapter jsonAdapter = new JSONAdapter();

            //GET DailyMenu JSON
            String daily = zomatoAccess.getDailyMenu(restaurants[0].getId());

            //GET DailyMenu List
            dailyList = jsonAdapter.getDailyMenu(daily);

            return dailyList;
        }

    }

    private class ZomatoAsync extends AsyncTask<Void, Void, List<Restaurant>> {

        @Override
        protected List<Restaurant> doInBackground(Void... voids) {

            List<Restaurant> restaurantList;

            ZomatoAccess zomatoAccess = new ZomatoAccess();
            JSONAdapter jsonAdapter = new JSONAdapter();
            String resList = null;
            //GET LOCATION
            String res = zomatoAccess.findNearbyLocation(lat, lon, address);
            int city_id = jsonAdapter.getLocationId(res);
            //GET RESTAURANT LIST
            resList = zomatoAccess.findMatchingRestaurants(name, city_id, lat, lon);
            restaurantList = jsonAdapter.getRestaurantList(resList);

            if (zomatoId != 0){
                String predictedRestaurant = zomatoAccess.getRestaurantDetails(Integer.toString(zomatoId));
                List<Restaurant> predictedRestaurantList = jsonAdapter.getRestaurantDetails(predictedRestaurant);
                predictedRestaurantList.addAll(restaurantList);
                return predictedRestaurantList;
            }

            return restaurantList;
        }

        @Override
        protected void onPostExecute(List<Restaurant> newRestaurants) {

            restaurant = newRestaurants.get(0);
            restaurants = newRestaurants;

            setupViewElements(root);
            setupOnClickListener();

            retrieveRestaurantImage();
            getDailyMenu();
            retrieveRestaurantReviews(inflater);

            progressDialog.dismiss();
        }

    }

    private class GetClosestAsync extends AsyncTask<Void, Void, List<Restaurant>> {

        @Override
        protected List<Restaurant> doInBackground(Void... voids) {

            List<Restaurant> restaurantList;

            ZomatoAccess zomatoAccess = new ZomatoAccess();
            JSONAdapter jsonAdapter = new JSONAdapter();
            String resList = null;
            //GET LOCATION
            String res = zomatoAccess.findNearbyLocation(lat, lon, address);
            int city_id = jsonAdapter.getLocationId(res);
            //GET RESTAURANT LIST
            resList = zomatoAccess.findNearbyRestaurants(name, city_id, lat, lon);
            restaurantList = jsonAdapter.getRestaurantList(resList);

            return restaurantList;
        }

    }

    private void setupViewElements(View root) {

        viewName = root.findViewById(R.id.restaurantName);
        viewAddress = root.findViewById(R.id.addressContent);
        viewRating = root.findViewById(R.id.ratingNumber);
        viewImage = root.findViewById(R.id.imageView2);
        ratingStar = root.findViewById(R.id.ratingStar);
        gMapText = root.findViewById(R.id.googleMapText);
        allReview = root.findViewById(R.id.allreviews);
        menuView = root.findViewById(R.id.dailylistView);
        listView = root.findViewById(R.id.reviewlistView);
        normalMenu = root.findViewById(R.id.normalmenuText);
        currency = root.findViewById(R.id.currency);
        price = root.findViewById(R.id.priceContent);
        buttonNotRight = root.findViewById(R.id.btnNotRightRestaurant);
        buttonCheapest = root.findViewById(R.id.btnNearbyRestaurant);

        viewName.setText(restaurant.getName());
        viewAddress.setText(restaurant.getAddress());
        viewRating.setText(restaurant.getRating());
        currency.setText(restaurant.getCurrency());
        price.setText(restaurant.getPriceForTwo());
        ratingStar.setImageResource(R.drawable.staricon);

    }

    private void setupOnClickListener(){

        normalMenu.setOnClickListener(new View.OnClickListener() {
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

        buttonNotRight.setOnClickListener(new View.OnClickListener() {
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

        buttonCheapest.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                FragmentManager fm = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fm.beginTransaction();
                Bundle bundle = new Bundle();

                GetClosestAsync getClosestAsync = new GetClosestAsync();
                try {
                    List<Restaurant> restaurantList = getClosestAsync.execute().get();
                    List<Restaurant> tempList = new ArrayList<>();

                    /* Filtering cheaper only */
                    for (int i=0; i<restaurantList.size() ;i++) {
                        if (Double.parseDouble(restaurantList.get(i).getPriceForTwo()) < Double.parseDouble(restaurant.getPriceForTwo())) {
                            tempList.add(restaurantList.get(i));
                        }
                    }

                    restaurantList = tempList;

                    int sizeCount = 0;
                    while (sizeCount<restaurantList.size()) {
                        bundle.putSerializable("Restaurant"+sizeCount, restaurantList.get(sizeCount));
                        sizeCount++;
                    }
                    bundle.putInt("Count",restaurantList.size());

                    SearchResultFragment fragment = new SearchResultFragment();

                    fragment.setArguments(bundle);
                    fragmentTransaction.replace(R.id.fragmentContent, fragment);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        });

    }

    private void getDailyMenu() {
        GetDailyAsync getDailyAsync = new GetDailyAsync();
        List<DailyMenu> menuList=null;
       try {
            menuList = getDailyAsync.execute(restaurant).get();

            for (int i=0; i<menuList.size(); i++) {
                DailyMenu menu = menuList.get(i);
                List<Food> foods = menu.getDishes();
                View viDaily = inflater.inflate(R.layout.daily_layout, null);
                TextView dailyName = viDaily.findViewById(R.id.dailyName);
                TextView dailyStart = viDaily.findViewById(R.id.dailyStart);
                TextView dailyEnd = viDaily.findViewById(R.id.dailyEnd);
                dailyName.setText(menu.getName());
                dailyStart.setText(menu.getStart_date());
                dailyEnd.setText(menu.getEnd_date());
                menuView.addView(viDaily);

                for (int j=0; j<foods.size();i++) {
                    View vi = inflater.inflate(R.layout.dishes_layout, null);
                    TextView foodName = vi.findViewById(R.id.menuName);
                    TextView foodPrice = vi.findViewById(R.id.menuPrice);
                    foodName.setText(foods.get(j).getName());
                    foodPrice.setText(foods.get(j).getPrice());
                    menuView.addView(vi);
                }
            }

            if (menuList.size()==0) {
                View vi = inflater.inflate(R.layout.menu_notfound, null);
                menuView.addView(vi);
            }

        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void retrieveRestaurantImage() {
        URL url = null;
        try {
            url = new URL(restaurant.getImage());
            GetImageAsync getImageAsync = new GetImageAsync();
            try {
                viewImage.setImageBitmap(getImageAsync.execute(url).get());
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

    }

    private void retrieveRestaurantReviews(LayoutInflater inflater) {
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
