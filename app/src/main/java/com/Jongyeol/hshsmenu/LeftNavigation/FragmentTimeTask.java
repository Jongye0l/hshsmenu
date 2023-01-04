package com.Jongyeol.hshsmenu.LeftNavigation;

import static com.Jongyeol.hshsmenu.MainActivity.data;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.LinearLayout;
import android.widget.TextView;

import com.Jongyeol.hshsmenu.MainActivity;
import com.Jongyeol.hshsmenu.R;
import com.google.android.gms.ads.AdView;
import com.ssomai.android.scalablelayout.ScalableLayout;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ProtocolException;
import java.net.Socket;

import io.teamif.patrick.comcigan.ComciganAPI;
import io.teamif.patrick.comcigan.ComciganRegexKt;
import io.teamif.patrick.comcigan.ComciganSchool;
import io.teamif.patrick.comcigan.SchoolClassroomData;
import io.teamif.patrick.comcigan.SchoolDayData;

public class FragmentTimeTask extends Fragment {
    MainActivity activity;
    LinearLayout table;
    ConstraintLayout loading;

    public FragmentTimeTask(MainActivity activity) {
        this.activity = activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup)inflater.inflate(R.layout.fragment_time_task, container, false);
        table = rootView.findViewById(R.id.task_table);
        loading = rootView.findViewById(R.id.loading);
        loading.setVisibility(View.VISIBLE);
        int grade = data.preferences.getInt("grade", 1);
        int classroom = data.preferences.getInt("classroom", 1);
        ((TextView) rootView.findViewById(R.id.menuText)).setText(grade + "학년 " + classroom + "반 시간표");
        new Thread("TimeTask-Listener") {
            @Override
            public void run() {
                try {
                    Socket socket = new Socket("Jongyeol.kro.kr", 1209);
                    DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                    DataInputStream in = new DataInputStream(socket.getInputStream());
                    out.writeInt(2);
                    out.writeByte(1);
                    out.writeByte(grade);
                    out.writeByte(classroom);
                    for(int day = 1; day < 6; day++) {
                        View v = table.getChildAt(day);
                        for(int period = 1; period < 8; period++) {
                            String sub = in.readUTF();
                            System.out.println(day + " " + period + " " + sub);
                            int finalPeriod = period;
                            activity.runOnUiThread(() -> {
                                try {
                                    ((TextView)((ScalableLayout)v).getChildAt(finalPeriod)).setText(sub);
                                } catch (IllegalArgumentException | IndexOutOfBoundsException ignored) {}
                            });
                        }
                    }
                    activity.runOnUiThread(() -> {
                        loading.setVisibility(View.INVISIBLE);
                        table.setVisibility(View.VISIBLE);
                    });
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
        ((AdView) rootView.findViewById(R.id.adView)).loadAd(data.adRequest);
        return rootView;
    }
}