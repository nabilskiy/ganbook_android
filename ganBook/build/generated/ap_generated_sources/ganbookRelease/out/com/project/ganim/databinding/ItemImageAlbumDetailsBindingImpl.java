package com.project.ganim.databinding;
import com.project.ganim.R;
import com.project.ganim.BR;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.View;
@SuppressWarnings("unchecked")
@javax.annotation.Generated("Android Data Binding")
public class ItemImageAlbumDetailsBindingImpl extends ItemImageAlbumDetailsBinding  {

    @Nullable
    private static final androidx.databinding.ViewDataBinding.IncludedLayouts sIncludes;
    @Nullable
    private static final android.util.SparseIntArray sViewsWithIds;
    static {
        sIncludes = null;
        sViewsWithIds = new android.util.SparseIntArray();
        sViewsWithIds.put(R.id.iv_image, 6);
        sViewsWithIds.put(R.id.photoDescriptionIndicator, 7);
    }
    // views
    @NonNull
    private final android.widget.RelativeLayout mboundView0;
    // variables
    // values
    // listeners
    private OnClickListenerImpl mHandlersOnRetryButtonClickedAndroidViewViewOnClickListener;
    // Inverse Binding Event Handlers

    public ItemImageAlbumDetailsBindingImpl(@Nullable androidx.databinding.DataBindingComponent bindingComponent, @NonNull View root) {
        this(bindingComponent, root, mapBindings(bindingComponent, root, 8, sIncludes, sViewsWithIds));
    }
    private ItemImageAlbumDetailsBindingImpl(androidx.databinding.DataBindingComponent bindingComponent, View root, Object[] bindings) {
        super(bindingComponent, root, 1
            , (android.widget.TextView) bindings[3]
            , (android.widget.ImageView) bindings[6]
            , (android.widget.ImageView) bindings[7]
            , (android.widget.ImageView) bindings[2]
            , (android.widget.CheckBox) bindings[5]
            , (android.widget.ProgressBar) bindings[1]
            , (android.widget.ImageView) bindings[4]
            );
        this.duration.setTag(null);
        this.mboundView0 = (android.widget.RelativeLayout) bindings[0];
        this.mboundView0.setTag(null);
        this.play.setTag(null);
        this.selectionCheck.setTag(null);
        this.uploadProgress.setTag(null);
        this.uploadStateImage.setTag(null);
        setRootTag(root);
        // listeners
        invalidateAll();
    }

