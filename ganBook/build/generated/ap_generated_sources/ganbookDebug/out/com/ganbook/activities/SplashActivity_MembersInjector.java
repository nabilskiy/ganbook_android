package com.ganbook.activities;

import com.ganbook.interfaces.IGanbookApiCommercial;
import dagger.MembersInjector;
import dagger.internal.DaggerGenerated;
import dagger.internal.InjectedFieldSignature;
import dagger.internal.QualifierMetadata;
import javax.annotation.Generated;
import javax.inject.Named;
import javax.inject.Provider;

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
public final class SplashActivity_MembersInjector implements MembersInjector<SplashActivity> {
  private final Provider<IGanbookApiCommercial> ganbookApiCommercialProvider;

  public SplashActivity_MembersInjector(
      Provider<IGanbookApiCommercial> ganbookApiCommercialProvider) {
    this.ganbookApiCommercialProvider = ganbookApiCommercialProvider;
  }

  public static MembersInjector<SplashActivity> create(
      Provider<IGanbookApiCommercial> ganbookApiCommercialProvider) {
    return new SplashActivity_MembersInjector(ganbookApiCommercialProvider);
  }

  @Override
  public void injectMembers(SplashActivity instance) {
    injectGanbookApiCommercial(instance, ganbookApiCommercialProvider.get());
  }

  @InjectedFieldSignature("com.ganbook.activities.SplashActivity.ganbookApiCommercial")
  @Named("COMMERCIAL")
  public static void injectGanbookApiCommercial(SplashActivity instance,
      IGanbookApiCommercial ganbookApiCommercial) {
    instance.ganbookApiCommercial = ganbookApiCommercial;
  }
}
