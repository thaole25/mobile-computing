package com.example.restaurantrecognition.ui.recentmatches;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.restaurantrecognition.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
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
            SharedPreferences sharedPreferences = context.getSharedPreferences(RecentMatchItem.PREFERENCES_STORE_NAME, Context.MODE_PRIVATE);
            String matchListJson = sharedPreferences.getString(RecentMatchItem.PREFERENCES_STORE_NAME, null);
            Gson gson = new Gson();
            Type type = new TypeToken<List<RecentMatchItem>>(){}.getType();
            List<RecentMatchItem> recentMatchItemList = gson.fromJson(matchListJson, type);
            if (recentMatchItemList != null) {
                recyclerView.setAdapter(new RecentMatchesRecyclerViewAdapter(recentMatchItemList, mListener));
            } else {
                recyclerView.setAdapter(new RecentMatchesRecyclerViewAdapter(new ArrayList<>(), mListener));
            }
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
