package server.stub;

import domain.model.card.Card;

import java.util.List;

public class ServerMessageParser {


    public String parseGetUsername() {
        return "000@GET";
    }

    public String parseMessage(String message) {
        return "001@" + message;
    }

    public String parseCardToPlayRequest() {
        return "103";
    }

    public String parsePlayerHand(List<Card> cards) {

        StringBuilder handString = new StringBuilder();
        for (Card card : cards) {
            handString.append(parseCard(card)).append(";");
        }
        return "102@" + handString;
    }

    public String parseGameBoard(List<Card> cards) {
        StringBuilder cardString = new StringBuilder("103@");
        for (Card card : cards) {
            cardString.append(parseCard(card)).append(";");
        }

        return cardString.toString();
    }

    public String parseAskForStart() {
        return "002@AskForStart";
    }

    public String parseDemandStart() {
        return "003@DemandRoundStart";
    }

    public String parseDemandCards() {
        return "004@DemandCards";
    }

    public String parseFinishedRound(String player, int place) {
        return "005@" + player + "§" + place;
    }

    private String parseCard(Card card) {
        return card.getCardColor().toString() + ":" + card.getCardValue().toString();
    }

}