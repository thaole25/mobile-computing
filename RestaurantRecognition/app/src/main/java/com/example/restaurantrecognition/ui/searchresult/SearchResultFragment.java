package com.example.restaurantrecognition.ui.searchresult;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.restaurantrecognition.R;
import com.example.restaurantrecognition.ui.FragmentInteractionListener;
import com.example.restaurantrecognition.ui.adapter.JSONAdapter;
import com.example.restaurantrecognition.ui.adapter.Restaurant;
import com.example.restaurantrecognition.ui.adapter.RestaurantListAdapter;
import com.example.restaurantrecognition.ui.recentmatches.RecentMatchesFragment;
import com.example.restaurantrecognition.ui.restaurantresult.RestaurantResultFragment;
import com.example.restaurantrecognition.ui.zomatoapi.ZomatoAccess;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;

public class SearchResultFragment extends Fragment {

    private SearchResultViewModel searchResultViewModel;
    double lat = -37.7;
    double lon = 144.9;
    String resName = "Pronto Pizza";
    String resAddress = "Parkville, University of Melbourne, Grattan Street";

    ListView listView;
    RestaurantListAdapter listViewAdapter;

    ProgressDialog progressDialog;

    private FragmentInteractionListener mListener;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        searchResultViewModel =
                ViewModelProviders.of(this).get(SearchResultViewModel.class);
        View root = inflater.inflate(R.layout.fragment_search_result, container, false);

        listView = root.findViewById(R.id.simpleListView);
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setTitle("Loading Restaurant Data...");
        progressDialog.setMessage("Please Wait...");
        progressDialog.show();

        getRestaurantList();

        return root;
    }

    private void getRestaurantList() {

        ZomatoAsync zomatoAsync = new ZomatoAsync();
        zomatoAsync.execute();

        return;

    }

    private class ZomatoAsync extends AsyncTask<Void, Void, List<Restaurant>>{

        @Override
        protected List<Restaurant> doInBackground(Void... voids) {

            List<Restaurant> restaurantList;

            ZomatoAccess zomatoAccess = new ZomatoAccess();
            JSONAdapter jsonAdapter = new JSONAdapter();
            //GET LOCATION
            String res = zomatoAccess.findNearbyLocation(lat,lon,resAddress);
            int city_id = jsonAdapter.getLocationId(res);
            //GET RESTAURANT LIST
            String resList = zomatoAccess.findMatchingRestaurants(resName,city_id,lat,lon);
            restaurantList = jsonAdapter.getRestaurantList(resList);

            listViewAdapter = new RestaurantListAdapter(
                    getActivity(),R.layout.items_layout,restaurantList
            );

            return restaurantList;
        }

        @Override
        protected void onPostExecute(List<Restaurant> restaurants) {
            progressDialog.dismiss();
            listView.setAdapter(listViewAdapter);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {

                    FragmentManager fm = getActivity().getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fm.beginTransaction();
                    Bundle bundle = new Bundle();

                    Restaurant restaurantObject = restaurants.get(position);
                    bundle.putSerializable("Restaurant", restaurantObject);


                    RestaurantResultFragment fragment = new RestaurantResultFragment();

                    fragment.setArguments(bundle);
                    fragmentTransaction.replace(R.id.fragmentContent, fragment);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                }});
        }

    }

}
