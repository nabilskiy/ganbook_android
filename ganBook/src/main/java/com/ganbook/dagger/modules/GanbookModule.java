package com.ganbook.dagger.modules;

import com.ganbook.dagger.scopes.UserScope;
import com.ganbook.interfaces.GanbookApiInterface;
import com.ganbook.interfaces.IGanbookApiCommercial;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;

/**
 * Created by dmytro_vodnik on 7/27/16.
 * working on ganbook1 project
 */
@Module
public class GanbookModule {

    @Provides
    @Named("POST")
    @UserScope
    public GanbookApiInterface providesGitHubInterfacePOST(@Named("POST") Retrofit retrofit) {
        return retrofit.create(GanbookApiInterface.class);
    }

    @Provides
    @Named("GET")
    @UserScope
    public GanbookApiInterface providesGitHubInterfaceGET(@Named("GET") Retrofit retrofit) {
        return retrofit.create(GanbookApiInterface.class);
    }

    @Provides
    @Named("COMMERCIAL")
    @UserScope
    public IGanbookApiCommercial providesCommercialInteface(@Named("COMMERCIAL") Retrofit retrofit) {
        return retrofit.create(IGanbookApiCommercial.class);
    }
}
