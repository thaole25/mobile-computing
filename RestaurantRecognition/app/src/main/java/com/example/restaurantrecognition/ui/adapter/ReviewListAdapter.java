package com.example.restaurantrecognition.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.restaurantrecognition.R;

import org.w3c.dom.Text;

import java.util.List;

public class ReviewListAdapter extends ArrayAdapter<Review> {

    Context mCtx;
    int resource;
    List<Review> reviewList;
    TextView reviewerName, reviewerRating, reviewContent, reviewTime;
    ImageView starIcon;

    public ReviewListAdapter(Context mCtx, int resource, List<Review> reviewList) {
        super(mCtx, resource, reviewList);
        this.mCtx = mCtx;
        this.resource = resource;
        this.reviewList = reviewList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {


        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.review_layout, null);

        reviewerName = view.findViewById(R.id.reviewerName);
        reviewerRating = view.findViewById(R.id.reviewerRating);
        reviewContent = view.findViewById(R.id.reviewContent);
        reviewTime = view.findViewById(R.id.reviewTime);
        starIcon = view.findViewById(R.id.reviewStar);
        starIcon.setImageResource(R.drawable.staricon);

        Review review = reviewList.get(position);
        reviewerName.setText(review.getUser_name());
        reviewerRating.setText(review.getRating());
        reviewContent.setText(review.getReview_text());
        reviewTime.setText(review.getReview_time());

        return view;
    }

}
