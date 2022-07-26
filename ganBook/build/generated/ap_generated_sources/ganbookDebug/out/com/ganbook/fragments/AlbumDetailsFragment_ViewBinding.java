// Generated code from Butter Knife. Do not modify!
package com.ganbook.fragments;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ViewSwitcher;
import androidx.annotation.CallSuper;
import androidx.annotation.UiThread;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.project.ganim.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class AlbumDetailsFragment_ViewBinding implements Unbinder {
  private AlbumDetailsFragment target;

  @UiThread
  public AlbumDetailsFragment_ViewBinding(AlbumDetailsFragment target, View source) {
    this.target = target;

    target.like = Utils.findRequiredViewAsType(source, R.id.switcher, "field 'like'", ViewSwitcher.class);
    target.imagesGallery = Utils.findRequiredViewAsType(source, R.id.images_gallery, "field 'imagesGallery'", RecyclerView.class);
    target.tryAgain = Utils.findRequiredViewAsType(source, R.id.try_again_btn, "field 'tryAgain'", ImageView.class);
    target.addThumbBtn = Utils.findRequiredViewAsType(source, R.id.set_thumb_button, "field 'addThumbBtn'", ImageView.class);
    target.selectThumbnailBtn = Utils.findRequiredViewAsType(source, R.id.add_thumb, "field 'selectThumbnailBtn'", ImageView.class);
    target.progressBar = Utils.findRequiredViewAsType(source, R.id.album_detail_progressbar, "field 'progressBar'", ProgressBar.class);
    target.likesLayout = Utils.findRequiredViewAsType(source, R.id.likes_layout, "field 'likesLayout'", LinearLayout.class);
    target.commentsLayout = Utils.findRequiredViewAsType(source, R.id.comments_layout, "field 'commentsLayout'", LinearLayout.class);
    target.userActionButton = Utils.findRequiredViewAsType(source, R.id.user_action_button, "field 'userActionButton'", ImageView.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    AlbumDetailsFragment target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.like = null;
    target.imagesGallery = null;
    target.tryAgain = null;
    target.addThumbBtn = null;
    target.selectThumbnailBtn = null;
    target.progressBar = null;
    target.likesLayout = null;
    target.commentsLayout = null;
    target.userActionButton = null;
  }
}
