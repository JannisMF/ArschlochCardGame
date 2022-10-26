package client.stub;

import domain.model.card.Card;

import java.util.List;

public class ClientMessageParser {
    public String parseJoin(String pId) {
        return "002@" + pId;
    }

    public String parseMessage(String pId, String message) {
        return "001@" + pId + "§" + message;
    }

    public String parseIsTurn(String pId) {
        return "004@" + pId;
    }

    public String parsePlayCards(String pId, List<Card> cards) {
        StringBuilder cardString = new StringBuilder();
        for (Card card : cards) {
            cardString.append(parseCard(card)).append(';');
        }
        if (cards.size() == 0) {
            return "101@" + pId;
        }
        return "100@" + pId + "§" + cardString;
    }

    public String parseLeave(String pId) {
        return "003@" + pId;
    }

    private String parseCard(Card card) {
        return card.getCardColor().toString() + ":" + card.getCardValue().toString();
    }

}
