package domain.model.card;

import domain.model.card.enums.CardColor;
import domain.model.card.enums.CardValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Card implements Comparable {

    private CardValue cardValue;
    private CardColor cardColor;

    public Card(String cardString) {
        String[] values = cardString.split(":");
        cardColor = CardColor.valueOf(values[0]);
        cardValue = CardValue.valueOf(values[1]);
    }

    @Override
    public String toString() {
        return cardColor.getSymbol() + cardValue.getName();
    }

    @Override
    public boolean equals(Object obj) {
        if (this.toString().equals(obj.toString())) {
            return true;
        }
        return false;
    }

    @Override
    public int compareTo(Object o) {
        Card comparable = (Card) o;
        if (comparable.getCardValue().getValue() < this.getCardValue().getValue()) {
            return 1;
        } else if (comparable.getCardValue().getValue() > this.getCardValue().getValue()) {
            return -1;
        } else {
            if (comparable.getCardColor().getValue() < this.getCardColor().getValue()) {
                return 1;
            } else if (comparable.getCardColor().getValue() > this.getCardColor().getValue()) {
                return -1;
            } else {
                return 0;
            }
        }
    }
}