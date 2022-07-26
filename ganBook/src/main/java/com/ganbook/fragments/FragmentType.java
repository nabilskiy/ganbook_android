package com.ganbook.fragments;

public enum FragmentType {
	Main(MainScreenFragment.class), 
	Show_Gallery(SingleAlbumFragment.class),
	Single_Image(SingleImageFragment.class),
//	Add_Kid(AddKidFragment.class),
	Single_Message(SingleMessageFragment.class),
	Album_List(AlbumListFragment.class),
	Contact_Profile(SingleContactProfileFragement.class); 
	
	public final Class<?> clazz;
	
	private FragmentType(Class<?> clazz) {
		this.clazz = clazz;
	}

}
