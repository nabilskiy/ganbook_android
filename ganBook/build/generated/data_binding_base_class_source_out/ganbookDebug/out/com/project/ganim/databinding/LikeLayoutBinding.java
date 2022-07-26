// Generated by data binding compiler. Do not edit!
package com.project.ganim.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ViewSwitcher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.Bindable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import com.ganbook.handlers.AlbumDetailsHandlers;
import com.ganbook.models.AlbumsAnswer;
import com.project.ganim.R;
import java.lang.Deprecated;
import java.lang.Object;

public abstract class LikeLayoutBinding extends ViewDataBinding {
  @NonNull
  public final ImageView heartActive;

  @NonNull
  public final ImageView heartInactive;

  @NonNull
  public final ViewSwitcher switcher;

  @Bindable
  protected AlbumsAnswer mAlbum;

  @Bindable
  protected AlbumDetailsHandlers mHandlers;

  protected LikeLayoutBinding(Object _bindingComponent, View _root, int _localFieldCount,
      ImageView heartActive, ImageView heartInactive, ViewSwitcher switcher) {
    super(_bindingComponent, _root, _localFieldCount);
    this.heartActive = heartActive;
    this.heartInactive = heartInactive;
    this.switcher = switcher;
  }

  public abstract void setAlbum(@Nullable AlbumsAnswer album);

  @Nullable
  public AlbumsAnswer getAlbum() {
    return mAlbum;
  }

  public abstract void setHandlers(@Nullable AlbumDetailsHandlers handlers);

  @Nullable
  public AlbumDetailsHandlers getHandlers() {
    return mHandlers;
  }

  @NonNull
  public static LikeLayoutBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup root, boolean attachToRoot) {
    return inflate(inflater, root, attachToRoot, DataBindingUtil.getDefaultComponent());
  }

  /**
   * This method receives DataBindingComponent instance as type Object instead of
   * type DataBindingComponent to avoid causing too many compilation errors if
   * compilation fails for another reason.
   * https://issuetracker.google.com/issues/116541301
   * @Deprecated Use DataBindingUtil.inflate(inflater, R.layout.like_layout, root, attachToRoot, component)
   */
  @NonNull
  @Deprecated
  public static LikeLayoutBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup root, boolean attachToRoot, @Nullable Object component) {
    return ViewDataBinding.<LikeLayoutBinding>inflateInternal(inflater, R.layout.like_layout, root, attachToRoot, component);
  }

  @NonNull
  public static LikeLayoutBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, DataBindingUtil.getDefaultComponent());
  }

  /**
   * This method receives DataBindingComponent instance as type Object instead of
   * type DataBindingComponent to avoid causing too many compilation errors if
   * compilation fails for another reason.
   * https://issuetracker.google.com/issues/116541301
   * @Deprecated Use DataBindingUtil.inflate(inflater, R.layout.like_layout, null, false, component)
   */
  @NonNull
  @Deprecated
  public static LikeLayoutBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable Object component) {
    return ViewDataBinding.<LikeLayoutBinding>inflateInternal(inflater, R.layout.like_layout, null, false, component);
  }

  public static LikeLayoutBinding bind(@NonNull View view) {
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
  public static LikeLayoutBinding bind(@NonNull View view, @Nullable Object component) {
    return (LikeLayoutBinding)bind(component, view, R.layout.like_layout);
  }
}
