package jcu.cp3307.brownscriterionnumberguessing;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.Menu;
import android.widget.ListView;

public class HighscoreActivity extends Activity {
	List<Highscore> highscoreList;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_highscore);
		
		highscoreList = new ArrayList<Highscore>();
		reloadHighscoreList();
		
		ListView highscoreListView = (ListView)findViewById(R.id.highscoreListView);
		HighscoreAdapter adapter = new HighscoreAdapter(this, highscoreList);
		highscoreListView.setAdapter(adapter);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		reloadHighscoreList();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.highscore, menu);
		return true;
	}
	
	private void reloadHighscoreList(){
		highscoreList.clear();
		GameOpenHelper gameopenhelper = new GameOpenHelper(this);
		SQLiteDatabase db = gameopenhelper.getReadableDatabase();
		Cursor cursor = db.query(true, "GameDB", null, null, null, null, null, "Score DESC", "10");
		while(cursor.moveToNext()){
			highscoreList.add(new Highscore(cursor.getString(0), cursor.getInt(1)));
		}
	}

}
