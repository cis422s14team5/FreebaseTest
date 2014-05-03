package com.thedragons;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;


public class ServerThread extends Thread {
    private Socket socket = null;

    public ServerThread(Socket socket) {
        super("ServerThread");
        this.socket = socket;
    }

    public void run() {
            try {
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                ServerProcessor serverProcessor = new ServerProcessor();
                String output = serverProcessor.processInput("");
                out.println(output);

                String input;
                while ((input = in.readLine()) != null) {
                    output = serverProcessor.processInput(input);
                    out.println(output);
                    if (output.equals("Bye")) {
                        break;
                    }
                }
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
    }
}
