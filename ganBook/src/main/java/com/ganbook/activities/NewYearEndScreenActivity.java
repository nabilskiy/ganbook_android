package com.ganbook.activities;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import com.ganbook.activities.NewYearActivity1._Class;
import com.ganbook.app.MyApp;
import com.project.ganim.R;

import java.util.ArrayList;
import java.util.HashMap;

public class NewYearEndScreenActivity extends BaseAppCompatActivity {

	ListView kids_list;
	HashMap<String, _Class> chosen_kids;
	ArrayList<Item> classes;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_end_new_year);
//		setActionBar(R.layout.actionbar_classname);

		chosen_kids = new HashMap<String, NewYearActivity1._Class>(NewYearActivity1.chosen_kids);

		mapToArrayList();

		kids_list = (ListView) findViewById(R.id.kids_list);
		kids_list.setAdapter(new EndScreenAdapter());

	}

	private class EndScreenAdapter extends ArrayAdapter<Item> {
		private LayoutInflater mInflater;
		
		public EndScreenAdapter() {
	    	super(MyApp.context, 0, classes);
	        mInflater = LayoutInflater.from(MyApp.context);
	    }

	    @Override
	    public int getViewTypeCount() {
	        return RowType.values().length;

	    }

	    @Override
	    public int getItemViewType(int position) {
	        return getItem(position).getViewType();
	    }

	    @Override
	    public View getView(int position, View convertView, ViewGroup parent) {
	        return getItem(position).getView(mInflater, convertView, parent);
	    }
	    
	}

	public void mapToArrayList()
	{
		HashMap<String, ArrayList<_Class>> classesKidsMap = new HashMap<String, ArrayList<_Class>>();
		HashMap<String, String> classesIdName = new HashMap<String, String>();

		classes = new ArrayList<Item>();

		if( chosen_kids == null)
		{
			return;
		}

		//    	List<String> list = new ArrayList<String>();
		//    	list.addAll(chosen_kids.keySet());
		//    	

		for (String kid_id : chosen_kids.keySet()) 
		{
			_Class current = chosen_kids.get(kid_id);

			ArrayList<_Class> kids_class =  classesKidsMap.get(current.class_id);
			if(kids_class == null)
			{
				kids_class = new ArrayList<NewYearActivity1._Class>();
			}
			kids_class.add(current);
			classesKidsMap.put(current.class_id, kids_class);

			classesIdName.put(current.class_id, current.class_name);
		}

		for (String class_id : classesKidsMap.keySet()) 
		{
			Item header = new HeaderItem(classesIdName.get(class_id));
			classes.add(header);

			ArrayList<_Class> kids_class = classesKidsMap.get(class_id);

			//        	Collections.sort(list, new Comparator<String>() {
			//    	    public int compare(String o1, String o2) {
			//	    	        Integer i1 = Integer.parseInt(o1);
			//	    	        Integer i2 = Integer.parseInt(o2);
			//	    	        return (i1 > i2 ? -1 : (i1 == i2 ? 0 : 1));
			//	    	    }
			//	    	});

			for (_Class kid : kids_class) {
				Item kidItem = new  KidItem(kid.kid_name, kid.kid_pic, kid.def_img);
				classes.add(kidItem);
			}
		}

	}

	public enum RowType 
	{
		KID_ITEM, HEADER_ITEM
	}

	public static class KidItem implements Item 
	{
		public final String         kid_name;
		public final String         kid_pic;
		public final int         def_img;

		public KidItem(String kid_name, String kid_pic, int def_img) {
			this.kid_name = kid_name;
			this.kid_pic = kid_pic;
			this.def_img = def_img;
		}

		@Override
		public int getViewType() {
			return RowType.KID_ITEM.ordinal();
		}

		@Override
		public View getView(LayoutInflater inflater, View convertView, ViewGroup parent) {
			View view;
			if (convertView == null) {
				view = (View) inflater.inflate(R.layout.newyear_row_item, parent, false);
				// Do some initialization
			} else {
				view = convertView;
			}

			CheckBox cb = (CheckBox) view.findViewById(R.id.check);
			cb.setVisibility(View.GONE);

			TextView kid_name_tv = (TextView) view.findViewById(R.id.kid_name);  
			kid_name_tv.setText(kid_name);

//			CircleImageView image = (CircleImageView) view.findViewById(R.id.num_viewed);
//			NewYearActivity1.getKidPicture(kid_pic, image, def_img);


			return view;
		}

	}

	public static class HeaderItem implements Item 
	{
		public final String         class_name;

		public HeaderItem(String class_name) {
			this.class_name = class_name;
		}

		@Override
		public int getViewType() {
			return RowType.HEADER_ITEM.ordinal();
		}

		@Override
		public View getView(LayoutInflater inflater, View convertView, ViewGroup parent) {
			View view;
			if (convertView == null) {
				view = (View) inflater.inflate(R.layout.item_new_year_header, parent, false);
				// Do some initialization
			} else {
				view = convertView;
			}

			TextView header = (TextView) view.findViewById(R.id.header);
			header.setText(class_name);

			return view;
		}
	}

	public interface Item 
	{
		public int getViewType();
		public View getView(LayoutInflater inflater, View convertView, ViewGroup parent);
	}

}
