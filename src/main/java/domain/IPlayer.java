package domain;

import domain.model.card.Card;

import java.util.List;

public interface IPlayer {
    String getUsername();

    void updateHand(List<Card> cards);

    void updatePlayfield(List<Card> cards);

    void demandRoundStart();

    void demandCards();

    boolean askForStart();

    void broadcastMessage(String message);

    void finishedPlayer(IPlayer player, int place);
}
