package com.example.restaurantrecognition.ui.recentmatches;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.restaurantrecognition.R;
import com.example.restaurantrecognition.ui.recentmatches.RecentMatchesFragment.OnListFragmentInteractionListener;
import com.example.restaurantrecognition.ui.recentmatches.dummy.DummyContent.DummyItem;

import java.util.List;

public class RecentMatchesRecyclerViewAdapter extends RecyclerView.Adapter<RecentMatchesRecyclerViewAdapter.ViewHolder> {

    private final List<RecentMatchItem> mMatches;
    private final OnListFragmentInteractionListener mListener;

    public RecentMatchesRecyclerViewAdapter(List<RecentMatchItem> matches, OnListFragmentInteractionListener listener) {
        mMatches = matches;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_recentmatches, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mMatch = mMatches.get(position);
        holder.mRestaurantNameView.setText(mMatches.get(position).restaurantName);

        holder.mView.setOnClickListener(v -> {
            if (null != mListener) {
                mListener.onListFragmentInteraction(holder.mMatch);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mMatches.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mRestaurantNameView;
        public RecentMatchItem mMatch;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mRestaurantNameView = (TextView) view.findViewById(R.id.restaurant_name);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mRestaurantNameView.getText() + "'";
        }
    }
}
