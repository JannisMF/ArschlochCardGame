package client.controller;


import client.app.PlayerImpl;
import domain.io.CardGamePrinter;
import domain.io.CardGameReader;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ClientRuntime {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        try {
            Socket socket = new Socket("localhost", 42068);
            CardGameReader reader = new CardGameReader(new InputStreamReader(socket.getInputStream()));
            CardGamePrinter writer = new CardGamePrinter(new PrintWriter(socket.getOutputStream()));
            GameClientProxy gameClientProxy = new GameClientProxy(reader, writer);
            System.out.println("Willkommen bei dem Spiel Arschloch! Du kannst das Spiel jederzeit durch 'Ende' beenden.");
            System.out.println("Gib deinen Spielernamen ein: ");
            String name = sc.nextLine();
            PlayerImpl player = new PlayerImpl(name, gameClientProxy);
            gameClientProxy.joinRoom(player);

            while (true) {
                if (sc.nextLine().equals("Ende")) {
                    gameClientProxy.leave(player);
                    break;
                }
            }

            gameClientProxy.technicalDisconnect();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        sc.close();
    }

}
