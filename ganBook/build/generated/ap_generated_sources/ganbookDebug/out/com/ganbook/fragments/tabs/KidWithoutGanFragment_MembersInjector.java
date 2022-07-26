package com.ganbook.fragments.tabs;

import com.ganbook.fragments.BaseFragment_MembersInjector;
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
public final class KidWithoutGanFragment_MembersInjector implements MembersInjector<KidWithoutGanFragment> {
  private final Provider<GanbookApiInterface> ganbookApiInterfacePOSTProvider;

  private final Provider<GanbookApiInterface> ganbookApiInterfaceGETProvider;

  private final Provider<GanbookApiInterface> ganbookApiInterfaceGETProvider2;

  public KidWithoutGanFragment_MembersInjector(
      Provider<GanbookApiInterface> ganbookApiInterfacePOSTProvider,
      Provider<GanbookApiInterface> ganbookApiInterfaceGETProvider,
      Provider<GanbookApiInterface> ganbookApiInterfaceGETProvider2) {
    this.ganbookApiInterfacePOSTProvider = ganbookApiInterfacePOSTProvider;
    this.ganbookApiInterfaceGETProvider = ganbookApiInterfaceGETProvider;
    this.ganbookApiInterfaceGETProvider2 = ganbookApiInterfaceGETProvider2;
  }

  public static MembersInjector<KidWithoutGanFragment> create(
      Provider<GanbookApiInterface> ganbookApiInterfacePOSTProvider,
      Provider<GanbookApiInterface> ganbookApiInterfaceGETProvider,
      Provider<GanbookApiInterface> ganbookApiInterfaceGETProvider2) {
    return new KidWithoutGanFragment_MembersInjector(ganbookApiInterfacePOSTProvider, ganbookApiInterfaceGETProvider, ganbookApiInterfaceGETProvider2);
  }

  @Override
  public void injectMembers(KidWithoutGanFragment instance) {
    BaseFragment_MembersInjector.injectGanbookApiInterfacePOST(instance, ganbookApiInterfacePOSTProvider.get());
    BaseFragment_MembersInjector.injectGanbookApiInterfaceGET(instance, ganbookApiInterfaceGETProvider.get());
    injectGanbookApiInterfaceGET(instance, ganbookApiInterfaceGETProvider2.get());
  }

  @InjectedFieldSignature("com.ganbook.fragments.tabs.KidWithoutGanFragment.ganbookApiInterfaceGET")
  @Named("GET")
  public static void injectGanbookApiInterfaceGET(KidWithoutGanFragment instance,
      GanbookApiInterface ganbookApiInterfaceGET) {
    instance.ganbookApiInterfaceGET = ganbookApiInterfaceGET;
  }
}
