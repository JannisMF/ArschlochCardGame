package server.controller;


import domain.IPlayer;
import domain.comm.Request;
import domain.io.CardGamePrinter;
import domain.io.CardGameReader;
import domain.model.card.Card;
import lombok.Setter;
import server.application.GameImpl;
import server.stub.ServerMessageCompiler;
import server.stub.ServerMessageParser;

import java.io.IOException;
import java.net.Socket;
import java.util.List;
import java.util.Map;

public class PlayerClientProxy implements IPlayer {

    private final Socket socket;
    private final CardGameReader reader;
    private final CardGamePrinter writer;
    private final ServerMessageParser parser;
    private final ServerMessageCompiler compiler;
    @Setter
    private Map<IPlayer, String> currentPlayers;

    public PlayerClientProxy(Socket socket, CardGameReader reader, CardGamePrinter writer, GameImpl game) {
        this.reader = reader;
        this.writer = writer;
        parser = new ServerMessageParser();
        compiler = new ServerMessageCompiler();
        this.socket = socket;
        currentPlayers = game.getPlayerComms();
    }

    @Override
    public String getUsername() {

        try {
            writer.println(parser.parseGetUsername());
            String response = reader.readLine();
            Request request = compiler.compile(socket.getInetAddress().toString(), response);
            if (request.getCode().equals("100")) {
                return request.getMessage();
            }
            throw new RuntimeException(reader.readLine());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void updateHand(List<Card> cards) {
        writer.println(parser.parsePlayerHand(cards));
    }

    @Override
    public void updatePlayfield(List<Card> cards) {
        writer.println(parser.parseGameBoard(cards));
        try {
            if (reader.readLine().equals("200")) {
                throw new IOException();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void demandRoundStart() {
        writer.println(parser.parseDemandStart());
    }

    @Override
    public void demandCards() {
        writer.println(parser.parseDemandCards());
    }

    @Override
    public boolean askForStart() {
        try {
            writer.println(parser.parseAskForStart());
            if (reader.readLine().equals("100")) {
                return true;
            } else {
                return false;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }


    @Override
    public void broadcastMessage(String message) {
        writer.println(parser.parseMessage(message));
        try {
            if (reader.readLine().equals("200")) {
                throw new IOException();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void finishedPlayer(IPlayer player, int place) {
        writer.println(parser.parseFinishedRound(currentPlayers.get(player), place));
        try {
            if (reader.readLine().equals("200")) {
                throw new IOException();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}