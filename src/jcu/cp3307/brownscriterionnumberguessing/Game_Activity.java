package jcu.cp3307.brownscriterionnumberguessing;

import java.util.List;
import java.util.Random;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class Game_Activity extends Activity {
	private ArrayAdapter<Integer> numbers;
	private GridView gridview;
	private String name;
	private String cardType;
	private String range;
	private List<Card> deck;
	private int currentCard;
	private int noOfCards;
	private boolean isComplete;
	private float begin, end;
	private int minDistance;
	private int score;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game);
		
		//set fonts
		TextView textview = (TextView)findViewById(R.id.welcomeTxt);
		setFont(textview);
		textview = (TextView)findViewById(R.id.numberPresentLbl);
		setFont(textview);
		textview = (TextView)findViewById(R.id.cardNumberLbl);
		setFont(textview);
		
		Button btn = (Button)findViewById(R.id.yesBtn);
		setFont(btn);
		btn = (Button)findViewById(R.id.noBtn);
		setFont(btn);
		
		//get extras
		Bundle extras = this.getIntent().getExtras();
		if(extras != null){
			name = extras.getString("Name");
			cardType = extras.getString("CardType"); 
			range = extras.getString("Range");
		}
		
		//get cards
		int upperLimit = Integer.parseInt(range.split("-")[1]);
		CardGenerator generator = new CardGenerator(upperLimit, cardType);
		deck = generator.GetDeck();
		
		//initialize variables
		currentCard = 0;
		noOfCards = deck.size()-1;
		isComplete = false;
		minDistance = 150;
		
		SetCardNumberLabel();
		
		//create GridView and show the current card (first)
		gridview = (GridView) findViewById(R.id.GridView1);
		numbers = new ArrayAdapter<Integer>(this, R.layout.custom_adapter_layout);
		gridview.setAdapter(numbers);
		ChangeAdapterValues(deck.get(currentCard).cardNumbers);
		
		//touch listener to detect swipes
		gridview.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch(event.getAction()){
					case MotionEvent.ACTION_DOWN:
						begin = event.getX();
						break;
					case MotionEvent.ACTION_UP:
						end = event.getX();
						float deltaX = end - begin;
						//left to right swipe
						if(end > begin && Math.abs(deltaX) >= minDistance){
							PrevCard();
							SetCardNumberLabel();
							Boolean isPresent = deck.get(currentCard).numberIsPresent;
							//dont show buttons if the card is already marked yes/no
							if(isPresent != null){
								ToggleButtonsVisibility(false);
								//set yes/no label accordingly
								if(isPresent){
									SetPresentLabel(true);
								}
								else{
									SetPresentLabel(false);
								}
							}
							else{
								//show buttons and hide yes/no label if the card not yet marked
								ToggleButtonsVisibility(true);
								SetPresentLabel(null);
							}
						}
						//right to left swipe
						else if(begin > end && Math.abs(deltaX) >= minDistance){
							NextCard();
							SetCardNumberLabel();
							Boolean isPresent = deck.get(currentCard).numberIsPresent;
							//dont show buttons if the card is already marked yes/no
							if(isPresent != null){
								ToggleButtonsVisibility(false);
								//set yes/no label accordingly
								if(isPresent){
									SetPresentLabel(true);
								}
								else{
									SetPresentLabel(false);
								}
							}
							else{
								//show buttons and hide yes/no label if the card not yet marked
								ToggleButtonsVisibility(true);
								SetPresentLabel(null);
							}
						}
						break;
				}
				return false;
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.game_, menu);
		return true;
	}
	
	//show next card
	private void NextCard(){
		playSfx("page_flip");
		currentCard++;
		if(currentCard > noOfCards){
			currentCard = noOfCards;
		}
		ChangeAdapterValues(deck.get(currentCard).cardNumbers);
	}
	
	//show previous card
	private void PrevCard(){
		playSfx("page_flip");
		currentCard--;
		if(currentCard < 0){
			currentCard = 0;
		}
		ChangeAdapterValues(deck.get(currentCard).cardNumbers);
	}
	
	//replace all items in array adapter with new list
	private void ChangeAdapterValues(List<Integer> intList){
		numbers.clear();
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            numbers.addAll(intList);
        } else {
            for(int i : intList) {
                numbers.add(i);
            }
        }
	}
	
	//yes button clicked
	public void PresentClicked(View view){
		playSfx("button_click");
		deck.get(currentCard).numberIsPresent = true;
		SetPresentLabel(true);
		ToggleButtonsVisibility(false);
		isComplete = CompletionCheck();
		
		if(isComplete){
			int guessedNumber = GuessTheNumber(deck);
			ShowAppGuess(guessedNumber);
		}
	}
	
	//no button clicked
	public void NotPresentClicked(View view){
		playSfx("button_click");
		deck.get(currentCard).numberIsPresent = false;
		SetPresentLabel(false);
		ToggleButtonsVisibility(false);
		isComplete = CompletionCheck();
		
		if(isComplete){
			int guessedNumber = GuessTheNumber(deck);
			ShowAppGuess(guessedNumber);
		}
	}
	
	private int GuessTheNumber(List<Card> deck){
        //80% correct
        Random random = new Random();
        int i = random.nextInt(100);
        if(i > 20){
        	int result = 0;
            for(Card card:deck){
                if(card.numberIsPresent){
                    result += card.cardNumbers.get(0);
                }
            }
        	return result;
        }
        else{
        	int upperLimit = Integer.parseInt(range.split("-")[1]);
        	int lowerLimit = Integer.parseInt(range.split("-")[0]);
        	int rand = random.nextInt((upperLimit - lowerLimit)+1) + lowerLimit;
        	return rand;
        }
    }
	
	private void ShowAppGuess(int guessedNumber){
		AlertDialog.Builder dialogbox = new AlertDialog.Builder(this);
		final AlertDialog.Builder responseDialog = new AlertDialog.Builder(this);
		
		dialogbox.setTitle("My Guess");
		dialogbox.setMessage("Your secret number is " + guessedNumber);
		dialogbox.setPositiveButton("Correct", new OnClickListener() {
			@Override
			//app's guess is correct
			public void onClick(DialogInterface dialog, int which) {
				responseDialog.setTitle("Yay!");
				responseDialog.setMessage("Yeah! I guessed it correctly.");
				responseDialog.setPositiveButton("HighScore", new OnClickListener(){
					@Override
					public void onClick(DialogInterface dialog, int which) {
						//start highscore activity and finish this activity
						Intent intent = new Intent(getApplication(), HighscoreActivity.class);
						finish();
						startActivity(intent);
					}
				});
				responseDialog.setNegativeButton("Back to Menu", new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						finish();
					}
				});
				responseDialog.show();
			}
		});
		//app's guess is wrong
		dialogbox.setNegativeButton("Wrong", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				responseDialog.setTitle("Aww!");
				responseDialog.setMessage("Aww! Too bad :(");
				responseDialog.setPositiveButton("Back to Menu", new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						finish();
					}
				});
				responseDialog.show();
			}
		});
		dialogbox.show();
	}
	
	//set yes/no label according to input
	//true means child's number is present in the card
	//false means child's number is absent in the card
	//null means the card has not been marked yet
	private void SetPresentLabel(Boolean isPresent){
		TextView lbl = (TextView)findViewById(R.id.numberPresentLbl);
		if(isPresent == null){
			lbl.setVisibility(View.INVISIBLE);
		}
		else if(!isPresent){
			lbl.setVisibility(View.VISIBLE);
			lbl.setText("Your number cannot be found here!");
			lbl.setTextColor(Color.RED);
		}
		else{
			lbl.setVisibility(View.VISIBLE);
			lbl.setText("Your number can be found here!");
			lbl.setTextColor(Color.GREEN);
		}
	}
	
	//show/hide buttons depending on input
	private void ToggleButtonsVisibility(boolean isVisible){
		RelativeLayout btnLayout = (RelativeLayout)findViewById(R.id.buttonsLayout);
		if(!isVisible){
			btnLayout.setVisibility(View.INVISIBLE);
		}
		else{
			btnLayout.setVisibility(View.VISIBLE);
		}
	}
	
	//check whether all card has been marked yes/no
	private boolean CompletionCheck(){
		for(Card card : deck){
			if(card.numberIsPresent == null){
				return false;
			}
		}
		return true;
	}
	
	private void SetCardNumberLabel(){
		TextView cardNoLbl = (TextView)findViewById(R.id.cardNumberLbl);
		cardNoLbl.setText("Card " + (currentCard+1) + " out of " + (noOfCards+1));
	}
	
	private void setFont(TextView textView) {
	    Typeface tf = Typeface.createFromAsset(textView.getContext().getAssets(), "DK Crayon Crumble.ttf");
	    textView.setTypeface(tf);
	}
	
	private void setFont(Button button) {
	    Typeface tf = Typeface.createFromAsset(button.getContext().getAssets(), "scribble box demo.ttf");
	    button.setTypeface(tf);
	}
	
	private void playSfx(String audioName){
		int resID = getResources().getIdentifier(audioName, "raw", getPackageName());
		MediaPlayer mp = MediaPlayer.create(this, resID);
		mp.start();
		
		mp.setOnCompletionListener(new OnCompletionListener() {
			@Override
			public void onCompletion(MediaPlayer mp) {
				mp.release();
			}
		});
	}
}
