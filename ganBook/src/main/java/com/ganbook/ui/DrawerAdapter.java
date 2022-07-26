package com.ganbook.ui;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ganbook.activities.MainActivity;
import com.ganbook.communication.json.GetUserKids_Response;
import com.ganbook.universalloader.UILManager;
import com.ganbook.user.User;
import com.ganbook.utils.ActiveUtils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.project.ganim.R;

import java.util.ArrayList;

public class DrawerAdapter extends BaseAdapter {
	
	private static final int SELECTED_BG_COLOR = Color.parseColor("#CFCED3");
	private static final int NOT_SELECTED_BG_COLOR = Color.parseColor("#DFDEE3");
	
	private Context context;
	private ArrayList<NavDrawerItem> navDrawerItems;
	
	private final int Add_Class_Ind;
	
	private ViewHolder holder = null;
	private DisplayImageOptions uilOptions;
		
	
	@Override
	public int getViewTypeCount() {
		return 2;
	}
	
	@Override
	public int getItemViewType(int position) {
		if (position < Add_Class_Ind) {
			return 0;
		} else {
			return 1; // Add Class, Invite Parents, Settings
		}
	}

	public DrawerAdapter(Context ctx, ArrayList<NavDrawerItem> _navDrawerItems) {
		this.context = ctx;
		this.navDrawerItems = (ArrayList<NavDrawerItem>) _navDrawerItems.clone(); // must be cloned!!
		int numKids = navDrawerItems.size();
		Add_Class_Ind = numKids;

	}

	@Override
	public int getCount() {
		return navDrawerItems.size();
	}

	@Override
	public Object getItem(int position) {
		return navDrawerItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	static class ViewHolder {
	    RelativeLayout drawer_item;
		TextView upper_text;
		TextView mid_text;
		TextView lower_text;
		TextView buttom_text;
		CircleImageView image;
		RelativeLayout notify_layout;
		TextView num;
	};


	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
 		final NavDrawerItem selItem = navDrawerItems.get(position);
		if (selItem.isSpecialType()) {
			return getView_special(position, convertView, parent, selItem.getSpecialTypeIndex());
		} else {
			if (User.isParent()) {
				return getView_kidRecord(position, convertView, parent);
			} else {
				return getView_classRecord(position, convertView, parent);
			}
		}
	}
	
	private View getView_special(final int position, View convertView, ViewGroup parent, int specialTypeIndex) {
 		final NavDrawerItem selItem = navDrawerItems.get(position);
		View row = convertView;
		if (row == null) {
			LayoutInflater mInflater = (LayoutInflater)context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
			row = mInflater.inflate(R.layout.drawer_special_item, null); 
		}
		TextView caption = (TextView) row.findViewById(R.id.caption);
		caption.setText(selItem.getSpecialTextResId());

		ImageView fav_icon = (ImageView) row.findViewById(R.id.fav_iv);
		if(specialTypeIndex == NavDrawerItem.SPECIAL_FAVORITES)
		{
			fav_icon.setVisibility(View.VISIBLE);
		}
		else
		{
			fav_icon.setVisibility(View.GONE);
		}

		return row;
	}
	
	private View getView_classRecord(final int position, View convertView, ViewGroup parent) {
		View row = convertView;
		if (row == null) {
			LayoutInflater mInflater = (LayoutInflater)context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
			row = mInflater.inflate(R.layout.drawer_class_elem, null); 
			holder = new ViewHolder();
			holder.upper_text = (TextView) row.findViewById(R.id.upper_text);
			holder.lower_text = (TextView) row.findViewById(R.id.lower_text);
			holder.image = (CircleImageView) row.findViewById(R.id.item_image);
			holder.drawer_item  = (RelativeLayout) row.findViewById(R.id.drawer_item);
			holder.notify_layout  = (RelativeLayout) row.findViewById(R.id.notify_layout);
			holder.num  = (TextView) row.findViewById(R.id.num);

			row.setTag(holder);
		} 
		else {  
			holder = (ViewHolder) row.getTag(); 
		}
		NavDrawerItem curItem = navDrawerItems.get(position);

		holder.upper_text.setText(curItem.get_name());
		holder.lower_text.setText(curItem.getLowerDrawerLine());

		holder.image.setImageResource(R.drawable.new_class_icon);
		
		final boolean selected = (position == User.current.getSelectedItemIndex());
		if (selected) { 
			holder.drawer_item.setBackgroundColor(SELECTED_BG_COLOR);
		} else {			
			holder.drawer_item.setBackgroundColor(NOT_SELECTED_BG_COLOR);
		}
		
		String pending_parents = User.current.getClassPendingParents(curItem.elem.id);
		if(pending_parents != null && Integer.valueOf(pending_parents) > 0)
		{
			holder.notify_layout.setVisibility(View.VISIBLE);
			holder.num.setText(pending_parents);
		}
		else
		{
			holder.notify_layout.setVisibility(View.GONE);
		}
		
		return row;
	}

