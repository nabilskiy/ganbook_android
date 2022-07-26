package com.ganbook.dagger.components;

import com.ganbook.activities.BaseAppCompatActivity;
import com.ganbook.activities.BaseAppCompatActivity_MembersInjector;
import com.ganbook.activities.CustomAdsActivity;
import com.ganbook.activities.CustomAdsActivity_MembersInjector;
import com.ganbook.activities.SplashActivity;
import com.ganbook.activities.SplashActivity_MembersInjector;
import com.ganbook.adapters.AlbumViewersAdapter;
import com.ganbook.adapters.KidWithoutGanAdapter;
import com.ganbook.adapters.KidWithoutGanAdapter_MembersInjector;
import com.ganbook.adapters.TabAlbumsAdapter;
import com.ganbook.adapters.TabAlbumsAdapter_MembersInjector;
import com.ganbook.dagger.modules.GanbookModule;
import com.ganbook.dagger.modules.GanbookModule_ProvidesCommercialIntefaceFactory;
import com.ganbook.dagger.modules.GanbookModule_ProvidesGitHubInterfaceGETFactory;
import com.ganbook.dagger.modules.GanbookModule_ProvidesGitHubInterfacePOSTFactory;
import com.ganbook.fragments.AlbumDetailsFragment;
import com.ganbook.fragments.AlbumDetailsFragment_MembersInjector;
import com.ganbook.fragments.AlbumDrawingsDetailsFragment;
import com.ganbook.fragments.AlbumDrawingsDetailsFragment_MembersInjector;
import com.ganbook.fragments.BaseFragment;
import com.ganbook.fragments.BaseFragment_MembersInjector;
import com.ganbook.fragments.DrawingDetailsFragment;
import com.ganbook.fragments.PictureDetailsFragment;
import com.ganbook.fragments.PictureDetailsFragment_MembersInjector;
import com.ganbook.fragments.SingleContactProfileFragement;
import com.ganbook.fragments.SingleContactProfileFragement_MembersInjector;
import com.ganbook.fragments.SingleMessageFragment;
import com.ganbook.fragments.SingleMessageFragment_MembersInjector;
import com.ganbook.fragments.tabs.AlbumsFragment;
import com.ganbook.fragments.tabs.AlbumsFragment_MembersInjector;
import com.ganbook.fragments.tabs.ContactsFragment;
import com.ganbook.fragments.tabs.ContactsFragment_MembersInjector;
import com.ganbook.fragments.tabs.EventsFragment;
import com.ganbook.fragments.tabs.EventsFragment_MembersInjector;
import com.ganbook.fragments.tabs.KidWithoutGanFragment;
import com.ganbook.fragments.tabs.KidWithoutGanFragment_MembersInjector;
import com.ganbook.fragments.tabs.MessagesFragment;
import com.ganbook.fragments.tabs.MessagesFragment_MembersInjector;
import com.ganbook.handlers.AlbumDetailsHandlers;
import com.ganbook.handlers.AlbumDetailsHandlers_MembersInjector;
import com.ganbook.interfaces.GanbookApiInterface;
import com.ganbook.interfaces.IGanbookApiCommercial;
import com.ganbook.services.SingleCommercialService;
import com.ganbook.services.SingleCommercialService_MembersInjector;
import com.ganbook.services.SupportUploadService;
import com.ganbook.services.SupportUploadService_MembersInjector;
import com.ganbook.services.UpdateKidPicService;
import com.ganbook.services.UpdateKidPicService_MembersInjector;
import com.ganbook.services.UploadAudioService;
import com.ganbook.services.UploadAudioService_MembersInjector;
import com.ganbook.services.UploadDrawingService;
import com.ganbook.services.UploadDrawingService_MembersInjector;
import com.ganbook.services.UploadInstitutionLogoService;
import com.ganbook.services.UploadInstitutionLogoService_MembersInjector;
import com.ganbook.services.UploadMessageAttachmentService;
import com.ganbook.services.UploadService;
import com.ganbook.services.UploadService_MembersInjector;
import com.ganbook.services.UploadTeacherImageService;
import com.ganbook.services.UploadTeacherImageService_MembersInjector;
import com.ganbook.ui.ContactListAdapter;
import com.ganbook.ui.ContactListAdapter_MembersInjector;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import dagger.internal.DaggerGenerated;
import dagger.internal.DoubleCheck;
import dagger.internal.Preconditions;
import javax.annotation.Generated;
import javax.inject.Provider;
import retrofit2.Retrofit;

