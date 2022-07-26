// Generated by data binding compiler. Do not edit!
package com.project.ganim.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.Bindable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;
import com.ganbook.handlers.AlbumDetailsHandlers;
import com.ganbook.models.AlbumsAnswer;
import com.project.ganim.R;
import java.lang.Deprecated;
import java.lang.Object;

public abstract class FragmentAlbumDetailsBinding extends ViewDataBinding {
  @NonNull
  public final ImageView addThumb;

  @NonNull
  public final ProgressBar albumDetailProgressbar;

  @NonNull
  public final RelativeLayout androidAlbumsFragment;

  @NonNull
  public final RelativeLayout bottomBar;

  @NonNull
  public final ImageView commentsImage;

  @NonNull
  public final LinearLayout commentsLayout;

  @NonNull
  public final ImageView deleteBtn;

  @NonNull
  public final RecyclerView imagesGallery;

  @NonNull
  public final LinearLayout likesLayout;

  @NonNull
  public final TextView numComments;

  @NonNull
  public final TextView numLikes;

  @NonNull
  public final ImageView setThumbButton;

  @NonNull
  public final ImageView tryAgainBtn;

  @NonNull
  public final ImageView userActionButton;

  @Bindable
  protected AlbumsAnswer mAlbum;

  @Bindable
  protected boolean mIsParent;

  @Bindable
  protected boolean mIsStaff;

  @Bindable
  protected AlbumDetailsHandlers mHandlers;

  @Bindable
  protected boolean mShowDelete;

  protected FragmentAlbumDetailsBinding(Object _bindingComponent, View _root, int _localFieldCount,
      ImageView addThumb, ProgressBar albumDetailProgressbar, RelativeLayout androidAlbumsFragment,
      RelativeLayout bottomBar, ImageView commentsImage, LinearLayout commentsLayout,
      ImageView deleteBtn, RecyclerView imagesGallery, LinearLayout likesLayout,
      TextView numComments, TextView numLikes, ImageView setThumbButton, ImageView tryAgainBtn,
      ImageView userActionButton) {
    super(_bindingComponent, _root, _localFieldCount);
    this.addThumb = addThumb;
    this.albumDetailProgressbar = albumDetailProgressbar;
    this.androidAlbumsFragment = androidAlbumsFragment;
    this.bottomBar = bottomBar;
    this.commentsImage = commentsImage;
    this.commentsLayout = commentsLayout;
    this.deleteBtn = deleteBtn;
    this.imagesGallery = imagesGallery;
    this.likesLayout = likesLayout;
    this.numComments = numComments;
    this.numLikes = numLikes;
    this.setThumbButton = setThumbButton;
    this.tryAgainBtn = tryAgainBtn;
    this.userActionButton = userActionButton;
  }

  public abstract void setAlbum(@Nullable AlbumsAnswer album);

  @Nullable
  public AlbumsAnswer getAlbum() {
    return mAlbum;
  }

  public abstract void setIsParent(boolean isParent);

  public boolean getIsParent() {
    return mIsParent;
  }

  public abstract void setIsStaff(boolean isStaff);

  public boolean getIsStaff() {
    return mIsStaff;
  }

  public abstract void setHandlers(@Nullable AlbumDetailsHandlers handlers);

  @Nullable
  public AlbumDetailsHandlers getHandlers() {
    return mHandlers;
  }

  public abstract void setShowDelete(boolean showDelete);

  public boolean getShowDelete() {
    return mShowDelete;
  }

  @NonNull
  public static FragmentAlbumDetailsBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup root, boolean attachToRoot) {
    return inflate(inflater, root, attachToRoot, DataBindingUtil.getDefaultComponent());
  }

  /**
   * This method receives DataBindingComponent instance as type Object instead of
   * type DataBindingComponent to avoid causing too many compilation errors if
   * compilation fails for another reason.
   * https://issuetracker.google.com/issues/116541301
   * @Deprecated Use DataBindingUtil.inflate(inflater, R.layout.fragment_album_details, root, attachToRoot, component)
   */
  @NonNull
  @Deprecated
  public static FragmentAlbumDetailsBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup root, boolean attachToRoot, @Nullable Object component) {
    return ViewDataBinding.<FragmentAlbumDetailsBinding>inflateInternal(inflater, R.layout.fragment_album_details, root, attachToRoot, component);
  }

  @NonNull
  public static FragmentAlbumDetailsBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, DataBindingUtil.getDefaultComponent());
  }

  /**
   * This method receives DataBindingComponent instance as type Object instead of
   * type DataBindingComponent to avoid causing too many compilation errors if
   * compilation fails for another reason.
   * https://issuetracker.google.com/issues/116541301
   * @Deprecated Use DataBindingUtil.inflate(inflater, R.layout.fragment_album_details, null, false, component)
   */
  @NonNull
  @Deprecated
  public static FragmentAlbumDetailsBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable Object component) {
    return ViewDataBinding.<FragmentAlbumDetailsBinding>inflateInternal(inflater, R.layout.fragment_album_details, null, false, component);
  }

  public static FragmentAlbumDetailsBinding bind(@NonNull View view) {
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
  public static FragmentAlbumDetailsBinding bind(@NonNull View view, @Nullable Object component) {
    return (FragmentAlbumDetailsBinding)bind(component, view, R.layout.fragment_album_details);
  }
}
