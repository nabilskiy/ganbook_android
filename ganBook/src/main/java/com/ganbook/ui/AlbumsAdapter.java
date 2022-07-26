package com.ganbook.ui;

import android.animation.Animator;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.ganbook.activities.CommentsActivity;
import com.ganbook.activities.MainActivity;
import com.ganbook.app.MyApp;
import com.ganbook.communication.json.HistoryDetails;
import com.ganbook.communication.json.getalbum_response;
import com.ganbook.communication.json.transmitter.JsonTransmitter;
import com.ganbook.fragments.AlbumListFragment;
import com.ganbook.ui.AlbumsAdapter.Item;
import com.ganbook.universalloader.UILManager;
import com.ganbook.user.User;
import com.ganbook.utils.CurrentYear;
import com.ganbook.utils.DateFormatter;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;
import com.project.ganim.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;



public class AlbumsAdapter extends ArrayAdapter<Item> {
    private LayoutInflater mInflater;
    private static MainActivity activity;
    private static boolean see_more_created = false;
    private static DisplayImageOptions defaultOptions = UILManager.createDefaultDisplayOpyions(R.drawable.album_off);


    public AlbumsAdapter(Context context, MainActivity activity) {
    	super(context, 0);
        
        mInflater = LayoutInflater.from(context);
        AlbumsAdapter.activity = activity;
        see_more_created = false;

    }
    

