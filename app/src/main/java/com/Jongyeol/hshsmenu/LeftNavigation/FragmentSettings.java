package com.Jongyeol.hshsmenu.LeftNavigation;

import static com.Jongyeol.hshsmenu.MainActivity.data;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.Jongyeol.hshsmenu.MainActivity;
import com.Jongyeol.hshsmenu.R;


public class FragmentSettings extends Fragment {
    MainActivity activity;
    public FragmentSettings(MainActivity activity) {
        this.activity = activity;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup)inflater.inflate(R.layout.fragment_settings, container, false);
        int grade = data.preferences.getInt("grade", 1);
        int classroom = data.preferences.getInt("classroom", 1);
        EditText gradeText = rootView.findViewById(R.id.grade_edit);
        EditText classText = rootView.findViewById(R.id.classroom_edit);
        gradeText.setText(grade + "");
        classText.setText(classroom + "");
        gradeText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.toString().equals("")) return;
                int i = Integer.parseInt(s.toString());
                if(i > 3 || i < 1) {
                    gradeText.setText(1 + "");
                    i = 1;
                }
                SharedPreferences.Editor editor = data.preferences.edit();
                editor.putInt("grade", i);
                editor.apply();
            }
        });
        classText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.toString().equals("")) return;
                int i = Integer.parseInt(s.toString());
                if(i > 4 || i < 1) {
                    classText.setText(1 + "");
                    i = 1;
                }
                SharedPreferences.Editor editor = data.preferences.edit();
                editor.putInt("classroom", i);
                editor.apply();
            }
        });
        return rootView;
    }
}