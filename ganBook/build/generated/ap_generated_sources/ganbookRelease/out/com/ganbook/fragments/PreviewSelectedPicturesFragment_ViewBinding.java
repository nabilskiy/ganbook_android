// Generated code from Butter Knife. Do not modify!
package com.ganbook.fragments;

import android.view.View;
import android.widget.ImageView;
import androidx.annotation.CallSuper;
import androidx.annotation.UiThread;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.project.ganim.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class PreviewSelectedPicturesFragment_ViewBinding implements Unbinder {
  private PreviewSelectedPicturesFragment target;

  @UiThread
  public PreviewSelectedPicturesFragment_ViewBinding(PreviewSelectedPicturesFragment target,
      View source) {
    this.target = target;

    target.previewsRecycler = Utils.findRequiredViewAsType(source, R.id.previews_recycler, "field 'previewsRecycler'", RecyclerView.class);
    target.sendBtn = Utils.findRequiredViewAsType(source, R.id.send_btn, "field 'sendBtn'", ImageView.class);
    target.mViewPager = Utils.findRequiredViewAsType(source, R.id.view_pager, "field 'mViewPager'", ViewPager.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    PreviewSelectedPicturesFragment target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.previewsRecycler = null;
    target.sendBtn = null;
    target.mViewPager = null;
  }
}
