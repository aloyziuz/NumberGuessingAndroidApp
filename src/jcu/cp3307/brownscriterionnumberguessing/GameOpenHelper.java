package jcu.cp3307.brownscriterionnumberguessing;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class GameOpenHelper extends SQLiteOpenHelper {

	public GameOpenHelper(Context context){
		super(context, "GameData", null, 1);
	}

	@Override
	public void onCreate(SQLiteDatabase arg0) {
		setup(arg0);
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		setup(arg0);
	}
	
	private void setup(SQLiteDatabase database){
		String query = "create table if not exists GameDB (Name TEXT, Score INTEGER)";
		database.execSQL(query);
	}
}
