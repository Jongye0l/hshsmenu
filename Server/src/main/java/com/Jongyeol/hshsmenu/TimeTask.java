package com.Jongyeol.hshsmenu;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class TimeTask {
    private final Map<String, Map<Integer, String>> tasks = new HashMap<>();
    private final Date date;
    private final DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
    public TimeTask(Date date) {
        this.date = date;
    }
    public void addTask(String date, int period, String subject) {
        Map<Integer, String> ta;
        if(tasks.containsKey(date)) ta = tasks.get(date);
        else ta = new HashMap<>();
        ta.put(period, subject);
        tasks.put(date, ta);
    }
    public void sendSocket(Socket socket) {
        try {
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            for(int i = 0; i < 5; i++) {
                Map<Integer, String> ta = tasks.get(dateFormat.format(date));
                if(ta == null) {
                    for(int i2 = 0; i2 < 7; i2++) out.writeUTF("");
                    continue;
                }
                for(int i2 = 1; i2 < 8; i2++) {
                    String st = ta.get(i2);
                    if(st == null) st = "";
                    out.writeUTF(st);
                }
                date.setDate(date.getDate() + 1);
            }
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
