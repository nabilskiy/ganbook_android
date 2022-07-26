// Generated code from Butter Knife. Do not modify!
package com.ganbook.services;

import android.view.View;
import android.widget.ImageView;
import androidx.annotation.CallSuper;
import androidx.annotation.UiThread;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.project.ganim.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class SupportUploadService_ViewBinding implements Unbinder {
  private SupportUploadService target;

  @UiThread
  public SupportUploadService_ViewBinding(SupportUploadService target, View source) {
    this.target = target;

    target.tryAgain = Utils.findRequiredViewAsType(source, R.id.try_again_btn, "field 'tryAgain'", ImageView.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    SupportUploadService target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.tryAgain = null;
  }
}
