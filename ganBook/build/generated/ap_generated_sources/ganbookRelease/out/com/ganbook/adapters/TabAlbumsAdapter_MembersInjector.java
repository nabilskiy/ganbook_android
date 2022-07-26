package com.ganbook.adapters;

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
public final class TabAlbumsAdapter_MembersInjector implements MembersInjector<TabAlbumsAdapter> {
  private final Provider<GanbookApiInterface> ganbookApiInterfacePOSTProvider;

  private final Provider<IGanbookApiCommercial> ganbookApiCommercialProvider;

  public TabAlbumsAdapter_MembersInjector(
      Provider<GanbookApiInterface> ganbookApiInterfacePOSTProvider,
      Provider<IGanbookApiCommercial> ganbookApiCommercialProvider) {
    this.ganbookApiInterfacePOSTProvider = ganbookApiInterfacePOSTProvider;
    this.ganbookApiCommercialProvider = ganbookApiCommercialProvider;
  }

  public static MembersInjector<TabAlbumsAdapter> create(
      Provider<GanbookApiInterface> ganbookApiInterfacePOSTProvider,
      Provider<IGanbookApiCommercial> ganbookApiCommercialProvider) {
    return new TabAlbumsAdapter_MembersInjector(ganbookApiInterfacePOSTProvider, ganbookApiCommercialProvider);
  }

  @Override
  public void injectMembers(TabAlbumsAdapter instance) {
    injectGanbookApiInterfacePOST(instance, ganbookApiInterfacePOSTProvider.get());
    injectGanbookApiCommercial(instance, ganbookApiCommercialProvider.get());
  }

  @InjectedFieldSignature("com.ganbook.adapters.TabAlbumsAdapter.ganbookApiInterfacePOST")
  @Named("POST")
  public static void injectGanbookApiInterfacePOST(TabAlbumsAdapter instance,
      GanbookApiInterface ganbookApiInterfacePOST) {
    instance.ganbookApiInterfacePOST = ganbookApiInterfacePOST;
  }

  @InjectedFieldSignature("com.ganbook.adapters.TabAlbumsAdapter.ganbookApiCommercial")
  @Named("COMMERCIAL")
  public static void injectGanbookApiCommercial(TabAlbumsAdapter instance,
      IGanbookApiCommercial ganbookApiCommercial) {
    instance.ganbookApiCommercial = ganbookApiCommercial;
  }
}
