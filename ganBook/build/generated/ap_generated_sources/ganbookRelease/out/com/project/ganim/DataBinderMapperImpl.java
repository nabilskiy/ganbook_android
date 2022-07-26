package com.project.ganim;

import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.View;
import androidx.databinding.DataBinderMapper;
import androidx.databinding.DataBindingComponent;
import androidx.databinding.ViewDataBinding;
import com.project.ganim.databinding.FragmentAlbumDetailsBindingImpl;
import com.project.ganim.databinding.FragmentAlbumDrawingsDetailsBindingImpl;
import com.project.ganim.databinding.FragmentMessagesBindingImpl;
import com.project.ganim.databinding.FragmentPictureDetailsBindingImpl;
import com.project.ganim.databinding.HeaderAlbumListBindingImpl;
import com.project.ganim.databinding.ItemAlbumListBindingImpl;
import com.project.ganim.databinding.ItemDrawingAlbumDetailsBindingImpl;
import com.project.ganim.databinding.ItemGridGalleryBindingImpl;
import com.project.ganim.databinding.ItemImageAlbumDetailsBindingImpl;
import com.project.ganim.databinding.ItemMessageBindingImpl;
import com.project.ganim.databinding.ItemPreviewBindingImpl;
import com.project.ganim.databinding.LikeLayoutBindingImpl;
import java.lang.IllegalArgumentException;
import java.lang.Integer;
import java.lang.Object;
import java.lang.Override;
import java.lang.RuntimeException;
import java.lang.String;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.annotation.Generated;

@Generated("Android Data Binding")
public class DataBinderMapperImpl extends DataBinderMapper {
  private static final int LAYOUT_FRAGMENTALBUMDETAILS = 1;

  private static final int LAYOUT_FRAGMENTALBUMDRAWINGSDETAILS = 2;

  private static final int LAYOUT_FRAGMENTMESSAGES = 3;

  private static final int LAYOUT_FRAGMENTPICTUREDETAILS = 4;

  private static final int LAYOUT_HEADERALBUMLIST = 5;

  private static final int LAYOUT_ITEMALBUMLIST = 6;

  private static final int LAYOUT_ITEMDRAWINGALBUMDETAILS = 7;

  private static final int LAYOUT_ITEMGRIDGALLERY = 8;

  private static final int LAYOUT_ITEMIMAGEALBUMDETAILS = 9;

  private static final int LAYOUT_ITEMMESSAGE = 10;

  private static final int LAYOUT_ITEMPREVIEW = 11;

  private static final int LAYOUT_LIKELAYOUT = 12;

  private static final SparseIntArray INTERNAL_LAYOUT_ID_LOOKUP = new SparseIntArray(12);

