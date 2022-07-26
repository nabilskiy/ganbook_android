package com.project.ganim.databinding;
import com.project.ganim.R;
import com.project.ganim.BR;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.View;
@SuppressWarnings("unchecked")
@javax.annotation.Generated("Android Data Binding")
public class LikeLayoutBindingImpl extends LikeLayoutBinding  {

    @Nullable
    private static final androidx.databinding.ViewDataBinding.IncludedLayouts sIncludes;
    @Nullable
    private static final android.util.SparseIntArray sViewsWithIds;
    static {
        sIncludes = null;
        sViewsWithIds = new android.util.SparseIntArray();
        sViewsWithIds.put(R.id.heart_inactive, 1);
        sViewsWithIds.put(R.id.heart_active, 2);
    }
    // views
    // variables
    // values
    // listeners
    private OnClickListenerImpl mHandlersLikeSwitcherClickAndroidViewViewOnClickListener;
    // Inverse Binding Event Handlers

    public LikeLayoutBindingImpl(@Nullable androidx.databinding.DataBindingComponent bindingComponent, @NonNull View root) {
        this(bindingComponent, root, mapBindings(bindingComponent, root, 3, sIncludes, sViewsWithIds));
    }
    private LikeLayoutBindingImpl(androidx.databinding.DataBindingComponent bindingComponent, View root, Object[] bindings) {
        super(bindingComponent, root, 1
            , (android.widget.ImageView) bindings[2]
            , (android.widget.ImageView) bindings[1]
            , (android.widget.ViewSwitcher) bindings[0]
            );
        this.switcher.setTag(null);
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
        if (BR.album == variableId) {
            setAlbum((com.ganbook.models.AlbumsAnswer) variable);
        }
        else if (BR.handlers == variableId) {
            setHandlers((com.ganbook.handlers.AlbumDetailsHandlers) variable);
        }
        else {
            variableSet = false;
        }
            return variableSet;
    }

    public void setAlbum(@Nullable com.ganbook.models.AlbumsAnswer Album) {
        this.mAlbum = Album;
    }
    public void setHandlers(@Nullable com.ganbook.handlers.AlbumDetailsHandlers Handlers) {
        this.mHandlers = Handlers;
        synchronized(this) {
            mDirtyFlags |= 0x2L;
        }
        notifyPropertyChanged(BR.handlers);
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
        return false;
    }

    @Override
    protected void executeBindings() {
        long dirtyFlags = 0;
        synchronized(this) {
            dirtyFlags = mDirtyFlags;
            mDirtyFlags = 0;
        }
        com.ganbook.handlers.AlbumDetailsHandlers handlers = mHandlers;
        android.view.View.OnClickListener handlersLikeSwitcherClickAndroidViewViewOnClickListener = null;

        if ((dirtyFlags & 0x6L) != 0) {



                if (handlers != null) {
                    // read handlers::likeSwitcherClick
                    handlersLikeSwitcherClickAndroidViewViewOnClickListener = (((mHandlersLikeSwitcherClickAndroidViewViewOnClickListener == null) ? (mHandlersLikeSwitcherClickAndroidViewViewOnClickListener = new OnClickListenerImpl()) : mHandlersLikeSwitcherClickAndroidViewViewOnClickListener).setValue(handlers));
                }
        }
        // batch finished
        if ((dirtyFlags & 0x6L) != 0) {
            // api target 1

            this.switcher.setOnClickListener(handlersLikeSwitcherClickAndroidViewViewOnClickListener);
        }
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
            this.value.likeSwitcherClick(arg0); 
        }
    }
    // callback impls
    // dirty flag
    private  long mDirtyFlags = 0xffffffffffffffffL;
    /* flag mapping
        flag 0 (0x1L): album
        flag 1 (0x2L): handlers
        flag 2 (0x3L): null
    flag mapping end*/
    //end
}