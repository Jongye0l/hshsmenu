package com.Jongyeol.hshsmenu.LeftNavigation;

import static com.Jongyeol.hshsmenu.MainActivity.data;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.Jongyeol.hshsmenu.BottomNavigation.FragmentInfo;
import com.Jongyeol.hshsmenu.BottomNavigation.FragmentMain;
import com.Jongyeol.hshsmenu.BottomNavigation.FragmentNull;
import com.Jongyeol.hshsmenu.MainActivity;
import com.Jongyeol.hshsmenu.R;
import com.google.android.gms.ads.AdView;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.text.SimpleDateFormat;

public class FragmentHome extends Fragment {
    MainActivity activity;
    FragmentInfo fragmentInfo;
    FragmentMain fragmentMain;
    FragmentNull fragmentNull;
    SimpleDateFormat dateFormat, dateFormat2, dateFormat3;
    TextView today;
    Fragment nowFrag;
    boolean notFirst;

    public FragmentHome(MainActivity activity) {
        this.activity = activity;
        fragmentMain = new FragmentMain(activity);
        fragmentInfo = new FragmentInfo(activity);
        nowFrag = fragmentMain;
        fragmentNull = new FragmentNull();
        dateFormat = new SimpleDateFormat("yyyyMMdd");
        dateFormat2 = new SimpleDateFormat("yyyy년 MM월 dd일");
        dateFormat3 = new SimpleDateFormat("E");
        notFirst = false;
        reload();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup)inflater.inflate(R.layout.fragment_home, container, false);
        ((BottomNavigationView)rootView.findViewById(R.id.navigation_bar)).setOnNavigationItemSelectedListener(new ItemSelectedListener());
        rootView.findViewById(R.id.next).setOnClickListener(view -> {
            data.date.setDate(data.date.getDate() + 1);
            reload();
        });
        rootView.findViewById(R.id.Previous).setOnClickListener(view -> {
            data.date.setDate(data.date.getDate() - 1);
            reload();
        });
        ((AdView) rootView.findViewById(R.id.adView)).loadAd(data.adRequest);
        today = rootView.findViewById(R.id.today);
        load(false);
        if(notFirst) {
            activity.getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, fragmentNull).commitAllowingStateLoss();
            new Thread() {
                public void run() {
                    try {
                        sleep(0);
                        activity.runOnUiThread(() -> activity.getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, nowFrag).commitAllowingStateLoss());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }.start();
        } else {
            activity.getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, nowFrag).commitAllowingStateLoss();
            notFirst = true;
        }
        return rootView;
    }

    public void reload() {
        String todateweek = "";
        switch (dateFormat3.format(data.date)) {
            case "Mon":
                todateweek = " (월)";
                break;
            case "Tue":
                todateweek = " (화)";
                break;
            case "Wed":
                todateweek = " (수)";
                break;
            case "Thu":
                todateweek = " (목)";
                break;
            case "Fri":
                todateweek = " (금)";
                break;
            case "Sat":
                todateweek = " (토)";
                break;
            case "Sun":
                todateweek = " (일)";
                break;
        }
        if (todateweek.equals("")) {
            todateweek = " (" + dateFormat3.format(data.date) + ")";
        }
        data.today = dateFormat2.format(data.date) + todateweek + " 급식";
        data.lunchText = "식단을 받아오는중...";
        data.dinnerText = "식단을 받아오는중...";
        data.menuText = "식단을 받아오는중...";
        load(false);
        new Thread() {
            @Override
            public void run() {
                try {
                    Socket socket = new Socket("Jongyeol.kro.kr", 1209);
                    DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                    DataInputStream in = new DataInputStream(socket.getInputStream());
                    out.writeInt(2);
                    out.writeByte(0);
                    out.writeUTF(dateFormat.format(data.date));
                    data.lunchText = in.readUTF();
                    data.dinnerText = in.readUTF();
                    data.menuText = in.readUTF();
                } catch (IOException e) {
                    data.lunchText = "인터넷이 연결되어있지 않습니다.";
                    data.dinnerText = "인터넷이 연결되어있지 않습니다.";
                    data.menuText = "인터넷이 연결되어있지 않습니다.";
                }
                load(true);
            }
        }.start();
    }
    public void load(boolean mainThread) {
        if(mainThread) {
            activity.runOnUiThread(() -> load(false));
        } else {
            if(today != null) today.setText(data.today);
            fragmentMain.reload();
            fragmentInfo.reload();
        }
    }

    class ItemSelectedListener implements BottomNavigationView.OnNavigationItemSelectedListener {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            FragmentTransaction transaction = activity.getSupportFragmentManager().beginTransaction();
            switch (menuItem.getItemId()) {
                case R.id.menu_main:
                    transaction.replace(R.id.frame_layout, fragmentMain).commitAllowingStateLoss();
                    nowFrag = fragmentMain;
                    break;
                case R.id.menu_info:
                    transaction.replace(R.id.frame_layout, fragmentInfo).commitAllowingStateLoss();
                    nowFrag = fragmentInfo;
                    break;
            }
            return true;
        }
    }

}