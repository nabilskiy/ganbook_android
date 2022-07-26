// Generated code from Butter Knife. Do not modify!
package com.ganbook.ui;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.CallSuper;
import androidx.annotation.UiThread;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.project.ganim.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class FavoritesAdapter$FavoriteItem$FavoritetemViewHolder_ViewBinding implements Unbinder {
  private FavoritesAdapter.FavoriteItem.FavoritetemViewHolder target;

  @UiThread
  public FavoritesAdapter$FavoriteItem$FavoritetemViewHolder_ViewBinding(
      FavoritesAdapter.FavoriteItem.FavoritetemViewHolder target, View source) {
    this.target = target;

    target.header = Utils.findRequiredViewAsType(source, R.id.header, "field 'header'", TextView.class);
    target.album_image = Utils.findRequiredViewAsType(source, R.id.iv_image, "field 'album_image'", ImageView.class);
    target.play = Utils.findRequiredViewAsType(source, R.id.play, "field 'play'", ImageView.class);
    target.duration = Utils.findRequiredViewAsType(source, R.id.duration, "field 'duration'", TextView.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    FavoritesAdapter.FavoriteItem.FavoritetemViewHolder target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.header = null;
    target.album_image = null;
    target.play = null;
    target.duration = null;
  }
}
