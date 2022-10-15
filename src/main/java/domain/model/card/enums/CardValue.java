package domain.model.card.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum CardValue {

    TWO(1, "2", "2"),
    THREE(2, "3", "3"),
    FOUR(3, "4", "4"),
    FIVE(4, "5", "5"),
    SIX(5, "6", "6"),
    SEVEN(6, "7", "7"),
    EIGHT(7, "8", "8"),
    NINE(8, "9", "9"),
    TEN(9, "10", "10"),
    JACK(10, "J", "Jack"),
    QUEEN(11, "Q", "Queen"),
    KING(12, "K", "King"),
    ACE(13, "A", "Ace");

    private int value;
    private String name;
    private String fullName;
}