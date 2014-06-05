package jcu.cp3307.brownscriterionnumberguessing;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class MainActivity extends Activity {
	
	private boolean isShown;
	private String spinnerArray[];
	private String rangeArray[];
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		//change fonts
		TextView textview = (TextView)findViewById(R.id.welcomeTxt);
		setFont(textview);
		textview = (TextView)findViewById(R.id.rangeTxt);
		setFont(textview);
		textview = (TextView)findViewById(R.id.cardtypeTxt);
		setFont(textview);
		textview = (TextView)findViewById(R.id.numberPresentLbl);
		setFont(textview);
		
		Button btn = (Button)findViewById(R.id.playBtn);
		setFont(btn);
		btn = (Button)findViewById(R.id.highscoreBtn);
		setFont(btn);
		btn = (Button)findViewById(R.id.appBtn);
		setFont(btn);
		btn = (Button)findViewById(R.id.childBtn);
		setFont(btn);
		btn = (Button)findViewById(R.id.freeBtn);
		setFont(btn);
		
		EditText edittext = (EditText)findViewById(R.id.nameTxt);
		setFont(edittext);
		edittext = (EditText)findViewById(R.id.lowerBoundTxt);
		setFont(edittext);
		edittext = (EditText)findViewById(R.id.upperBoundTxt);
		setFont(edittext);
		
		isShown = false;
		
		//populate card type spinner
		spinnerArray = new String[3];
		spinnerArray[0] = "Binary";
		spinnerArray[1] = "Fibonacci";
		spinnerArray[2] = "Prime";
		Spinner cardspinner = (Spinner) findViewById(R.id.cardTypeSpinner);
		ArrayAdapter<String> cardAdapter = new ArrayAdapter<String>(this, R.layout.custom_spinner_item, spinnerArray);
		cardspinner.setAdapter(cardAdapter);
		
		//populate range spinner
		rangeArray = new String[4];
		rangeArray[0] = "1-10";
		rangeArray[1] = "1-20";
		rangeArray[2] = "1-50";
		rangeArray[3] = "Dynamic";
		Spinner rangespinner = (Spinner) findViewById(R.id.rangeSpinner);
		ArrayAdapter<String> rangeAdapter = new ArrayAdapter<String>(this, R.layout.custom_spinner_item, rangeArray);
		rangespinner.setAdapter(rangeAdapter);
		
		//add listener to range spinner
		//triggers when an item is selected on range spinner
		rangespinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parentView, View selectedItemView,
					int position, long id) {
				//if dynamic range is selected, show the textfields. else hide the textfields
				if(position == 3){
					findViewById(R.id.numberPresentLbl).setVisibility(View.VISIBLE);
					findViewById(R.id.lowerBoundTxt).setVisibility(View.VISIBLE);
					findViewById(R.id.upperBoundTxt).setVisibility(View.VISIBLE);
				}
				else{
					findViewById(R.id.numberPresentLbl).setVisibility(View.INVISIBLE);
					findViewById(R.id.lowerBoundTxt).setVisibility(View.INVISIBLE);
					findViewById(R.id.upperBoundTxt).setVisibility(View.INVISIBLE);
				}
			}
			
			@Override
			public void onNothingSelected(AdapterView<?> parentView) {
				//empty
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	//toggle sub-menu
	public void PlayClicked(View view){
		playSfx("button_click");
		
		Button childBtn = (Button) findViewById(R.id.childBtn);
		Button appBtn = (Button) findViewById(R.id.appBtn);
		Button freeBtn = (Button) findViewById(R.id.freeBtn);
		
		if(!isShown){
			childBtn.setVisibility(View.VISIBLE);
			appBtn.setVisibility(View.VISIBLE);
			freeBtn.setVisibility(View.VISIBLE);
			
			isShown = true;
		}
		else{
			childBtn.setVisibility(View.INVISIBLE);
			appBtn.setVisibility(View.INVISIBLE);
			freeBtn.setVisibility(View.INVISIBLE);
			
			isShown = false;
		}
	}
	
	public void AppGuessClicked(View view){
		playSfx("button_click");
		
		ShowErrors();

		String[] data = GetData();
		String nameStr = data[0];
		String cardTypeStr = data[1];
		String rangeStr = data[2];
		
		if(ValidateValues(nameStr, rangeStr)){
			Intent intent = new Intent(this, Game_Activity.class);
			intent.putExtra("Name", nameStr);
			intent.putExtra("CardType", cardTypeStr);
			intent.putExtra("Range", rangeStr);
			startActivity(intent);
		}
	}
	
	public void ChildGuessClicked(View view){
		playSfx("button_click");
		
		ShowErrors();
		
		String[] data = GetData();
		String nameStr = data[0];
		String cardTypeStr = data[1];
		String rangeStr = data[2];
		
		if(ValidateValues(nameStr, rangeStr)){
			Intent intent = new Intent(this, ChildGameActivity.class);
			intent.putExtra("Name", nameStr);
			intent.putExtra("CardType", cardTypeStr);
			intent.putExtra("Range", rangeStr);
			startActivity(intent);
		}
	}
	
	public void FreePlayClicked(View view){
		playSfx("button_click");
		
		ShowErrors();
		
		String[] data = GetData();
		String nameStr = data[0];
		String cardTypeStr = data[1];
		String rangeStr = data[2];
		
		if(ValidateValues(nameStr, rangeStr)){
			Intent intent = new Intent(this, FreeGameActivity.class);
			intent.putExtra("CardType", cardTypeStr);
			intent.putExtra("Range", rangeStr);
			startActivity(intent);
		}
	}
	
	public void highscoreClicked(View view){
		playSfx("button_click");
		Intent intent = new Intent(this, HighscoreActivity.class);
		startActivity(intent);
	}
	
	private void ShowErrors(){
		//since error can only happen in name and dynamic range inputs, I only check those
		Spinner rangeSpinner = (Spinner) findViewById(R.id.rangeSpinner);
		EditText nameTxt = (EditText) findViewById(R.id.nameTxt);
		EditText lowerBoundTxt = (EditText)findViewById(R.id.lowerBoundTxt);
		EditText upperBoundTxt = (EditText)findViewById(R.id.upperBoundTxt);
		
		String nameStr = nameTxt.getText().toString();
		String lowerBoundStr = lowerBoundTxt.getText().toString();
		String upperBoundStr = upperBoundTxt.getText().toString();
		
		//show name errors
		if(nameStr.equals("")){
			nameTxt.setError("Please Fill in Your Name");
		}
		
		//show dynamic range error
		if(rangeSpinner.getSelectedItem().toString().equals("Dynamic")){
			//show lower bound error
			if(lowerBoundStr.equals("")){
				lowerBoundTxt.setError("Lower Range Cannot Be Empty");
			}
			else if(!isNumber(lowerBoundStr)){
				lowerBoundTxt.setError("Should Be Numbers");
			}
			else if(Integer.parseInt(lowerBoundStr) > 20 || Integer.parseInt(lowerBoundStr) < 1){
				lowerBoundTxt.setError("Should be between 1-20");
			}
			
			//show upper bound error
			if(upperBoundStr.equals("")){
				upperBoundTxt.setError("Upper Range Cannot Be Empty");
			}
			else if(!isNumber(upperBoundStr)){
				upperBoundTxt.setError("Should Be Numbers");
			}
			else if(Integer.parseInt(upperBoundStr) > 100){
				upperBoundTxt.setError("Should be no more than 100");
			}
			else if(isNumber(lowerBoundStr) && Integer.parseInt(lowerBoundStr) >= Integer.parseInt(upperBoundStr)){
				upperBoundTxt.setError("Upper Range Should be Bigger than Lower Range");
			}
		}
	}
	
	//retrieve data from spinners and textfields
	private String[] GetData(){
		Spinner cardtypeSpinner = (Spinner) findViewById(R.id.cardTypeSpinner);
		Spinner rangeSpinner = (Spinner) findViewById(R.id.rangeSpinner);
		EditText nameTxt = (EditText) findViewById(R.id.nameTxt);
		
		String cardTypeStr = cardtypeSpinner.getSelectedItem().toString();
		String nameStr = nameTxt.getText().toString();
		String rangeStr;
		if(rangeSpinner.getSelectedItem().toString().equals("Dynamic")){
			EditText lowerBoundTxt = (EditText)findViewById(R.id.lowerBoundTxt);
			EditText upperBoundTxt = (EditText)findViewById(R.id.upperBoundTxt);
			rangeStr = lowerBoundTxt.getText().toString() + "-" + upperBoundTxt.getText().toString();
		}
		else{
			rangeStr = rangeSpinner.getSelectedItem().toString();
		}
		
		String[] data = new String[3];
		data[0] = nameStr;
		data[1] = cardTypeStr;
		data[2] = rangeStr;
		
		return data;
	}
	
	//check whether string is numeric
	private boolean isNumber(String str){
		try{
			Integer.parseInt(str);
		}
		catch(NumberFormatException ex){
			return false;
		}
		return true;
	}
	
	//validate the name and dynamic range fields
	private boolean ValidateValues(String name, String range){
		//lower bound regex : 1-9 or 10-19 or 20
		//upper bound regex : 1-9 or 10-99 or 100 
		if(!name.equals("") && range.matches("([1-9]|1[0-9]|20)-([1-9]|[1-9][0-9]|100)")){
			String[] bounds = range.split("-");
			if(isNumber(bounds[0]) && isNumber(bounds[1])){
				int lower = Integer.parseInt(bounds[0]);
				int upper = Integer.parseInt(bounds[1]);
				if(lower >= 1 && lower <= 20 && upper <= 100 && lower < upper){
					return true;
				}
			}
		}
		return false;
	}
	
	private void setFont(TextView textView) {
	    Typeface tf = Typeface.createFromAsset(textView.getContext().getAssets(), "DK Crayon Crumble.ttf");
	    textView.setTypeface(tf);
	}
	
	private void setFont(Button button) {
	    Typeface tf = Typeface.createFromAsset(button.getContext().getAssets(), "scribble box demo.ttf");
	    button.setTypeface(tf);
	}
	
	private void setFont(EditText button) {
	    Typeface tf = Typeface.createFromAsset(button.getContext().getAssets(), "DK Crayon Crumble.ttf");
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
