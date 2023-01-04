package com.Jongyeol.hshsmenu;

import com.Jongyeol.MultiplyServer.ServerTemplete;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

public class Server implements ServerTemplete {
    @Override
    public void run(Socket socket) {
        try {
            DataInputStream in = new DataInputStream(socket.getInputStream());
            Screen.values()[in.readByte()].run(socket);
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getId() {
        return 2;
    }
}