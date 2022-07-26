package com.ganbook.communication.json;

public class getalbum_response extends BaseResponse {
	public String year; //": "15689",
	
    public String album_id; //": "15689",
    public String album_name; //": "watermark",
    public String picture; //": "1589277815689-1826969173.jpeg.jpeg",
    public String album_date; //": "2015-01-26 17:11:18",
    public String pic_count; //": "2",
    public String videos_count; //": "0",
    public String album_views; //": "5"
    public String unseen_photos;
	public boolean user_album_like;
	public String album_likes;
	public String album_comments;
    public String album_description;
    public transient int new_numPicts; 
    
    public getalbum_response(String year, String album_id, String album_name, String album_description, String picture, String album_date, String pic_count, String videos_count, String album_views, String unseen_photos, boolean user_album_like, String likes, String album_comments)
    {
    	this.year = year;
    	this.album_id = album_id;
    	this.album_name = album_name;
    	this.picture = picture;
    	this.album_date = album_date;
    	this.pic_count = pic_count;
    	this.videos_count = videos_count;
    	this.album_views = album_views;
    	this.unseen_photos = unseen_photos;
		this.user_album_like = user_album_like;
		this.album_likes = likes;
		this.album_comments = album_comments;
		this.album_description = album_description;
    }
    
    public int getNumPictures() {
    	if (new_numPicts > 0) {
    		return new_numPicts; // an intermediate number while uploading 
    	}    	
    	try { 
    		if (pic_count != null) {
    			int count = Integer.parseInt(pic_count);
    			return count;
    		}
    	}
    	catch (Exception e) {
    		int jj=234;
    		jj++;
    	}
		return 0;
    }
    
}
