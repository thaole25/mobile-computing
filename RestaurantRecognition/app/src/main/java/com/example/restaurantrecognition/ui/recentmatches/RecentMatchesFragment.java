package com.example.restaurantrecognition.ui.recentmatches;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.restaurantrecognition.R;
import com.example.restaurantrecognition.database.AppDatabase;
import com.example.restaurantrecognition.database.RecentMatch;

import java.util.ArrayList;
import java.util.List;

public class RecentMatchesFragment extends Fragment {

    private OnListFragmentInteractionListener mListener;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public RecentMatchesFragment() {
    }

    public static RecentMatchesFragment newInstance(int columnCount) {
        RecentMatchesFragment fragment = new RecentMatchesFragment();
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recentmatches_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            AppDatabase appDatabase = AppDatabase.getInstance(context);
            List<RecentMatch> recentMatches = appDatabase.recentMatchDao().getAll();
            List<RecentMatchItem> recentMatchItems = new ArrayList<>();
            for (RecentMatch match : recentMatches) {
                recentMatchItems.add(new RecentMatchItem(match.id, match.restaurantName, match.zomatoId));
            }
            recyclerView.setAdapter(new RecentMatchesRecyclerViewAdapter(recentMatchItems, mListener));
        }
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnListFragmentInteractionListener {
        void onListFragmentInteraction(RecentMatchItem item);
    }
}
