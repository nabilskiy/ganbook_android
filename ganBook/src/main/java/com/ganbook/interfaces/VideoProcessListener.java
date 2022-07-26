package com.ganbook.interfaces;

import java.io.File;

/**
 * Created by dmytro_vodnik on 1/6/17.
 * working on ganbook1 project
 */
public interface VideoProcessListener {

    void onSuccess(File file);
    void onError(String error);
}
