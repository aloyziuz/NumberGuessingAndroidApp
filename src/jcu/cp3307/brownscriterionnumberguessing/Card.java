package jcu.cp3307.brownscriterionnumberguessing;

import java.util.ArrayList;
import java.util.Collections;

public class Card {
    ArrayList<Integer> cardNumbers;
    Boolean numberIsPresent;
    
    public Card(){
        cardNumbers = new ArrayList<Integer>();
        numberIsPresent = null;
    }
    
    public void SortNumbers(){
        Collections.sort(cardNumbers);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for(int num:cardNumbers){
            sb.append(num);
            sb.append(", ");
        }
        sb.setLength(sb.length()-2);
        sb.append(". Total: " + cardNumbers.size());
        return sb.toString();
    } 
}