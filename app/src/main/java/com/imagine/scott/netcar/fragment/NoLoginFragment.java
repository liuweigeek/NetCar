package com.imagine.scott.netcar.fragment;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.imagine.scott.netcar.R;

public class NoLoginFragment extends BaseFragment {

    private String mParam1;
    private String mParam2;

    public NoLoginFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_no_login, container, false);
    }

    public void onShowFragment() {

    }
}
