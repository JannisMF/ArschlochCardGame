package server.controller;

import domain.IPlayer;
import domain.comm.PlayerCommandModel;
import domain.comm.Request;
import domain.exceptions.ArschlochGameException;
import domain.io.CardGamePrinter;
import domain.io.CardGameReader;
import lombok.extern.slf4j.Slf4j;
import server.application.GameImpl;
import server.stub.ServerMessageCompiler;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class GameServerProxy implements Runnable {

    private final CardGameReader reader;
    private final CardGamePrinter writer;
    private final GameImpl game;
    private final Map<String, IPlayer> alreadyDeserializedChatters;
    private final Socket socket;
    private final ServerMessageCompiler compiler;
    private boolean isRunning = true;


    public GameServerProxy(Socket socket, GameImpl game) {
        try {
            this.socket = socket;
            reader = new CardGameReader(new InputStreamReader(socket.getInputStream()));
            writer = new CardGamePrinter(new PrintWriter(socket.getOutputStream()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        this.game = game;
        alreadyDeserializedChatters = new HashMap<>();
        compiler = new ServerMessageCompiler();
    }

    @Override
    public void run() {
        while (isRunning) {
            String response = readLine();
            Request request = compiler.compile(socket.getInetAddress().toString(), response);
            switch (request.getCode()) {
                case "002" -> join(request.getMessage());
                case "100" -> playCard(request.getMessage());
                case "101" -> playCard(request.getMessage() + "§skip");
                case "003" -> leave(request.getMessage());
                case "099" -> technicalDisconnect();
                default -> error();
            }
        }
    }

    private void playCard(String message) {
        PlayerCommandModel model = compiler.splitOfPlayer(message);
        try {
            if (model.getCommand().equals("skip")) {
                game.playCards(getPlayerFromResponse(model.getPlayer()), new ArrayList<>());
            } else {
                game.playCards(getPlayerFromResponse(model.getPlayer()), compiler.compileCardList(model.getCommand()));
            }
            writer.println("100");
        } catch (ArschlochGameException e) {
            writer.println("200");
            e.printStackTrace();
        }
    }


    private String readLine() {
        try {
            return reader.readLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void join(String message) {
        IPlayer chatterFromResponse = getPlayerFromResponse(message);
        try {
            game.joinRoom(chatterFromResponse);
            writer.println("100");
        } catch (Exception e) {
            writer.println("200");
        }
    }

    private IPlayer getPlayerFromResponse(String player) {

        String identifierResponse = player;

        if (alreadyDeserializedChatters.containsKey(identifierResponse)) {
            return alreadyDeserializedChatters.get(identifierResponse);
        }
        try {
            int port = Integer.parseInt(readLine());
            Socket socket = new Socket(this.socket.getInetAddress(), port);
            CardGameReader cardGameReader = new CardGameReader(new InputStreamReader(new BufferedInputStream(socket.getInputStream())));
            CardGamePrinter cardGamePrinter = new CardGamePrinter(new PrintWriter(socket.getOutputStream()));
            PlayerClientProxy chatterClientProxy = new PlayerClientProxy(socket, cardGameReader, cardGamePrinter, game);
            alreadyDeserializedChatters.put(identifierResponse, chatterClientProxy);
            game.getPlayerComms().put(chatterClientProxy, identifierResponse);
            String usernameResponse = readLine();
            return alreadyDeserializedChatters.get(identifierResponse);
        } catch (NumberFormatException | IOException e) {
            throw new RuntimeException(e);
        }
    }


    private void leave(String message) {

        IPlayer chatter = getPlayerFromResponse(message);
        try {
            game.leave(chatter);
            writer.println("100");
        } catch (Exception e) {
            writer.println("200");
        }
    }

    private void technicalDisconnect() {
        writer.println("Closing connection now");
        isRunning = false;
    }

    private void error() {
        writer.println("Could not interpret the sent message ;(");
    }
}