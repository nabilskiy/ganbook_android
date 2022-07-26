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
public final class CustomAdsActivity_MembersInjector implements MembersInjector<CustomAdsActivity> {
  private final Provider<IGanbookApiCommercial> ganbookApiCommercialProvider;

  public CustomAdsActivity_MembersInjector(
      Provider<IGanbookApiCommercial> ganbookApiCommercialProvider) {
    this.ganbookApiCommercialProvider = ganbookApiCommercialProvider;
  }

  public static MembersInjector<CustomAdsActivity> create(
      Provider<IGanbookApiCommercial> ganbookApiCommercialProvider) {
    return new CustomAdsActivity_MembersInjector(ganbookApiCommercialProvider);
  }

  @Override
  public void injectMembers(CustomAdsActivity instance) {
    injectGanbookApiCommercial(instance, ganbookApiCommercialProvider.get());
  }

  @InjectedFieldSignature("com.ganbook.activities.CustomAdsActivity.ganbookApiCommercial")
  @Named("COMMERCIAL")
  public static void injectGanbookApiCommercial(CustomAdsActivity instance,
      IGanbookApiCommercial ganbookApiCommercial) {
    instance.ganbookApiCommercial = ganbookApiCommercial;
  }
}
