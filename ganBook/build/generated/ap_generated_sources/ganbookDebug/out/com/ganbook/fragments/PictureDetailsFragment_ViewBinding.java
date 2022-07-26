// Generated code from Butter Knife. Do not modify!
package com.ganbook.fragments;

import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.ViewSwitcher;
import androidx.annotation.CallSuper;
import androidx.annotation.UiThread;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.ganbook.ui.zoomable.ExtendedViewPager;
import com.project.ganim.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class PictureDetailsFragment_ViewBinding implements Unbinder {
  private PictureDetailsFragment target;

  @UiThread
  public PictureDetailsFragment_ViewBinding(PictureDetailsFragment target, View source) {
    this.target = target;

    target.mViewPager = Utils.findRequiredViewAsType(source, R.id.view_pager, "field 'mViewPager'", ExtendedViewPager.class);
    target.zoomFooter = Utils.findRequiredViewAsType(source, R.id.zoom_footer, "field 'zoomFooter'", RelativeLayout.class);
    target.zoomHeader = Utils.findRequiredViewAsType(source, R.id.zoom_header, "field 'zoomHeader'", RelativeLayout.class);
    target.favoriteSwitcher = Utils.findRequiredViewAsType(source, R.id.switcher, "field 'favoriteSwitcher'", ViewSwitcher.class);
    target.save = Utils.findRequiredViewAsType(source, R.id.save_right_btn, "field 'save'", ImageButton.class);
    target.addPhotoComment = Utils.findRequiredViewAsType(source, R.id.addPhotoCommentBtn, "field 'addPhotoComment'", ImageButton.class);
    target.share = Utils.findRequiredViewAsType(source, R.id.share_pict_btn, "field 'share'", ImageButton.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    PictureDetailsFragment target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.mViewPager = null;
    target.zoomFooter = null;
    target.zoomHeader = null;
    target.favoriteSwitcher = null;
    target.save = null;
    target.addPhotoComment = null;
    target.share = null;
  }
}
