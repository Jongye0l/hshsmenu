package com.Jongyeol.hshsmenu;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentTransaction;

import com.Jongyeol.hshsmenu.LeftNavigation.FragmentHome;
import com.Jongyeol.hshsmenu.LeftNavigation.FragmentSettings;
import com.Jongyeol.hshsmenu.LeftNavigation.FragmentTimeTask;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.material.navigation.NavigationView;

import io.teamif.patrick.comcigan.ComciganAPI;

public class MainActivity extends AppCompatActivity {
    long backKeyPressedTime = 0;
    public static MainData data;
    DrawerLayout drawer;
    FragmentHome fragmentHome;
    FragmentTimeTask fragmentTimeTask;
    FragmentSettings fragmentSettings;
    Toast toast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new ItemSelectedListener());
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        if(data == null) data = new MainData(getSharedPreferences("Data", MODE_PRIVATE));
        MobileAds.initialize(this, initializationStatus -> System.out.println("광고가 로딩되었습니다."));
        AdView adView = new AdView(this);
        adView.setAdSize(AdSize.BANNER);
        adView.setAdUnitId(getString(R.string.banner_ad_unit_id));
        fragmentHome = new FragmentHome(this);
        fragmentTimeTask = new FragmentTimeTask(this);
        fragmentSettings = new FragmentSettings(this);
        if(savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragmentHome).commit();
            navigationView.setCheckedItem(R.id.nav_home);
        }
        toast = Toast.makeText(this,"'뒤로가기'버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT);
    }
    @Override
    public void onBackPressed() {
        if(drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
                backKeyPressedTime = System.currentTimeMillis();
                toast.show();
                return;
            }
            if (System.currentTimeMillis() <= backKeyPressedTime + 2000) {
                toast.cancel();
                finish();
            }
        }
    }

    class ItemSelectedListener implements NavigationView.OnNavigationItemSelectedListener {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            switch (menuItem.getItemId()) {
                case R.id.nav_home:
                    transaction.replace(R.id.fragment_container, fragmentHome).commitAllowingStateLoss();
                    break;
                case R.id.nav_time_task:
                    transaction.replace(R.id.fragment_container, fragmentTimeTask).commitAllowingStateLoss();
                    break;
                case R.id.nav_settings:
                    transaction.replace(R.id.fragment_container, fragmentSettings).commitAllowingStateLoss();
                    break;
            }
            drawer.closeDrawer(GravityCompat.START);
            return true;
        }
    }
}
