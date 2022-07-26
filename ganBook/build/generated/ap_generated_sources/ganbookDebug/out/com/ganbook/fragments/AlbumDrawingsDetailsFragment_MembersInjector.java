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
public final class AlbumDrawingsDetailsFragment_MembersInjector implements MembersInjector<AlbumDrawingsDetailsFragment> {
  private final Provider<GanbookApiInterface> ganbookApiInterfaceGETProvider;

  private final Provider<GanbookApiInterface> ganbookApiInterfacePOSTProvider;

  public AlbumDrawingsDetailsFragment_MembersInjector(
      Provider<GanbookApiInterface> ganbookApiInterfaceGETProvider,
      Provider<GanbookApiInterface> ganbookApiInterfacePOSTProvider) {
    this.ganbookApiInterfaceGETProvider = ganbookApiInterfaceGETProvider;
    this.ganbookApiInterfacePOSTProvider = ganbookApiInterfacePOSTProvider;
  }

  public static MembersInjector<AlbumDrawingsDetailsFragment> create(
      Provider<GanbookApiInterface> ganbookApiInterfaceGETProvider,
      Provider<GanbookApiInterface> ganbookApiInterfacePOSTProvider) {
    return new AlbumDrawingsDetailsFragment_MembersInjector(ganbookApiInterfaceGETProvider, ganbookApiInterfacePOSTProvider);
  }

  @Override
  public void injectMembers(AlbumDrawingsDetailsFragment instance) {
    injectGanbookApiInterfaceGET(instance, ganbookApiInterfaceGETProvider.get());
    injectGanbookApiInterfacePOST(instance, ganbookApiInterfacePOSTProvider.get());
  }

  @InjectedFieldSignature("com.ganbook.fragments.AlbumDrawingsDetailsFragment.ganbookApiInterfaceGET")
  @Named("GET")
  public static void injectGanbookApiInterfaceGET(AlbumDrawingsDetailsFragment instance,
      GanbookApiInterface ganbookApiInterfaceGET) {
    instance.ganbookApiInterfaceGET = ganbookApiInterfaceGET;
  }

  @InjectedFieldSignature("com.ganbook.fragments.AlbumDrawingsDetailsFragment.ganbookApiInterfacePOST")
  @Named("POST")
  public static void injectGanbookApiInterfacePOST(AlbumDrawingsDetailsFragment instance,
      GanbookApiInterface ganbookApiInterfacePOST) {
    instance.ganbookApiInterfacePOST = ganbookApiInterfacePOST;
  }
}
