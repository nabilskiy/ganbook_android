// Generated code from Butter Knife. Do not modify!
package com.ganbook.activities;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.annotation.CallSuper;
import androidx.annotation.UiThread;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.project.ganim.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class AddAlbumActivity_ViewBinding implements Unbinder {
  private AddAlbumActivity target;

  @UiThread
  public AddAlbumActivity_ViewBinding(AddAlbumActivity target) {
    this(target, target.getWindow().getDecorView());
  }

  @UiThread
  public AddAlbumActivity_ViewBinding(AddAlbumActivity target, View source) {
    this.target = target;

    target.add_album_name = Utils.findRequiredViewAsType(source, R.id.add_album_name, "field 'add_album_name'", EditText.class);
    target.add_album_btn = Utils.findRequiredViewAsType(source, R.id.add_album_btn, "field 'add_album_btn'", Button.class);
    target.add_album_description = Utils.findRequiredViewAsType(source, R.id.add_album_description, "field 'add_album_description'", EditText.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    AddAlbumActivity target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.add_album_name = null;
    target.add_album_btn = null;
    target.add_album_description = null;
  }
}
