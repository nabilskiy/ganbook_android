package com.ganbook.interfaces;

/**
 * Created by dmytro_vodnik on 6/8/16.
 * working on ganbook1 project
 */
public interface ProgressIteractionInterface {

    void loading(String message);
    void success(String message);
    void error(String message);
}
