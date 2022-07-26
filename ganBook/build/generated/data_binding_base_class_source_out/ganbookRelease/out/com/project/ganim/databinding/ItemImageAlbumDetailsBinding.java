// Generated by data binding compiler. Do not edit!
package com.project.ganim.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.Bindable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import com.ganbook.handlers.PictureAlbumDetailsHandlers;
import com.ganbook.models.PictureAnswer;
import com.project.ganim.R;
import java.lang.Deprecated;
import java.lang.Object;

public abstract class ItemImageAlbumDetailsBinding extends ViewDataBinding {
  @NonNull
  public final TextView duration;

  @NonNull
  public final ImageView ivImage;

  @NonNull
  public final ImageView photoDescriptionIndicator;

  @NonNull
  public final ImageView play;

  @NonNull
  public final CheckBox selectionCheck;

  @NonNull
  public final ProgressBar uploadProgress;

  @NonNull
  public final ImageView uploadStateImage;

  @Bindable
  protected PictureAnswer mImage;

  @Bindable
  protected PictureAlbumDetailsHandlers mHandlers;

  @Bindable
  protected boolean mSelection;

  protected ItemImageAlbumDetailsBinding(Object _bindingComponent, View _root, int _localFieldCount,
      TextView duration, ImageView ivImage, ImageView photoDescriptionIndicator, ImageView play,
      CheckBox selectionCheck, ProgressBar uploadProgress, ImageView uploadStateImage) {
    super(_bindingComponent, _root, _localFieldCount);
    this.duration = duration;
    this.ivImage = ivImage;
    this.photoDescriptionIndicator = photoDescriptionIndicator;
    this.play = play;
    this.selectionCheck = selectionCheck;
    this.uploadProgress = uploadProgress;
    this.uploadStateImage = uploadStateImage;
  }

  public abstract void setImage(@Nullable PictureAnswer image);

  @Nullable
  public PictureAnswer getImage() {
    return mImage;
  }

  public abstract void setHandlers(@Nullable PictureAlbumDetailsHandlers handlers);

  @Nullable
  public PictureAlbumDetailsHandlers getHandlers() {
    return mHandlers;
  }

  public abstract void setSelection(boolean selection);

  public boolean getSelection() {
    return mSelection;
  }

  @NonNull
  public static ItemImageAlbumDetailsBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup root, boolean attachToRoot) {
    return inflate(inflater, root, attachToRoot, DataBindingUtil.getDefaultComponent());
  }

  /**
   * This method receives DataBindingComponent instance as type Object instead of
   * type DataBindingComponent to avoid causing too many compilation errors if
   * compilation fails for another reason.
   * https://issuetracker.google.com/issues/116541301
   * @Deprecated Use DataBindingUtil.inflate(inflater, R.layout.item_image_album_details, root, attachToRoot, component)
   */
  @NonNull
  @Deprecated
  public static ItemImageAlbumDetailsBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup root, boolean attachToRoot, @Nullable Object component) {
    return ViewDataBinding.<ItemImageAlbumDetailsBinding>inflateInternal(inflater, R.layout.item_image_album_details, root, attachToRoot, component);
  }

  @NonNull
  public static ItemImageAlbumDetailsBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, DataBindingUtil.getDefaultComponent());
  }

  /**
   * This method receives DataBindingComponent instance as type Object instead of
   * type DataBindingComponent to avoid causing too many compilation errors if
   * compilation fails for another reason.
   * https://issuetracker.google.com/issues/116541301
   * @Deprecated Use DataBindingUtil.inflate(inflater, R.layout.item_image_album_details, null, false, component)
   */
  @NonNull
  @Deprecated
  public static ItemImageAlbumDetailsBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable Object component) {
    return ViewDataBinding.<ItemImageAlbumDetailsBinding>inflateInternal(inflater, R.layout.item_image_album_details, null, false, component);
  }

  public static ItemImageAlbumDetailsBinding bind(@NonNull View view) {
    return bind(view, DataBindingUtil.getDefaultComponent());
  }

  /**
   * This method receives DataBindingComponent instance as type Object instead of
   * type DataBindingComponent to avoid causing too many compilation errors if
   * compilation fails for another reason.
   * https://issuetracker.google.com/issues/116541301
   * @Deprecated Use DataBindingUtil.bind(view, component)
   */
  @Deprecated
  public static ItemImageAlbumDetailsBinding bind(@NonNull View view, @Nullable Object component) {
    return (ItemImageAlbumDetailsBinding)bind(component, view, R.layout.item_image_album_details);
  }
}