package com.luminous.pick;

import android.os.Parcel;
import android.os.Parcelable;

public class SingleImage implements Parcelable {

	public String id;
	public String sdcardPath;
	public String uri;
	public String duration;
	public boolean isSeleted = false;

	public SingleImage(){}

	protected SingleImage(Parcel in) {
		id = in.readString();
		sdcardPath = in.readString();
		uri = in.readString();
		duration = in.readString();
		isSeleted = in.readByte() != 0;
	}

	public static final Creator<SingleImage> CREATOR = new Creator<SingleImage>() {
		@Override
		public SingleImage createFromParcel(Parcel in) {
			return new SingleImage(in);
		}

		@Override
		public SingleImage[] newArray(int size) {
			return new SingleImage[size];
		}
	};

	@Override
	public String toString() {
		return "SingleImage{" +
				"isSeleted=" + isSeleted +
				", duration='" + duration + '\'' +
				", uri='" + uri + '\'' +
				", sdcardPath='" + sdcardPath + '\'' +
				", id='" + id + '\'' +
				'}';
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(id);
		dest.writeString(sdcardPath);
		dest.writeString(uri);
		dest.writeString(duration);
		dest.writeByte((byte) (isSeleted ? 1 : 0));
	}
}
