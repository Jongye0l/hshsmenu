package com.Jongyeol.hsmsmenu;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    String nums;
    TextView textView1;
    TextView textView2;
    Date date = new Date();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView1 = (TextView) findViewById(R.id.lunch);
        textView2 = (TextView) findViewById(R.id.dinner);
        Reload();
    }
    public void Reload() {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        String todate = format.format(date);
        TextView today = (TextView) findViewById(R.id.today);
        SimpleDateFormat format2 = new SimpleDateFormat("yyyy년 MM월 dd일");
        String todate2 = format2.format(date);
        today.setText(todate2 + " 급식");
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
                        }
                        if (contents.select("dt").text().equals("석식")) {
                            bundle2.putString("numbers2", nums);
                            Message msg = handler2.obtainMessage();
                            msg.setData(bundle2);
                            handler2.sendMessage(msg);
                        }
                    }
                    if(doc.select("div.tch-no-data").text().equals("식단이 없습니다.")) {
                        textView1.setText("식단이 없습니다.");
                        textView2.setText("식단이 없습니다.");
                    }
                    if (textView2.getText().equals("식단을 받아오는중...")) {
                        if (textView1.getText().equals("식단을 받아오는중...")) {
                            textView1.setText("식단을 받아올 수 없습니다.");
                            textView2.setText("식단을 받아올 수 없습니다.");
                        } else {
                            textView2.setText("식단이 없습니다.");
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
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
}
