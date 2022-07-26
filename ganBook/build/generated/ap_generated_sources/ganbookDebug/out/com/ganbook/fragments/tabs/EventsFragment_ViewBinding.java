// Generated code from Butter Knife. Do not modify!
package com.ganbook.fragments.tabs;

import android.view.View;
import android.widget.ListView;
import androidx.annotation.CallSuper;
import androidx.annotation.UiThread;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.project.ganim.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class EventsFragment_ViewBinding implements Unbinder {
  private EventsFragment target;

  @UiThread
  public EventsFragment_ViewBinding(EventsFragment target, View source) {
    this.target = target;

    target.swipeRefreshLayout = Utils.findRequiredViewAsType(source, R.id.events_refresher, "field 'swipeRefreshLayout'", SwipeRefreshLayout.class);
    target.eventsListView = Utils.findRequiredViewAsType(source, R.id.events_list, "field 'eventsListView'", ListView.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    EventsFragment target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.swipeRefreshLayout = null;
    target.eventsListView = null;
  }
}
