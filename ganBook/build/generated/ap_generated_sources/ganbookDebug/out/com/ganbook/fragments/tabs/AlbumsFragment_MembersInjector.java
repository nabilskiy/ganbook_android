package com.ganbook.fragments.tabs;

import com.ganbook.interfaces.GanbookApiInterface;
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
public final class AlbumsFragment_MembersInjector implements MembersInjector<AlbumsFragment> {
  private final Provider<GanbookApiInterface> ganbookApiInterfaceGETProvider;

  private final Provider<IGanbookApiCommercial> ganbookApiCommercialProvider;

  public AlbumsFragment_MembersInjector(
      Provider<GanbookApiInterface> ganbookApiInterfaceGETProvider,
      Provider<IGanbookApiCommercial> ganbookApiCommercialProvider) {
    this.ganbookApiInterfaceGETProvider = ganbookApiInterfaceGETProvider;
    this.ganbookApiCommercialProvider = ganbookApiCommercialProvider;
  }

  public static MembersInjector<AlbumsFragment> create(
      Provider<GanbookApiInterface> ganbookApiInterfaceGETProvider,
      Provider<IGanbookApiCommercial> ganbookApiCommercialProvider) {
    return new AlbumsFragment_MembersInjector(ganbookApiInterfaceGETProvider, ganbookApiCommercialProvider);
  }

  @Override
  public void injectMembers(AlbumsFragment instance) {
    injectGanbookApiInterfaceGET(instance, ganbookApiInterfaceGETProvider.get());
    injectGanbookApiCommercial(instance, ganbookApiCommercialProvider.get());
  }

  @InjectedFieldSignature("com.ganbook.fragments.tabs.AlbumsFragment.ganbookApiInterfaceGET")
  @Named("GET")
  public static void injectGanbookApiInterfaceGET(AlbumsFragment instance,
      GanbookApiInterface ganbookApiInterfaceGET) {
    instance.ganbookApiInterfaceGET = ganbookApiInterfaceGET;
  }

  @InjectedFieldSignature("com.ganbook.fragments.tabs.AlbumsFragment.ganbookApiCommercial")
  @Named("COMMERCIAL")
  public static void injectGanbookApiCommercial(AlbumsFragment instance,
      IGanbookApiCommercial ganbookApiCommercial) {
    instance.ganbookApiCommercial = ganbookApiCommercial;
  }
}
