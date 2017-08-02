package com.imagine.scott.netcar.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.imagine.scott.netcar.R;

public class LoadFailedFragment extends BaseFragment {

    public static LoadFailedFragment newInstance(String param1, String param2) {
        LoadFailedFragment fragment = new LoadFailedFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_load_failed, container, false);
    }
    public void onShowFragment() {

    }
}
