package com.project.ganim.databinding;
import com.project.ganim.R;
import com.project.ganim.BR;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.View;
@SuppressWarnings("unchecked")
@javax.annotation.Generated("Android Data Binding")
public class ItemAlbumListBindingImpl extends ItemAlbumListBinding  {

    @Nullable
    private static final androidx.databinding.ViewDataBinding.IncludedLayouts sIncludes;
    @Nullable
    private static final android.util.SparseIntArray sViewsWithIds;
    static {
        sIncludes = null;
        sViewsWithIds = new android.util.SparseIntArray();
        sViewsWithIds.put(R.id.heart_active_center, 14);
        sViewsWithIds.put(R.id.num_new_image, 15);
        sViewsWithIds.put(R.id.title_layout, 16);
        sViewsWithIds.put(R.id.album_details_layout, 17);
        sViewsWithIds.put(R.id.comment_likes_icons, 18);
        sViewsWithIds.put(R.id.layout_comments, 19);
        sViewsWithIds.put(R.id.comments_image, 20);
        sViewsWithIds.put(R.id.layout_likes, 21);
        sViewsWithIds.put(R.id.likes_image_base, 22);
        sViewsWithIds.put(R.id.switcher, 23);
        sViewsWithIds.put(R.id.heart_inactive, 24);
        sViewsWithIds.put(R.id.heart_active, 25);
        sViewsWithIds.put(R.id.layout_description, 26);
        sViewsWithIds.put(R.id.description_image, 27);
        sViewsWithIds.put(R.id.views_image, 28);
    }
    // views
    // variables
    // values
    // listeners
    // Inverse Binding Event Handlers

    public ItemAlbumListBindingImpl(@Nullable androidx.databinding.DataBindingComponent bindingComponent, @NonNull View root) {
        this(bindingComponent, root, mapBindings(bindingComponent, root, 29, sIncludes, sViewsWithIds));
    }
    private ItemAlbumListBindingImpl(androidx.databinding.DataBindingComponent bindingComponent, View root, Object[] bindings) {
        super(bindingComponent, root, 1
            , (android.widget.TextView) bindings[6]
            , (android.widget.LinearLayout) bindings[17]
            , (android.widget.TextView) bindings[5]
            , (android.widget.LinearLayout) bindings[18]
            , (android.widget.ImageView) bindings[20]
            , (android.widget.ImageView) bindings[27]
            , (android.widget.ImageView) bindings[25]
            , (android.widget.ImageView) bindings[14]
            , (android.widget.ImageView) bindings[24]
            , (android.widget.ImageView) bindings[1]
            , (android.widget.LinearLayout) bindings[19]
            , (android.widget.LinearLayout) bindings[26]
            , (android.widget.LinearLayout) bindings[21]
            , (android.widget.ImageView) bindings[22]
            , (android.widget.ImageView) bindings[2]
            , (android.widget.RelativeLayout) bindings[3]
            , (android.widget.TextView) bindings[11]
            , (android.widget.TextView) bindings[12]
            , (android.widget.ImageView) bindings[15]
            , (android.widget.TextView) bindings[4]
            , (android.widget.TextView) bindings[8]
            , (android.widget.ImageView) bindings[7]
            , (android.widget.TextView) bindings[10]
            , (android.widget.ImageView) bindings[9]
            , (android.widget.TextView) bindings[13]
            , (android.widget.RelativeLayout) bindings[0]
            , (android.widget.ViewSwitcher) bindings[23]
            , (android.widget.RelativeLayout) bindings[16]
            , (android.widget.ImageView) bindings[28]
            );
        this.albumDate.setTag(null);
        this.albumTitle.setTag(null);
        this.itemImage.setTag(null);
        this.newImage.setTag(null);
        this.notifyLayout.setTag(null);
        this.numComments.setTag(null);
        this.numLikes.setTag(null);
        this.numNewText.setTag(null);
        this.numPhotos.setTag(null);
        this.numPicsDot.setTag(null);
        this.numVideos.setTag(null);
        this.numVideosDot.setTag(null);
        this.numViewsText.setTag(null);
        this.parentAlbumLayout.setTag(null);
        setRootTag(root);
        // listeners
        invalidateAll();
    }