    @Override
    public void invalidateAll() {
        synchronized(this) {
                mDirtyFlags = 0x20L;
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
        if (BR.image == variableId) {
            setImage((com.ganbook.models.PictureAnswer) variable);
        }
        else if (BR.selection == variableId) {
            setSelection((boolean) variable);
        }
        else if (BR.handlers == variableId) {
            setHandlers((com.ganbook.handlers.PictureAlbumDetailsHandlers) variable);
        }
        else {
            variableSet = false;
        }
            return variableSet;
    }

    public void setImage(@Nullable com.ganbook.models.PictureAnswer Image) {
        updateRegistration(0, Image);
        this.mImage = Image;
        synchronized(this) {
            mDirtyFlags |= 0x1L;
        }
        notifyPropertyChanged(BR.image);
        super.requestRebind();
    }
    public void setSelection(boolean Selection) {
        this.mSelection = Selection;
        synchronized(this) {
            mDirtyFlags |= 0x2L;
        }
        notifyPropertyChanged(BR.selection);
        super.requestRebind();
    }
    public void setHandlers(@Nullable com.ganbook.handlers.PictureAlbumDetailsHandlers Handlers) {
        this.mHandlers = Handlers;
        synchronized(this) {
            mDirtyFlags |= 0x4L;
        }
        notifyPropertyChanged(BR.handlers);
        super.requestRebind();
    }

    @Override
    protected boolean onFieldChange(int localFieldId, Object object, int fieldId) {
        switch (localFieldId) {
            case 0 :
                return onChangeImage((com.ganbook.models.PictureAnswer) object, fieldId);
        }
        return false;
    }
    private boolean onChangeImage(com.ganbook.models.PictureAnswer Image, int fieldId) {
        if (fieldId == BR._all) {
            synchronized(this) {
                    mDirtyFlags |= 0x1L;
            }
            return true;
        }
        else if (fieldId == BR.status) {
            synchronized(this) {
                    mDirtyFlags |= 0x8L;
            }
            return true;
        }
        else if (fieldId == BR.progress) {
            synchronized(this) {
                    mDirtyFlags |= 0x10L;
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
        com.ganbook.models.PictureAnswer image = mImage;
        boolean imageStatusInt2 = false;
        boolean imageSelected = false;
        boolean imageStatusInt0 = false;
        boolean imageVideoDurationJavaLangObjectNull = false;
        boolean selection = mSelection;
        int imageStatus = 0;
        int imageVideoDurationJavaLangObjectNullInt8Int0 = 0;
        int imageStatusInt0Int0Int8 = 0;
        java.lang.String imageVideoDuration = null;
        com.ganbook.handlers.PictureAlbumDetailsHandlers handlers = mHandlers;
        int imageProgress = 0;
        android.view.View.OnClickListener handlersOnRetryButtonClickedAndroidViewViewOnClickListener = null;
        int imageStatusInt2Int0Int8 = 0;
        int selectionInt0Int8 = 0;

        if ((dirtyFlags & 0x39L) != 0) {


            if ((dirtyFlags & 0x21L) != 0) {

                    if (image != null) {
                        // read image.selected
                        imageSelected = image.isSelected();
                        // read image.videoDuration
                        imageVideoDuration = image.getVideoDuration();
                    }


                    // read image.videoDuration == null
                    imageVideoDurationJavaLangObjectNull = (imageVideoDuration) == (null);
                if((dirtyFlags & 0x21L) != 0) {
                    if(imageVideoDurationJavaLangObjectNull) {
                            dirtyFlags |= 0x80L;
                    }
                    else {
                            dirtyFlags |= 0x40L;
                    }
                }


                    // read image.videoDuration == null ? 8 : 0
                    imageVideoDurationJavaLangObjectNullInt8Int0 = ((imageVideoDurationJavaLangObjectNull) ? (8) : (0));
            }
            if ((dirtyFlags & 0x29L) != 0) {

                    if (image != null) {
                        // read image.status
                        imageStatus = image.getStatus();
                    }


                    // read image.status == 2
                    imageStatusInt2 = (imageStatus) == (2);
                    // read image.status == 0
                    imageStatusInt0 = (imageStatus) == (0);
                if((dirtyFlags & 0x29L) != 0) {
                    if(imageStatusInt2) {
                            dirtyFlags |= 0x800L;
                    }
                    else {
                            dirtyFlags |= 0x400L;
                    }
                }
                if((dirtyFlags & 0x29L) != 0) {
                    if(imageStatusInt0) {
                            dirtyFlags |= 0x200L;
                    }
                    else {
                            dirtyFlags |= 0x100L;
                    }
                }


                    // read image.status == 2 ? 0 : 8
                    imageStatusInt2Int0Int8 = ((imageStatusInt2) ? (0) : (8));
                    // read image.status == 0 ? 0 : 8
                    imageStatusInt0Int0Int8 = ((imageStatusInt0) ? (0) : (8));
            }
            if ((dirtyFlags & 0x31L) != 0) {

                    if (image != null) {
                        // read image.progress
                        imageProgress = image.getProgress();
                    }
            }
        }
        if ((dirtyFlags & 0x22L) != 0) {

            if((dirtyFlags & 0x22L) != 0) {
                if(selection) {
                        dirtyFlags |= 0x2000L;
                }
                else {
                        dirtyFlags |= 0x1000L;
                }
            }


                // read selection ? 0 : 8
                selectionInt0Int8 = ((selection) ? (0) : (8));
        }
        if ((dirtyFlags & 0x24L) != 0) {



                if (handlers != null) {
                    // read handlers::onRetryButtonClicked
                    handlersOnRetryButtonClickedAndroidViewViewOnClickListener = (((mHandlersOnRetryButtonClickedAndroidViewViewOnClickListener == null) ? (mHandlersOnRetryButtonClickedAndroidViewViewOnClickListener = new OnClickListenerImpl()) : mHandlersOnRetryButtonClickedAndroidViewViewOnClickListener).setValue(handlers));
                }
        }
        // batch finished
        if ((dirtyFlags & 0x21L) != 0) {
            // api target 1

            androidx.databinding.adapters.TextViewBindingAdapter.setText(this.duration, imageVideoDuration);
            this.duration.setVisibility(imageVideoDurationJavaLangObjectNullInt8Int0);
            this.play.setVisibility(imageVideoDurationJavaLangObjectNullInt8Int0);
            androidx.databinding.adapters.CompoundButtonBindingAdapter.setChecked(this.selectionCheck, imageSelected);
        }
        if ((dirtyFlags & 0x22L) != 0) {
            // api target 1

            this.selectionCheck.setVisibility(selectionInt0Int8);
        }
        if ((dirtyFlags & 0x29L) != 0) {
            // api target 1

            this.uploadProgress.setVisibility(imageStatusInt0Int0Int8);
            this.uploadStateImage.setVisibility(imageStatusInt2Int0Int8);
        }
        if ((dirtyFlags & 0x31L) != 0) {
            // api target 1

            this.uploadProgress.setProgress(imageProgress);
        }
        if ((dirtyFlags & 0x24L) != 0) {
            // api target 1

            this.uploadStateImage.setOnClickListener(handlersOnRetryButtonClickedAndroidViewViewOnClickListener);
        }
    }
    // Listener Stub Implementations
    public static class OnClickListenerImpl implements android.view.View.OnClickListener{
        private com.ganbook.handlers.PictureAlbumDetailsHandlers value;
        public OnClickListenerImpl setValue(com.ganbook.handlers.PictureAlbumDetailsHandlers value) {
            this.value = value;
            return value == null ? null : this;
        }
        @Override
        public void onClick(android.view.View arg0) {
            this.value.onRetryButtonClicked(arg0); 
        }
    }
    // callback impls
    // dirty flag
    private  long mDirtyFlags = 0xffffffffffffffffL;
    /* flag mapping
        flag 0 (0x1L): image
        flag 1 (0x2L): selection
        flag 2 (0x3L): handlers
        flag 3 (0x4L): image.status
        flag 4 (0x5L): image.progress
        flag 5 (0x6L): null
        flag 6 (0x7L): image.videoDuration == null ? 8 : 0
        flag 7 (0x8L): image.videoDuration == null ? 8 : 0
        flag 8 (0x9L): image.status == 0 ? 0 : 8
        flag 9 (0xaL): image.status == 0 ? 0 : 8
        flag 10 (0xbL): image.status == 2 ? 0 : 8
        flag 11 (0xcL): image.status == 2 ? 0 : 8
        flag 12 (0xdL): selection ? 0 : 8
        flag 13 (0xeL): selection ? 0 : 8
    flag mapping end*/
    //end
}