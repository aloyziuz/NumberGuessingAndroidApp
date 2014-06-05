package jcu.cp3307.brownscriterionnumberguessing;

import java.util.ArrayList;

public class CardGenerator {
	private ArrayList<Integer> binary;
	private ArrayList<Integer> fibonacci;
	private ArrayList<Integer> prime;
	private ArrayList<Card> deck;
	private int limit;
	
	public CardGenerator(int upperLimit, String cardType){
		binary = new ArrayList<Integer>();
        binary.add(1);
        binary.add(2);
        binary.add(4);
        binary.add(8);
        binary.add(16);
        binary.add(32);
        binary.add(64);
        binary.add(128);
        
        fibonacci = new ArrayList<Integer>();
        fibonacci.add(1);
        fibonacci.add(2);
        fibonacci.add(3);
        fibonacci.add(5);
        fibonacci.add(8);
        fibonacci.add(13);
        fibonacci.add(21);
        fibonacci.add(34);
        fibonacci.add(55);
        fibonacci.add(89);
        fibonacci.add(144);
        
        prime = new ArrayList<Integer>();
        prime.add(1);
        prime.add(2);
        prime.add(3);
        prime.add(5);
        prime.add(7);
        prime.add(11);
        prime.add(13);
        prime.add(17);
        prime.add(19);
        prime.add(23);
        prime.add(29);
        prime.add(31);
        prime.add(37);
        prime.add(41);
        prime.add(43);
        prime.add(47);
        prime.add(53);
        prime.add(59);
        prime.add(61);
        prime.add(67);
        prime.add(71);
        prime.add(73);
        prime.add(79);
        prime.add(83);
        prime.add(89);
        prime.add(97);
        prime.add(101);
        prime.add(103);
        prime.add(107);
        prime.add(109);
        prime.add(113);
        
		this.limit = upperLimit;
		if(cardType.equals("Binary")){
			deck = GenerateBinaryCards(this.limit);
		}
		else if(cardType.equals("Fibonacci")){
			deck = GenerateFibonacciCards(this.limit);
		}
		else if(cardType.equals("Prime")){
			deck = GeneratePrimeCards(this.limit);
		}
	}
	
	public boolean ChangeDeck(String newCardType, int newLimit){
		this.limit = newLimit;
		if(newCardType.equals("Binary")){
			deck = GenerateBinaryCards(this.limit);
			return true;
		}
		else if(newCardType.equals("Fibonacci")){
			deck = GenerateFibonacciCards(this.limit);
			return true;
		}
		else if(newCardType.equals("Prime")){
			deck = GeneratePrimeCards(this.limit);
			return true;
		}
		return false;
	}
	
	public ArrayList<Card> GetDeck(){
		return this.deck;
	}
	
	private ArrayList<Card> GenerateBinaryCards(int limit){
		ArrayList<Card> cards = GenerateCards(binary, limit);
		ArrayList<Card> deck = GenerateNumbers(cards, limit);
		return deck;
	}
	
	private ArrayList<Card> GenerateFibonacciCards(int limit){
		ArrayList<Card> cards = GenerateCards(fibonacci, limit);
		ArrayList<Card> deck = GenerateNumbers(cards, limit);
		return deck;
	}
	
	private ArrayList<Card> GeneratePrimeCards(int limit){
		ArrayList<Card> cards = GenerateCards(prime, limit);
		ArrayList<Card> deck = GenerateNumbers(cards, limit);
		return deck;
	}
	
	private ArrayList<Card> GenerateCards(ArrayList<Integer> numSequence, int limit){
        ArrayList<Card> deck = new ArrayList<Card>();
        ArrayList<Integer> usedInt = new ArrayList<Integer>();
        float halfLimit = limit*0.50f;
        
        for(int i = 0; i < numSequence.size(); i++){
            if(numSequence.get(i) < halfLimit){
                usedInt.add(numSequence.get(i));
            }
            else if(numSequence.get(i) >= halfLimit && numSequence.get(i) < limit){
                usedInt.add(numSequence.get(i));
                break;
            }
        }
        
        for(int i = 0; i < usedInt.size(); i++){
            Card card = new Card();
            card.cardNumbers.add(usedInt.get(i));
            deck.add(card);
        }
        
        return deck;
    }
	
	private ArrayList<Card> GenerateNumbers(ArrayList<Card> deck, int limit){
        for(int i = limit; i > 1; i--){
            int remainder = i;
            for(int seqNum = deck.size()-1; seqNum >= 0; seqNum--){
                if((remainder - deck.get(seqNum).cardNumbers.get(0)) > 0){
                    remainder -= deck.get(seqNum).cardNumbers.get(0);
                    deck.get(seqNum).cardNumbers.add(i);
                }
                else if((remainder - deck.get(seqNum).cardNumbers.get(0)) == 0){
                    remainder -= deck.get(seqNum).cardNumbers.get(0);
                    if(i != deck.get(seqNum).cardNumbers.get(0)){
                        deck.get(seqNum).cardNumbers.add(i);
                    }
                    break;
                }
            }
            //leftover checking
            if(remainder > 0){
                System.out.println("leftover on number " + i);
            }
        }
        for(Card card:deck){
            card.SortNumbers();
        }
        
        return deck;
    }
}
