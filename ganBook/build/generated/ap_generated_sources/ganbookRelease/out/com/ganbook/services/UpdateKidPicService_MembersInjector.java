package com.ganbook.services;

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
public final class UpdateKidPicService_MembersInjector implements MembersInjector<UpdateKidPicService> {
  private final Provider<GanbookApiInterface> ganbookApiInterfacePOSTProvider;

  public UpdateKidPicService_MembersInjector(
      Provider<GanbookApiInterface> ganbookApiInterfacePOSTProvider) {
    this.ganbookApiInterfacePOSTProvider = ganbookApiInterfacePOSTProvider;
  }

  public static MembersInjector<UpdateKidPicService> create(
      Provider<GanbookApiInterface> ganbookApiInterfacePOSTProvider) {
    return new UpdateKidPicService_MembersInjector(ganbookApiInterfacePOSTProvider);
  }

  @Override
  public void injectMembers(UpdateKidPicService instance) {
    injectGanbookApiInterfacePOST(instance, ganbookApiInterfacePOSTProvider.get());
  }

  @InjectedFieldSignature("com.ganbook.services.UpdateKidPicService.ganbookApiInterfacePOST")
  @Named("POST")
  public static void injectGanbookApiInterfacePOST(UpdateKidPicService instance,
      GanbookApiInterface ganbookApiInterfacePOST) {
    instance.ganbookApiInterfacePOST = ganbookApiInterfacePOST;
  }
}
