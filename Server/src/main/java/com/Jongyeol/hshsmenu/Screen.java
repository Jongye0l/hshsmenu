package com.Jongyeol.hshsmenu;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public enum Screen {
    CookMenu(socket -> {
        try {
            DataInputStream in = new DataInputStream(socket.getInputStream());
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            Document doc = Jsoup.connect("https://school.cbe.go.kr/hshs-h/M01030803/list?ymd=" + in.readUTF()).get();
            Elements contents = doc.select("ul.tch-lnc-list");
            StringBuilder menuSt = new StringBuilder("");
            Elements lunch = null, dinner = null;
            for(Element el : contents.select("li.tch-lnc-wrap")) {
                if (el.select("dt").text().equals("중식")) lunch = el.select("dd.tch-lnc").select("ul");
                if (el.select("dt").text().equals("석식")) dinner = el.select("dd.tch-lnc").select("ul");
                if(!menuSt.toString().equals("")) menuSt.append("\n");
                menuSt.append(el.select("dt").text()).append("\n");
                for(Element el2 : el.select("dd.tch-lnc").select("ul").select("li")) menuSt.append(el2.text()).append("\n");
            }
            if(lunch == null) {
                out.writeUTF("식단이 없습니다.");
            } else {
                StringBuilder lunchTextBuilder = new StringBuilder();
                for(Element el : lunch.select("li")) {
                    lunchTextBuilder.append(el.text()).append("\n");
                }
                String lunchText = lunchTextBuilder.toString().replace("*", "").replace(".", "");
                for (int i = 0; i < 10; i++) {
                    lunchText = lunchText.replace("" + i, "");
                }
                lunchText = lunchText.replace("()", "");
                out.writeUTF(lunchText);
            }
            if(dinner == null) {
                out.writeUTF("식단이 없습니다.");
            } else {
                StringBuilder dinnerTextBuilder = new StringBuilder();
                for(Element el : dinner.select("li")) {
                    dinnerTextBuilder.append(el.text()).append("\n");
                }
                String dinnerText = dinnerTextBuilder.toString().replace("*", "").replace(".", "");
                for (int i = 0; i < 10; i++) {
                    dinnerText = dinnerText.replace("" + i, "");
                }
                dinnerText = dinnerText.replace("()", "");
                out.writeUTF(dinnerText);
            }
            Elements el = contents.select("li.tch-made");
            menuSt.append("\n").append(el.select("dt").text()).append("\n").append(el.select("dd").text()).append("\n");
            el = contents.select("li.tch-bigo");
            menuSt.append("\n").append(el.select("dt").text());
            for(Element el2 : el.select("dd")) menuSt.append(el2).append("\n");
            String menu_st = menuSt.toString().replaceAll("<dd>", "");
            menu_st = menu_st.replaceAll("<br>", "");
            menu_st = menu_st.replaceAll("</dd>", "");
            menu_st = menu_st.replaceAll("&lt;br&gt;", "\n");
            if (doc.select("div.tch-no-data").text().equals("식단이 없습니다.")) menu_st = "식단이 없습니다.";
            out.writeUTF(menu_st);
            in.close();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }),
    TimeTask(socket -> {
        try {
            DataInputStream in = new DataInputStream(socket.getInputStream());
            Calendar cal = Calendar.getInstance();
            int week = cal.get(Calendar.DAY_OF_WEEK);
            if(week == 7) week = 0;
            week -= 2;
            Date date = new Date();
            date.setDate(date.getDate() - week);
            Date date2 = (Date) date.clone();
            date2.setDate(date2.getDate() + 4);
            DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
            byte grade = in.readByte();
            byte classroom = in.readByte();
            Document doc = Jsoup.connect("https://open.neis.go.kr/hub/hisTimetable?KEY=d275d1fec7254cae85c367ebe94d8809&Type=json&ATPT_OFCDC_SC_CODE=M10&SD_SCHUL_CODE=8000102&TI_FROM_YMD=" + dateFormat.format(date) + "&TI_TO_YMD=" + dateFormat.format(date2) + "&GRADE=" + grade + "&CLASS_NM=0" + classroom).get();
            String text = doc.body().text();
            com.Jongyeol.hshsmenu.TimeTask timeTask = new TimeTask(date);
            try {JsonArray list = JsonParser.parseString(text).getAsJsonObject().getAsJsonArray("hisTimetable").get(1).getAsJsonObject().getAsJsonArray("row");
                for(JsonElement element : list.asList()) {
                    int period = element.getAsJsonObject().get("PERIO").getAsInt();
                    String subject = element.getAsJsonObject().get("ITRT_CNTNT").getAsString();
                    String date3 = element.getAsJsonObject().get("ALL_TI_YMD").getAsString();
                    timeTask.addTask(date3, period, subject);
                }
                timeTask.sendSocket(socket);
                date.setDate(date.getDate() + 1);
            } catch (NullPointerException e) {
                timeTask.sendSocket(socket);
                System.out.println("[hshsmenu] An error log has been generated.");
                File file = new File("hshsmenu/ErrorLog");
                if(!file.exists()) file.mkdir();
                DateFormat dateFormat2 = new SimpleDateFormat("yyyyMMddhhmmss");
                file = new File("hshsmenu/ErrorLog/" + dateFormat2.format(new Date()) + ".log");
                file.createNewFile();
                FileOutputStream out = new FileOutputStream(file);
                out.write((e.getMessage() + "\n").getBytes());
                for(StackTraceElement el : e.getStackTrace()) {
                    out.write((el.toString() + "\n").getBytes());
                }
                out.write(("https://open.neis.go.kr/hub/hisTimetable?KEY=d275d1fec7254cae85c367ebe94d8809&Type=json&ATPT_OFCDC_SC_CODE=M10&SD_SCHUL_CODE=8000102&TI_FROM_YMD=" + dateFormat.format(date) + "&TI_TO_YMD=" + dateFormat.format(date2) + "&GRADE=" + grade + "&CLASS_NM=0" + classroom + "\n").getBytes());
                out.write(text.getBytes());
                out.close();
            }
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }),
    ;
    private final Runner runner;
    Screen(Runner runner) {
        this.runner = runner;
    }
    public void run(Socket socket) {
        runner.run(socket);
    }
    private interface Runner {
        void run(Socket socket);
    }
}
