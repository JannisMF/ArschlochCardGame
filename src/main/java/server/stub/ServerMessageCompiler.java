package server.stub;

import domain.comm.PlayerCommandModel;
import domain.comm.Request;
import domain.model.card.Card;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class ServerMessageCompiler {
    public Request compile(String hostname, String message) {
        log.debug("Compiling message: {}", message);
        String[] splitMessage = message.split("@");
        Request request = new Request(hostname, splitMessage[0], splitMessage[1]);


        return request;
    }

    public List<Card> compileCardList(String cardString) {
        List<Card> cards = new ArrayList<>();
        String[] cardStrings = cardString.split(";");
        for (String card : cardStrings) {
            cards.add(new Card(card));
        }
        return cards;
    }

    public PlayerCommandModel splitOfPlayer(String message) {
        String[] splitMessage = message.split("§");
        return new PlayerCommandModel(splitMessage[0], splitMessage[1]);

    }
}