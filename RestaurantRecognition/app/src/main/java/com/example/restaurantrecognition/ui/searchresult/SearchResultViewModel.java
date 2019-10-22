package com.example.restaurantrecognition.ui.searchresult;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SearchResultViewModel extends ViewModel {
    private MutableLiveData<String> mText;

    public SearchResultViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is Search Result fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}
