package com.ganbook.ui;

import com.ganbook.app.MyApp;
import com.ganbook.communication.datamodel.ClassDetails_2;
import com.ganbook.user.NameAndId;
import com.ganbook.user.User;
import com.project.ganim.R;

public class NavDrawerItem {
	
	private static final int SPECIAL_NONE = 0;
	public static final int SPECIAL_ADDCLASS = 1;
	public static final int SPECIAL_INVITEPARENT = 2;
	public static final int SPECIAL_SETTINGS = 3;
	public static final int SPECIAL_PTA = 4;
	public static final int SPECIAL_FAVORITES = 5;
	public static final int SPECIAL_PICKDROP = 6;


	private String name;
//	private String text;
	public final NameAndId elem;
	private final int icon;
	private final int specialTypeIndex ;
	private final int position;
	private int badgeCount = 0;

	public int getSpecailTypeIndex()
	{
		return specialTypeIndex;
	}

	public NavDrawerItem(int position, String name, NameAndId elem, int icon) {
		this(position, name, elem, icon, SPECIAL_NONE);
	}
	
	private NavDrawerItem(int position, String name, NameAndId elem, int icon, int specialInd) {
		this.position = position;
		this.name = name;
		this.elem = elem;
		this.icon = icon;
		this.specialTypeIndex = specialInd;
	}
	
	public String get_name() {
		return name;
	}

	public void set_name(String _name) {
		this.name = _name;
	}

//	public String get_text() {
//		return text;
//	}
//
//	public void set_text(String _text) {
//		this.text = _text;
//	}

	public int getIcon() {
		return icon;
	}

	public int getPosition() { return position; }

	public int getBadgeCount() { return  badgeCount; }
	public void setBadgeCount(int newVal) { badgeCount = newVal; }

	public static NavDrawerItem createFavoritesItem() {
		return new NavDrawerItem(-1, null, null, -1, SPECIAL_FAVORITES);
	}

	public static NavDrawerItem createAddClassItem() {
		return new NavDrawerItem(-1, null, null, -1, SPECIAL_ADDCLASS);
	}

	public static NavDrawerItem createInviteParentItem() {
		return new NavDrawerItem(-1, null, null, -1, SPECIAL_INVITEPARENT);
	}
	
	public static NavDrawerItem createPtaItem() {
		return new NavDrawerItem(-1, null, null, -1, SPECIAL_PTA);
	}
	
	public static NavDrawerItem createSettingsItem() {
		return new NavDrawerItem(-1, null, null, -1, SPECIAL_SETTINGS);
	}

	public static NavDrawerItem createPickDropItem() {
		return new NavDrawerItem(-1, null, null, -1, SPECIAL_PICKDROP);
	}

	public boolean isSpecialType() {
		return specialTypeIndex != SPECIAL_NONE; 
	}

	public int getSpecialTextResId() {
		switch (specialTypeIndex) {
		case SPECIAL_ADDCLASS:
			if (User.isParent()) {
                return R.string.add_kid;
			} else {
				return R.string.add_class_sidemenu;
			}
		case SPECIAL_PTA:
			return R.string.assign_pta;
		case SPECIAL_INVITEPARENT:
			return R.string.invite_parents;
		case SPECIAL_SETTINGS:
			return R.string.settings;
		case SPECIAL_FAVORITES:
			return R.string.favorites;
		case SPECIAL_PICKDROP:
			return R.string.pickup_drop_hours_text;
		}
		throw new RuntimeException();
	}

	public int getSpecialTypeIndex() {
		return specialTypeIndex;
	}

	public String getLowerDrawerLine() {
		if (User.isTeacher()) {
			ClassDetails_2 aClass = User.current.getClass(position);
			if (aClass==null) {
				return "";
			}
			String kidCount = aClass.class_num_kids;
			String parentCount = aClass.class_num_parents;
			String kids = MyApp.context.getResources().getString(R.string.kids);
			String parents = MyApp.context.getResources().getString(R.string.parents);
			return kidCount + " " + kids + ", " + parentCount + " " + parents;   
		}
		else {
			//ggggFix set lower drawer line for parent
		}
		return null;
	}

}
