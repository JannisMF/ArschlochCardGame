package client.controller;


import client.app.PlayerImpl;
import client.stub.ClientMessageCompiler;
import domain.IPlayer;
import domain.comm.PlayerCommandModel;

import domain.comm.Request;
import domain.io.CardGamePrinter;
import domain.io.CardGameReader;

import java.io.IOException;
import java.net.Socket;
import java.util.Map;

public class PlayerServerProxy implements Runnable {

    private final IPlayer player;
    private final CardGameReader reader;
    private final CardGamePrinter writer;
    private final Socket socket;
    private final ClientMessageCompiler compiler;
    private final Map<IPlayer, String> players;
    private boolean isRunning;

    public PlayerServerProxy(IPlayer player, Socket socket, CardGameReader reader, CardGamePrinter writer, Map<IPlayer, String> playerMap) {
        this.player = player;
        this.reader = reader;
        this.writer = writer;
        this.isRunning = true;
        this.socket = socket;
        compiler = new ClientMessageCompiler();
        players = playerMap;
    }

    @Override
    public void run() {
        while (isRunning) {
            String response = readLine();
            Request request = compiler.compile(socket.getInetAddress().toString(), response);
            switch (request.getCode()) {
                case "000" -> getName();
                case "001" -> broadcastMessage(request.getMessage());
                case "002" -> askForStart();
                case "003" -> demandRoundStart();
                case "004" -> demandCards();
                case "005" -> playerFinished(request.getMessage());
                case "102" -> updateHand(request.getMessage());
                case "103" -> updatePlayfield(request.getMessage());
                case "099" -> technicalDisconnect();
                default -> error();
            }
        }
    }

    private void playerFinished(String message) {
        PlayerCommandModel model = compiler.splitOfPlayer(message);
        player.finishedPlayer(findKeyByValue(model.getPlayer()), Integer.parseInt(model.getCommand()));
    }

    private void demandCards() {
        player.demandCards();
    }

    private void updatePlayfield(String message) {
        player.updatePlayfield(compiler.compileCardList(message));
    }

    private void updateHand(String cards) {
        player.updateHand(compiler.compileCardList(cards));
    }

    private void demandRoundStart() {
        player.demandRoundStart();
    }

    private void askForStart() {
        if (player.askForStart()) {
            writer.println("100");
        } else {
            writer.println("200");
        }
    }

    private void getName() {
        try {
            String username = player.getUsername();
            writer.println("100");
            //writer.println(username);
        } catch (Exception e) {
            writer.println("200");
            writer.println(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    private void broadcastMessage(String message) {

        player.broadcastMessage(message);
    }

    private void technicalDisconnect() {
        writer.println("Disconnecting now ;(");
        isRunning = false;
    }

    private void error() {
        System.err.println("Hier ist etwas schiefgelaufen!");
    }

    private String readLine() {
        try {
            return reader.readLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private IPlayer findKeyByValue(String player) {
        IPlayer key = null;
        for (Map.Entry<IPlayer, String> entry : players.entrySet()) {
            if (entry.getValue().equals(player)) {
                key = entry.getKey();
            }
        }
        if (key == null) {
            return new PlayerImpl(player, null);
        }
        return key;
    }
}
