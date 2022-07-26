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
public final class NetModule_ProvideRetrofitPOSTFactory implements Factory<Retrofit> {
  private final NetModule module;

  private final Provider<Gson> gsonProvider;

  public NetModule_ProvideRetrofitPOSTFactory(NetModule module, Provider<Gson> gsonProvider) {
    this.module = module;
    this.gsonProvider = gsonProvider;
  }

  @Override
  public Retrofit get() {
    return provideRetrofitPOST(module, gsonProvider.get());
  }

  public static NetModule_ProvideRetrofitPOSTFactory create(NetModule module,
      Provider<Gson> gsonProvider) {
    return new NetModule_ProvideRetrofitPOSTFactory(module, gsonProvider);
  }

  public static Retrofit provideRetrofitPOST(NetModule instance, Gson gson) {
    return Preconditions.checkNotNullFromProvides(instance.provideRetrofitPOST(gson));
  }
}
