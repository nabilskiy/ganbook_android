// Generated code from Butter Knife. Do not modify!
package com.ganbook.fragments.tabs;

import android.view.View;
import androidx.annotation.CallSuper;
import androidx.annotation.UiThread;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.project.ganim.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class AlbumsFragment_ViewBinding implements Unbinder {
  private AlbumsFragment target;

  @UiThread
  public AlbumsFragment_ViewBinding(AlbumsFragment target, View source) {
    this.target = target;

    target.albumsRecycler = Utils.findRequiredViewAsType(source, R.id.albums_recycler, "field 'albumsRecycler'", RecyclerView.class);
    target.albumsRefresher = Utils.findRequiredViewAsType(source, R.id.albums_refresher, "field 'albumsRefresher'", SwipeRefreshLayout.class);
    target.addButton = source.findViewById(R.id.add_album);
  }

  @Override
  @CallSuper
  public void unbind() {
    AlbumsFragment target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.albumsRecycler = null;
    target.albumsRefresher = null;
    target.addButton = null;
  }
}
