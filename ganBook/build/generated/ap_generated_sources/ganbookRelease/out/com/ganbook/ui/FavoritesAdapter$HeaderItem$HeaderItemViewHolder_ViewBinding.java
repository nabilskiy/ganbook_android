// Generated code from Butter Knife. Do not modify!
package com.ganbook.ui;

import android.view.View;
import android.widget.TextView;
import androidx.annotation.CallSuper;
import androidx.annotation.UiThread;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.project.ganim.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class FavoritesAdapter$HeaderItem$HeaderItemViewHolder_ViewBinding implements Unbinder {
  private FavoritesAdapter.HeaderItem.HeaderItemViewHolder target;

  @UiThread
  public FavoritesAdapter$HeaderItem$HeaderItemViewHolder_ViewBinding(
      FavoritesAdapter.HeaderItem.HeaderItemViewHolder target, View source) {
    this.target = target;

    target.header = Utils.findRequiredViewAsType(source, R.id.header, "field 'header'", TextView.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    FavoritesAdapter.HeaderItem.HeaderItemViewHolder target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.header = null;
  }
}
