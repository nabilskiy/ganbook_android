package com.ganbook.dagger.modules;

import com.google.gson.Gson;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.Generated;
import javax.inject.Provider;
import retrofit2.Retrofit;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata("javax.inject.Named")
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes"
})
public final class NetModule_ProvideRetrofitCommercialFactory implements Factory<Retrofit> {
  private final NetModule module;

  private final Provider<Gson> gsonProvider;

  public NetModule_ProvideRetrofitCommercialFactory(NetModule module, Provider<Gson> gsonProvider) {
    this.module = module;
    this.gsonProvider = gsonProvider;
  }

  @Override
  public Retrofit get() {
    return provideRetrofitCommercial(module, gsonProvider.get());
  }

  public static NetModule_ProvideRetrofitCommercialFactory create(NetModule module,
      Provider<Gson> gsonProvider) {
    return new NetModule_ProvideRetrofitCommercialFactory(module, gsonProvider);
  }

  public static Retrofit provideRetrofitCommercial(NetModule instance, Gson gson) {
    return Preconditions.checkNotNullFromProvides(instance.provideRetrofitCommercial(gson));
  }
}