    @Override
    public void invalidateAll() {
        synchronized(this) {
                mDirtyFlags = 0x80L;
        }
        requestRebind();
    }

    @Override
    public boolean hasPendingBindings() {
        synchronized(this) {
            if (mDirtyFlags != 0) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean setVariable(int variableId, @Nullable Object variable)  {
        boolean variableSet = true;
        if (BR.showNewLabel == variableId) {
            setShowNewLabel((boolean) variable);
        }
        else if (BR.videosTitle == variableId) {
            setVideosTitle((java.lang.String) variable);
        }
        else if (BR.preview == variableId) {
            setPreview((java.lang.String) variable);
        }
        else if (BR.photosTitle == variableId) {
            setPhotosTitle((java.lang.String) variable);
        }
        else if (BR.album == variableId) {
            setAlbum((com.ganbook.models.AlbumsAnswer) variable);
        }
        else {
            variableSet = false;
        }
            return variableSet;
    }

    public void setShowNewLabel(boolean ShowNewLabel) {
        this.mShowNewLabel = ShowNewLabel;
        synchronized(this) {
            mDirtyFlags |= 0x2L;
        }
        notifyPropertyChanged(BR.showNewLabel);
        super.requestRebind();
    }
    public void setVideosTitle(@Nullable java.lang.String VideosTitle) {
        this.mVideosTitle = VideosTitle;
        synchronized(this) {
            mDirtyFlags |= 0x4L;
        }
        notifyPropertyChanged(BR.videosTitle);
        super.requestRebind();
    }
    public void setPreview(@Nullable java.lang.String Preview) {
        this.mPreview = Preview;
        synchronized(this) {
            mDirtyFlags |= 0x8L;
        }
        notifyPropertyChanged(BR.preview);
        super.requestRebind();
    }
    public void setPhotosTitle(@Nullable java.lang.String PhotosTitle) {
        this.mPhotosTitle = PhotosTitle;
        synchronized(this) {
            mDirtyFlags |= 0x10L;
        }
        notifyPropertyChanged(BR.photosTitle);
        super.requestRebind();
    }
    public void setAlbum(@Nullable com.ganbook.models.AlbumsAnswer Album) {
        updateRegistration(0, Album);
        this.mAlbum = Album;
        synchronized(this) {
            mDirtyFlags |= 0x1L;
        }
        notifyPropertyChanged(BR.album);
        super.requestRebind();
    }

    @Override
    protected boolean onFieldChange(int localFieldId, Object object, int fieldId) {
        switch (localFieldId) {
            case 0 :
                return onChangeAlbum((com.ganbook.models.AlbumsAnswer) object, fieldId);
        }
        return false;
    }
    private boolean onChangeAlbum(com.ganbook.models.AlbumsAnswer Album, int fieldId) {
        if (fieldId == BR._all) {
            synchronized(this) {
                    mDirtyFlags |= 0x1L;
            }
            return true;
        }
        else if (fieldId == BR.unseenPhotos) {
            synchronized(this) {
                    mDirtyFlags |= 0x20L;
            }
            return true;
        }
        else if (fieldId == BR.likesCount) {
            synchronized(this) {
                    mDirtyFlags |= 0x40L;
            }
            return true;
        }
        return false;
    }

    @Override
    protected void executeBindings() {
        long dirtyFlags = 0;
        synchronized(this) {
            dirtyFlags = mDirtyFlags;
            mDirtyFlags = 0;
        }
        boolean showNewLabel = mShowNewLabel;
        int albumCommentsCount = 0;
        java.lang.String videosTitle = mVideosTitle;
        java.lang.String stringValueOfAlbumCommentsCount = null;
        int albumAlbumViews = 0;
        int showNewLabelInt4Int0 = 0;
        int albumLikesCount = 0;
        int albumVideosCountInt0Int4Int0 = 0;
        java.lang.String dateFormatterFormatForAlbumListAlbumAlbumDate = null;
        int albumVideosCount = 0;
        boolean albumPicCountInt0 = false;
        java.lang.String stringValueOfAlbumAlbumViews = null;
        java.lang.String albumAlbumName = null;
        java.lang.String preview = mPreview;
        boolean ShowNewLabel1 = false;
        java.lang.String stringValueOfAlbumLikesCount = null;
        int albumPicCount = 0;
        java.lang.String stringValueOfAlbumUnseenPhotos = null;
        int albumPicCountInt0Int4Int0 = 0;
        java.lang.String photosTitle = mPhotosTitle;
        com.ganbook.models.AlbumsAnswer album = mAlbum;
        java.util.Date albumAlbumDate = null;
        boolean albumVideosCountInt0 = false;
        int albumUnseenPhotos = 0;

        if ((dirtyFlags & 0x82L) != 0) {



                // read !showNewLabel
                ShowNewLabel1 = !showNewLabel;
            if((dirtyFlags & 0x82L) != 0) {
                if(ShowNewLabel1) {
                        dirtyFlags |= 0x200L;
                }
                else {
                        dirtyFlags |= 0x100L;
                }
            }


                // read !showNewLabel ? 4 : 0
                showNewLabelInt4Int0 = ((ShowNewLabel1) ? (4) : (0));
        }
        if ((dirtyFlags & 0x84L) != 0) {
        }
        if ((dirtyFlags & 0x88L) != 0) {
        }
        if ((dirtyFlags & 0x90L) != 0) {
        }
        if ((dirtyFlags & 0xe1L) != 0) {


            if ((dirtyFlags & 0x81L) != 0) {

                    if (album != null) {
                        // read album.commentsCount
                        albumCommentsCount = album.getCommentsCount();
                        // read album.albumViews
                        albumAlbumViews = album.getAlbumViews();
                        // read album.videosCount
                        albumVideosCount = album.getVideosCount();
                        // read album.albumName
                        albumAlbumName = album.getAlbumName();
                        // read album.picCount
                        albumPicCount = album.getPicCount();
                        // read album.albumDate
                        albumAlbumDate = album.getAlbumDate();
                    }


                    // read String.valueOf(album.commentsCount)
                    stringValueOfAlbumCommentsCount = java.lang.String.valueOf(albumCommentsCount);
                    // read String.valueOf(album.albumViews)
                    stringValueOfAlbumAlbumViews = java.lang.String.valueOf(albumAlbumViews);
                    // read album.videosCount == 0
                    albumVideosCountInt0 = (albumVideosCount) == (0);
                    // read album.picCount == 0
                    albumPicCountInt0 = (albumPicCount) == (0);
                    // read DateFormatter.formatForAlbumList(album.albumDate)
                    dateFormatterFormatForAlbumListAlbumAlbumDate = com.ganbook.utils.DateFormatter.formatForAlbumList(albumAlbumDate);
                if((dirtyFlags & 0x81L) != 0) {
                    if(albumVideosCountInt0) {
                            dirtyFlags |= 0x800L;
                    }
                    else {
                            dirtyFlags |= 0x400L;
                    }
                }
                if((dirtyFlags & 0x81L) != 0) {
                    if(albumPicCountInt0) {
                            dirtyFlags |= 0x2000L;
                    }
                    else {
                            dirtyFlags |= 0x1000L;
                    }
                }


                    // read album.videosCount == 0 ? 4 : 0
                    albumVideosCountInt0Int4Int0 = ((albumVideosCountInt0) ? (4) : (0));
                    // read album.picCount == 0 ? 4 : 0
                    albumPicCountInt0Int4Int0 = ((albumPicCountInt0) ? (4) : (0));
            }
            if ((dirtyFlags & 0xc1L) != 0) {

                    if (album != null) {
                        // read album.likesCount
                        albumLikesCount = album.getLikesCount();
                    }


                    // read String.valueOf(album.likesCount)
                    stringValueOfAlbumLikesCount = java.lang.String.valueOf(albumLikesCount);
            }
            if ((dirtyFlags & 0xa1L) != 0) {

                    if (album != null) {
                        // read album.unseenPhotos
                        albumUnseenPhotos = album.getUnseenPhotos();
                    }


                    // read String.valueOf(album.unseenPhotos)
                    stringValueOfAlbumUnseenPhotos = java.lang.String.valueOf(albumUnseenPhotos);
            }
        }
        // batch finished
        if ((dirtyFlags & 0x81L) != 0) {
            // api target 1

            androidx.databinding.adapters.TextViewBindingAdapter.setText(this.albumDate, dateFormatterFormatForAlbumListAlbumAlbumDate);
            androidx.databinding.adapters.TextViewBindingAdapter.setText(this.albumTitle, albumAlbumName);
            androidx.databinding.adapters.TextViewBindingAdapter.setText(this.numComments, stringValueOfAlbumCommentsCount);
            this.numPhotos.setVisibility(albumPicCountInt0Int4Int0);
            this.numPicsDot.setVisibility(albumPicCountInt0Int4Int0);
            this.numVideosDot.setVisibility(albumVideosCountInt0Int4Int0);
            androidx.databinding.adapters.TextViewBindingAdapter.setText(this.numViewsText, stringValueOfAlbumAlbumViews);
        }
        if ((dirtyFlags & 0x88L) != 0) {
            // api target 1

            com.ganbook.utils.binding_utils.PictureBinding.loadPicFromPath(this.itemImage, preview);
        }
        if ((dirtyFlags & 0x82L) != 0) {
            // api target 1

            this.newImage.setVisibility(showNewLabelInt4Int0);
            this.notifyLayout.setVisibility(showNewLabelInt4Int0);
        }
        if ((dirtyFlags & 0xc1L) != 0) {
            // api target 1

            androidx.databinding.adapters.TextViewBindingAdapter.setText(this.numLikes, stringValueOfAlbumLikesCount);
        }
        if ((dirtyFlags & 0xa1L) != 0) {
            // api target 1

            androidx.databinding.adapters.TextViewBindingAdapter.setText(this.numNewText, stringValueOfAlbumUnseenPhotos);
        }
        if ((dirtyFlags & 0x90L) != 0) {
            // api target 1

            androidx.databinding.adapters.TextViewBindingAdapter.setText(this.numPhotos, photosTitle);
        }
        if ((dirtyFlags & 0x84L) != 0) {
            // api target 1

            androidx.databinding.adapters.TextViewBindingAdapter.setText(this.numVideos, videosTitle);
        }
    }
    // Listener Stub Implementations
    // callback impls
    // dirty flag
    private  long mDirtyFlags = 0xffffffffffffffffL;
    /* flag mapping
        flag 0 (0x1L): album
        flag 1 (0x2L): showNewLabel
        flag 2 (0x3L): videosTitle
        flag 3 (0x4L): preview
        flag 4 (0x5L): photosTitle
        flag 5 (0x6L): album.unseenPhotos
        flag 6 (0x7L): album.likesCount
        flag 7 (0x8L): null
        flag 8 (0x9L): !showNewLabel ? 4 : 0
        flag 9 (0xaL): !showNewLabel ? 4 : 0
        flag 10 (0xbL): album.videosCount == 0 ? 4 : 0
        flag 11 (0xcL): album.videosCount == 0 ? 4 : 0
        flag 12 (0xdL): album.picCount == 0 ? 4 : 0
        flag 13 (0xeL): album.picCount == 0 ? 4 : 0
    flag mapping end*/
    //end
}