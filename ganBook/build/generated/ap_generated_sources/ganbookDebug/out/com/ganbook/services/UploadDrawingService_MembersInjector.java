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
public final class UploadDrawingService_MembersInjector implements MembersInjector<UploadDrawingService> {
  private final Provider<GanbookApiInterface> ganbookApiInterfacePOSTProvider;

  public UploadDrawingService_MembersInjector(
      Provider<GanbookApiInterface> ganbookApiInterfacePOSTProvider) {
    this.ganbookApiInterfacePOSTProvider = ganbookApiInterfacePOSTProvider;
  }

  public static MembersInjector<UploadDrawingService> create(
      Provider<GanbookApiInterface> ganbookApiInterfacePOSTProvider) {
    return new UploadDrawingService_MembersInjector(ganbookApiInterfacePOSTProvider);
  }

  @Override
  public void injectMembers(UploadDrawingService instance) {
    injectGanbookApiInterfacePOST(instance, ganbookApiInterfacePOSTProvider.get());
  }

  @InjectedFieldSignature("com.ganbook.services.UploadDrawingService.ganbookApiInterfacePOST")
  @Named("POST")
  public static void injectGanbookApiInterfacePOST(UploadDrawingService instance,
      GanbookApiInterface ganbookApiInterfacePOST) {
    instance.ganbookApiInterfacePOST = ganbookApiInterfacePOST;
  }
}
