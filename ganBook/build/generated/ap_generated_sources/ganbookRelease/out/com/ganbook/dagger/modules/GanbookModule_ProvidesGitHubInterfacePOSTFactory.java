package com.ganbook.dagger.modules;

import com.ganbook.interfaces.GanbookApiInterface;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.Generated;
import javax.inject.Provider;
import retrofit2.Retrofit;

@ScopeMetadata("com.ganbook.dagger.scopes.UserScope")
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
public final class GanbookModule_ProvidesGitHubInterfacePOSTFactory implements Factory<GanbookApiInterface> {
  private final GanbookModule module;

  private final Provider<Retrofit> retrofitProvider;

  public GanbookModule_ProvidesGitHubInterfacePOSTFactory(GanbookModule module,
      Provider<Retrofit> retrofitProvider) {
    this.module = module;
    this.retrofitProvider = retrofitProvider;
  }

  @Override
  public GanbookApiInterface get() {
    return providesGitHubInterfacePOST(module, retrofitProvider.get());
  }

  public static GanbookModule_ProvidesGitHubInterfacePOSTFactory create(GanbookModule module,
      Provider<Retrofit> retrofitProvider) {
    return new GanbookModule_ProvidesGitHubInterfacePOSTFactory(module, retrofitProvider);
  }

  public static GanbookApiInterface providesGitHubInterfacePOST(GanbookModule instance,
      Retrofit retrofit) {
    return Preconditions.checkNotNullFromProvides(instance.providesGitHubInterfacePOST(retrofit));
  }
}
