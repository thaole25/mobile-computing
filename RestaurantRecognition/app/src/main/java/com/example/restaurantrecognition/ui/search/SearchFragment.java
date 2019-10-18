package com.example.restaurantrecognition.ui.search;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.example.restaurantrecognition.R;
import com.example.restaurantrecognition.ui.FragmentInteractionListener;
import com.example.restaurantrecognition.ui.recentmatches.RecentMatchesFragment;
import com.example.restaurantrecognition.ui.searchresult.SearchResultFragment;

public class SearchFragment extends Fragment {

    @BindView(R.id.btnSearchImage)
    Button buttonSearchImage;

    private FragmentInteractionListener mListener;
    private SearchViewModel searchViewModel;

    static final int REQUEST_IMAGE_CAPTURE = 1;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        mListener = (FragmentInteractionListener)getActivity();
        searchViewModel = ViewModelProviders.of(this).get(SearchViewModel.class);
        View searchView = inflater.inflate(R.layout.fragment_search, container, false);
        ButterKnife.bind(this, searchView);

        buttonSearchImage.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                mListener.changeFragment(1);
                /*try {
                    Fragment fragment = (Fragment) (SearchResultFragment.class).newInstance();

                    FragmentTransaction fragmentTransaction = getActivity()
                            .getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.fragmentContent, fragment);
                    fragmentTransaction.commit();
                } catch (IllegalAccessException e) {

                } catch (java.lang.InstantiationException e) {
                    e.printStackTrace();
                }*/


                /*Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (cameraIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE);
                }
                 */
            }
        });

        return searchView;
    }

}