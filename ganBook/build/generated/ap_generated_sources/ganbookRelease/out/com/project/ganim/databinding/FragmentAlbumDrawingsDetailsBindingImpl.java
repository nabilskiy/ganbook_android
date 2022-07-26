package com.project.ganim.databinding;
import com.project.ganim.R;
import com.project.ganim.BR;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.View;
@SuppressWarnings("unchecked")
@javax.annotation.Generated("Android Data Binding")
public class FragmentAlbumDrawingsDetailsBindingImpl extends FragmentAlbumDrawingsDetailsBinding  {

    @Nullable
    private static final androidx.databinding.ViewDataBinding.IncludedLayouts sIncludes;
    @Nullable
    private static final android.util.SparseIntArray sViewsWithIds;
    static {
        sIncludes = null;
        sViewsWithIds = new android.util.SparseIntArray();
        sViewsWithIds.put(R.id.drawing_gallery, 2);
        sViewsWithIds.put(R.id.album_detail_progressbar, 3);
        sViewsWithIds.put(R.id.bottom_bar, 4);
        sViewsWithIds.put(R.id.user_action_button, 5);
    }
    // views
    // variables
    // values
    // listeners
    // Inverse Binding Event Handlers

    public FragmentAlbumDrawingsDetailsBindingImpl(@Nullable androidx.databinding.DataBindingComponent bindingComponent, @NonNull View root) {
        this(bindingComponent, root, mapBindings(bindingComponent, root, 6, sIncludes, sViewsWithIds));
    }
    private FragmentAlbumDrawingsDetailsBindingImpl(androidx.databinding.DataBindingComponent bindingComponent, View root, Object[] bindings) {
        super(bindingComponent, root, 1
            , (android.widget.ProgressBar) bindings[3]
            , (android.widget.RelativeLayout) bindings[0]
            , (android.widget.RelativeLayout) bindings[4]
            , (android.widget.ImageView) bindings[1]
            , (androidx.recyclerview.widget.RecyclerView) bindings[2]
            , (android.widget.ImageView) bindings[5]
            );
        this.androidAlbumsFragment.setTag(null);
        this.deleteBtn.setTag(null);
        setRootTag(root);
        // listeners
        invalidateAll();
    }

    @Override
    public void invalidateAll() {
        synchronized(this) {
                mDirtyFlags = 0x10L;
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
            setAlbum((com.ganbook.models.DrawingAnswer) variable);
        }
        else if (BR.isParent == variableId) {
            setIsParent((boolean) variable);
        }
        else if (BR.showDelete == variableId) {
            setShowDelete((boolean) variable);
        }
        else if (BR.isStaff == variableId) {
            setIsStaff((boolean) variable);
        }
        else {
            variableSet = false;
        }
            return variableSet;
    }

    public void setAlbum(@Nullable com.ganbook.models.DrawingAnswer Album) {
        this.mAlbum = Album;
    }
    public void setIsParent(boolean IsParent) {
        this.mIsParent = IsParent;
    }
    public void setShowDelete(boolean ShowDelete) {
        this.mShowDelete = ShowDelete;
        synchronized(this) {
            mDirtyFlags |= 0x4L;
        }
        notifyPropertyChanged(BR.showDelete);
        super.requestRebind();
    }
    public void setIsStaff(boolean IsStaff) {
        this.mIsStaff = IsStaff;
    }

    @Override
    protected boolean onFieldChange(int localFieldId, Object object, int fieldId) {
        switch (localFieldId) {
            case 0 :
                return onChangeAlbum((com.ganbook.models.DrawingAnswer) object, fieldId);
        }
        return false;
    }
    private boolean onChangeAlbum(com.ganbook.models.DrawingAnswer Album, int fieldId) {
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
        boolean showDelete = mShowDelete;
        int showDeleteInt0Int8 = 0;

        if ((dirtyFlags & 0x14L) != 0) {

            if((dirtyFlags & 0x14L) != 0) {
                if(showDelete) {
                        dirtyFlags |= 0x40L;
                }
                else {
                        dirtyFlags |= 0x20L;
                }
            }


                // read showDelete ? 0 : 8
                showDeleteInt0Int8 = ((showDelete) ? (0) : (8));
        }
        // batch finished
        if ((dirtyFlags & 0x14L) != 0) {
            // api target 1

            this.deleteBtn.setVisibility(showDeleteInt0Int8);
        }
    }
    // Listener Stub Implementations
    // callback impls
    // dirty flag
    private  long mDirtyFlags = 0xffffffffffffffffL;
    /* flag mapping
        flag 0 (0x1L): album
        flag 1 (0x2L): isParent
        flag 2 (0x3L): showDelete
        flag 3 (0x4L): isStaff
        flag 4 (0x5L): null
        flag 5 (0x6L): showDelete ? 0 : 8
        flag 6 (0x7L): showDelete ? 0 : 8
    flag mapping end*/
    //end
}