  static {
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.project.ganim.R.layout.fragment_album_details, LAYOUT_FRAGMENTALBUMDETAILS);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.project.ganim.R.layout.fragment_album_drawings_details, LAYOUT_FRAGMENTALBUMDRAWINGSDETAILS);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.project.ganim.R.layout.fragment_messages, LAYOUT_FRAGMENTMESSAGES);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.project.ganim.R.layout.fragment_picture_details, LAYOUT_FRAGMENTPICTUREDETAILS);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.project.ganim.R.layout.header_album_list, LAYOUT_HEADERALBUMLIST);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.project.ganim.R.layout.item_album_list, LAYOUT_ITEMALBUMLIST);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.project.ganim.R.layout.item_drawing_album_details, LAYOUT_ITEMDRAWINGALBUMDETAILS);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.project.ganim.R.layout.item_grid_gallery, LAYOUT_ITEMGRIDGALLERY);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.project.ganim.R.layout.item_image_album_details, LAYOUT_ITEMIMAGEALBUMDETAILS);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.project.ganim.R.layout.item_message, LAYOUT_ITEMMESSAGE);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.project.ganim.R.layout.item_preview, LAYOUT_ITEMPREVIEW);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.project.ganim.R.layout.like_layout, LAYOUT_LIKELAYOUT);
  }

  @Override
  public ViewDataBinding getDataBinder(DataBindingComponent component, View view, int layoutId) {
    int localizedLayoutId = INTERNAL_LAYOUT_ID_LOOKUP.get(layoutId);
    if(localizedLayoutId > 0) {
      final Object tag = view.getTag();
      if(tag == null) {
        throw new RuntimeException("view must have a tag");
      }
      switch(localizedLayoutId) {
        case  LAYOUT_FRAGMENTALBUMDETAILS: {
          if ("layout/fragment_album_details_0".equals(tag)) {
            return new FragmentAlbumDetailsBindingImpl(component, view);
          }
          throw new IllegalArgumentException("The tag for fragment_album_details is invalid. Received: " + tag);
        }
        case  LAYOUT_FRAGMENTALBUMDRAWINGSDETAILS: {
          if ("layout/fragment_album_drawings_details_0".equals(tag)) {
            return new FragmentAlbumDrawingsDetailsBindingImpl(component, view);
          }
          throw new IllegalArgumentException("The tag for fragment_album_drawings_details is invalid. Received: " + tag);
        }
        case  LAYOUT_FRAGMENTMESSAGES: {
          if ("layout/fragment_messages_0".equals(tag)) {
            return new FragmentMessagesBindingImpl(component, view);
          }
          throw new IllegalArgumentException("The tag for fragment_messages is invalid. Received: " + tag);
        }
        case  LAYOUT_FRAGMENTPICTUREDETAILS: {
          if ("layout/fragment_picture_details_0".equals(tag)) {
            return new FragmentPictureDetailsBindingImpl(component, view);
          }
          throw new IllegalArgumentException("The tag for fragment_picture_details is invalid. Received: " + tag);
        }
        case  LAYOUT_HEADERALBUMLIST: {
          if ("layout/header_album_list_0".equals(tag)) {
            return new HeaderAlbumListBindingImpl(component, view);
          }
          throw new IllegalArgumentException("The tag for header_album_list is invalid. Received: " + tag);
        }
        case  LAYOUT_ITEMALBUMLIST: {
          if ("layout/item_album_list_0".equals(tag)) {
            return new ItemAlbumListBindingImpl(component, view);
          }
          throw new IllegalArgumentException("The tag for item_album_list is invalid. Received: " + tag);
        }
        case  LAYOUT_ITEMDRAWINGALBUMDETAILS: {
          if ("layout/item_drawing_album_details_0".equals(tag)) {
            return new ItemDrawingAlbumDetailsBindingImpl(component, view);
          }
          throw new IllegalArgumentException("The tag for item_drawing_album_details is invalid. Received: " + tag);
        }
        case  LAYOUT_ITEMGRIDGALLERY: {
          if ("layout/item_grid_gallery_0".equals(tag)) {
            return new ItemGridGalleryBindingImpl(component, view);
          }
          throw new IllegalArgumentException("The tag for item_grid_gallery is invalid. Received: " + tag);
        }
        case  LAYOUT_ITEMIMAGEALBUMDETAILS: {
          if ("layout/item_image_album_details_0".equals(tag)) {
            return new ItemImageAlbumDetailsBindingImpl(component, view);
          }
          throw new IllegalArgumentException("The tag for item_image_album_details is invalid. Received: " + tag);
        }
        case  LAYOUT_ITEMMESSAGE: {
          if ("layout/item_message_0".equals(tag)) {
            return new ItemMessageBindingImpl(component, view);
          }
          throw new IllegalArgumentException("The tag for item_message is invalid. Received: " + tag);
        }
        case  LAYOUT_ITEMPREVIEW: {
          if ("layout/item_preview_0".equals(tag)) {
            return new ItemPreviewBindingImpl(component, view);
          }
          throw new IllegalArgumentException("The tag for item_preview is invalid. Received: " + tag);
        }
        case  LAYOUT_LIKELAYOUT: {
          if ("layout/like_layout_0".equals(tag)) {
            return new LikeLayoutBindingImpl(component, view);
          }
          throw new IllegalArgumentException("The tag for like_layout is invalid. Received: " + tag);
        }
      }
    }
    return null;
  }

  @Override
  public ViewDataBinding getDataBinder(DataBindingComponent component, View[] views, int layoutId) {
    if(views == null || views.length == 0) {
      return null;
    }
    int localizedLayoutId = INTERNAL_LAYOUT_ID_LOOKUP.get(layoutId);
    if(localizedLayoutId > 0) {
      final Object tag = views[0].getTag();
      if(tag == null) {
        throw new RuntimeException("view must have a tag");
      }
      switch(localizedLayoutId) {
      }
    }
    return null;
  }

  @Override
  public int getLayoutId(String tag) {
    if (tag == null) {
      return 0;
    }
    Integer tmpVal = InnerLayoutIdLookup.sKeys.get(tag);
    return tmpVal == null ? 0 : tmpVal;
  }

  @Override
  public String convertBrIdToString(int localId) {
    String tmpVal = InnerBrLookup.sKeys.get(localId);
    return tmpVal;
  }

  @Override
  public List<DataBinderMapper> collectDependencies() {
    ArrayList<DataBinderMapper> result = new ArrayList<DataBinderMapper>(1);
    result.add(new androidx.databinding.library.baseAdapters.DataBinderMapperImpl());
    return result;
  }

  private static class InnerBrLookup {
    static final SparseArray<String> sKeys = new SparseArray<String>(33);

    static {
      sKeys.put(0, "_all");
      sKeys.put(1, "album");
      sKeys.put(2, "date");
      sKeys.put(3, "down");
      sKeys.put(4, "drawing");
      sKeys.put(5, "favorite");
      sKeys.put(6, "firstPicture");
      sKeys.put(7, "gridPicture");
      sKeys.put(8, "handlers");
      sKeys.put(9, "highlight");
      sKeys.put(10, "image");
      sKeys.put(11, "isParent");
      sKeys.put(12, "isSelected");
      sKeys.put(13, "isStaff");
      sKeys.put(14, "likesCount");
      sKeys.put(15, "media");
      sKeys.put(16, "message");
      sKeys.put(17, "photosTitle");
      sKeys.put(18, "picPath");
      sKeys.put(19, "pictureActive");
      sKeys.put(20, "preview");
      sKeys.put(21, "progress");
      sKeys.put(22, "selected");
      sKeys.put(23, "selection");
      sKeys.put(24, "showDelete");
      sKeys.put(25, "showNewLabel");
      sKeys.put(26, "status");
      sKeys.put(27, "title");
      sKeys.put(28, "unseenPhotos");
      sKeys.put(29, "up");
      sKeys.put(30, "videosTitle");
      sKeys.put(31, "viewed");
      sKeys.put(32, "visibleHint");
    }
  }

  private static class InnerLayoutIdLookup {
    static final HashMap<String, Integer> sKeys = new HashMap<String, Integer>(12);

    static {
      sKeys.put("layout/fragment_album_details_0", com.project.ganim.R.layout.fragment_album_details);
      sKeys.put("layout/fragment_album_drawings_details_0", com.project.ganim.R.layout.fragment_album_drawings_details);
      sKeys.put("layout/fragment_messages_0", com.project.ganim.R.layout.fragment_messages);
      sKeys.put("layout/fragment_picture_details_0", com.project.ganim.R.layout.fragment_picture_details);
      sKeys.put("layout/header_album_list_0", com.project.ganim.R.layout.header_album_list);
      sKeys.put("layout/item_album_list_0", com.project.ganim.R.layout.item_album_list);
      sKeys.put("layout/item_drawing_album_details_0", com.project.ganim.R.layout.item_drawing_album_details);
      sKeys.put("layout/item_grid_gallery_0", com.project.ganim.R.layout.item_grid_gallery);
      sKeys.put("layout/item_image_album_details_0", com.project.ganim.R.layout.item_image_album_details);
      sKeys.put("layout/item_message_0", com.project.ganim.R.layout.item_message);
      sKeys.put("layout/item_preview_0", com.project.ganim.R.layout.item_preview);
      sKeys.put("layout/like_layout_0", com.project.ganim.R.layout.like_layout);
    }
  }
}
