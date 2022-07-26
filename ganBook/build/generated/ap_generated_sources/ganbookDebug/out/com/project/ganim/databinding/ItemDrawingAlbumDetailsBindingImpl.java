package com.project.ganim.databinding;
import com.project.ganim.R;
import com.project.ganim.BR;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.View;
@SuppressWarnings("unchecked")
@javax.annotation.Generated("Android Data Binding")
public class ItemDrawingAlbumDetailsBindingImpl extends ItemDrawingAlbumDetailsBinding  {

    @Nullable
    private static final androidx.databinding.ViewDataBinding.IncludedLayouts sIncludes;
    @Nullable
    private static final android.util.SparseIntArray sViewsWithIds;
    static {
        sIncludes = null;
        sViewsWithIds = new android.util.SparseIntArray();
        sViewsWithIds.put(R.id.drawing_single_image, 2);
        sViewsWithIds.put(R.id.upload_progress, 3);
    }
    // views
    @NonNull
    private final android.widget.RelativeLayout mboundView0;
    // variables
    // values
    // listeners
    // Inverse Binding Event Handlers

    public ItemDrawingAlbumDetailsBindingImpl(@Nullable androidx.databinding.DataBindingComponent bindingComponent, @NonNull View root) {
        this(bindingComponent, root, mapBindings(bindingComponent, root, 4, sIncludes, sViewsWithIds));
    }
    private ItemDrawingAlbumDetailsBindingImpl(androidx.databinding.DataBindingComponent bindingComponent, View root, Object[] bindings) {
        super(bindingComponent, root, 1
            , (android.widget.ImageView) bindings[2]
            , (android.widget.CheckBox) bindings[1]
            , (android.widget.ProgressBar) bindings[3]
            );
        this.mboundView0 = (android.widget.RelativeLayout) bindings[0];
        this.mboundView0.setTag(null);
        this.selectionCheck.setTag(null);
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
        if (BR.selection == variableId) {
            setSelection((boolean) variable);
        }
        else if (BR.drawing == variableId) {
            setDrawing((com.ganbook.models.DrawingAnswer) variable);
        }
        else {
            variableSet = false;
        }
            return variableSet;
    }

    public void setSelection(boolean Selection) {
        this.mSelection = Selection;
        synchronized(this) {
            mDirtyFlags |= 0x2L;
        }
        notifyPropertyChanged(BR.selection);
        super.requestRebind();
    }
    public void setDrawing(@Nullable com.ganbook.models.DrawingAnswer Drawing) {
        updateRegistration(0, Drawing);
        this.mDrawing = Drawing;
        synchronized(this) {
            mDirtyFlags |= 0x1L;
        }
        notifyPropertyChanged(BR.drawing);
        super.requestRebind();
    }

    @Override
    protected boolean onFieldChange(int localFieldId, Object object, int fieldId) {
        switch (localFieldId) {
            case 0 :
                return onChangeDrawing((com.ganbook.models.DrawingAnswer) object, fieldId);
        }
        return false;
    }
    private boolean onChangeDrawing(com.ganbook.models.DrawingAnswer Drawing, int fieldId) {
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
        boolean selection = mSelection;
        boolean drawingSelected = false;
        int selectionInt0Int8 = 0;
        com.ganbook.models.DrawingAnswer drawing = mDrawing;

        if ((dirtyFlags & 0x6L) != 0) {

            if((dirtyFlags & 0x6L) != 0) {
                if(selection) {
                        dirtyFlags |= 0x10L;
                }
                else {
                        dirtyFlags |= 0x8L;
                }
            }


                // read selection ? 0 : 8
                selectionInt0Int8 = ((selection) ? (0) : (8));
        }
        if ((dirtyFlags & 0x5L) != 0) {



                if (drawing != null) {
                    // read drawing.selected
                    drawingSelected = drawing.isSelected();
                }
        }
        // batch finished
        if ((dirtyFlags & 0x6L) != 0) {
            // api target 1

            this.selectionCheck.setVisibility(selectionInt0Int8);
        }
        if ((dirtyFlags & 0x5L) != 0) {
            // api target 1

            androidx.databinding.adapters.CompoundButtonBindingAdapter.setChecked(this.selectionCheck, drawingSelected);
        }
    }
    // Listener Stub Implementations
    // callback impls
    // dirty flag
    private  long mDirtyFlags = 0xffffffffffffffffL;
    /* flag mapping
        flag 0 (0x1L): drawing
        flag 1 (0x2L): selection
        flag 2 (0x3L): null
        flag 3 (0x4L): selection ? 0 : 8
        flag 4 (0x5L): selection ? 0 : 8
    flag mapping end*/
    //end
}