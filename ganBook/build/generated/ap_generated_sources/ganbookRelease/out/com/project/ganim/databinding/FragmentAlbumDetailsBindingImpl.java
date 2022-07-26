package com.project.ganim.databinding;
import com.project.ganim.R;
import com.project.ganim.BR;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.View;
@SuppressWarnings("unchecked")
@javax.annotation.Generated("Android Data Binding")
public class FragmentAlbumDetailsBindingImpl extends FragmentAlbumDetailsBinding  {

    @Nullable
    private static final androidx.databinding.ViewDataBinding.IncludedLayouts sIncludes;
    @Nullable
    private static final android.util.SparseIntArray sViewsWithIds;
    static {
        sIncludes = new androidx.databinding.ViewDataBinding.IncludedLayouts(16);
        sIncludes.setIncludes(6, 
            new String[] {"like_layout"},
            new int[] {7},
            new int[] {com.project.ganim.R.layout.like_layout});
        sViewsWithIds = new android.util.SparseIntArray();
        sViewsWithIds.put(R.id.images_gallery, 8);
        sViewsWithIds.put(R.id.album_detail_progressbar, 9);
        sViewsWithIds.put(R.id.bottom_bar, 10);
        sViewsWithIds.put(R.id.user_action_button, 11);
        sViewsWithIds.put(R.id.set_thumb_button, 12);
        sViewsWithIds.put(R.id.add_thumb, 13);
        sViewsWithIds.put(R.id.comments_layout, 14);
        sViewsWithIds.put(R.id.likes_layout, 15);
    }
    // views
    @NonNull
    private final android.widget.RelativeLayout mboundView6;
    @Nullable
    private final com.project.ganim.databinding.LikeLayoutBinding mboundView61;
    // variables
    // values
    // listeners
    private OnClickListenerImpl mHandlersOnTryAgainClickAndroidViewViewOnClickListener;
    private OnClickListenerImpl1 mHandlersOpenCommentsClickAndroidViewViewOnClickListener;
    private OnClickListenerImpl2 mHandlersOnDeleteImagesClickAndroidViewViewOnClickListener;
    // Inverse Binding Event Handlers

    public FragmentAlbumDetailsBindingImpl(@Nullable androidx.databinding.DataBindingComponent bindingComponent, @NonNull View root) {
        this(bindingComponent, root, mapBindings(bindingComponent, root, 16, sIncludes, sViewsWithIds));
    }
    private FragmentAlbumDetailsBindingImpl(androidx.databinding.DataBindingComponent bindingComponent, View root, Object[] bindings) {
        super(bindingComponent, root, 1
            , (android.widget.ImageView) bindings[13]
            , (android.widget.ProgressBar) bindings[9]
            , (android.widget.RelativeLayout) bindings[0]
            , (android.widget.RelativeLayout) bindings[10]
            , (android.widget.ImageView) bindings[4]
            , (android.widget.LinearLayout) bindings[14]
            , (android.widget.ImageView) bindings[2]
            , (androidx.recyclerview.widget.RecyclerView) bindings[8]
            , (android.widget.LinearLayout) bindings[15]
            , (android.widget.TextView) bindings[3]
            , (android.widget.TextView) bindings[5]
            , (android.widget.ImageView) bindings[12]
            , (android.widget.ImageView) bindings[1]
            , (android.widget.ImageView) bindings[11]
            );
        this.androidAlbumsFragment.setTag(null);
        this.commentsImage.setTag(null);
        this.deleteBtn.setTag(null);
        this.mboundView6 = (android.widget.RelativeLayout) bindings[6];
        this.mboundView6.setTag(null);
        this.mboundView61 = (com.project.ganim.databinding.LikeLayoutBinding) bindings[7];
        setContainedBinding(this.mboundView61);
        this.numComments.setTag(null);
        this.numLikes.setTag(null);
        this.tryAgainBtn.setTag(null);
        setRootTag(root);
        // listeners
        invalidateAll();
    }

    @Override
    public void invalidateAll() {
        synchronized(this) {
                mDirtyFlags = 0x40L;
        }
        mboundView61.invalidateAll();
        requestRebind();
    }

