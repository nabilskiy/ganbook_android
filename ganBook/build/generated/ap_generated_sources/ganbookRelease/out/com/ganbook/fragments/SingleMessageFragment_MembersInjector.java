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
public final class SingleMessageFragment_MembersInjector implements MembersInjector<SingleMessageFragment> {
  private final Provider<GanbookApiInterface> ganbookApiInterfaceGETProvider;

  public SingleMessageFragment_MembersInjector(
      Provider<GanbookApiInterface> ganbookApiInterfaceGETProvider) {
    this.ganbookApiInterfaceGETProvider = ganbookApiInterfaceGETProvider;
  }

  public static MembersInjector<SingleMessageFragment> create(
      Provider<GanbookApiInterface> ganbookApiInterfaceGETProvider) {
    return new SingleMessageFragment_MembersInjector(ganbookApiInterfaceGETProvider);
  }

  @Override
  public void injectMembers(SingleMessageFragment instance) {
    injectGanbookApiInterfaceGET(instance, ganbookApiInterfaceGETProvider.get());
  }

  @InjectedFieldSignature("com.ganbook.fragments.SingleMessageFragment.ganbookApiInterfaceGET")
  @Named("GET")
  public static void injectGanbookApiInterfaceGET(SingleMessageFragment instance,
      GanbookApiInterface ganbookApiInterfaceGET) {
    instance.ganbookApiInterfaceGET = ganbookApiInterfaceGET;
  }
}
