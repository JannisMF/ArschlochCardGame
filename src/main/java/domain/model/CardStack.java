package domain.model;

import domain.model.card.Card;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Stack;

public class CardStack extends Stack<Card> {
    public CardStack() {

    }

    public CardStack(Card[] cards) {
        for (Card card : cards) {
            this.push(card);
        }
    }

    public void shuffle() {
        Collections.shuffle(this);
    }

    public ArrayList<Card>[] split(int splitNumber) {
        int stackSize = this.size() / splitNumber;
        ArrayList<Card>[] splits;
        splits = new ArrayList[splitNumber];
        for (int i = splitNumber; i > 0; i--) {
            ArrayList<Card> newSplit = new ArrayList<>();
            for (int j = stackSize; j > 0; j--) {
                newSplit.add(this.pop());
            }
            splits[i - 1] = newSplit;
        }
        return splits;
    }

    @Override
    public synchronized String toString() {
        StringBuilder output = new StringBuilder();
        for (Card card : this) {
            output.append(card.toString()).append(";");
        }
        return output.toString();
    }
}