package com.example.restaurantrecognition.ui.searchresult;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.restaurantrecognition.R;
import com.example.restaurantrecognition.ui.adapter.Restaurant;
import com.example.restaurantrecognition.ui.adapter.RestaurantListAdapter;
import com.example.restaurantrecognition.ui.recentmatches.RecentMatchesViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

public class SearchResultFragment extends Fragment {

    private SearchResultViewModel searchResultViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        /* To-DO
        *  1. Fetch array of results from the Zomato API
        *       Requires : ZomatoAccess
        *  2. Fetch information from each element in the array, including :
        *       - Name
        *       - Rating
        *       - Image
        *       - Descriptions?
        *  3. Create a ListViewAdapter to adapt with the information above
        *  4. View the Information on the ListView UI
        *  */


        /* DUMMY ARRAY */
        String[] menuItems = {"KFC", "Pronto Pizza", "Egg Sake"};
        List<Restaurant> restaurantList = new ArrayList<>();
        restaurantList.add(new Restaurant(1,"KFC", "142 Grattan St", "5"));
        restaurantList.add(new Restaurant(2, "Pronto Pizza", "Parkville, University of Melbourne", "3.5"));
        restaurantList.add(new Restaurant(3, "Egg Sake", "Parkville, Basement of \nUnion House UoM", "4.5"));

        searchResultViewModel =
                ViewModelProviders.of(this).get(SearchResultViewModel.class);
        View root = inflater.inflate(R.layout.fragment_search_result, container, false);


        ListView listView = root.findViewById(R.id.simpleListView);
        RestaurantListAdapter listViewAdapter = new RestaurantListAdapter(
               getActivity(),R.layout.items_layout,restaurantList
        );

        //ArrayAdapter<String> listViewAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1,menuItems);

        listView.setAdapter(listViewAdapter);

        return root;
    }

}