@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes"
})
public final class DaggerGanbookApiComponent {
  private DaggerGanbookApiComponent() {
  }

  public static Builder builder() {
    return new Builder();
  }

  public static final class Builder {
    private GanbookModule ganbookModule;

    private NetComponent netComponent;

    private Builder() {
    }

    public Builder ganbookModule(GanbookModule ganbookModule) {
      this.ganbookModule = Preconditions.checkNotNull(ganbookModule);
      return this;
    }

    public Builder netComponent(NetComponent netComponent) {
      this.netComponent = Preconditions.checkNotNull(netComponent);
      return this;
    }

    public GanbookApiComponent build() {
      if (ganbookModule == null) {
        this.ganbookModule = new GanbookModule();
      }
      Preconditions.checkBuilderRequirement(netComponent, NetComponent.class);
      return new GanbookApiComponentImpl(ganbookModule, netComponent);
    }
  }

  private static final class GanbookApiComponentImpl implements GanbookApiComponent {
    private final GanbookApiComponentImpl ganbookApiComponentImpl = this;

    private Provider<Retrofit> retrofitGETProvider;

    private Provider<GanbookApiInterface> providesGitHubInterfaceGETProvider;

    private Provider<Retrofit> retrofitCommercialProvider;

    private Provider<IGanbookApiCommercial> providesCommercialIntefaceProvider;

    private Provider<Retrofit> retrofitPOSTProvider;

    private Provider<GanbookApiInterface> providesGitHubInterfacePOSTProvider;

    private GanbookApiComponentImpl(GanbookModule ganbookModuleParam,
        NetComponent netComponentParam) {

      initialize(ganbookModuleParam, netComponentParam);

    }

    @SuppressWarnings("unchecked")
    private void initialize(final GanbookModule ganbookModuleParam,
        final NetComponent netComponentParam) {
      this.retrofitGETProvider = new RetrofitGETProvider(netComponentParam);
      this.providesGitHubInterfaceGETProvider = DoubleCheck.provider(GanbookModule_ProvidesGitHubInterfaceGETFactory.create(ganbookModuleParam, retrofitGETProvider));
      this.retrofitCommercialProvider = new RetrofitCommercialProvider(netComponentParam);
      this.providesCommercialIntefaceProvider = DoubleCheck.provider(GanbookModule_ProvidesCommercialIntefaceFactory.create(ganbookModuleParam, retrofitCommercialProvider));
      this.retrofitPOSTProvider = new RetrofitPOSTProvider(netComponentParam);
      this.providesGitHubInterfacePOSTProvider = DoubleCheck.provider(GanbookModule_ProvidesGitHubInterfacePOSTFactory.create(ganbookModuleParam, retrofitPOSTProvider));
    }

    @Override
    public void inject(AlbumsFragment albumsFragment) {
      injectAlbumsFragment(albumsFragment);
    }

    @Override
    public void inject(MessagesFragment messagesFragment) {
      injectMessagesFragment(messagesFragment);
    }

    @Override
    public void inject(TabAlbumsAdapter tabAlbumsAdapter) {
      injectTabAlbumsAdapter(tabAlbumsAdapter);
    }

    @Override
    public void inject(AlbumDetailsFragment albumDetailsFragment) {
      injectAlbumDetailsFragment(albumDetailsFragment);
    }

    @Override
    public void inject(SingleContactProfileFragement singleContactProfileFragement) {
      injectSingleContactProfileFragement(singleContactProfileFragement);
    }

    @Override
    public void inject(PictureDetailsFragment pictureDetailsFragment) {
      injectPictureDetailsFragment(pictureDetailsFragment);
    }

    @Override
    public void inject(SingleMessageFragment singleMessageFragment) {
      injectSingleMessageFragment(singleMessageFragment);
    }

    @Override
    public void inject(ContactsFragment contactsFragment) {
      injectContactsFragment(contactsFragment);
    }

    @Override
    public void inject(EventsFragment eventsFragment) {
      injectEventsFragment(eventsFragment);
    }

    @Override
    public void inject(AlbumDetailsHandlers albumDetailsHandlers) {
      injectAlbumDetailsHandlers(albumDetailsHandlers);
    }

