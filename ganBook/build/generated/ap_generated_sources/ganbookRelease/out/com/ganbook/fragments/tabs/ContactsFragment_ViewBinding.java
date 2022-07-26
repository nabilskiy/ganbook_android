// Generated code from Butter Knife. Do not modify!
package com.ganbook.fragments.tabs;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import androidx.annotation.CallSuper;
import androidx.annotation.UiThread;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.project.ganim.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class ContactsFragment_ViewBinding implements Unbinder {
  private ContactsFragment target;

  @UiThread
  public ContactsFragment_ViewBinding(ContactsFragment target, View source) {
    this.target = target;

    target.contactsRefsresher = Utils.findRequiredViewAsType(source, R.id.contacts_refresher, "field 'contactsRefsresher'", SwipeRefreshLayout.class);
    target.pbHeaderProgress = Utils.findRequiredViewAsType(source, R.id.pbHeaderProgress, "field 'pbHeaderProgress'", ProgressBar.class);
    target.contactList = Utils.findRequiredViewAsType(source, R.id.contact_list, "field 'contactList'", ListView.class);
    target.searchBar = Utils.findRequiredViewAsType(source, R.id.search_bar, "field 'searchBar'", EditText.class);
    target.cancelSearch = Utils.findRequiredViewAsType(source, R.id.cancel_search, "field 'cancelSearch'", Button.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    ContactsFragment target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.contactsRefsresher = null;
    target.pbHeaderProgress = null;
    target.contactList = null;
    target.searchBar = null;
    target.cancelSearch = null;
  }
}
