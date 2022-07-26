package com.ganbook.dagger.components;

import com.ganbook.dagger.modules.AppModule;
import com.ganbook.dagger.modules.NetModule;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Component;
import retrofit2.Retrofit;

/**
 * Created by dmytro_vodnik on 7/27/16.
 * working on ganbook1 project
 */
@Singleton
@Component(modules={AppModule.class, NetModule.class})
public interface NetComponent {

    @Named("POST")
    Retrofit retrofitPOST();

    @Named("GET")
    Retrofit retrofitGET();

    @Named("COMMERCIAL")
    Retrofit retrofitCommercial();
}