    public AlbumsAdapter(Context context, HashMap<String, ArrayList<getalbum_response>> map, MainActivity activity) {
    	super(context, 0, mapToArrayList(map));
        
        mInflater = LayoutInflater.from(context);
        AlbumsAdapter.activity = activity;
        see_more_created = false;
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
    
    public static ArrayList<Item> mapToArrayList(HashMap<String, ArrayList<getalbum_response>> map)
    {
    	ArrayList<Item> albums = new ArrayList<Item>();
    	
    	if(map == null || map.isEmpty())
    	{
    		return albums;
    	}
    	
    	List<String> list = new ArrayList<String>();
    	list.addAll(map.keySet());
    	
    	Collections.sort(list, new Comparator<String>() {
    	    public int compare(String o1, String o2) {
    	        Integer i1 = Integer.parseInt(o1);
    	        Integer i2 = Integer.parseInt(o2);
    	        return (i1 > i2 ? -1 : (i1 == i2 ? 0 : 1));
    	    }
    	});
    	
    	for (String year : list) 
    	{
    		String class_name="";
    		String gan_name="";
    		
    		if(!year.equals(CurrentYear.get()))
            {
    			if(User.isParent())
    			{
	    	        HistoryDetails details = User.current.getCurrentKidHistoryByYear(year);
	    	        class_name = details.class_name;
	            	gan_name = details.gan_name;
    			}
    			else
    			{
    				class_name = User.current.getCurrentClassName();
    				gan_name = User.current.getCurrentGanName();
    			}
            }
            else
            {
            	class_name = User.current.getCurrentClassName();
            	gan_name = User.current.getCurrentGanName();
            }
    		
    		Item yearItem = new HeaderItem(year,gan_name,class_name);
    		
    		albums.add(yearItem);
    		
    		ArrayList<getalbum_response> albumsArr = map.get(year);

            if(albumsArr.isEmpty() && year.equals(CurrentYear.get())) {
                Item textItem = new TextItem();
                albums.add(textItem);
            }
            else {
                for (getalbum_response album : albumsArr) {
                    if (User.isTeacher() || (User.isParent() && Integer.valueOf(album.pic_count) > 0) || (User.isParent() && Integer.valueOf(album.videos_count) > 0)) {
                        Item albumItem = new AlbumItem(year, album.album_id, album.album_name, album.album_description, album.album_date, album.album_views, album.pic_count, album.picture, album.videos_count, album.unseen_photos, album.user_album_like, album.album_likes, album.album_comments);
                        albums.add(albumItem);
                    }
                }
            }
    	}
    	 
    	return albums;
    }
    
    public enum RowType 
    {
    	ALBUM_ITEM, HEADER_ITEM, TEXT_ITEM
    }
    
    public static class AlbumItem implements Item 
    {
    	public final String         year;
    	public final String         album_id;
    	public final String         album_name;
        public final String         album_date;
        public final String         album_views;
        public final String         pic_count;
        public final String         album_image;
        public final String         video_count;
        public final String         unseen_photos;
        public final boolean         user_album_like;
        public String         likes;
        public final String         album_comments;
        public final String album_description;

        public AlbumItem(String year, String album_id, String album_name, String album_description, String album_date, String album_views,
        		String pic_count, String album_image, String video_count, String unseen_photos, boolean user_album_like, String likes, String album_comments) {
        	this.year = year;
        	this.album_id = album_id;
            this.album_name = album_name;
            this.album_date = album_date;
            this.album_views = album_views;
            this.pic_count = pic_count;
            this.album_image = album_image;
            this.video_count = video_count;
            this.unseen_photos = unseen_photos;
            this.user_album_like = user_album_like;
            this.likes = likes;
            this.album_comments = album_comments;
            this.album_description = album_description;
        }

        @Override
        public int getViewType() {
            return RowType.ALBUM_ITEM.ordinal();
        }

        class ViewHolder {
            TextView album_title_text_view,album_date_text_view,album_num_photos_text_view,album_num_videos_text_view,num_likes_text_view,num_new_text,num_views_text,num_comments;
            ImageView album_image_view,num_videos_dot,num_pics_dot,heart_active,heart_inactive,new_image,comments_image,heart_active_center;
            ViewSwitcher switcher;
            RelativeLayout notify_layout;
            LinearLayout layout_comments,layout_likes;
        };

        @Override
        public View getView(LayoutInflater inflater, View convertView, ViewGroup parent) {
//            View view;
            final ViewHolder vholder;

            if (convertView == null) {
                convertView = (View) inflater.inflate(R.layout.item_composer_new, parent, false);
                vholder = new ViewHolder();
                vholder.album_title_text_view = (TextView) convertView.findViewById(R.id.album_title);
                vholder.album_date_text_view = (TextView) convertView.findViewById(R.id.album_date);
                vholder.album_num_photos_text_view = (TextView) convertView.findViewById(R.id.num_photos);
                vholder.album_num_videos_text_view = (TextView) convertView.findViewById(R.id.num_videos);
                vholder.num_likes_text_view = (TextView) convertView.findViewById(R.id.num_likes);
                vholder.album_image_view = (ImageView) convertView.findViewById(R.id.item_image);
                vholder.num_pics_dot = (ImageView) convertView.findViewById(R.id.num_pics_dot);
                vholder.num_videos_dot = (ImageView) convertView.findViewById(R.id.num_videos_dot);
                vholder.heart_active = (ImageView) convertView.findViewById(R.id.heart_active);
                vholder.heart_inactive = (ImageView) convertView.findViewById(R.id.heart_inactive);
                vholder.switcher = (ViewSwitcher) convertView.findViewById(R.id.switcher);
                vholder.num_new_text = (TextView) convertView.findViewById(R.id.num_new_text);
                vholder.num_views_text = (TextView) convertView.findViewById(R.id.num_views_text);
                vholder.new_image = (ImageView) convertView.findViewById(R.id.new_image);
                vholder.notify_layout = (RelativeLayout) convertView.findViewById(R.id.notify_layout);
                vholder.num_comments = (TextView) convertView.findViewById(R.id.num_comments);
                vholder.comments_image = (ImageView) convertView.findViewById(R.id.comments_image);
                vholder.heart_active_center = (ImageView) convertView.findViewById(R.id.heart_active_center);
                vholder.layout_comments = (LinearLayout) convertView.findViewById(R.id.layout_comments);
                vholder.layout_likes = (LinearLayout) convertView.findViewById(R.id.layout_likes);
                vholder.layout_comments = (LinearLayout) convertView.findViewById(R.id.layout_comments);


                convertView.setTag(vholder);
            } else {
                vholder = (ViewHolder) convertView.getTag();;
            }


            vholder.album_title_text_view.setText(album_name);

//            TextView album_date_text = (TextView) view.findViewById(R.id.album_date);
//            String formatted_date = DateFormatter.getInAlbumFormat(album_date,"MMMM dd");
            vholder.album_date_text_view.setText(DateFormatter.formatStringDate(album_date,"yyyy-MM-dd hh:mm","MMMM dd"));

            vholder.num_views_text.setText(album_views);

            vholder.num_comments.setText(album_comments);

            String photosPref = activity.getResources().getString(R.string.photos_pref);
            String videosPref = activity.getResources().getString(R.string.videos_pref);
            String videoPref = activity.getResources().getString(R.string.video_pref);
            String num_photos = "";
            String num_videos = "";

            if(pic_count != null && Integer.valueOf(pic_count)>0)
            {
                vholder.num_pics_dot.setVisibility(View.VISIBLE);
                num_photos = pic_count + " " + photosPref;
            }
            else
            {
                vholder.num_pics_dot.setVisibility(View.GONE);
            }

            vholder.album_num_photos_text_view.setText(num_photos);

            if(video_count != null && Integer.valueOf(video_count)>0)
            {

                vholder.num_videos_dot.setVisibility(View.VISIBLE);

                if(Integer.valueOf(video_count)==1)
                {   
                    num_videos = videoPref;
                }
                else
                {
                    num_videos = video_count + " " + videosPref;
                }

            }
            else
            {
                vholder.num_videos_dot.setVisibility(View.GONE);
            }

            vholder.album_num_videos_text_view.setText(num_videos);

            if(likes == null)
            {
                likes = "0";
            }
            vholder.num_likes_text_view.setText(likes);


            if(unseen_photos != null && Integer.valueOf(unseen_photos) > 0)
			{
                vholder.new_image.setVisibility(View.VISIBLE);
                vholder.notify_layout.setVisibility(View.VISIBLE);
                vholder.num_new_text.setText(unseen_photos);
			}
            else
            {
                vholder.new_image.setVisibility(View.GONE);
                vholder.notify_layout.setVisibility(View.GONE);
            }
            

            String class_id="";
            String gan_id="";
            
            if(!year.equals(CurrentYear.get()) && User.isParent())
            {
    	        HistoryDetails details = User.current.getCurrentKidHistoryByYear(year);
    	        class_id = details.class_id;
            	gan_id = details.gan_id;
            }
            else
            {
            	class_id = User.current.getCurrentClassId();
            	gan_id = User.current.getCurrentGanId();
            }
            
    	    final String imageUrl = getAlbumThumUrl(album_image, album_id, class_id, gan_id);

            ImageAware imageAware = new ImageViewAware(vholder.album_image_view, false);
            UILManager.imageLoader.displayImage(imageUrl, imageAware,defaultOptions);

            if(user_album_like)
            {
                vholder.switcher.setDisplayedChild(1);
            }
            else
            {
                vholder.switcher.setDisplayedChild(0);
            }

            vholder.switcher.setTag(album_id);

            vholder.switcher.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    ViewSwitcher switcher = (ViewSwitcher) v;

                    String likes = vholder.num_likes_text_view.getText().toString();
                    Integer likes_num = Integer.valueOf(likes);

                    if (switcher.getDisplayedChild() == R.id.heart_active) {

                        switcher.showNext();

                    } else {

                        switcher.showPrevious();

                    }

                    if(switcher.getDisplayedChild() == 0)
                    {
                        likes_num--;
                        AlbumListFragment.updateAlbumLikes(album_id,false);
                    }
                    else
                    {
                        MediaPlayer mp = MediaPlayer.create(MyApp.context, R.raw.like);
                        mp.start();
                        likes_num++;
                        AlbumListFragment.updateAlbumLikes(album_id,true);

                        vholder.heart_active_center.setVisibility(View.VISIBLE);


                        vholder.heart_active_center.setAlpha(0.f);
                        vholder.heart_active_center.setScaleX(0.f);
                        vholder.heart_active_center.setScaleY(0.f);
                        vholder.heart_active_center.animate()
                                .alpha(1.f)
                                .scaleX(1.f).scaleY(1.f)
                                .setDuration(500)
                                .setListener(new Animator.AnimatorListener() {
                                    @Override
                                    public void onAnimationStart(Animator animation) {

                                    }

                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        vholder.heart_active_center.animate()
                                                .alpha(0.f)
                                                .scaleX(0.f).scaleY(0.f)
                                                .setDuration(500)
                                                .setListener(new Animator.AnimatorListener() {
                                                    @Override
                                                    public void onAnimationEnd(Animator animation) {

                                                    }

                                                    @Override
                                                    public void onAnimationStart(Animator animation) {

                                                    }

                                                    @Override
                                                    public void onAnimationCancel(Animator animation) {

                                                    }

                                                    @Override
                                                    public void onAnimationRepeat(Animator animation) {

                                                    }
                                                })
                                                .start();
                                    }

                                    @Override
                                    public void onAnimationCancel(Animator animation) {

                                    }

                                    @Override
                                    public void onAnimationRepeat(Animator animation) {

                                    }
                                })
                                .start();
                    }

                    vholder.num_likes_text_view.setText(likes_num.toString());

                    JsonTransmitter.send_updatealbumlike((String) v.getTag());
                }
            });

            vholder.comments_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(MyApp.context, CommentsActivity.class);
                    i.putExtra("album_id",album_id);
                    activity.startActivity(i);
                }
            });

            if(User.isTeacher())
            {
                if (User.getCurrentLikeForbidden()) {
                    vholder.layout_likes.setVisibility(View.GONE);
                }
                else {
                    vholder.layout_likes.setVisibility(View.VISIBLE);
                }

                if (User.getCurrentCommentForbidden()) {
                    vholder.layout_comments.setVisibility(View.GONE);
                }
                else
                {
                    vholder.layout_comments.setVisibility(View.VISIBLE);
                }
            }
            else {

                if(!year.equals(CurrentYear.get()))
                {
                    HistoryDetails details = User.current.getCurrentKidHistoryByYear(year);
                    if(details.like_forbidden)
                    {
                        vholder.layout_likes.setVisibility(View.GONE);
                    } else {
                        vholder.layout_likes.setVisibility(View.VISIBLE);
                    }

                    if(details.comment_forbidden)
                    {
                        vholder.layout_comments.setVisibility(View.GONE);
                    } else {
                        vholder.layout_comments.setVisibility(View.VISIBLE);
                    }

                }
                else
                {
                    if (User.current.getCurrentKidLikesForbidden()) {
                        vholder.layout_likes.setVisibility(View.GONE);
                    }
                    else {
                        vholder.layout_likes.setVisibility(View.VISIBLE);
                    }

                    if (User.current.getCurrentKidCommentsForbidden()) {
                        vholder.layout_comments.setVisibility(View.GONE);
                    }
                    else {
                        vholder.layout_comments.setVisibility(View.VISIBLE);
                    }
                }
            }

            return convertView;
        }
        
        public String getAlbumThumUrl(String picture, String album_id, String class_id, String gan_id) {
    		String url = JsonTransmitter.PICTURE_HOST + 
    						gan_id + "/" + 
    						class_id + "/" + 
    						album_id + "/" + //JsonTransmitter.TMB +
    						picture;
    		return url;
    	}

    }
    
    public static class HeaderItem implements Item 
    {
    	public final String         name;
    	public final String         gan_name;
    	public final String         class_name;

        public HeaderItem(String name, String gan_name, String class_name) {
            this.name = name;
            this.gan_name = gan_name;
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
                view = (View) inflater.inflate(R.layout.item_composer_header, parent, false);
                // Do some initialization
            } else {
                view = convertView;
            }

            TextView header = (TextView) view.findViewById(R.id.header);
            String header_text = "";
            if(!CurrentYear.get().equals(name) && User.isTeacher())
            {
                header_text = name;
            }
            else {
                header_text = name + "\n" + gan_name + " - " + class_name;
            }

            header.setText(header_text);

            TextView see_more = (TextView) view.findViewById(R.id.see_more);
            
            if(!CurrentYear.get().equals(name) && !see_more_created)
            {
            	see_more.setVisibility(View.VISIBLE);
                see_more_created = true;
            }
            else
            {
            	see_more.setVisibility(View.GONE);
            }
            
            return view;
        }
    }

    public static class TextItem implements Item
    {


        public TextItem() {

        }

        @Override
        public int getViewType() {
            return RowType.TEXT_ITEM.ordinal();
        }

        @Override
        public View getView(LayoutInflater inflater, View convertView, ViewGroup parent) {
            View view;
            if (convertView == null) {
                view = (View) inflater.inflate(R.layout.item_composer_text, parent, false);
                // Do some initialization
            } else {
                view = convertView;
            }

            TextView tv = (TextView) view.findViewById(R.id.tv);

            if(User.isParent())
            {
                tv.setText(activity.getString(R.string.empty_albums_list));
            }

            return view;
        }
    }
    
    public interface Item 
    {
        public int getViewType();
        public View getView(LayoutInflater inflater, View convertView, ViewGroup parent);
    }

}


