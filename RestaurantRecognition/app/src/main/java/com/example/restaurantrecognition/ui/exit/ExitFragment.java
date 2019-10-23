package com.example.restaurantrecognition.ui.exit;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.restaurantrecognition.R;
import com.example.restaurantrecognition.SideMenuActivity;

public class ExitFragment extends Fragment {
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        Intent exitIntent = new Intent(getActivity(), SideMenuActivity.class);
        exitIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        exitIntent.putExtra("EXIT", true);
        startActivity(exitIntent);
        getActivity().finish();
        return null;
    }
}