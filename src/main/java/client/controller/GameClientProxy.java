package client.controller;


import client.stub.ClientMessageParser;
import domain.model.Fred;
import domain.IGame;
import domain.IPlayer;
import domain.model.card.Card;
import domain.exceptions.custom.CardNotInHandException;
import domain.io.CardGamePrinter;
import domain.io.CardGameReader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class GameClientProxy implements IGame {

    private final CardGameReader reader;
    private final CardGamePrinter writer;

    private final ClientMessageParser parser;

    private Map<IPlayer, String> alreadyHandledChatters;

    public GameClientProxy(CardGameReader reader, CardGamePrinter writer) {
        this.reader = reader;
        this.writer = writer;
        this.alreadyHandledChatters = new HashMap<>();
        this.parser = new ClientMessageParser();
    }

    @Override
    public void joinRoom(IPlayer player) {
        sendChatter(player);
        readResponse();
    }





    @Override
    public void playCards(IPlayer player, List<Card> cards) throws CardNotInHandException {
        writer.println(parser.parsePlayCards(alreadyHandledChatters.get(player), cards));
        try {
            String status = reader.readLine();
            if (status.equals("200")) {
                throw new CardNotInHandException();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void leave(IPlayer player) {
        writer.println(parser.parseLeave(alreadyHandledChatters.get(player)));
        readResponse();

    }

    public void technicalDisconnect() {
        writer.println("099");
        readProtocol();
    }

    private void sendChatter(IPlayer player) {

        if (alreadyHandledChatters.containsKey(player)) {
            writer.println(parser.parseJoin(alreadyHandledChatters.get(player)));
        } else {
            String uuid = UUID.randomUUID().toString();
            alreadyHandledChatters.put(player, uuid);
            writer.println(parser.parseJoin(uuid));
            try {
                ServerSocket socket = new ServerSocket(0);
                int localPort = socket.getLocalPort();
                writer.println(String.valueOf(localPort));
                Socket accept = socket.accept();
                CardGamePrinter myPrintWriter = new CardGamePrinter(new PrintWriter(accept.getOutputStream()));
                CardGameReader myBufferedReader = new CardGameReader(new BufferedReader(new InputStreamReader(accept.getInputStream())));
                Fred fred = new Fred(new PlayerServerProxy(player, accept, myBufferedReader, myPrintWriter, alreadyHandledChatters));
                fred.start();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            writer.println(player.getUsername());
        }
    }

    private void readResponse() {
        String status = null;
        try {
            status = reader.readLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (status.equals("200")) {
            throw new RuntimeException();
        }
    }

    private void readProtocol() {
        try {
            String protocol = reader.readLine();
            System.out.printf("Protocol: " + protocol);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}
