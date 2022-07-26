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
public final class PictureDetailsFragment_MembersInjector implements MembersInjector<PictureDetailsFragment> {
  private final Provider<GanbookApiInterface> ganbookApiInterfacePOSTProvider;

  public PictureDetailsFragment_MembersInjector(
      Provider<GanbookApiInterface> ganbookApiInterfacePOSTProvider) {
    this.ganbookApiInterfacePOSTProvider = ganbookApiInterfacePOSTProvider;
  }

  public static MembersInjector<PictureDetailsFragment> create(
      Provider<GanbookApiInterface> ganbookApiInterfacePOSTProvider) {
    return new PictureDetailsFragment_MembersInjector(ganbookApiInterfacePOSTProvider);
  }

  @Override
  public void injectMembers(PictureDetailsFragment instance) {
    injectGanbookApiInterfacePOST(instance, ganbookApiInterfacePOSTProvider.get());
  }

  @InjectedFieldSignature("com.ganbook.fragments.PictureDetailsFragment.ganbookApiInterfacePOST")
  @Named("POST")
  public static void injectGanbookApiInterfacePOST(PictureDetailsFragment instance,
      GanbookApiInterface ganbookApiInterfacePOST) {
    instance.ganbookApiInterfacePOST = ganbookApiInterfacePOST;
  }
}
