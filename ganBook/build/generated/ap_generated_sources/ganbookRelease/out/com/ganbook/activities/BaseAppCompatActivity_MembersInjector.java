package com.ganbook.activities;

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
public final class BaseAppCompatActivity_MembersInjector implements MembersInjector<BaseAppCompatActivity> {
  private final Provider<GanbookApiInterface> ganbookApiInterfacePOSTProvider;

  private final Provider<GanbookApiInterface> ganbookApiInterfaceGETProvider;

  public BaseAppCompatActivity_MembersInjector(
      Provider<GanbookApiInterface> ganbookApiInterfacePOSTProvider,
      Provider<GanbookApiInterface> ganbookApiInterfaceGETProvider) {
    this.ganbookApiInterfacePOSTProvider = ganbookApiInterfacePOSTProvider;
    this.ganbookApiInterfaceGETProvider = ganbookApiInterfaceGETProvider;
  }

  public static MembersInjector<BaseAppCompatActivity> create(
      Provider<GanbookApiInterface> ganbookApiInterfacePOSTProvider,
      Provider<GanbookApiInterface> ganbookApiInterfaceGETProvider) {
    return new BaseAppCompatActivity_MembersInjector(ganbookApiInterfacePOSTProvider, ganbookApiInterfaceGETProvider);
  }

  @Override
  public void injectMembers(BaseAppCompatActivity instance) {
    injectGanbookApiInterfacePOST(instance, ganbookApiInterfacePOSTProvider.get());
    injectGanbookApiInterfaceGET(instance, ganbookApiInterfaceGETProvider.get());
  }

  @InjectedFieldSignature("com.ganbook.activities.BaseAppCompatActivity.ganbookApiInterfacePOST")
  @Named("POST")
  public static void injectGanbookApiInterfacePOST(BaseAppCompatActivity instance,
      GanbookApiInterface ganbookApiInterfacePOST) {
    instance.ganbookApiInterfacePOST = ganbookApiInterfacePOST;
  }

  @InjectedFieldSignature("com.ganbook.activities.BaseAppCompatActivity.ganbookApiInterfaceGET")
  @Named("GET")
  public static void injectGanbookApiInterfaceGET(BaseAppCompatActivity instance,
      GanbookApiInterface ganbookApiInterfaceGET) {
    instance.ganbookApiInterfaceGET = ganbookApiInterfaceGET;
  }
}
