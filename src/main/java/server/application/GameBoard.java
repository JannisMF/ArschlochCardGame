package server.application;

import domain.IPlayer;
import domain.model.card.Card;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;

@NoArgsConstructor
@Data
public class GameBoard {
    private IPlayer lastPlace;
    private Stack<Card> cardsOnTable = new Stack<>();
    private Map<IPlayer, List<Card>> cardsOfPlayer = new HashMap<>();

    private List<IPlayer> allPlayers = new ArrayList<>();

    private Queue<IPlayer> currentPlayer = new LinkedList<>(); // If Just 1 Player is left, then the game is over
    private Map<IPlayer, Integer> playersOut = new HashMap<>(); // player, placement

    // If a players skips turn this counter goes one up.
    private int skipTurnCounter = 0;
}