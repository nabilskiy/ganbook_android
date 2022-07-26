package com.ganbook.handlers;

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
public final class AlbumDetailsHandlers_MembersInjector implements MembersInjector<AlbumDetailsHandlers> {
  private final Provider<GanbookApiInterface> ganbookApiInterfacePOSTProvider;

  public AlbumDetailsHandlers_MembersInjector(
      Provider<GanbookApiInterface> ganbookApiInterfacePOSTProvider) {
    this.ganbookApiInterfacePOSTProvider = ganbookApiInterfacePOSTProvider;
  }

  public static MembersInjector<AlbumDetailsHandlers> create(
      Provider<GanbookApiInterface> ganbookApiInterfacePOSTProvider) {
    return new AlbumDetailsHandlers_MembersInjector(ganbookApiInterfacePOSTProvider);
  }

  @Override
  public void injectMembers(AlbumDetailsHandlers instance) {
    injectGanbookApiInterfacePOST(instance, ganbookApiInterfacePOSTProvider.get());
  }

  @InjectedFieldSignature("com.ganbook.handlers.AlbumDetailsHandlers.ganbookApiInterfacePOST")
  @Named("POST")
  public static void injectGanbookApiInterfacePOST(AlbumDetailsHandlers instance,
      GanbookApiInterface ganbookApiInterfacePOST) {
    instance.ganbookApiInterfacePOST = ganbookApiInterfacePOST;
  }
}
