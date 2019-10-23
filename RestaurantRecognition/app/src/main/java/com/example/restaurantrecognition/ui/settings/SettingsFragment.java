package com.example.restaurantrecognition.ui.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.restaurantrecognition.R;
import com.example.restaurantrecognition.ui.recentmatches.RecentMatchItem;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SettingsFragment extends Fragment {

    private SettingsViewModel settingsViewModel;

    @BindView(R.id.btnClearRecentMatches)
    Button btnClearRecentMatches;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        settingsViewModel =
                ViewModelProviders.of(this).get(SettingsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_settings, container, false);
        ButterKnife.bind(this, root);
        btnClearRecentMatches.setOnClickListener(view -> {
            SharedPreferences sharedPreferences = getContext().getSharedPreferences(RecentMatchItem.PREFERENCES_STORE_NAME, Context.MODE_PRIVATE);
            sharedPreferences.edit().clear().commit();
            Toast.makeText(getContext(), "Cleared all saved recent matches", Toast.LENGTH_SHORT).show();
        });
        return root;
    }
}