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
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;

public class ChildGameActivity extends Activity {
	private ArrayAdapter<Integer> numbers;
	private GridView gridview;
	private String name;
	private String cardType;
	private String range;
	private int secretNumber;
	private List<Card> deck;
	private int currentCard;
	private int noOfCards;
	private int minDistance;
	private int score;
	private float begin, end;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_child_game);
		
		//change fonts
		TextView textview = (TextView)findViewById(R.id.numberPresentLbl);
		setFont(textview);
		textview = (TextView)findViewById(R.id.cardNumberLbl);
		setFont(textview);
		
		Button btn = (Button)findViewById(R.id.guessBtn);
		setFont(btn);
		
		//get extras
		Bundle extras = this.getIntent().getExtras();
		if(extras != null){
			name = extras.getString("Name");
			cardType = extras.getString("CardType"); 
			range = extras.getString("Range");
		}
		
		//get number limit
		int lowerLimit = Integer.parseInt(range.split("-")[0]);
		int upperLimit = Integer.parseInt(range.split("-")[1]);
		//get a secret number within the limit
		Random generator = new Random();
		secretNumber = generator.nextInt((upperLimit - lowerLimit)+1) + lowerLimit;
		
		//generate cards
		CardGenerator cardGenerator = new CardGenerator(upperLimit, cardType);
		deck = cardGenerator.GetDeck();
		currentCard = 0;
		noOfCards = deck.size()-1;
		minDistance = 150;
		
		SetCardNumberLabel();
		
		//show numbers of first card in gridview
		gridview = (GridView) findViewById(R.id.GridView1);
		numbers = new ArrayAdapter<Integer>(this, R.layout.custom_adapter_layout);
		gridview.setAdapter(numbers);
		ChangeAdapterValues(deck.get(currentCard).cardNumbers);
		
		//add touch listener for swiping
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
						//right to left swipe
						if(begin > end && Math.abs(deltaX) >= minDistance){
							NextCard();
							SetCardNumberLabel();
							boolean isPresent = deck.get(currentCard).numberIsPresent;
							if(isPresent){
								SetPresentLabel(true);
							}
							else{
								SetPresentLabel(false);
							}
						}
						break;
				}
				return false;
			}
		});
		
		//mark all the card (yes/no)
		MarkCards(secretNumber);
		//check the first card contains the number
		boolean isPresent = deck.get(currentCard).numberIsPresent;
		if(isPresent){
			SetPresentLabel(true);
		}
		else{
			SetPresentLabel(false);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.child_game, menu);
		return true;
	}
	
	public void GuessClicked(View view){
		playSfx("button_click");
		
		//first dialog box is for the player to input his guess
		//second dialog box is for the result (correct or wrong)
		AlertDialog.Builder dialogbox = new AlertDialog.Builder(this);
		final AlertDialog.Builder resultDialog = new AlertDialog.Builder(this);
		
		dialogbox.setTitle("Guess My Number");
		dialogbox.setMessage("You think you know my secret number?\nTell my your guess:");
		final EditText guessTxt = new EditText(this);
		dialogbox.setView(guessTxt);
		dialogbox.setPositiveButton("Guess", new OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				try{
					//the answer is correct
					if(Integer.parseInt(guessTxt.getText().toString()) == secretNumber){						
						resultDialog.setTitle("Congratulations!");
						resultDialog.setMessage("You guessed my secret number correctly!\nYou win the game");
						resultDialog.setPositiveButton("See Highscore", new OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								//start highscore activity and finish this activity
								Intent intent = new Intent(getApplication(), HighscoreActivity.class);
								finish();
								startActivity(intent);
							}
						});
						resultDialog.setNegativeButton("Back to Menu", new OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								finish();
							}
						});
						resultDialog.show();
					}
					//wrong answer
					else{
						resultDialog.setTitle("Failed");
						resultDialog.setMessage("Your guess is not my secret number. :(\nBetter try again.");
						resultDialog.setPositiveButton("Back to Menu", new OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								finish();
							}
						});
						resultDialog.show();
					}
				}
				//just in case if the player input alphabets, catch exception
				catch(NumberFormatException ex){
					resultDialog.setTitle("Failed");
					resultDialog.setMessage("Your guess is not my secret number. :(\nBetter try again.");
					resultDialog.setPositiveButton("Back to Menu", new OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							finish();
						}
					});
					resultDialog.show();
				}
			}
		});
		
		dialogbox.show();
	}
	
	//replace all current items in array adapter with a new list
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
	
	//show next card and show guess button if last card
	private void NextCard(){
		playSfx("page_flip");
		
		currentCard++;
		if(currentCard > noOfCards){
			currentCard = noOfCards;
		}
		ChangeAdapterValues(deck.get(currentCard).cardNumbers);
		
		if(currentCard == noOfCards){
			//show button to guess
			Button guessbtn = (Button)findViewById(R.id.guessBtn);
			guessbtn.setVisibility(View.VISIBLE);
		}
	}
	
	//set yes/no label, true means present, false means absent
	private void SetPresentLabel(boolean isPresent){
		TextView lbl = (TextView)findViewById(R.id.numberPresentLbl);
		if(!isPresent){
			lbl.setText("My secret number is not here. :(");
			lbl.setTextColor(Color.RED);
		}
		else{
			lbl.setText("One of these numbers is my secret number!");
			lbl.setTextColor(Color.GREEN);
		}
	}
	
	//mark all cards whether secret number is present or not in each card
	private void MarkCards(int secretNumber){
		for(Card card:deck){
			if(card.cardNumbers.contains(secretNumber)){
				card.numberIsPresent = true;
			}
			else{
				card.numberIsPresent = false;
			}
		}
	}
	
	//set card numbers
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
