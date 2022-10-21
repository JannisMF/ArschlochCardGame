package server.application;

import domain.IGame;
import domain.IPlayer;
import domain.exceptions.ArschlochGameException;
import domain.exceptions.custom.CardIsLowerException;
import domain.exceptions.custom.CardNotInHandException;
import domain.exceptions.custom.CardsHaveDifferentValuesException;
import domain.exceptions.custom.PlayerIsNotAtTurnException;
import domain.model.card.Card;
import domain.model.card.enums.CardValue;
import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

@Data
public class GameImpl implements IGame {

    private final Map<IPlayer, String> playerComms = new HashMap<>();
    boolean playDone = false;
    private GameBoard gameBoard = new GameBoard();

    @Override
    public void joinRoom(IPlayer player) {
        if (gameBoard.getAllPlayers().contains(player)) {
            throw new RuntimeException("Player Already in Game");
        } else {
            gameBoard.getAllPlayers().add(player);
        }
    }


    public boolean isTurn(IPlayer player) {
        return player.equals(gameBoard.getCurrentPlayer().peek());
    }

    @Override
    public void playCards(IPlayer player, List<Card> cardsToPlace) throws ArschlochGameException {
        if (!cardsToPlace.isEmpty()) { // The player plays cards
            gameBoard.setSkipTurnCounter(0);
            checkRules(cardsToPlace, player);
            placeCards(cardsToPlace, player);
        } else { // The Player skips turn
            gameBoard.setSkipTurnCounter(gameBoard.getSkipTurnCounter() + 1);
        }
        if (isPlayerOut(player)) {
            gameBoard.getCurrentPlayer().remove(player);
            gameBoard.getPlayersOut().put(player, gameBoard.getPlayersOut().size() + 1);
            informFinishedPlayer(player);
        } else if (!containsAce(cardsToPlace)) {
            gameBoard.getCurrentPlayer().add(gameBoard.getCurrentPlayer().remove());
        }

        playDone = true;
    }

    private boolean containsAce(List<Card> cards) {
        boolean contains = false;
        for (Card card : cards) {
            if (card.getCardValue().getName().equals(CardValue.ACE.getName())) {
                contains = true;
            }
        }
        return contains;
    }

    @Override
    public void leave(IPlayer player) {
        gameBoard.getAllPlayers().remove(player);
    }

    private void placeCards(List<Card> cardsToPlace, IPlayer player) {
        List<Card> newCardsInHand = gameBoard.getCardsOfPlayer().get(player);
        for (Card card : cardsToPlace) {
            gameBoard.getCardsOnTable().push(card);
            newCardsInHand.remove(card);
        }
        gameBoard.getCardsOfPlayer().replace(player, newCardsInHand);
        if (!newCardsInHand.isEmpty()) {
            player.updateHand(gameBoard.getCardsOfPlayer().get(player));
        }


    }

    private void checkRules(List<Card> cardsToPlace, IPlayer player) throws ArschlochGameException {
        // Checks
        if (!isTurn(player)) {
            throw new PlayerIsNotAtTurnException();
        }
        if (!isInHand(cardsToPlace, player)) {
            throw new CardNotInHandException();
        }
        if (!areSameCards(cardsToPlace)) {
            throw new CardsHaveDifferentValuesException();
        }
        if (!isHigherCardPair(cardsToPlace)) {
            throw new CardIsLowerException();
        }
    }

    private boolean isPlayerOut(IPlayer player) {
        return gameBoard.getCardsOfPlayer().get(player).size() == 0;
    }

    private boolean isInHand(List<Card> cardsToPlace, IPlayer player) {
        for (Card card : cardsToPlace) {
            if (gameBoard.getCardsOfPlayer().get(player).stream().noneMatch(elem -> elem.equals(card))) {
                return false;
            }
        }
        return true;
    }

    private boolean areSameCards(List<Card> cardsToPlace) {
        if (cardsToPlace.size() <= 1) {
            return true;
        }
        for (Card card1 : cardsToPlace) {
            for (Card card2 : cardsToPlace)
                if (!card1.getCardValue().getName().equals(card2.getCardValue().getName())) {
                    return false;
                }
        }
        return true;
    }

    private boolean isHigherCardPair(List<Card> cardsToPlace) {
        if (gameBoard.getCardsOnTable().isEmpty()) {
            return true;
        }
        int cardValueOnTop = gameBoard.getCardsOnTable().peek().getCardValue().getValue();
        // Just check first card because due to previous checks we know that all cardsToPlace are the same
        int cardValueToPlace = cardsToPlace.get(0).getCardValue().getValue();

        return cardValueToPlace > cardValueOnTop;
    }

    private void informFinishedPlayer(IPlayer player) {
        for (IPlayer p : gameBoard.getAllPlayers()) {
            p.finishedPlayer(player, gameBoard.getPlayersOut().get(player));
        }
    }

    public void resetGamebord() {
        gameBoard.setCardsOnTable(new Stack<>());
        gameBoard.setPlayersOut(new HashMap<>());
        gameBoard.getCurrentPlayer().clear();
    }

    public void sortBeforeRound() {
        if (gameBoard.getLastPlace() != null) {
            IPlayer currentPlayer;
            do {
                currentPlayer = gameBoard.getCurrentPlayer().peek();
                gameBoard.getCurrentPlayer().add(gameBoard.getCurrentPlayer().remove());
            } while (currentPlayer.equals(gameBoard.getLastPlace()));

        }
    }
}