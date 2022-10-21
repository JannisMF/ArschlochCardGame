package domain.model.card.decks;

import domain.model.card.Card;
import domain.model.card.enums.CardColor;
import domain.model.card.enums.CardValue;

import java.util.ArrayList;
import java.util.List;

public class Poker {
    public static final Card[] deck = newCardDeck();

    private static Card[] newCardDeck() {
        List<Card> cards = new ArrayList<Card>();
        for (CardColor cardColor : CardColor.values()) {
            for (CardValue cardValue : CardValue.values()) {
                cards.add(new Card(cardValue, cardColor));
            }
        }
        return cards.toArray(new Card[cards.size()]);
    }
}