package com.example.restaurantrecognition.ui.searchresult;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.restaurantrecognition.R;
import com.example.restaurantrecognition.ui.adapter.Restaurant;
import com.example.restaurantrecognition.ui.adapter.RestaurantListAdapter;
import com.example.restaurantrecognition.ui.restaurantresult.OtherRestaurantResultFragment;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;

public class SearchResultFragment extends Fragment {

    private SearchResultViewModel searchResultViewModel;

    ListView listView;
    RestaurantListAdapter listViewAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        searchResultViewModel = ViewModelProviders.of(this).get(SearchResultViewModel.class);
        View root = inflater.inflate(R.layout.fragment_search_result, container, false);

        listView = root.findViewById(R.id.simpleListView);
        Bundle bundle = this.getArguments();
        int resCount = bundle.getInt("Count");
        List<Restaurant> restaurants = new ArrayList<>();

        for (int i = 0; i < resCount; i++) {
            restaurants.add((Restaurant) bundle.getSerializable("Restaurant" + i));
        }

        listViewAdapter = new RestaurantListAdapter(
                getActivity(), R.layout.items_layout, restaurants
        );
        listView.setAdapter(listViewAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                FragmentManager fm = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fm.beginTransaction();
                Bundle bundle = new Bundle();

                Restaurant restaurantObject = restaurants.get(position);
                bundle.putSerializable("Restaurant", restaurantObject);

                OtherRestaurantResultFragment fragment = new OtherRestaurantResultFragment();
                fragment.setArguments(bundle);
                fragmentTransaction.replace(R.id.fragmentContent, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });
        return root;
    }

}
