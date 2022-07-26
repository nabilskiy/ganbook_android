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
public final class AlbumDetailsFragment_MembersInjector implements MembersInjector<AlbumDetailsFragment> {
  private final Provider<GanbookApiInterface> ganbookApiInterfacePOSTProvider;

  private final Provider<GanbookApiInterface> ganbookApiInterfaceGETProvider;

  public AlbumDetailsFragment_MembersInjector(
      Provider<GanbookApiInterface> ganbookApiInterfacePOSTProvider,
      Provider<GanbookApiInterface> ganbookApiInterfaceGETProvider) {
    this.ganbookApiInterfacePOSTProvider = ganbookApiInterfacePOSTProvider;
    this.ganbookApiInterfaceGETProvider = ganbookApiInterfaceGETProvider;
  }

  public static MembersInjector<AlbumDetailsFragment> create(
      Provider<GanbookApiInterface> ganbookApiInterfacePOSTProvider,
      Provider<GanbookApiInterface> ganbookApiInterfaceGETProvider) {
    return new AlbumDetailsFragment_MembersInjector(ganbookApiInterfacePOSTProvider, ganbookApiInterfaceGETProvider);
  }

  @Override
  public void injectMembers(AlbumDetailsFragment instance) {
    injectGanbookApiInterfacePOST(instance, ganbookApiInterfacePOSTProvider.get());
    injectGanbookApiInterfaceGET(instance, ganbookApiInterfaceGETProvider.get());
  }

  @InjectedFieldSignature("com.ganbook.fragments.AlbumDetailsFragment.ganbookApiInterfacePOST")
  @Named("POST")
  public static void injectGanbookApiInterfacePOST(AlbumDetailsFragment instance,
      GanbookApiInterface ganbookApiInterfacePOST) {
    instance.ganbookApiInterfacePOST = ganbookApiInterfacePOST;
  }

  @InjectedFieldSignature("com.ganbook.fragments.AlbumDetailsFragment.ganbookApiInterfaceGET")
  @Named("GET")
  public static void injectGanbookApiInterfaceGET(AlbumDetailsFragment instance,
      GanbookApiInterface ganbookApiInterfaceGET) {
    instance.ganbookApiInterfaceGET = ganbookApiInterfaceGET;
  }
}
