package com.ganbook.datamodel;

public class ClassDetails {
	private String title;
	private int imageUrl;

    public ClassDetails(String title,int imageUrl) {         
        this.title = title;
        this.imageUrl = imageUrl;
    }

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(int imageUrl) {
		this.imageUrl = imageUrl;
	}
     
}
