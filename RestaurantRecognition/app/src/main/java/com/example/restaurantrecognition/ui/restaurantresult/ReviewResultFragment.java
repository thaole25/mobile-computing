package com.example.restaurantrecognition.ui.restaurantresult;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.restaurantrecognition.R;
import com.example.restaurantrecognition.ui.adapter.JSONAdapter;
import com.example.restaurantrecognition.ui.adapter.Restaurant;
import com.example.restaurantrecognition.ui.adapter.RestaurantListAdapter;
import com.example.restaurantrecognition.ui.adapter.Review;
import com.example.restaurantrecognition.ui.adapter.ReviewListAdapter;
import com.example.restaurantrecognition.ui.searchresult.SearchResultViewModel;
import com.example.restaurantrecognition.ui.zomatoapi.ZomatoAccess;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class ReviewResultFragment extends Fragment {

    SearchResultViewModel reviewResultViewModel;
    ReviewListAdapter listViewAdapter;
    List<Review> reviewList;
    ListView listView;
    Restaurant restaurant;
    int offset;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        reviewResultViewModel =
                ViewModelProviders.of(this).get(SearchResultViewModel.class);
        View root = inflater.inflate(R.layout.review_fragment, container, false);

        listView = root.findViewById(R.id.reviewFragment);

        offset = 0;

        Bundle bundle = this.getArguments();
        restaurant = (Restaurant)bundle.get("Restaurant");

        GetReviewAsync getReviewAsync = new GetReviewAsync();
        try {
            reviewList = getReviewAsync.execute(restaurant).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        listViewAdapter = new ReviewListAdapter(
                getActivity(),R.layout.review_layout,reviewList
        );

        listView.setAdapter(listViewAdapter);
        listView.setOnScrollListener(onScrollListener());

        return root;
    }

    private class GetReviewAsync extends AsyncTask<Restaurant, Void, List<Review>> {

        List<Review> reviewList = new ArrayList<>();

        @Override
        protected List<Review> doInBackground(Restaurant... restaurants) {

            ZomatoAccess zomatoAccess = new ZomatoAccess();
            JSONAdapter jsonAdapter = new JSONAdapter();

            //GET Reviews
            String reviews = zomatoAccess.getReview(restaurants[0].getId(),offset);

            //GET RESTAURANT LIST
            reviewList = jsonAdapter.getReviews(reviews);

            return reviewList;
        }

    }

    private AbsListView.OnScrollListener onScrollListener() {
        return new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                int threshold = 1;
                int count = listView.getCount();

                if (scrollState == SCROLL_STATE_IDLE) {
                    if (listView.getLastVisiblePosition() >= (count - threshold)) {
                        GetReviewAsync getReviewAsync = new GetReviewAsync();
                        offset = offset+5;
                        try {
                            List<Review> newReviews = getReviewAsync.execute(restaurant).get();
                            for (int i=0 ; i<newReviews.size() ; i++) {
                                reviewList.add(newReviews.get(i));
                            }
                            listViewAdapter.notifyDataSetChanged();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }


            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
                                 int totalItemCount) {
            }

        };
    }
}
