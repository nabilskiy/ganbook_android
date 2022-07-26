// Generated code from Butter Knife. Do not modify!
package com.ganbook.fragments;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.CallSuper;
import androidx.annotation.UiThread;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.ganbook.ui.zoomable.ExtendedViewPager;
import com.project.ganim.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class DrawingDetailsFragment_ViewBinding implements Unbinder {
  private DrawingDetailsFragment target;

  @UiThread
  public DrawingDetailsFragment_ViewBinding(DrawingDetailsFragment target, View source) {
    this.target = target;

    target.drawingViewPager = Utils.findRequiredViewAsType(source, R.id.drawing_view_pager, "field 'drawingViewPager'", ExtendedViewPager.class);
    target.drawingDescriptionText = Utils.findRequiredViewAsType(source, R.id.drawingDesicrpitonText, "field 'drawingDescriptionText'", TextView.class);
    target.drawingDetailsTitle = Utils.findRequiredViewAsType(source, R.id.drawing_details_title, "field 'drawingDetailsTitle'", TextView.class);
    target.saveDrawing = Utils.findRequiredViewAsType(source, R.id.save_right_btn, "field 'saveDrawing'", TextView.class);
    target.drawingBackButotn = Utils.findRequiredViewAsType(source, R.id.drawingBackButton, "field 'drawingBackButotn'", ImageView.class);
    target.playBtn = Utils.findRequiredViewAsType(source, R.id.playBtn, "field 'playBtn'", Button.class);
    target.stopBtn = Utils.findRequiredViewAsType(source, R.id.stopBtn, "field 'stopBtn'", Button.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    DrawingDetailsFragment target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.drawingViewPager = null;
    target.drawingDescriptionText = null;
    target.drawingDetailsTitle = null;
    target.saveDrawing = null;
    target.drawingBackButotn = null;
    target.playBtn = null;
    target.stopBtn = null;
  }
}
