package com.project.ganim.databinding;
import com.project.ganim.R;
import com.project.ganim.BR;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.View;
@SuppressWarnings("unchecked")
@javax.annotation.Generated("Android Data Binding")
public class ItemGridGalleryBindingImpl extends ItemGridGalleryBinding  {

    @Nullable
    private static final androidx.databinding.ViewDataBinding.IncludedLayouts sIncludes;
    @Nullable
    private static final android.util.SparseIntArray sViewsWithIds;
    static {
        sIncludes = null;
        sViewsWithIds = new android.util.SparseIntArray();
        sViewsWithIds.put(R.id.imageView, 4);
    }
    // views
    @NonNull
    private final android.widget.RelativeLayout mboundView0;
    // variables
    // values
    // listeners
    // Inverse Binding Event Handlers

    public ItemGridGalleryBindingImpl(@Nullable androidx.databinding.DataBindingComponent bindingComponent, @NonNull View root) {
        this(bindingComponent, root, mapBindings(bindingComponent, root, 5, sIncludes, sViewsWithIds));
    }
    private ItemGridGalleryBindingImpl(androidx.databinding.DataBindingComponent bindingComponent, View root, Object[] bindings) {
        super(bindingComponent, root, 1
            , (android.widget.ImageView) bindings[4]
            , (android.widget.ImageView) bindings[2]
            , (android.widget.CheckBox) bindings[1]
            , (android.widget.TextView) bindings[3]
            );
        this.indicator.setTag(null);
        this.mboundView0 = (android.widget.RelativeLayout) bindings[0];
        this.mboundView0.setTag(null);
        this.selectionCheck.setTag(null);
        this.textView.setTag(null);
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
        if (BR.gridPicture == variableId) {
            setGridPicture((com.ganbook.models.GridViewPicture) variable);
        }
        else {
            variableSet = false;
        }
            return variableSet;
    }

    public void setGridPicture(@Nullable com.ganbook.models.GridViewPicture GridPicture) {
        updateRegistration(0, GridPicture);
        this.mGridPicture = GridPicture;
        synchronized(this) {
            mDirtyFlags |= 0x1L;
        }
        notifyPropertyChanged(BR.gridPicture);
        super.requestRebind();
    }

    @Override
    protected boolean onFieldChange(int localFieldId, Object object, int fieldId) {
        switch (localFieldId) {
            case 0 :
                return onChangeGridPicture((com.ganbook.models.GridViewPicture) object, fieldId);
        }
        return false;
    }
    private boolean onChangeGridPicture(com.ganbook.models.GridViewPicture GridPicture, int fieldId) {
        if (fieldId == BR._all) {
            synchronized(this) {
                    mDirtyFlags |= 0x1L;
            }
            return true;
        }
        else if (fieldId == BR.selected) {
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
        int gridPictureIsDirectoryInt8Int0 = 0;
        com.ganbook.models.GridViewPicture gridPicture = mGridPicture;
        boolean gridPictureIsDirectory = false;
        boolean gridPictureSelected = false;
        java.lang.String gridPictureFileName = null;
        android.graphics.drawable.Drawable gridPictureIsDirectoryIndicatorAndroidDrawableIcFolderOpenWhite24dpJavaLangObjectNull = null;

        if ((dirtyFlags & 0x7L) != 0) {


            if ((dirtyFlags & 0x5L) != 0) {

                    if (gridPicture != null) {
                        // read gridPicture.isDirectory
                        gridPictureIsDirectory = gridPicture.isDirectory();
                        // read gridPicture.fileName
                        gridPictureFileName = gridPicture.getFileName();
                    }
                if((dirtyFlags & 0x5L) != 0) {
                    if(gridPictureIsDirectory) {
                            dirtyFlags |= 0x10L;
                            dirtyFlags |= 0x40L;
                    }
                    else {
                            dirtyFlags |= 0x8L;
                            dirtyFlags |= 0x20L;
                    }
                }


                    // read gridPicture.isDirectory ? 8 : 0
                    gridPictureIsDirectoryInt8Int0 = ((gridPictureIsDirectory) ? (8) : (0));
                    // read gridPicture.isDirectory ? @android:drawable/ic_folder_open_white_24dp : null
                    gridPictureIsDirectoryIndicatorAndroidDrawableIcFolderOpenWhite24dpJavaLangObjectNull = ((gridPictureIsDirectory) ? (androidx.appcompat.content.res.AppCompatResources.getDrawable(indicator.getContext(), R.drawable.ic_folder_open_white_24dp)) : (null));
            }

                if (gridPicture != null) {
                    // read gridPicture.selected
                    gridPictureSelected = gridPicture.isSelected();
                }
        }
        // batch finished
        if ((dirtyFlags & 0x5L) != 0) {
            // api target 1

            androidx.databinding.adapters.ImageViewBindingAdapter.setImageDrawable(this.indicator, gridPictureIsDirectoryIndicatorAndroidDrawableIcFolderOpenWhite24dpJavaLangObjectNull);
            this.selectionCheck.setVisibility(gridPictureIsDirectoryInt8Int0);
            androidx.databinding.adapters.TextViewBindingAdapter.setText(this.textView, gridPictureFileName);
        }
        if ((dirtyFlags & 0x7L) != 0) {
            // api target 1

            androidx.databinding.adapters.CompoundButtonBindingAdapter.setChecked(this.selectionCheck, gridPictureSelected);
        }
    }
    // Listener Stub Implementations
    // callback impls
    // dirty flag
    private  long mDirtyFlags = 0xffffffffffffffffL;
    /* flag mapping
        flag 0 (0x1L): gridPicture
        flag 1 (0x2L): gridPicture.selected
        flag 2 (0x3L): null
        flag 3 (0x4L): gridPicture.isDirectory ? 8 : 0
        flag 4 (0x5L): gridPicture.isDirectory ? 8 : 0
        flag 5 (0x6L): gridPicture.isDirectory ? @android:drawable/ic_folder_open_white_24dp : null
        flag 6 (0x7L): gridPicture.isDirectory ? @android:drawable/ic_folder_open_white_24dp : null
    flag mapping end*/
    //end
}