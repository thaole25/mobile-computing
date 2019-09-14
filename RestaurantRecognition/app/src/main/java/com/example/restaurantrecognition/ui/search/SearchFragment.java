package com.example.restaurantrecognition.ui.search;

import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.restaurantrecognition.R;

public class SearchFragment extends Fragment {

    private SearchViewModel searchViewModel;
    public Button button;

    static final int REQUEST_IMAGE_CAPTURE = 0;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        searchViewModel =
                ViewModelProviders.of(this).get(SearchViewModel.class);
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        button = view.findViewById(R.id.btnSearchImage);

        button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (cameraIntent.resolveActivity(getActivity().getPackageManager()) != null){
                    startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE);
                }
            }
        });

        return view;
    }

}