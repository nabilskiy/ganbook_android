package com.project.ganim.databinding;
import com.project.ganim.R;
import com.project.ganim.BR;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.View;
@SuppressWarnings("unchecked")
@javax.annotation.Generated("Android Data Binding")
public class FragmentMessagesBindingImpl extends FragmentMessagesBinding  {

    @Nullable
    private static final androidx.databinding.ViewDataBinding.IncludedLayouts sIncludes;
    @Nullable
    private static final android.util.SparseIntArray sViewsWithIds;
    static {
        sIncludes = null;
        sViewsWithIds = new android.util.SparseIntArray();
        sViewsWithIds.put(R.id.messagesFrame, 2);
        sViewsWithIds.put(R.id.messages_refresher, 3);
        sViewsWithIds.put(R.id.messages_recycler, 4);
        sViewsWithIds.put(R.id.send_msg_panel, 5);
        sViewsWithIds.put(R.id.sendMessageEditText, 6);
        sViewsWithIds.put(R.id.attachment, 7);
        sViewsWithIds.put(R.id.sendMessageBtn, 8);
    }
    // views
    @NonNull
    private final android.widget.LinearLayout mboundView0;
    @NonNull
    private final android.widget.TextView mboundView1;
    // variables
    // values
    // listeners
    // Inverse Binding Event Handlers

    public FragmentMessagesBindingImpl(@Nullable androidx.databinding.DataBindingComponent bindingComponent, @NonNull View root) {
        this(bindingComponent, root, mapBindings(bindingComponent, root, 9, sIncludes, sViewsWithIds));
    }
    private FragmentMessagesBindingImpl(androidx.databinding.DataBindingComponent bindingComponent, View root, Object[] bindings) {
        super(bindingComponent, root, 0
            , (android.widget.ImageView) bindings[7]
            , (android.widget.FrameLayout) bindings[2]
            , (androidx.recyclerview.widget.RecyclerView) bindings[4]
            , (androidx.swiperefreshlayout.widget.SwipeRefreshLayout) bindings[3]
            , (android.widget.TextView) bindings[8]
            , (android.widget.EditText) bindings[6]
            , (android.widget.RelativeLayout) bindings[5]
            );
        this.mboundView0 = (android.widget.LinearLayout) bindings[0];
        this.mboundView0.setTag(null);
        this.mboundView1 = (android.widget.TextView) bindings[1];
        this.mboundView1.setTag(null);
        setRootTag(root);
        // listeners
        invalidateAll();
    }

    @Override
    public void invalidateAll() {
        synchronized(this) {
                mDirtyFlags = 0x2L;
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
        if (BR.visibleHint == variableId) {
            setVisibleHint((boolean) variable);
        }
        else {
            variableSet = false;
        }
            return variableSet;
    }

    public void setVisibleHint(boolean VisibleHint) {
        this.mVisibleHint = VisibleHint;
        synchronized(this) {
            mDirtyFlags |= 0x1L;
        }
        notifyPropertyChanged(BR.visibleHint);
        super.requestRebind();
    }

    @Override
    protected boolean onFieldChange(int localFieldId, Object object, int fieldId) {
        switch (localFieldId) {
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
        int visibleHintInt0Int8 = 0;
        boolean visibleHint = mVisibleHint;

        if ((dirtyFlags & 0x3L) != 0) {

            if((dirtyFlags & 0x3L) != 0) {
                if(visibleHint) {
                        dirtyFlags |= 0x8L;
                }
                else {
                        dirtyFlags |= 0x4L;
                }
            }


                // read visibleHint ? 0 : 8
                visibleHintInt0Int8 = ((visibleHint) ? (0) : (8));
        }
        // batch finished
        if ((dirtyFlags & 0x3L) != 0) {
            // api target 1

            this.mboundView1.setVisibility(visibleHintInt0Int8);
        }
    }
    // Listener Stub Implementations
    // callback impls
    // dirty flag
    private  long mDirtyFlags = 0xffffffffffffffffL;
    /* flag mapping
        flag 0 (0x1L): visibleHint
        flag 1 (0x2L): null
        flag 2 (0x3L): visibleHint ? 0 : 8
        flag 3 (0x4L): visibleHint ? 0 : 8
    flag mapping end*/
    //end
}