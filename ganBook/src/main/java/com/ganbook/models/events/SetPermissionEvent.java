package com.ganbook.models.events;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by dmytro_vodnik on 7/25/16.
 * working on ganbook1 project
 */
public class SetPermissionEvent implements Parcelable {
    /**
     * 1 - likes
     * 0 - comments
     */
    public final int type;

    public SetPermissionEvent(int type) {
        this.type = type;
    }

    protected SetPermissionEvent(Parcel in) {
        type = in.readInt();
    }

    public static final Creator<SetPermissionEvent> CREATOR = new Creator<SetPermissionEvent>() {
        @Override
        public SetPermissionEvent createFromParcel(Parcel in) {
            return new SetPermissionEvent(in);
        }

        @Override
        public SetPermissionEvent[] newArray(int size) {
            return new SetPermissionEvent[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(type);
    }
}
