package com.ganbook.fragments.tabs;

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
public final class ContactsFragment_MembersInjector implements MembersInjector<ContactsFragment> {
  private final Provider<GanbookApiInterface> ganbookApiInterfaceGETProvider;

  public ContactsFragment_MembersInjector(
      Provider<GanbookApiInterface> ganbookApiInterfaceGETProvider) {
    this.ganbookApiInterfaceGETProvider = ganbookApiInterfaceGETProvider;
  }

  public static MembersInjector<ContactsFragment> create(
      Provider<GanbookApiInterface> ganbookApiInterfaceGETProvider) {
    return new ContactsFragment_MembersInjector(ganbookApiInterfaceGETProvider);
  }

  @Override
  public void injectMembers(ContactsFragment instance) {
    injectGanbookApiInterfaceGET(instance, ganbookApiInterfaceGETProvider.get());
  }

  @InjectedFieldSignature("com.ganbook.fragments.tabs.ContactsFragment.ganbookApiInterfaceGET")
  @Named("GET")
  public static void injectGanbookApiInterfaceGET(ContactsFragment instance,
      GanbookApiInterface ganbookApiInterfaceGET) {
    instance.ganbookApiInterfaceGET = ganbookApiInterfaceGET;
  }
}
