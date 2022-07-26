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
public final class UploadService_MembersInjector implements MembersInjector<UploadService> {
  private final Provider<GanbookApiInterface> ganbookApiInterfacePOSTProvider;

  public UploadService_MembersInjector(
      Provider<GanbookApiInterface> ganbookApiInterfacePOSTProvider) {
    this.ganbookApiInterfacePOSTProvider = ganbookApiInterfacePOSTProvider;
  }

  public static MembersInjector<UploadService> create(
      Provider<GanbookApiInterface> ganbookApiInterfacePOSTProvider) {
    return new UploadService_MembersInjector(ganbookApiInterfacePOSTProvider);
  }

  @Override
  public void injectMembers(UploadService instance) {
    injectGanbookApiInterfacePOST(instance, ganbookApiInterfacePOSTProvider.get());
  }

  @InjectedFieldSignature("com.ganbook.services.UploadService.ganbookApiInterfacePOST")
  @Named("POST")
  public static void injectGanbookApiInterfacePOST(UploadService instance,
      GanbookApiInterface ganbookApiInterfacePOST) {
    instance.ganbookApiInterfacePOST = ganbookApiInterfacePOST;
  }
}
