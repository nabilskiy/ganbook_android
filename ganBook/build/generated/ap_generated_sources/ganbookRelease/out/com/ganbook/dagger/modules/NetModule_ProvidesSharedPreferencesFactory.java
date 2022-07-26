package com.ganbook.dagger.modules;

import android.app.Application;
import android.content.SharedPreferences;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes"
})
public final class NetModule_ProvidesSharedPreferencesFactory implements Factory<SharedPreferences> {
  private final NetModule module;

  private final Provider<Application> applicationProvider;

  public NetModule_ProvidesSharedPreferencesFactory(NetModule module,
      Provider<Application> applicationProvider) {
    this.module = module;
    this.applicationProvider = applicationProvider;
  }

  @Override
  public SharedPreferences get() {
    return providesSharedPreferences(module, applicationProvider.get());
  }

  public static NetModule_ProvidesSharedPreferencesFactory create(NetModule module,
      Provider<Application> applicationProvider) {
    return new NetModule_ProvidesSharedPreferencesFactory(module, applicationProvider);
  }

  public static SharedPreferences providesSharedPreferences(NetModule instance,
      Application application) {
    return Preconditions.checkNotNullFromProvides(instance.providesSharedPreferences(application));
  }
}
