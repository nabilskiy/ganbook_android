// Generated code from Butter Knife. Do not modify!
package com.ganbook.fragments;

import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import androidx.annotation.CallSuper;
import androidx.annotation.UiThread;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.project.ganim.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class AlbumDrawingsDetailsFragment_ViewBinding implements Unbinder {
  private AlbumDrawingsDetailsFragment target;

  @UiThread
  public AlbumDrawingsDetailsFragment_ViewBinding(AlbumDrawingsDetailsFragment target,
      View source) {
    this.target = target;

    target.userActionButton = Utils.findRequiredViewAsType(source, R.id.user_action_button, "field 'userActionButton'", ImageView.class);
    target.drawingGallery = Utils.findRequiredViewAsType(source, R.id.drawing_gallery, "field 'drawingGallery'", RecyclerView.class);
    target.progressBar = Utils.findRequiredViewAsType(source, R.id.album_detail_progressbar, "field 'progressBar'", ProgressBar.class);
    target.deleteBtn = Utils.findRequiredViewAsType(source, R.id.delete_btn, "field 'deleteBtn'", ImageView.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    AlbumDrawingsDetailsFragment target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.userActionButton = null;
    target.drawingGallery = null;
    target.progressBar = null;
    target.deleteBtn = null;
  }
}
