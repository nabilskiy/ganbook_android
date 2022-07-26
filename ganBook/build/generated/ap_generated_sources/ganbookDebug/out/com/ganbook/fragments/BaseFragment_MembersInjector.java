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
public final class BaseFragment_MembersInjector implements MembersInjector<BaseFragment> {
  private final Provider<GanbookApiInterface> ganbookApiInterfacePOSTProvider;

  private final Provider<GanbookApiInterface> ganbookApiInterfaceGETProvider;

  public BaseFragment_MembersInjector(Provider<GanbookApiInterface> ganbookApiInterfacePOSTProvider,
      Provider<GanbookApiInterface> ganbookApiInterfaceGETProvider) {
    this.ganbookApiInterfacePOSTProvider = ganbookApiInterfacePOSTProvider;
    this.ganbookApiInterfaceGETProvider = ganbookApiInterfaceGETProvider;
  }

  public static MembersInjector<BaseFragment> create(
      Provider<GanbookApiInterface> ganbookApiInterfacePOSTProvider,
      Provider<GanbookApiInterface> ganbookApiInterfaceGETProvider) {
    return new BaseFragment_MembersInjector(ganbookApiInterfacePOSTProvider, ganbookApiInterfaceGETProvider);
  }

  @Override
  public void injectMembers(BaseFragment instance) {
    injectGanbookApiInterfacePOST(instance, ganbookApiInterfacePOSTProvider.get());
    injectGanbookApiInterfaceGET(instance, ganbookApiInterfaceGETProvider.get());
  }

  @InjectedFieldSignature("com.ganbook.fragments.BaseFragment.ganbookApiInterfacePOST")
  @Named("POST")
  public static void injectGanbookApiInterfacePOST(BaseFragment instance,
      GanbookApiInterface ganbookApiInterfacePOST) {
    instance.ganbookApiInterfacePOST = ganbookApiInterfacePOST;
  }

  @InjectedFieldSignature("com.ganbook.fragments.BaseFragment.ganbookApiInterfaceGET")
  @Named("GET")
  public static void injectGanbookApiInterfaceGET(BaseFragment instance,
      GanbookApiInterface ganbookApiInterfaceGET) {
    instance.ganbookApiInterfaceGET = ganbookApiInterfaceGET;
  }
}
