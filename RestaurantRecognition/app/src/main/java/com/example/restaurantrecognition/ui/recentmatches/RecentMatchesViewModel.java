package com.example.restaurantrecognition.ui.recentmatches;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class RecentMatchesViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public RecentMatchesViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is Recent Matches fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}