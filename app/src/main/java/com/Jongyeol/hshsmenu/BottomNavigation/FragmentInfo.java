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

public class FragmentInfo extends Fragment {
    MainActivity activity;
    TextView menu;
    public FragmentInfo(MainActivity activity) {
        this.activity = activity;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup)inflater.inflate(R.layout.fragment_info, container, false);
        menu = rootView.findViewById(R.id.menu);
        reload();
        return rootView;
    }
    public void reload() {
        if(menu == null) return;
        menu.setText(data.menuText);
    }
}