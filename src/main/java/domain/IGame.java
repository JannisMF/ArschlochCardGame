package domain;

import domain.model.card.Card;
import domain.exceptions.ArschlochGameException;

import java.util.List;

public interface IGame {
    void joinRoom(IPlayer player);


    void playCards(IPlayer player, List<Card> cards) throws ArschlochGameException;

    void leave(IPlayer player);

}