    @Override
    public void inject(SupportUploadService supportUploadService) {
      injectSupportUploadService(supportUploadService);
    }

    @Override
    public void inject(UploadService uploadService) {
      injectUploadService(uploadService);
    }

    @Override
    public void inject(ContactListAdapter contactListAdapter) {
      injectContactListAdapter(contactListAdapter);
    }

    @Override
    public void inject(BaseAppCompatActivity baseAppCompatActivity) {
      injectBaseAppCompatActivity(baseAppCompatActivity);
    }

    @Override
    public void inject(BaseFragment baseFragment) {
      injectBaseFragment(baseFragment);
    }

    @Override
    public void inject(KidWithoutGanFragment kidWithoutGanFragment) {
      injectKidWithoutGanFragment(kidWithoutGanFragment);
    }

    @Override
    public void inject(KidWithoutGanAdapter kidWithoutGanAdapter) {
      injectKidWithoutGanAdapter(kidWithoutGanAdapter);
    }

    @Override
    public void inject(UpdateKidPicService updateKidPicService) {
      injectUpdateKidPicService(updateKidPicService);
    }

    @Override
    public void inject(UploadInstitutionLogoService uploadInstitutionLogoService) {
      injectUploadInstitutionLogoService(uploadInstitutionLogoService);
    }

    @Override
    public void inject(UploadTeacherImageService uploadTeacherImageService) {
      injectUploadTeacherImageService(uploadTeacherImageService);
    }

    @Override
    public void inject(UploadDrawingService uploadDrawingService) {
      injectUploadDrawingService(uploadDrawingService);
    }

    @Override
    public void inject(AlbumViewersAdapter albumViewersAdapter) {
    }

    @Override
    public void inject(AlbumDrawingsDetailsFragment albumDrawingsDetailsFragment) {
      injectAlbumDrawingsDetailsFragment(albumDrawingsDetailsFragment);
    }

    @Override
    public void inject(DrawingDetailsFragment drawingDetailsFragment) {
    }

    @Override
    public void inject(UploadAudioService uploadAudioService) {
      injectUploadAudioService(uploadAudioService);
    }

    @Override
    public void inject(UploadMessageAttachmentService uploadMessageAttachmentService) {
    }

    @Override
    public void inject(SplashActivity splashActivity) {
      injectSplashActivity(splashActivity);
    }

    @Override
    public void inject(CustomAdsActivity customAdsActivity) {
      injectCustomAdsActivity(customAdsActivity);
    }

    @Override
    public void inject(SingleCommercialService singleCommercialService) {
      injectSingleCommercialService(singleCommercialService);
    }

    @CanIgnoreReturnValue
    private AlbumsFragment injectAlbumsFragment(AlbumsFragment instance) {
      AlbumsFragment_MembersInjector.injectGanbookApiInterfaceGET(instance, providesGitHubInterfaceGETProvider.get());
      AlbumsFragment_MembersInjector.injectGanbookApiCommercial(instance, providesCommercialIntefaceProvider.get());
      return instance;
    }

    @CanIgnoreReturnValue
    private MessagesFragment injectMessagesFragment(MessagesFragment instance) {
      MessagesFragment_MembersInjector.injectGanbookApiInterfacePOST(instance, providesGitHubInterfacePOSTProvider.get());
      MessagesFragment_MembersInjector.injectGanbookApiInterfaceGET(instance, providesGitHubInterfaceGETProvider.get());
      return instance;
    }

    @CanIgnoreReturnValue
    private TabAlbumsAdapter injectTabAlbumsAdapter(TabAlbumsAdapter instance) {
      TabAlbumsAdapter_MembersInjector.injectGanbookApiInterfacePOST(instance, providesGitHubInterfacePOSTProvider.get());
      TabAlbumsAdapter_MembersInjector.injectGanbookApiCommercial(instance, providesCommercialIntefaceProvider.get());
      return instance;
    }

    @CanIgnoreReturnValue
    private AlbumDetailsFragment injectAlbumDetailsFragment(AlbumDetailsFragment instance) {
      AlbumDetailsFragment_MembersInjector.injectGanbookApiInterfacePOST(instance, providesGitHubInterfacePOSTProvider.get());
      AlbumDetailsFragment_MembersInjector.injectGanbookApiInterfaceGET(instance, providesGitHubInterfaceGETProvider.get());
      return instance;
    }

