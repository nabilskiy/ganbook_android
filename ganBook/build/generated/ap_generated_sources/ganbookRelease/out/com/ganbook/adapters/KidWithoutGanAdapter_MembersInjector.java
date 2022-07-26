package com.ganbook.adapters;

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
public final class KidWithoutGanAdapter_MembersInjector implements MembersInjector<KidWithoutGanAdapter> {
  private final Provider<GanbookApiInterface> ganbookApiInterfacePOSTProvider;

  public KidWithoutGanAdapter_MembersInjector(
      Provider<GanbookApiInterface> ganbookApiInterfacePOSTProvider) {
    this.ganbookApiInterfacePOSTProvider = ganbookApiInterfacePOSTProvider;
  }

  public static MembersInjector<KidWithoutGanAdapter> create(
      Provider<GanbookApiInterface> ganbookApiInterfacePOSTProvider) {
    return new KidWithoutGanAdapter_MembersInjector(ganbookApiInterfacePOSTProvider);
  }

  @Override
  public void injectMembers(KidWithoutGanAdapter instance) {
    injectGanbookApiInterfacePOST(instance, ganbookApiInterfacePOSTProvider.get());
  }

  @InjectedFieldSignature("com.ganbook.adapters.KidWithoutGanAdapter.ganbookApiInterfacePOST")
  @Named("POST")
  public static void injectGanbookApiInterfacePOST(KidWithoutGanAdapter instance,
      GanbookApiInterface ganbookApiInterfacePOST) {
    instance.ganbookApiInterfacePOST = ganbookApiInterfacePOST;
  }
}