    @Override
    public boolean hasPendingBindings() {
        synchronized(this) {
            if (mDirtyFlags != 0) {
                return true;
            }
        }
        if (mboundView61.hasPendingBindings()) {
            return true;
        }
        return false;
    }

    @Override
    public boolean setVariable(int variableId, @Nullable Object variable)  {
        boolean variableSet = true;
        if (BR.isStaff == variableId) {
            setIsStaff((boolean) variable);
        }
        else if (BR.album == variableId) {
            setAlbum((com.ganbook.models.AlbumsAnswer) variable);
        }
        else if (BR.isParent == variableId) {
            setIsParent((boolean) variable);
        }
        else if (BR.showDelete == variableId) {
            setShowDelete((boolean) variable);
        }
        else if (BR.handlers == variableId) {
            setHandlers((com.ganbook.handlers.AlbumDetailsHandlers) variable);
        }
        else {
            variableSet = false;
        }
            return variableSet;
    }

    public void setIsStaff(boolean IsStaff) {
        this.mIsStaff = IsStaff;
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
    public void setIsParent(boolean IsParent) {
        this.mIsParent = IsParent;
    }
    public void setShowDelete(boolean ShowDelete) {
        this.mShowDelete = ShowDelete;
        synchronized(this) {
            mDirtyFlags |= 0x8L;
        }
        notifyPropertyChanged(BR.showDelete);
        super.requestRebind();
    }
    public void setHandlers(@Nullable com.ganbook.handlers.AlbumDetailsHandlers Handlers) {
        this.mHandlers = Handlers;
        synchronized(this) {
            mDirtyFlags |= 0x10L;
        }
        notifyPropertyChanged(BR.handlers);
        super.requestRebind();
    }

    @Override
    public void setLifecycleOwner(@Nullable androidx.lifecycle.LifecycleOwner lifecycleOwner) {
        super.setLifecycleOwner(lifecycleOwner);
        mboundView61.setLifecycleOwner(lifecycleOwner);
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
        else if (fieldId == BR.likesCount) {
            synchronized(this) {
                    mDirtyFlags |= 0x20L;
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
        int albumCommentsCount = 0;
        java.lang.String stringValueOfAlbumCommentsCount = null;
        int albumLikesCount = 0;
        com.ganbook.models.AlbumsAnswer album = mAlbum;
        android.view.View.OnClickListener handlersOnTryAgainClickAndroidViewViewOnClickListener = null;
        android.view.View.OnClickListener handlersOpenCommentsClickAndroidViewViewOnClickListener = null;
        boolean showDelete = mShowDelete;
        android.view.View.OnClickListener handlersOnDeleteImagesClickAndroidViewViewOnClickListener = null;
        com.ganbook.handlers.AlbumDetailsHandlers handlers = mHandlers;
        int showDeleteInt0Int8 = 0;
        java.lang.String stringValueOfAlbumLikesCount = null;

        if ((dirtyFlags & 0x61L) != 0) {


            if ((dirtyFlags & 0x41L) != 0) {

                    if (album != null) {
                        // read album.commentsCount
                        albumCommentsCount = album.getCommentsCount();
                    }


                    // read String.valueOf(album.commentsCount)
                    stringValueOfAlbumCommentsCount = java.lang.String.valueOf(albumCommentsCount);
            }

                if (album != null) {
                    // read album.likesCount
                    albumLikesCount = album.getLikesCount();
                }


                // read String.valueOf(album.likesCount)
                stringValueOfAlbumLikesCount = java.lang.String.valueOf(albumLikesCount);
        }
        if ((dirtyFlags & 0x48L) != 0) {

            if((dirtyFlags & 0x48L) != 0) {
                if(showDelete) {
                        dirtyFlags |= 0x100L;
                }
                else {
                        dirtyFlags |= 0x80L;
                }
            }


                // read showDelete ? 0 : 8
                showDeleteInt0Int8 = ((showDelete) ? (0) : (8));
        }
        if ((dirtyFlags & 0x50L) != 0) {



                if (handlers != null) {
                    // read handlers::onTryAgainClick
                    handlersOnTryAgainClickAndroidViewViewOnClickListener = (((mHandlersOnTryAgainClickAndroidViewViewOnClickListener == null) ? (mHandlersOnTryAgainClickAndroidViewViewOnClickListener = new OnClickListenerImpl()) : mHandlersOnTryAgainClickAndroidViewViewOnClickListener).setValue(handlers));
                    // read handlers::openCommentsClick
                    handlersOpenCommentsClickAndroidViewViewOnClickListener = (((mHandlersOpenCommentsClickAndroidViewViewOnClickListener == null) ? (mHandlersOpenCommentsClickAndroidViewViewOnClickListener = new OnClickListenerImpl1()) : mHandlersOpenCommentsClickAndroidViewViewOnClickListener).setValue(handlers));
                    // read handlers::onDeleteImagesClick
                    handlersOnDeleteImagesClickAndroidViewViewOnClickListener = (((mHandlersOnDeleteImagesClickAndroidViewViewOnClickListener == null) ? (mHandlersOnDeleteImagesClickAndroidViewViewOnClickListener = new OnClickListenerImpl2()) : mHandlersOnDeleteImagesClickAndroidViewViewOnClickListener).setValue(handlers));
                }
        }
        // batch finished
        if ((dirtyFlags & 0x50L) != 0) {
            // api target 1

            this.commentsImage.setOnClickListener(handlersOpenCommentsClickAndroidViewViewOnClickListener);
            this.deleteBtn.setOnClickListener(handlersOnDeleteImagesClickAndroidViewViewOnClickListener);
            this.mboundView61.setHandlers(handlers);
            this.tryAgainBtn.setOnClickListener(handlersOnTryAgainClickAndroidViewViewOnClickListener);
        }
        if ((dirtyFlags & 0x48L) != 0) {
            // api target 1

            this.deleteBtn.setVisibility(showDeleteInt0Int8);
        }
        if ((dirtyFlags & 0x41L) != 0) {
            // api target 1

            this.mboundView61.setAlbum(album);
            androidx.databinding.adapters.TextViewBindingAdapter.setText(this.numComments, stringValueOfAlbumCommentsCount);
        }
        if ((dirtyFlags & 0x61L) != 0) {
            // api target 1

            androidx.databinding.adapters.TextViewBindingAdapter.setText(this.numLikes, stringValueOfAlbumLikesCount);
        }
        executeBindingsOn(mboundView61);
    }
    // Listener Stub Implementations
    public static class OnClickListenerImpl implements android.view.View.OnClickListener{
        private com.ganbook.handlers.AlbumDetailsHandlers value;
        public OnClickListenerImpl setValue(com.ganbook.handlers.AlbumDetailsHandlers value) {
            this.value = value;
            return value == null ? null : this;
        }
        @Override
        public void onClick(android.view.View arg0) {
            this.value.onTryAgainClick(arg0); 
        }
    }
    public static class OnClickListenerImpl1 implements android.view.View.OnClickListener{
        private com.ganbook.handlers.AlbumDetailsHandlers value;
        public OnClickListenerImpl1 setValue(com.ganbook.handlers.AlbumDetailsHandlers value) {
            this.value = value;
            return value == null ? null : this;
        }
        @Override
        public void onClick(android.view.View arg0) {
            this.value.openCommentsClick(arg0); 
        }
    }
    public static class OnClickListenerImpl2 implements android.view.View.OnClickListener{
        private com.ganbook.handlers.AlbumDetailsHandlers value;
        public OnClickListenerImpl2 setValue(com.ganbook.handlers.AlbumDetailsHandlers value) {
            this.value = value;
            return value == null ? null : this;
        }
        @Override
        public void onClick(android.view.View arg0) {
            this.value.onDeleteImagesClick(arg0); 
        }
    }
    // callback impls
    // dirty flag
    private  long mDirtyFlags = 0xffffffffffffffffL;
    /* flag mapping
        flag 0 (0x1L): album
        flag 1 (0x2L): isStaff
        flag 2 (0x3L): isParent
        flag 3 (0x4L): showDelete
        flag 4 (0x5L): handlers
        flag 5 (0x6L): album.likesCount
        flag 6 (0x7L): null
        flag 7 (0x8L): showDelete ? 0 : 8
        flag 8 (0x9L): showDelete ? 0 : 8
    flag mapping end*/
    //end
}