    @CanIgnoreReturnValue
    private SingleContactProfileFragement injectSingleContactProfileFragement(
        SingleContactProfileFragement instance) {
      SingleContactProfileFragement_MembersInjector.injectGanbookApiInterfaceGET(instance, providesGitHubInterfaceGETProvider.get());
      SingleContactProfileFragement_MembersInjector.injectGanbookApiInterfacePOST(instance, providesGitHubInterfacePOSTProvider.get());
      return instance;
    }

    @CanIgnoreReturnValue
    private PictureDetailsFragment injectPictureDetailsFragment(PictureDetailsFragment instance) {
      PictureDetailsFragment_MembersInjector.injectGanbookApiInterfacePOST(instance, providesGitHubInterfacePOSTProvider.get());
      return instance;
    }

    @CanIgnoreReturnValue
    private SingleMessageFragment injectSingleMessageFragment(SingleMessageFragment instance) {
      SingleMessageFragment_MembersInjector.injectGanbookApiInterfaceGET(instance, providesGitHubInterfaceGETProvider.get());
      return instance;
    }

    @CanIgnoreReturnValue
    private ContactsFragment injectContactsFragment(ContactsFragment instance) {
      ContactsFragment_MembersInjector.injectGanbookApiInterfaceGET(instance, providesGitHubInterfaceGETProvider.get());
      return instance;
    }

    @CanIgnoreReturnValue
    private EventsFragment injectEventsFragment(EventsFragment instance) {
      EventsFragment_MembersInjector.injectGanbookApiInterfaceGET(instance, providesGitHubInterfaceGETProvider.get());
      return instance;
    }

    @CanIgnoreReturnValue
    private AlbumDetailsHandlers injectAlbumDetailsHandlers(AlbumDetailsHandlers instance) {
      AlbumDetailsHandlers_MembersInjector.injectGanbookApiInterfacePOST(instance, providesGitHubInterfacePOSTProvider.get());
      return instance;
    }

    @CanIgnoreReturnValue
    private SupportUploadService injectSupportUploadService(SupportUploadService instance) {
      SupportUploadService_MembersInjector.injectGanbookApiInterfacePOST(instance, providesGitHubInterfacePOSTProvider.get());
      return instance;
    }

    @CanIgnoreReturnValue
    private UploadService injectUploadService(UploadService instance) {
      UploadService_MembersInjector.injectGanbookApiInterfacePOST(instance, providesGitHubInterfacePOSTProvider.get());
      return instance;
    }

    @CanIgnoreReturnValue
    private ContactListAdapter injectContactListAdapter(ContactListAdapter instance) {
      ContactListAdapter_MembersInjector.injectGanbookApiInterfacePOST(instance, providesGitHubInterfacePOSTProvider.get());
      return instance;
    }

    @CanIgnoreReturnValue
    private BaseAppCompatActivity injectBaseAppCompatActivity(BaseAppCompatActivity instance) {
      BaseAppCompatActivity_MembersInjector.injectGanbookApiInterfacePOST(instance, providesGitHubInterfacePOSTProvider.get());
      BaseAppCompatActivity_MembersInjector.injectGanbookApiInterfaceGET(instance, providesGitHubInterfaceGETProvider.get());
      return instance;
    }

    @CanIgnoreReturnValue
    private BaseFragment injectBaseFragment(BaseFragment instance) {
      BaseFragment_MembersInjector.injectGanbookApiInterfacePOST(instance, providesGitHubInterfacePOSTProvider.get());
      BaseFragment_MembersInjector.injectGanbookApiInterfaceGET(instance, providesGitHubInterfaceGETProvider.get());
      return instance;
    }

    @CanIgnoreReturnValue
    private KidWithoutGanFragment injectKidWithoutGanFragment(KidWithoutGanFragment instance) {
      BaseFragment_MembersInjector.injectGanbookApiInterfacePOST(instance, providesGitHubInterfacePOSTProvider.get());
      BaseFragment_MembersInjector.injectGanbookApiInterfaceGET(instance, providesGitHubInterfaceGETProvider.get());
      KidWithoutGanFragment_MembersInjector.injectGanbookApiInterfaceGET(instance, providesGitHubInterfaceGETProvider.get());
      return instance;
    }

