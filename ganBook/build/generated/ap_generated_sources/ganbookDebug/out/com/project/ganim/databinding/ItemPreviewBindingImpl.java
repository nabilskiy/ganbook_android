package com.project.ganim.databinding;
import com.project.ganim.R;
import com.project.ganim.BR;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.View;
@SuppressWarnings("unchecked")
@javax.annotation.Generated("Android Data Binding")
public class ItemPreviewBindingImpl extends ItemPreviewBinding  {

    @Nullable
    private static final androidx.databinding.ViewDataBinding.IncludedLayouts sIncludes;
    @Nullable
    private static final android.util.SparseIntArray sViewsWithIds;
    static {
        sIncludes = null;
        sViewsWithIds = null;
    }
    // views
    @NonNull
    private final android.widget.ImageView mboundView0;
    // variables
    // values
    // listeners
    // Inverse Binding Event Handlers

    public ItemPreviewBindingImpl(@Nullable androidx.databinding.DataBindingComponent bindingComponent, @NonNull View root) {
        this(bindingComponent, root, mapBindings(bindingComponent, root, 1, sIncludes, sViewsWithIds));
    }
    private ItemPreviewBindingImpl(androidx.databinding.DataBindingComponent bindingComponent, View root, Object[] bindings) {
        super(bindingComponent, root, 1
            );
        this.mboundView0 = (android.widget.ImageView) bindings[0];
        this.mboundView0.setTag(null);
        setRootTag(root);
        // listeners
        invalidateAll();
    }

    @Override
    public void invalidateAll() {
        synchronized(this) {
                mDirtyFlags = 0x4L;
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
        if (BR.media == variableId) {
            setMedia((com.ganbook.models.MediaFile) variable);
        }
        else {
            variableSet = false;
        }
            return variableSet;
    }

    public void setMedia(@Nullable com.ganbook.models.MediaFile Media) {
        updateRegistration(0, Media);
        this.mMedia = Media;
        synchronized(this) {
            mDirtyFlags |= 0x1L;
        }
        notifyPropertyChanged(BR.media);
        super.requestRebind();
    }

    @Override
    protected boolean onFieldChange(int localFieldId, Object object, int fieldId) {
        switch (localFieldId) {
            case 0 :
                return onChangeMedia((com.ganbook.models.MediaFile) object, fieldId);
        }
        return false;
    }
    private boolean onChangeMedia(com.ganbook.models.MediaFile Media, int fieldId) {
        if (fieldId == BR._all) {
            synchronized(this) {
                    mDirtyFlags |= 0x1L;
            }
            return true;
        }
        else if (fieldId == BR.highlight) {
            synchronized(this) {
                    mDirtyFlags |= 0x2L;
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
        com.ganbook.models.MediaFile media = mMedia;
        boolean mediaHighlight = false;
        java.lang.String mediaFilePath = null;
        android.graphics.drawable.Drawable mediaHighlightMboundView0AndroidDrawablePreviewFrameJavaLangObjectNull = null;

        if ((dirtyFlags & 0x7L) != 0) {



                if (media != null) {
                    // read media.highlight
                    mediaHighlight = media.isHighlight();
                }
            if((dirtyFlags & 0x7L) != 0) {
                if(mediaHighlight) {
                        dirtyFlags |= 0x10L;
                }
                else {
                        dirtyFlags |= 0x8L;
                }
            }


                // read media.highlight ? @android:drawable/preview_frame : null
                mediaHighlightMboundView0AndroidDrawablePreviewFrameJavaLangObjectNull = ((mediaHighlight) ? (androidx.appcompat.content.res.AppCompatResources.getDrawable(mboundView0.getContext(), R.drawable.preview_frame)) : (null));
            if ((dirtyFlags & 0x5L) != 0) {

                    if (media != null) {
                        // read media.filePath
                        mediaFilePath = media.getFilePath();
                    }
            }
        }
        // batch finished
        if ((dirtyFlags & 0x7L) != 0) {
            // api target 1

            androidx.databinding.adapters.ViewBindingAdapter.setBackground(this.mboundView0, mediaHighlightMboundView0AndroidDrawablePreviewFrameJavaLangObjectNull);
        }
        if ((dirtyFlags & 0x5L) != 0) {
            // api target 1

            com.ganbook.utils.binding_utils.PictureBinding.loadMedia(this.mboundView0, mediaFilePath);
        }
    }
    // Listener Stub Implementations
    // callback impls
    // dirty flag
    private  long mDirtyFlags = 0xffffffffffffffffL;
    /* flag mapping
        flag 0 (0x1L): media
        flag 1 (0x2L): media.highlight
        flag 2 (0x3L): null
        flag 3 (0x4L): media.highlight ? @android:drawable/preview_frame : null
        flag 4 (0x5L): media.highlight ? @android:drawable/preview_frame : null
    flag mapping end*/
    //end
}