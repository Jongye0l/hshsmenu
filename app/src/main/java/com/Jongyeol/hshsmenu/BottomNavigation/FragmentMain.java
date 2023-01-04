package com.Jongyeol.hshsmenu.BottomNavigation;

import static com.Jongyeol.hshsmenu.MainActivity.data;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.Jongyeol.hshsmenu.MainActivity;
import com.Jongyeol.hshsmenu.R;

public class FragmentMain extends Fragment {
    MainActivity activity;
    TextView lunchTextView;
    TextView dinnerTextView;
    TextView today;

    public FragmentMain(MainActivity activity) {
        this.activity = activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup)inflater.inflate(R.layout.fragment_main, container, false);
        lunchTextView = rootView.findViewById(R.id.lunch);
        dinnerTextView = rootView.findViewById(R.id.dinner);
        reload();
        return rootView;
    }
    public void reload() {
        if(lunchTextView == null || dinnerTextView == null) return;
        lunchTextView.setText(data.lunchText);
        dinnerTextView.setText(data.dinnerText);
    }
}