    @CanIgnoreReturnValue
    private KidWithoutGanAdapter injectKidWithoutGanAdapter(KidWithoutGanAdapter instance) {
      KidWithoutGanAdapter_MembersInjector.injectGanbookApiInterfacePOST(instance, providesGitHubInterfacePOSTProvider.get());
      return instance;
    }

    @CanIgnoreReturnValue
    private UpdateKidPicService injectUpdateKidPicService(UpdateKidPicService instance) {
      UpdateKidPicService_MembersInjector.injectGanbookApiInterfacePOST(instance, providesGitHubInterfacePOSTProvider.get());
      return instance;
    }

    @CanIgnoreReturnValue
    private UploadInstitutionLogoService injectUploadInstitutionLogoService(
        UploadInstitutionLogoService instance) {
      UploadInstitutionLogoService_MembersInjector.injectGanbookApiInterfacePOST(instance, providesGitHubInterfacePOSTProvider.get());
      return instance;
    }

    @CanIgnoreReturnValue
    private UploadTeacherImageService injectUploadTeacherImageService(
        UploadTeacherImageService instance) {
      UploadTeacherImageService_MembersInjector.injectGanbookApiInterfacePOST(instance, providesGitHubInterfacePOSTProvider.get());
      return instance;
    }

    @CanIgnoreReturnValue
    private UploadDrawingService injectUploadDrawingService(UploadDrawingService instance) {
      UploadDrawingService_MembersInjector.injectGanbookApiInterfacePOST(instance, providesGitHubInterfacePOSTProvider.get());
      return instance;
    }

    @CanIgnoreReturnValue
    private AlbumDrawingsDetailsFragment injectAlbumDrawingsDetailsFragment(
        AlbumDrawingsDetailsFragment instance) {
      AlbumDrawingsDetailsFragment_MembersInjector.injectGanbookApiInterfaceGET(instance, providesGitHubInterfaceGETProvider.get());
      AlbumDrawingsDetailsFragment_MembersInjector.injectGanbookApiInterfacePOST(instance, providesGitHubInterfacePOSTProvider.get());
      return instance;
    }

    @CanIgnoreReturnValue
    private UploadAudioService injectUploadAudioService(UploadAudioService instance) {
      UploadAudioService_MembersInjector.injectGanbookApiInterfacePOST(instance, providesGitHubInterfacePOSTProvider.get());
      return instance;
    }

    @CanIgnoreReturnValue
    private SplashActivity injectSplashActivity(SplashActivity instance) {
      SplashActivity_MembersInjector.injectGanbookApiCommercial(instance, providesCommercialIntefaceProvider.get());
      return instance;
    }

    @CanIgnoreReturnValue
    private CustomAdsActivity injectCustomAdsActivity(CustomAdsActivity instance) {
      CustomAdsActivity_MembersInjector.injectGanbookApiCommercial(instance, providesCommercialIntefaceProvider.get());
      return instance;
    }

    @CanIgnoreReturnValue
    private SingleCommercialService injectSingleCommercialService(
        SingleCommercialService instance) {
      SingleCommercialService_MembersInjector.injectGanbookApiCommercial(instance, providesCommercialIntefaceProvider.get());
      return instance;
    }

    private static final class RetrofitGETProvider implements Provider<Retrofit> {
      private final NetComponent netComponent;

      RetrofitGETProvider(NetComponent netComponent) {
        this.netComponent = netComponent;
      }

      @Override
      public Retrofit get() {
        return Preconditions.checkNotNullFromComponent(netComponent.retrofitGET());
      }
    }

    private static final class RetrofitCommercialProvider implements Provider<Retrofit> {
      private final NetComponent netComponent;

      RetrofitCommercialProvider(NetComponent netComponent) {
        this.netComponent = netComponent;
      }

      @Override
      public Retrofit get() {
        return Preconditions.checkNotNullFromComponent(netComponent.retrofitCommercial());
      }
    }

    private static final class RetrofitPOSTProvider implements Provider<Retrofit> {
      private final NetComponent netComponent;

      RetrofitPOSTProvider(NetComponent netComponent) {
        this.netComponent = netComponent;
      }

      @Override
      public Retrofit get() {
        return Preconditions.checkNotNullFromComponent(netComponent.retrofitPOST());
      }
    }
  }
}
