package com.example.lab1.ui.deposit;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class DepositViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public DepositViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is deposit fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}