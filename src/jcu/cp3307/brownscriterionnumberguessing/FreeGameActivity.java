package jcu.cp3307.brownscriterionnumberguessing;

import java.util.List;

import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.TextView;
import android.support.v4.app.NavUtils;
import android.annotation.TargetApi;
import android.graphics.Typeface;
import android.os.Build;

public class FreeGameActivity extends Activity {
	private ArrayAdapter<Integer> numbers;
	private GridView gridview;
	private String cardType;
	private String range;
	private List<Card> deck;
	private int currentCard;
	private int noOfCards;
	private float begin, end;
	private int minDistance;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_free_game);
		// Show the Up button in the action bar.
		setupActionBar();
		
		//change font
		TextView textview = (TextView)findViewById(R.id.freeCardNumberLbl);
		setFont(textview);
		
		//get extras
		Bundle extras = this.getIntent().getExtras();
		if(extras != null){
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
						}
						//right to left swipe
						else if(begin > end && Math.abs(deltaX) >= minDistance){
							NextCard();
							SetCardNumberLabel();
						}
						break;
				}
				return false;
			}
		});
}

	/**
	 * Set up the {@link android.app.ActionBar}, if the API is available.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.free_game, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
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
	
	private void SetCardNumberLabel(){
		TextView cardNoLbl = (TextView)findViewById(R.id.freeCardNumberLbl);
		cardNoLbl.setText("Card " + (currentCard+1) + " out of " + (noOfCards+1));
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
	
	private void setFont(TextView textView) {
	    Typeface tf = Typeface.createFromAsset(textView.getContext().getAssets(), "DK Crayon Crumble.ttf");
	    textView.setTypeface(tf);
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
