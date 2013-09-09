package uq.deco7381.runspyrun.model;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class ListAdapter_equipment extends BaseAdapter {

	private LayoutInflater adapterLayoutInflater;
	private ArrayList<Integer> arrayList;
	
	public ListAdapter_equipment(Context c) {
		// TODO Auto-generated constructor stub
		adapterLayoutInflater = LayoutInflater.from(c);
		arrayList = new ArrayList<Integer>();
	}

	public void addItem(int position){
		 arrayList.add(position);
	    this.notifyDataSetChanged();
	}
	
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return arrayList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		return null;
	}

}

class TagView{
    Button button;
    ImageView image;
    TextView text;

    public TagView(Button button,ImageView image, TextView text){
        this.button = button;
        this.image = image;
        this.text = text;

    }

}
