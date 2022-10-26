package client.app;

import client.controller.GameClientProxy;
import domain.IPlayer;
import domain.model.card.Card;
import domain.exceptions.custom.CardNotInHandException;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

public class PlayerImpl implements IPlayer {
    private final String username;
    //private final GameClientProxy proxy;
    private List<Card> cards;
    private int numberLaidCards;
    Scanner sc = new Scanner(System.in);

    public PlayerImpl(String username, GameClientProxy gameClientProxy) {
        this.username = username;
        proxy = gameClientProxy;
    }

    @Override
    public String getUsername() {
        return username;
    }

    private String scanAndCutInput() {
        String input = sc.nextLine();
        String cuttedInput = input.replaceAll("\\s+", "");
        return cuttedInput;
    }

    private void printEnd() {
        System.out.println("==================================================================================");
        System.out.println();
    }

    private void printBeginning() {
        System.out.println();
        System.out.println("==================================================================================");
    }

    @Override
    public void updateHand(List<Card> cards) {
        printBeginning();
        cards.sort(Comparator.naturalOrder());
        this.cards = cards;
        System.out.println("Deine Handkarte:");
        StringBuilder sb = new StringBuilder();
        sb.append("| ");
        int counter = 1;
        for (Card c : cards) {
            sb.append(counter + ": ");
            sb.append(c.getCardValue().getName());
            sb.append(c.getCardColor().getSymbol());
            sb.append(" | ");
            counter++;
        }
        System.out.println(sb);
        printEnd();
    }

    @Override
    public void updatePlayfield(List<Card> cards) {
        printBeginning();
        numberLaidCards = cards.size();
        if (numberLaidCards == 1)
            System.out.println("Es wurde folgende Karte gelegt:");
        else
            System.out.println("Es wurden folgende Karten gelegt:");
        StringBuilder sb = new StringBuilder();
        for (Card c : cards) {
            sb.append(c.getCardValue().getName());
            sb.append(c.getCardColor().getSymbol());
            sb.append(" ");
        }
        System.out.println(sb);
        printEnd();
    }

    @Override
    public void demandRoundStart() {
        printBeginning();
        System.out.println("Du startest einen NEUE Runde. Bitte lege eine oder mehrere Karten mit gleicher Wertigkeit.");
        System.out.println(("Gibt die Zahlen der Karten, die du spielen möchtest, durch Kommata getrennt ein."));
        String input = scanAndCutInput();
        System.out.println(input);
        String[] separatedInput = input.split(",");
        List<Card> playedCards = new ArrayList<>();
        for (String s : separatedInput) {
            int i = Integer.parseInt(s) - 1;
            playedCards.add(cards.get(i));
        }
        try {
            proxy.playCards(this, playedCards);
        } catch (CardNotInHandException e) {
            reDemandCards();
        }
        printEnd();
    }

    @Override
    public void demandCards() {
        printBeginning();
        if (numberLaidCards == 1)
            System.out.println("Du bist an der Reihe. Bitte lege eine höhere Karte oder schiebe diese Runde.");
        else
            System.out.println("Du bist an der Reihe. Bitte lege " + numberLaidCards + " höhere Karten oder schiebe diese Runde.");
        System.out.println(("Gibt die Zahlen der Karten, die du spielen möchtest, durch Kommata getrennt ein ODER 0 um zu schieben."));
        String input = scanAndCutInput();
        System.out.println(input);
        if (input.equals("0")) {
            try {
                proxy.playCards(this, new ArrayList<>());
            } catch (CardNotInHandException e) {
                reDemandCards();
            }
        } else {
            String[] separatedInput = input.split(",");
            List<Card> playedCards = new ArrayList<>();
            try {
                for (String s : separatedInput) {
                    int i = Integer.parseInt(s) - 1;
                    System.out.println(i);
                    playedCards.add(cards.get(i));
                }
            }catch (Exception e){
                reDemandCards();
            }

            try {
                proxy.playCards(this, playedCards);
            } catch (CardNotInHandException e) {
                reDemandCards();
            }
        }
        printEnd();
    }

    @Override
    public boolean askForStart() {
        printBeginning();
        System.out.println("Möchtest du die Runde starten? J für Ja ODER N für Nein");
        String input = scanAndCutInput();
        System.out.println(input);
        printEnd();
        return input.equals("j") || input.equals("J");
    }

    @Override
    public void broadcastMessage(String message) {
        printBeginning();
        System.out.println(message);
        printEnd();
    }

    @Override
    public void finishedPlayer(IPlayer player, int place) {
        printBeginning();
        if (player.getUsername().equals(this.getUsername())) {
            System.out.println("Du hast diese Runde beendet und bist " + place + ". Platz");
        } else {
            System.out.println("Ein Spieler ist Fertig und belegt den " + place + ". Platz");
        }
        printEnd();
    }

    private void reDemandCards() {
        printBeginning();
        System.out.println("Die Karten die du zuvor gelegt hast konnten nicht platziert werden. Bitte gebe neue Karten ein!!");
        System.out.println(("Gibt die Zahlen der Karten, die du spielen möchtest, durch Kommata getrennt ein  ODER 0 um zu schieben."));
        String input = scanAndCutInput();
        System.out.println(input);
        if (input.equals("0")) {
            try {
                proxy.playCards(this, new ArrayList<>());
            } catch (CardNotInHandException e) {
                reDemandCards();
            }
        } else {
            String[] separatedInput = input.split(",");
            List<Card> playedCards = new ArrayList<>();
            for (String s : separatedInput) {
                int i = Integer.parseInt(s) - 1;
                System.out.println(i);
                playedCards.add(cards.get(i));
            }
            try {
                proxy.playCards(this, playedCards);
            } catch (CardNotInHandException e) {
                reDemandCards();
            }
        }
        printEnd();
    }
}
