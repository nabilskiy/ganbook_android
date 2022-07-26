package com.ganbook.dagger.components;

import com.ganbook.activities.BaseAppCompatActivity;
import com.ganbook.activities.CustomAdsActivity;
import com.ganbook.activities.SplashActivity;
import com.ganbook.adapters.AlbumViewersAdapter;
import com.ganbook.adapters.KidWithoutGanAdapter;
import com.ganbook.adapters.TabAlbumsAdapter;
import com.ganbook.dagger.modules.GanbookModule;
import com.ganbook.dagger.scopes.UserScope;
import com.ganbook.fragments.AlbumDetailsFragment;
import com.ganbook.fragments.AlbumDrawingsDetailsFragment;
import com.ganbook.fragments.BaseFragment;
import com.ganbook.fragments.DrawingDetailsFragment;
import com.ganbook.fragments.PictureDetailsFragment;
import com.ganbook.fragments.SingleContactProfileFragement;
import com.ganbook.fragments.SingleMessageFragment;
import com.ganbook.fragments.tabs.AlbumsFragment;
import com.ganbook.fragments.tabs.ContactsFragment;
import com.ganbook.fragments.tabs.EventsFragment;
import com.ganbook.fragments.tabs.KidWithoutGanFragment;
import com.ganbook.fragments.tabs.MessagesFragment;
import com.ganbook.handlers.AlbumDetailsHandlers;
import com.ganbook.services.SingleCommercialService;
import com.ganbook.services.SupportUploadService;
import com.ganbook.services.UpdateKidPicService;
import com.ganbook.services.UploadAudioService;
import com.ganbook.services.UploadDrawingService;
import com.ganbook.services.UploadInstitutionLogoService;
import com.ganbook.services.UploadMessageAttachmentService;
import com.ganbook.services.UploadService;
import com.ganbook.services.UploadTeacherImageService;
import com.ganbook.ui.ContactListAdapter;

import dagger.Component;

/**
 * Created by dmytro_vodnik on 7/27/16.
 * working on ganbook1 project
 */
@UserScope
@Component(dependencies = NetComponent.class, modules = GanbookModule.class)
public interface GanbookApiComponent {

    void inject(AlbumsFragment albumsFragment);
    void inject(MessagesFragment messagesFragment);
    void inject(TabAlbumsAdapter tabAlbumsAdapter);
    void inject(AlbumDetailsFragment albumDetailsFragment);
    void inject(SingleContactProfileFragement singleContactProfileFragement);
    void inject(PictureDetailsFragment pictureDetailsFragment);
    void inject(SingleMessageFragment singleMessageFragment);
    void inject(ContactsFragment contactsFragment);
    void inject(EventsFragment eventsFragment);
    void inject(AlbumDetailsHandlers albumDetailsHandlers);
    void inject(SupportUploadService supportUploadService);
    void inject(UploadService uploadService);
    void inject(ContactListAdapter contactListAdapter);
    void inject(BaseAppCompatActivity baseAppCompatActivity);
    void inject(BaseFragment baseFragment);
    void inject(KidWithoutGanFragment kidWithoutGanFragment);
    void inject(KidWithoutGanAdapter kidWithoutGanAdapter);
    void inject(UpdateKidPicService updateKidPicService);
    void inject(UploadInstitutionLogoService uploadInstitutionLogoService);
    void inject(UploadTeacherImageService uploadTeacherImageService);
    void inject(UploadDrawingService uploadDrawingService);
    void inject(AlbumViewersAdapter albumViewersAdapter);
    void inject(AlbumDrawingsDetailsFragment albumDrawingsDetailsFragment);
    void inject(DrawingDetailsFragment drawingDetailsFragment);
    void inject(UploadAudioService uploadAudioService);
    void inject(UploadMessageAttachmentService uploadMessageAttachmentService);
    void inject(SplashActivity splashActivity);
    void inject(CustomAdsActivity customAdsActivity);
    void inject(SingleCommercialService singleCommercialService);
}
