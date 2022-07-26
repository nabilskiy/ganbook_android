package com.ganbook.interfaces;

import com.ganbook.models.AlbumsYearModel;

/**
 * Created by dmytro_vodnik on 8/19/16.
 * working on ganbook1 project
 */
public interface KidWithoutGanInterface {

    /**
     * load albums from server
     * @param year - year of album
     * @param classId - class of album
     * @param insertAfter - index after which we need insert albums in list
     * @param albumsYearModel
     */
    void loadAlbums(String year, String classId, String ganId, int insertAfter, AlbumsYearModel albumsYearModel);
    void call_getKindergarten(String ganCode);
    void call_getKids();
}
