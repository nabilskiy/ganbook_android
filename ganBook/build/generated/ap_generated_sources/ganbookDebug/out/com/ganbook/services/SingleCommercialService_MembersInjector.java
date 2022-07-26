package com.ganbook.services;

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
public final class SingleCommercialService_MembersInjector implements MembersInjector<SingleCommercialService> {
  private final Provider<IGanbookApiCommercial> ganbookApiCommercialProvider;

  public SingleCommercialService_MembersInjector(
      Provider<IGanbookApiCommercial> ganbookApiCommercialProvider) {
    this.ganbookApiCommercialProvider = ganbookApiCommercialProvider;
  }

  public static MembersInjector<SingleCommercialService> create(
      Provider<IGanbookApiCommercial> ganbookApiCommercialProvider) {
    return new SingleCommercialService_MembersInjector(ganbookApiCommercialProvider);
  }

  @Override
  public void injectMembers(SingleCommercialService instance) {
    injectGanbookApiCommercial(instance, ganbookApiCommercialProvider.get());
  }

  @InjectedFieldSignature("com.ganbook.services.SingleCommercialService.ganbookApiCommercial")
  @Named("COMMERCIAL")
  public static void injectGanbookApiCommercial(SingleCommercialService instance,
      IGanbookApiCommercial ganbookApiCommercial) {
    instance.ganbookApiCommercial = ganbookApiCommercial;
  }
}
