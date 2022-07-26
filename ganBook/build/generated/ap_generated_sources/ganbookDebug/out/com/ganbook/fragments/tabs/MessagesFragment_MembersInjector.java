package com.ganbook.fragments.tabs;

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
public final class MessagesFragment_MembersInjector implements MembersInjector<MessagesFragment> {
  private final Provider<GanbookApiInterface> ganbookApiInterfacePOSTProvider;

  private final Provider<GanbookApiInterface> ganbookApiInterfaceGETProvider;

  public MessagesFragment_MembersInjector(
      Provider<GanbookApiInterface> ganbookApiInterfacePOSTProvider,
      Provider<GanbookApiInterface> ganbookApiInterfaceGETProvider) {
    this.ganbookApiInterfacePOSTProvider = ganbookApiInterfacePOSTProvider;
    this.ganbookApiInterfaceGETProvider = ganbookApiInterfaceGETProvider;
  }

  public static MembersInjector<MessagesFragment> create(
      Provider<GanbookApiInterface> ganbookApiInterfacePOSTProvider,
      Provider<GanbookApiInterface> ganbookApiInterfaceGETProvider) {
    return new MessagesFragment_MembersInjector(ganbookApiInterfacePOSTProvider, ganbookApiInterfaceGETProvider);
  }

  @Override
  public void injectMembers(MessagesFragment instance) {
    injectGanbookApiInterfacePOST(instance, ganbookApiInterfacePOSTProvider.get());
    injectGanbookApiInterfaceGET(instance, ganbookApiInterfaceGETProvider.get());
  }

  @InjectedFieldSignature("com.ganbook.fragments.tabs.MessagesFragment.ganbookApiInterfacePOST")
  @Named("POST")
  public static void injectGanbookApiInterfacePOST(MessagesFragment instance,
      GanbookApiInterface ganbookApiInterfacePOST) {
    instance.ganbookApiInterfacePOST = ganbookApiInterfacePOST;
  }

  @InjectedFieldSignature("com.ganbook.fragments.tabs.MessagesFragment.ganbookApiInterfaceGET")
  @Named("GET")
  public static void injectGanbookApiInterfaceGET(MessagesFragment instance,
      GanbookApiInterface ganbookApiInterfaceGET) {
    instance.ganbookApiInterfaceGET = ganbookApiInterfaceGET;
  }
}
