/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Horopter.Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author SantoshKumar
 */
public class FileServer {

    public static void main(String args[]) {
        try {
            int id = 1;
            ServerSocket serverSocket;

            if (args.length >= 2) {
                serverSocket = new ServerSocket(Integer.parseInt(args[1]));
            } else {
                serverSocket = new ServerSocket(5027); //FTP port 21 usage will hinder other apps
            }

            while (true) {
                Socket thisSocket = serverSocket.accept();
                System.out.println("Client with ID " + id + " connected from " + thisSocket.getInetAddress().getHostName() + "...");
                Thread server = new FileThread(thisSocket, id, "C:/exam");
                id++;
                server.start();
            }
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
    }
}
