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
public final class EventsFragment_MembersInjector implements MembersInjector<EventsFragment> {
  private final Provider<GanbookApiInterface> ganbookApiInterfaceGETProvider;

  public EventsFragment_MembersInjector(
      Provider<GanbookApiInterface> ganbookApiInterfaceGETProvider) {
    this.ganbookApiInterfaceGETProvider = ganbookApiInterfaceGETProvider;
  }

  public static MembersInjector<EventsFragment> create(
      Provider<GanbookApiInterface> ganbookApiInterfaceGETProvider) {
    return new EventsFragment_MembersInjector(ganbookApiInterfaceGETProvider);
  }

  @Override
  public void injectMembers(EventsFragment instance) {
    injectGanbookApiInterfaceGET(instance, ganbookApiInterfaceGETProvider.get());
  }

  @InjectedFieldSignature("com.ganbook.fragments.tabs.EventsFragment.ganbookApiInterfaceGET")
  @Named("GET")
  public static void injectGanbookApiInterfaceGET(EventsFragment instance,
      GanbookApiInterface ganbookApiInterfaceGET) {
    instance.ganbookApiInterfaceGET = ganbookApiInterfaceGET;
  }
}
