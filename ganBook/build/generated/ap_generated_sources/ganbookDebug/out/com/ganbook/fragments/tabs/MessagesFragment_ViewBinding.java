// Generated code from Butter Knife. Do not modify!
package com.ganbook.fragments.tabs;

import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.CallSuper;
import androidx.annotation.UiThread;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.project.ganim.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class MessagesFragment_ViewBinding implements Unbinder {
  private MessagesFragment target;

  @UiThread
  public MessagesFragment_ViewBinding(MessagesFragment target, View source) {
    this.target = target;

    target.messagesRecycler = Utils.findRequiredViewAsType(source, R.id.messages_recycler, "field 'messagesRecycler'", RecyclerView.class);
    target.messagesRefresher = Utils.findRequiredViewAsType(source, R.id.messages_refresher, "field 'messagesRefresher'", SwipeRefreshLayout.class);
    target.sendMsgPanel = Utils.findRequiredViewAsType(source, R.id.send_msg_panel, "field 'sendMsgPanel'", RelativeLayout.class);
    target.messageText = Utils.findRequiredViewAsType(source, R.id.sendMessageEditText, "field 'messageText'", EditText.class);
    target.sendMessage = Utils.findRequiredViewAsType(source, R.id.sendMessageBtn, "field 'sendMessage'", TextView.class);
    target.messageAttachment = Utils.findRequiredViewAsType(source, R.id.attachment, "field 'messageAttachment'", ImageView.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    MessagesFragment target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.messagesRecycler = null;
    target.messagesRefresher = null;
    target.sendMsgPanel = null;
    target.messageText = null;
    target.sendMessage = null;
    target.messageAttachment = null;
  }
}
