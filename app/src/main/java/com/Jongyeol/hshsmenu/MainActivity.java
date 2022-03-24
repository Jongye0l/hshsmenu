package com.Jongyeol.hshsmenu;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    private AdView mAdView;
    String nums;
    String nums2;
    TextView textView1;
    TextView textView2;
    TextView version;
    ImageView Notificationi;
    TextView Notificationt;
    Button LateUpdate;
    Button NowUpdate;
    Date date = new Date();
    String downloadURL;
    Boolean Lunchisset;
    Boolean Dinnerisset;
    Boolean Info;
    private BackPressCloseHandler backPressCloseHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        backPressCloseHandler = new BackPressCloseHandler(this);
        version = (TextView) findViewById(R.id.version);
        Notificationi = (ImageView) findViewById(R.id.Notificationi);
        Notificationt = (TextView) findViewById(R.id.Notificationt);
        NowUpdate = (Button) findViewById(R.id.NowUpdate);
        LateUpdate = (Button) findViewById(R.id.LateUpdate);
        Updatenotit();
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        textView1 = (TextView) findViewById(R.id.lunch);
        textView2 = (TextView) findViewById(R.id.dinner);
        Reload();
    }
    @Override
    public void onBackPressed() {
        if(Info.equals(true)) {
            BackMainmenu();
        } else {
            backPressCloseHandler.onBackPressed();
        }
    }
    public void Reload() {
        Info = false;
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        String todate = format.format(date);
        TextView today = (TextView) findViewById(R.id.today);
        SimpleDateFormat format2 = new SimpleDateFormat("yyyy년 MM월 dd일");
        String todate2 = format2.format(date);
        today.setText(todate2 + " 급식");
        Lunchisset = false;
        Dinnerisset = false;
        final Bundle bundle1 = new Bundle();
        final Bundle bundle2 = new Bundle();
        new Thread(){
            @Override
            public void run() {
                Document doc = null;
                try {
                    doc = Jsoup.connect("https://school.cbe.go.kr/hshs-h/M01030803/list?ymd=" + todate).get();
                    for(Element contents : doc.select("li.tch-lnc-wrap")) {
                        nums = "";
                        Elements contents1 = contents.select("dd.tch-lnc");
                        for(Element el : contents1.select("li")){
                            nums += el.text() + "\n";
                        }
                        nums = nums.replace("*", "");
                        nums = nums.replace(".", "");
                        for(int i=0; i<10; i++) {
                            nums = nums.replace("" + i, "");
                        }
                        if(contents.select("dt").text().equals("중식")) {
                            bundle1.putString("numbers", nums);
                            Message msg = handler1.obtainMessage();
                            msg.setData(bundle1);
                            handler1.sendMessage(msg);
                            Lunchisset = true;
                        }
                        if (contents.select("dt").text().equals("석식")) {
                            bundle2.putString("numbers2", nums);
                            Message msg = handler2.obtainMessage();
                            msg.setData(bundle2);
                            handler2.sendMessage(msg);
                            Dinnerisset = true;
                        }
                    }
                    nums2 = "";
                    if(doc.select("div.tch-no-data").text().equals("식단이 없습니다.")) {
                        textView1.setText("식단이 없습니다.");
                        textView2.setText("식단이 없습니다.");
                        nums2 = "식단이 없습니다.";
                    }
                    if(Lunchisset.equals(false)) { textView1.setText("식단이 없습니다."); }
                    if(Dinnerisset.equals(false)) { textView2.setText("식단이 없습니다."); }
                    doc = Jsoup.connect("https://school.cbe.go.kr/hshs-h/M01030803/list?ymd=" + todate).get();
                    for(Element contents : doc.select("ul.tch-lnc-list")) {
                        for(Element el : contents.select("li.tch-lnc-wrap")){
                            nums2 += "\n" + el.select("dt").text() + "\n";
                            Elements contents1 = el.select("dd.tch-lnc");
                            for(Element el2 : contents1.select("li")){
                                nums2 += el2.text() + "\n";
                            }
                        }
                        Elements el = contents.select("li.tch-made");
                        nums2 += "\n" + el.select("dt").text() + "\n" + el.select("dd").text() + "\n";
                        el = contents.select("li.tch-bigo");
                        nums2 += "\n" + el.select("dt").text();
                        Elements el2 = el.select("dd");
                        for(Element el3 : el2){
                            nums2 += el3 + "\n";
                        }
                        nums2 = nums2.replaceAll("<dd>", "");
                        nums2 = nums2.replaceAll("<br>", "");
                        nums2 = nums2.replaceAll("</dd>", "");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    textView1.setText("인터넷이 연결되어있지 않습니다.");
                    textView2.setText("인터넷이 연결되어있지 않습니다.");
                }
            }
        }.start();
    }
    Handler handler1 = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            textView1.setText(bundle.getString("numbers"));
        }
    };
    Handler handler2 = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            textView2.setText(bundle.getString("numbers2"));
        }
    };
    Handler handler3 = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            version.setText(bundle.getString("numbers3"));
            version.setVisibility(View.VISIBLE);
            Notificationi.setVisibility(View.VISIBLE);
            Notificationt.setVisibility(View.VISIBLE);
            NowUpdate.setVisibility(View.VISIBLE);
            LateUpdate.setVisibility(View.VISIBLE);
        }
    };
    public void NextButtonClick(View view) {
        date.setDate(date.getDate() + 1);
        textView1.setText("식단을 받아오는중...");
        textView2.setText("식단을 받아오는중...");
        Reload();
    }
    public void PreviousButtonClick(View view) {
        date.setDate(date.getDate() - 1);
        textView1.setText("식단을 받아오는중...");
        textView2.setText("식단을 받아오는중...");
        Reload();
    }
    public void NowUpdate(View view) {
        Intent update = new Intent(Intent.ACTION_VIEW, Uri.parse(downloadURL));
        startActivity(update);
        Updatenotif();
    }
    public void LateUpdate(View view) { Updatenotif(); }
    public void Updatenotif() {
        version.setVisibility(View.INVISIBLE);
        Notificationi.setVisibility(View.INVISIBLE);
        Notificationt.setVisibility(View.INVISIBLE);
        NowUpdate.setVisibility(View.INVISIBLE);
        LateUpdate.setVisibility(View.INVISIBLE);
    }
    public void Updatenotit() {
        final Bundle bundle3 = new Bundle();
        new Thread() {
            @Override
            public void run() {
                Document doc = null;
                try {
                    doc = Jsoup.connect("https://jongye0l.github.io/hshsmenu/version/version.html").get();
                    if (!doc.text().equals("v1.2-pre6")) {
                        String text = "현재버전 : v1.2-pre6   최신버전 : " + doc.text();
                        downloadURL = Jsoup.connect("https://jongye0l.github.io/hshsmenu/version/link.html").get().text();
                        bundle3.putString("numbers3", text);
                        Message msg = handler3.obtainMessage();
                        msg.setData(bundle3);
                        handler3.sendMessage(msg);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }
    public void infoButtonClick(View view) {
        Info = true;
        setContentView(R.layout.info);
        TextView menu = (TextView) findViewById(R.id.menu);
        TextView today = (TextView) findViewById(R.id.today);
        menu.setText(nums2);
        SimpleDateFormat format2 = new SimpleDateFormat("yyyy년 MM월 dd일");
        String todate2 = format2.format(date);
        today.setText(todate2 + " 급식");
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }
    public void backButtonClick(View view) {
        BackMainmenu();
    }
    public void BackMainmenu() {
        setContentView(R.layout.activity_main);
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        version = (TextView) findViewById(R.id.version);
        Notificationi = (ImageView) findViewById(R.id.Notificationi);
        Notificationt = (TextView) findViewById(R.id.Notificationt);
        NowUpdate = (Button) findViewById(R.id.NowUpdate);
        LateUpdate = (Button) findViewById(R.id.LateUpdate);
        Updatenotit();
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        textView1 = (TextView) findViewById(R.id.lunch);
        textView2 = (TextView) findViewById(R.id.dinner);
        Reload();

    }
}
