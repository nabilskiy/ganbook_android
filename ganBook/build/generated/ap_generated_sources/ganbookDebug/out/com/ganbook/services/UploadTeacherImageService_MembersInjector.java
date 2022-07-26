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
public final class UploadTeacherImageService_MembersInjector implements MembersInjector<UploadTeacherImageService> {
  private final Provider<GanbookApiInterface> ganbookApiInterfacePOSTProvider;

  public UploadTeacherImageService_MembersInjector(
      Provider<GanbookApiInterface> ganbookApiInterfacePOSTProvider) {
    this.ganbookApiInterfacePOSTProvider = ganbookApiInterfacePOSTProvider;
  }

  public static MembersInjector<UploadTeacherImageService> create(
      Provider<GanbookApiInterface> ganbookApiInterfacePOSTProvider) {
    return new UploadTeacherImageService_MembersInjector(ganbookApiInterfacePOSTProvider);
  }

  @Override
  public void injectMembers(UploadTeacherImageService instance) {
    injectGanbookApiInterfacePOST(instance, ganbookApiInterfacePOSTProvider.get());
  }

  @InjectedFieldSignature("com.ganbook.services.UploadTeacherImageService.ganbookApiInterfacePOST")
  @Named("POST")
  public static void injectGanbookApiInterfacePOST(UploadTeacherImageService instance,
      GanbookApiInterface ganbookApiInterfacePOST) {
    instance.ganbookApiInterfacePOST = ganbookApiInterfacePOST;
  }
}
