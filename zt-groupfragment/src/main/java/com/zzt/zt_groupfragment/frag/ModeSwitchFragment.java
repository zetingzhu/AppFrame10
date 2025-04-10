package com.zzt.zt_groupfragment.frag;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import com.zzt.zt_groupfragment.R;

public class ModeSwitchFragment extends Fragment {

    private Button lightModeButton;
    private Button darkModeButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mode_switch, container, false);

        lightModeButton = view.findViewById(R.id.lightModeButton);
        darkModeButton = view.findViewById(R.id.darkModeButton);

        lightModeButton.setOnClickListener(v -> {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        });

        darkModeButton.setOnClickListener(v -> {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        });

        return view;
    }
}    