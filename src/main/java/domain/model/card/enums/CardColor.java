package domain.model.card.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum CardColor {

    CLUBS(1, "Clubs", "♣"),
    SPADES(2, "Spades", "♠"),
    HEARTS(3, "Hearts", "♡"),
    DIAMONDS(4, "Diamonds", "♢");

    private int value;
    private String name;
    private String symbol;
}