package com.ganbook.dagger.modules;

import com.google.gson.Gson;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.Generated;

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
public final class NetModule_ProvideGsonFactory implements Factory<Gson> {
  private final NetModule module;

  public NetModule_ProvideGsonFactory(NetModule module) {
    this.module = module;
  }

  @Override
  public Gson get() {
    return provideGson(module);
  }

  public static NetModule_ProvideGsonFactory create(NetModule module) {
    return new NetModule_ProvideGsonFactory(module);
  }

  public static Gson provideGson(NetModule instance) {
    return Preconditions.checkNotNullFromProvides(instance.provideGson());
  }
}
