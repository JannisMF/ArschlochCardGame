package server.controller;

import domain.IPlayer;
import domain.model.CardStack;
import domain.model.Fred;
import domain.model.card.Card;
import domain.model.card.decks.Poker;
import domain.model.card.enums.CardColor;
import domain.model.card.enums.CardValue;
import lombok.extern.slf4j.Slf4j;
import server.application.GameBoard;
import server.application.GameImpl;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
public class ServerRuntime {

    public static void main(String[] args) {
        int connectedClients = 0;
        GameImpl game = new GameImpl();

        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(42069);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        do {
            try {
                Socket clientSocket = serverSocket.accept();

                GameServerProxy gameServerProxy = new GameServerProxy(clientSocket, game);
                Fred t = new Fred(gameServerProxy);
                t.start();
                connectedClients++;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } while (connectedClients < 2);
        while (game.getGameBoard().getAllPlayers().size() != connectedClients) {
            try {
                TimeUnit.SECONDS.sleep(1);
                log.debug("Waiting");
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        // Spiel spielen
        while (allPlayersWantToStartARound(game.getGameBoard())) {
            playRound(game);
        }
    }


    private static void playRound(GameImpl game) {
        dealCards(game);
        game.sortBeforeRound();
        // Round-Lifecicle
        while (isInRound(game)) {
            game.setPlayDone(false);
            game.getGameBoard().getCurrentPlayer().peek().demandRoundStart();
            while (!game.isPlayDone()) {
                try {
                    TimeUnit.SECONDS.sleep(2);
                    log.debug("Waiting");
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            ;
            // Stack-Lifecicle
            while (isInStack(game)) {
                while (!game.isPlayDone()) {
                    try {
                        TimeUnit.SECONDS.sleep(2);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                if (isInStack(game)) {
                    game.setPlayDone(false);
                    updateAllBoards(game);
                    game.getGameBoard().getCurrentPlayer().peek().demandCards();
                }
            }
            updateAllBoards(game);
            sendMessageToAllPlayers("Die Runde wurde beendet. Der Kartenstapel wir zurückgesetzt.", game);
            game.getGameBoard().getCardsOnTable().clear();
            updateAllCurrentHands(game);
        }
        IPlayer lastPlace = game.getGameBoard().getCurrentPlayer().peek();
        lastPlace.finishedPlayer(lastPlace, game.getGameBoard().getAllPlayers().size());
        game.getGameBoard().setLastPlace(lastPlace);
        game.resetGamebord();
    }

    private static void dealCards(GameImpl game) {
        // Clear all Cards
        game.getGameBoard().getCardsOfPlayer().clear();
        Card[] cards = {new Card(CardValue.ACE, CardColor.CLUBS)};
        CardStack cardStack = new CardStack(Poker.deck);
        cardStack.shuffle();
        List<ArrayList<Card>> splittedCards = Arrays.stream(cardStack.split(game.getGameBoard().getAllPlayers().size())).toList();
        for (int i = 0; i < game.getGameBoard().getAllPlayers().size(); i++) {
            IPlayer player = game.getGameBoard().getAllPlayers().get(i);
            game.getGameBoard().getCurrentPlayer().add(player);
            game.getGameBoard().getCardsOfPlayer().put(player, splittedCards.get(i));
            player.updateHand(splittedCards.get(i));
        }
    }

    private static void updateAllBoards(GameImpl game) {
        List<IPlayer> currentPlayers = game.getGameBoard().getCurrentPlayer().stream().toList();
        // calculate top cards
        List<Card> topCards = new ArrayList<>();
        CardValue highestCardValue = game.getGameBoard().getCardsOnTable().peek().getCardValue();
        for (Card card : game.getGameBoard().getCardsOnTable()) {
            if (card.getCardValue().equals(highestCardValue)) {
                topCards.add(card);
            }
        }
        // update all active players hands
        for (IPlayer player : currentPlayers) {
            player.updatePlayfield(topCards);
        }
    }

    private static boolean allPlayersWantToStartARound(GameBoard gameBoard) {
        for (IPlayer player : gameBoard.getAllPlayers()) {
            if (!player.askForStart()) {
                return false;
            }
        }
        return true;
    }

    private static boolean isInRound(GameImpl game) {
        return game.getGameBoard().getCurrentPlayer().size() > 1;
    }

    private static boolean isInStack(GameImpl game) {
        if (!game.getGameBoard().getCardsOnTable().isEmpty() && game.getGameBoard().getCardsOnTable().peek().getCardValue().getName() == CardValue.ACE.getName()) {
            sendMessageToAllPlayers("Es wurde ein Ass gelegt!", game);
            return false;
        } else if (game.getGameBoard().getSkipTurnCounter() != game.getGameBoard().getCurrentPlayer().size() - 1) {
            return true;
        }
        sendMessageToAllPlayers("Alle Spieler haben Geschoben!", game);
        return false;

    }

    private static void sendMessageToAllPlayers(String message, GameImpl game) {

        for (IPlayer player : game.getGameBoard().getAllPlayers()) {
            player.broadcastMessage(message);
        }
    }

    private static void updateAllCurrentHands(GameImpl game) {
        for (IPlayer player : game.getGameBoard().getCurrentPlayer()) {
            player.updateHand(game.getGameBoard().getCardsOfPlayer().get(player));
        }
    }
}