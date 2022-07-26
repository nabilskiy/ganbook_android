package com.ganbook.fragments;

import com.ganbook.interfaces.GanbookApiInterface;
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
public final class SingleContactProfileFragement_MembersInjector implements MembersInjector<SingleContactProfileFragement> {
  private final Provider<GanbookApiInterface> ganbookApiInterfaceGETProvider;

  private final Provider<GanbookApiInterface> ganbookApiInterfacePOSTProvider;

  public SingleContactProfileFragement_MembersInjector(
      Provider<GanbookApiInterface> ganbookApiInterfaceGETProvider,
      Provider<GanbookApiInterface> ganbookApiInterfacePOSTProvider) {
    this.ganbookApiInterfaceGETProvider = ganbookApiInterfaceGETProvider;
    this.ganbookApiInterfacePOSTProvider = ganbookApiInterfacePOSTProvider;
  }

  public static MembersInjector<SingleContactProfileFragement> create(
      Provider<GanbookApiInterface> ganbookApiInterfaceGETProvider,
      Provider<GanbookApiInterface> ganbookApiInterfacePOSTProvider) {
    return new SingleContactProfileFragement_MembersInjector(ganbookApiInterfaceGETProvider, ganbookApiInterfacePOSTProvider);
  }

  @Override
  public void injectMembers(SingleContactProfileFragement instance) {
    injectGanbookApiInterfaceGET(instance, ganbookApiInterfaceGETProvider.get());
    injectGanbookApiInterfacePOST(instance, ganbookApiInterfacePOSTProvider.get());
  }

  @InjectedFieldSignature("com.ganbook.fragments.SingleContactProfileFragement.ganbookApiInterfaceGET")
  @Named("GET")
  public static void injectGanbookApiInterfaceGET(SingleContactProfileFragement instance,
      GanbookApiInterface ganbookApiInterfaceGET) {
    instance.ganbookApiInterfaceGET = ganbookApiInterfaceGET;
  }

  @InjectedFieldSignature("com.ganbook.fragments.SingleContactProfileFragement.ganbookApiInterfacePOST")
  @Named("POST")
  public static void injectGanbookApiInterfacePOST(SingleContactProfileFragement instance,
      GanbookApiInterface ganbookApiInterfacePOST) {
    instance.ganbookApiInterfacePOST = ganbookApiInterfacePOST;
  }
}