	private View getView_kidRecord(final int position, View convertView, ViewGroup parent) {
		View row = convertView;
		if (row == null) {
			LayoutInflater mInflater = (LayoutInflater)context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
			row = mInflater.inflate(R.layout.drawer_kid_elem, null); 
			holder = new ViewHolder();
			holder.upper_text = (TextView) row.findViewById(R.id.upper_text);
			holder.mid_text = (TextView) row.findViewById(R.id.mid_text);
			holder.lower_text = (TextView) row.findViewById(R.id.lower_text);
			holder.buttom_text = (TextView) row.findViewById(R.id.buttom_text);
			holder.image = (CircleImageView) row.findViewById(R.id.item_image);
			holder.drawer_item  = (RelativeLayout) row.findViewById(R.id.drawer_item);
			holder.notify_layout  = (RelativeLayout) row.findViewById(R.id.notify_layout);
			holder.num  = (TextView) row.findViewById(R.id.num);
			row.setTag(holder); 
		} 
		else {  
			holder = (ViewHolder) row.getTag(); 
		}
		NavDrawerItem curItem = navDrawerItems.get(position);
		String kid_id = curItem.elem.id;
		final GetUserKids_Response kidRecord = User.current.getKidById(kid_id);
		
		String gan_name = kidRecord.gan_name;
		String class_name = kidRecord.class_name;
		boolean isBoy = kidRecord.isBoy();
		String kid_pic = kidRecord.kid_pic;
		
		holder.upper_text.setText(curItem.get_name());
		if(!ActiveUtils.DISCONNECTED.equals(kidRecord.kid_active)) {
			holder.mid_text.setText(gan_name);
			holder.lower_text.setText(class_name);
		}

		if(ActiveUtils.WAITING_APPROVAL.equals(kidRecord.kid_active))
		{
			holder.buttom_text.setText(context.getString(R.string.waiting_for_approval));
			holder.buttom_text.setVisibility(View.VISIBLE);
		}
		else
		{
			holder.buttom_text.setVisibility(View.GONE);
		}
		
		if (kid_pic != null && kid_pic.length() > 3) {
			String url = ContactListAdapter.kidPicToUrl(kid_pic);

			if (uilOptions==null) {
				uilOptions = UILManager.createDefaultDisplayOpyions(R.drawable.girldefault);
			} 
			UILManager.imageLoader.displayImage(url, holder.image, uilOptions); 
			
		}
		else if (isBoy) {
			holder.image.setImageResource(R.drawable.boydefault);
		} 
		else {
			holder.image.setImageResource(R.drawable.girldefault);
		}
				
		final boolean selected = (position == User.current.getSelectedItemIndex());
		if (selected) { 
			holder.drawer_item.setBackgroundColor(SELECTED_BG_COLOR);
		} else {			
			holder.drawer_item.setBackgroundColor(Color.TRANSPARENT);
		}
		
		int unseen_photos = 0;
		if(kidRecord.unseen_photos != null)
		{
			unseen_photos = Integer.valueOf(kidRecord.unseen_photos);
		}
		
		int unread_message = 0;
		if(kidRecord.unread_messages != null)
		{
			unread_message = Integer.valueOf(kidRecord.unread_messages);
		}
		
		if((unread_message + unseen_photos)>0)
		{
			holder.notify_layout.setVisibility(View.VISIBLE);
			holder.num.setText(String.valueOf((unread_message + unseen_photos)));
		}
		else
		{
			holder.notify_layout.setVisibility(View.GONE);
		}
		
		return row;
	}

	private View prior_selected;
	
	public void markAsSelected(View view, int position) {
		view.setSelected(true);  
		if (prior_selected != null) {
			prior_selected.setSelected(false); 
			prior_selected.setBackgroundColor(Color.TRANSPARENT);
		}
		view.setBackgroundColor(SELECTED_BG_COLOR);
		prior_selected = view;
		notifyDataSetChanged(); 
	}

	public boolean handleSpecialType(int position) {
		final NavDrawerItem selItem = navDrawerItems.get(position);
		if (!selItem.isSpecialType()) {
			return false;
		}		
		switch (selItem.getSpecialTypeIndex()) { 	
		case NavDrawerItem.SPECIAL_ADDCLASS: 
			MainActivity.openAddClassScreen();
			break;
		case NavDrawerItem.SPECIAL_INVITEPARENT:
			MainActivity.InviteParent();
			break;
		case NavDrawerItem.SPECIAL_SETTINGS:		
			MainActivity.openSettingsScreen();
			break;
		case NavDrawerItem.SPECIAL_PTA:
			MainActivity.openPtaScreen();
			break;
		case NavDrawerItem.SPECIAL_FAVORITES:
			MainActivity.openFavoritesScreen();
			break;
		case NavDrawerItem.SPECIAL_PICKDROP:
			MainActivity.openPickDrop();
			break;
		}		
		return true; // is special
	}
	
	public void setNavDrawerItems(ArrayList<NavDrawerItem> navDrawerItems)
	{
		this.navDrawerItems = (ArrayList<NavDrawerItem>) navDrawerItems.clone(); 
	}
	
	public void addAll(ArrayList<NavDrawerItem> _navDrawerItems) {
//	    if(navDrawerItems==null){
	    	navDrawerItems = new ArrayList<NavDrawerItem>();
//	    }
	   	navDrawerItems.addAll(_navDrawerItems); 
	    notifyDataSetChanged();
	}

//	public void setLocationButtonEnabled(int position, boolean enabled)
//	{
//		navDrawerItems.get(position);
//	}


}
               