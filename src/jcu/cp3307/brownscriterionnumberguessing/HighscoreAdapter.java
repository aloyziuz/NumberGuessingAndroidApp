package jcu.cp3307.brownscriterionnumberguessing;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class HighscoreAdapter extends BaseAdapter {
	Context context;
	List<Highscore> highscoreList;
	private static LayoutInflater inflater;
	
	public HighscoreAdapter(Context context, List<Highscore> highscoreList){
		this.context = context;
		this.highscoreList = highscoreList;
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	@Override
	public int getCount() {
		return highscoreList.size();
	}

	@Override
	public Object getItem(int position) {
		return highscoreList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View vi = convertView;
        if (vi == null){
            vi = inflater.inflate(R.layout.custom_listview_row, null);
        }
        
        TextView nameLbl = (TextView)vi.findViewById(R.id.nameLbl);
        TextView scoreLbl = (TextView)vi.findViewById(R.id.scoreLbl);
        Highscore highscore = highscoreList.get(position);
        nameLbl.setText(highscore.name);
        scoreLbl.setText(highscore.score + "");
        
		return vi;
	}

}
