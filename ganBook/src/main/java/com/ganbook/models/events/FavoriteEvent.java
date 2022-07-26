package com.ganbook.models.events;

import com.ganbook.models.PictureAnswer;

/**
 * Created by dmytro_vodnik on 6/22/16.
 * working on ganbook1 project
 */
public class FavoriteEvent {
    public final int curPosition;
    public final PictureAnswer fav;

    public FavoriteEvent(int curInd, PictureAnswer pictureWithFav) {
        curPosition = curInd;
        fav = pictureWithFav;
    }

    @Override
    public String toString() {
        return "FavoriteEvent{" +
                "curPosition=" + curPosition +
                ", fav=" + fav +
                '}';
    }
}
