package com.ganbook.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import androidx.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.ganbook.fragments.PictureDetailsFragment;
import com.ganbook.handlers.PictureAlbumDetailsHandlers;
import com.ganbook.interfaces.AlbumDetailsInterface;
import com.ganbook.interfaces.CRUDAdapterInterface;
import com.ganbook.interfaces.Toggleable;
import com.ganbook.models.PictureAnswer;
import com.ganbook.utils.DBUtils.HelperFactory;
import com.ganbook.utils.FragmentUtils;
import com.ganbook.utils.StrUtils;
import com.project.ganim.R;
import com.project.ganim.databinding.ItemImageAlbumDetailsBinding;
import com.squareup.picasso.Picasso;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by dmytro_vodnik on 6/12/16.
 * working on ganbook1 project
 */
public class AlbumDetailsImagesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements CRUDAdapterInterface<PictureAnswer>, Toggleable<PictureAnswer> {

    private static final String TAG = AlbumDetailsImagesAdapter.class.getName();
    private final String classId;
    private final String ganId;
    Context context;
    LayoutInflater layoutInflater;
    List<PictureAnswer> albumsAnswers;
    AlbumDetailsInterface albumDetailsInterface;
    private boolean selectionState = false;
    private List<PictureAnswer> selectedItems;
    private PictureAnswer selectedItem;
    private PictureAnswer previousSelectedItem = new PictureAnswer();
    private boolean thumbnailSelection;

    public AlbumDetailsImagesAdapter(Context context, AlbumDetailsInterface albumDetailsInterface, String ganId, String classId) {
        setHasStableIds(true);
        this.context = context;
        this.layoutInflater = LayoutInflater.from(context);
        this.albumsAnswers = new ArrayList<>();
        this.albumDetailsInterface = albumDetailsInterface;
        selectedItems = new ArrayList<>();
        this.ganId = ganId;
        this.classId = classId;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void setThumbnailSelection(boolean thumbnailSelection) {
        this.thumbnailSelection = thumbnailSelection;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ItemImageAlbumDetailsBinding binding = DataBindingUtil.inflate(layoutInflater, R.layout.item_image_album_details,
                parent, false);

        return new ViewHolderItem(binding);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {

        final PictureAnswer pictureAnswer = getItem(position);
        ((ViewHolderItem) holder).binding.setImage(pictureAnswer);
//        ((ViewHolderItem) holder).binding.executePendingBindings();
        ((ViewHolderItem) holder).binding.setHandlers(new PictureAlbumDetailsHandlers(pictureAnswer,
                albumDetailsInterface, position));

        if(thumbnailSelection) {
            if (!pictureAnswer.getPictureName().contains(".mp4")) {
                ((ViewHolderItem) holder).binding.setSelection(selectionState);
            }
        } else {
            ((ViewHolderItem) holder).binding.setSelection(selectionState);
        }

        ((ViewHolderItem) holder).binding.selectionCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleItem(pictureAnswer, position);
            }
        });

        ((ViewHolderItem) holder).binding.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d(TAG, "onClick: showing " + pictureAnswer);

                showItem(pictureAnswer, position, ganId, classId);
            }
        });


        if (pictureAnswer.getPictureDescription() != null) {

            ((ViewHolderItem) holder).photoDescriptionIndicator.setVisibility(View.VISIBLE);

        } else {
            ((ViewHolderItem) holder).photoDescriptionIndicator.setVisibility(View.GONE);
        }


        final int size = (int) ((ViewHolderItem) holder).view.getResources().getDimension(R.dimen.img_tmb_size);

        if (pictureAnswer.getPictureName() != null) {

            if (pictureAnswer.getLocaFilePath() != null) {


                Drawable drawable = null;


                if (pictureAnswer.getVideoDuration() == null || pictureAnswer.getVideoDuration().equals("")) {



                    
                } else
                    drawable = new BitmapDrawable(((ViewHolderItem) holder).view.getResources(), ThumbnailUtils.createVideoThumbnail(pictureAnswer.getLocaFilePath(),
                            MediaStore.Images.Thumbnails.MINI_KIND));

                RequestOptions myOptions = new RequestOptions()
                        .fitCenter()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .override(100, 100);


                Glide.with(((ViewHolderItem) holder).view.getContext())
                        .asBitmap()
                        .load(drawable)
                        .apply(myOptions)
                        .into(((ViewHolderItem) holder).view);


                ColorMatrix matrix = new ColorMatrix();
                matrix.setSaturation(0);

                ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);

                ((ViewHolderItem) holder).view.setColorFilter(filter);

            } else {

                ((ViewHolderItem) holder).view.setColorFilter(null);

                String url = StrUtils.getAlbumFullSizeUrl("tmb" + pictureAnswer.getPictureName(), pictureAnswer.getAlbumId(), classId, ganId);

                if (pictureAnswer.getVideoDuration()!= null && !pictureAnswer.getVideoDuration().equals(""))
                    url = StrUtils.getAlbumTmbUrl(pictureAnswer.getPictureName(), pictureAnswer.getAlbumId(), classId, ganId);


               RequestOptions myOptions = new RequestOptions()
                        .centerCrop()
                        .dontAnimate()
                        .override(size, size)
                        .diskCacheStrategy(DiskCacheStrategy.ALL);

                RequestManager rm = Glide.with(((ViewHolderItem) holder).view.getContext());
                        rm
                        .load(url)
                        .listener(createLoggerListener("IMAGE"))
                        .apply(myOptions)
                        .into(((ViewHolderItem) holder).view);

            }
        }
        else
            Picasso.with(context).load(R.drawable.album_off).into(((ViewHolderItem) holder).view);
    }



    private RequestListener<Drawable> createLoggerListener(final String name) {

        return new RequestListener<Drawable>(){



            @Override

            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target target, boolean isFirstResource) {
                albumDetailsInterface.hideProgress();
                return false;

            }



            @SuppressLint("DefaultLocale")
            @Override

            public boolean onResourceReady(Drawable resource, Object model, Target target, DataSource dataSource, boolean isFirstResource) {
                albumDetailsInterface.hideProgress();
                if (resource instanceof BitmapDrawable) {

                    Bitmap bitmap = ((BitmapDrawable) resource).getBitmap();

                    Log.d("GlideApp",

                    String.format("Ready %s bitmap %,d bytes, size: %d x %d",

                    name,

                            bitmap.getByteCount(),

                            bitmap.getWidth(),

                            bitmap.getHeight()));

                }

                return false;

            }

        };

    }

    private void showItem(PictureAnswer pictureAnswer, int position, String ganId, String classId) {

        if (selectionState) {
            //we in selection mode, check uncheck picture here

            toggleItem(pictureAnswer, position);
        } else {
            //showing details picture here

            PictureDetailsFragment pictureDetailsFragment = PictureDetailsFragment.newInstance(position,
                    new ArrayList<>(getItems()), ganId, classId);

            FragmentUtils.openFragment(pictureDetailsFragment, R.id.content_root, PictureDetailsFragment.TAG,
                    context, true);
        }
    }

    @Override
    public int getItemCount() {
        return albumsAnswers.size();
    }

    @Override
    public PictureAnswer getItem(int position) {
        return albumsAnswers.get(position);
    }

    @Override
    public void addItem(PictureAnswer item) {

        albumsAnswers.add(0, item);
        notifyItemInserted(albumsAnswers.size());

        try {
            HelperFactory.getHelper().getPictureAnswerDAO().create(item);
        } catch (SQLException e) {
            Log.e(TAG, "error while create pic " + Log.getStackTraceString(e));
        }

        notifyDataSetChanged();
    }

    @Override
    public void addItem(PictureAnswer item, int index) {

    }

    @Override
    public void removeItem(PictureAnswer item) {

        Iterator<PictureAnswer> iter = albumsAnswers.iterator();

        while (iter.hasNext()) {
            PictureAnswer str = iter.next();

            if (str.getPictureName().equals(item.getPictureName()))
                iter.remove();
        }

        notifyDataSetChanged();
    }

    @Override
    public void addItems(final List<PictureAnswer> items) {

        new Thread(new Runnable() {
            @Override
            public void run() {

                albumsAnswers.addAll(0, items);

                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        notifyDataSetChanged();
                    }
                });
            }
        }).start();
    }

    @Override
    public void clearList() {

        albumsAnswers.clear();
        notifyDataSetChanged();
    }

    @Override
    public void updateItem(int pos, PictureAnswer item) {

        if (albumsAnswers.size() > 0 && pos != -1) {
            albumsAnswers.set(pos, item);
            notifyItemChanged(pos);
        }
    }

    @Override
    public void showItem(PictureAnswer item, int itemPosition) {

        if (selectionState) {
            //we in selection mode, check uncheck picture here

            toggleItem(item, itemPosition);
        } else {
            //showing details picture here

            PictureDetailsFragment pictureDetailsFragment = PictureDetailsFragment.newInstance(itemPosition,
                    new ArrayList<>(getItems()), ganId, classId);

            FragmentUtils.openFragment(pictureDetailsFragment, R.id.content_root, PictureDetailsFragment.TAG,
                    context, true);
        }
    }

    @Override
    public void toggleItem(PictureAnswer item, int itemPosition) {

        if(!thumbnailSelection) {
            item.setSelected(!item.isSelected());
        } else {
            item.setSelected(!item.isSelected());
            if(previousSelectedItem != item && previousSelectedItem != null) {
                previousSelectedItem.setSelected(false);
                previousSelectedItem = item;
                if(!item.getPictureName().contains(".mp4")){
                    notifyDataSetChanged();
                }
            }
        }
    }

    @Override
    public List<PictureAnswer> getItems() {
        return albumsAnswers;
    }

    @Override
    public void removeItem(int currentItemPosition) {

    }

    public List<PictureAnswer> getPendingItems() {

        List<PictureAnswer> pendingItems = new ArrayList<>();

        for (PictureAnswer p : albumsAnswers) {

            if (p.getStatus() == 0 || p.getStatus() == 2) {
                pendingItems.add(p);
                p.setStatus(0);
            }
        }

        notifyDataSetChanged();

        return pendingItems;
    }

    public int getItemPosition(PictureAnswer newPicture) {

        List<PictureAnswer> items = getItems();
        for (int i = 0; i < items.size(); i++) {
            PictureAnswer p = items.get(i);
            if (p.getPictureName().equals(newPicture.getPictureName()))
                return i;
        }

        return -1;
    }

    public void setSelectionState() {
        this.selectionState = !selectionState;
        notifyDataSetChanged();
    }

    public List<PictureAnswer> getSelectedItems() {

        selectedItems.clear();

        for (PictureAnswer p : getItems()) {

            if (p.isSelected())
                selectedItems.add(p);
        }

        Log.d(TAG, "getSelectedItems: selected items in adap = " + selectedItems);

        return selectedItems;
    }

    public PictureAnswer getSelectedItem() {
        for (PictureAnswer p : getItems()) {

            if (p.isSelected())
                selectedItem = p;
        }

        return selectedItem;
    }



    public void removeSelected() {

//        new Thread(new Runnable() {
//            @Override
//            public void run() {
                for (final PictureAnswer p : getSelectedItems()) {

                    try {
                        HelperFactory.getHelper().getPictureAnswerDAO().delete(p);
                    } catch (SQLException e) {
                        Log.e(TAG, "run: error while delete cached item = " + Log.getStackTraceString(e));
                    }

                    //update UI
                    ((Activity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            removeItem(p);
                        }
                    });
                }
//            }
//        }).start();
    }

    public void stopUpload() {

        for (PictureAnswer p : getItems())
            if (p.getStatus() == 0)
                p.setStatus(2);

        notifyDataSetChanged();
    }

    public void setSelectionState(boolean b) {

        this.selectionState = b;
        notifyDataSetChanged();
    }

    public class ViewHolderItem extends RecyclerView.ViewHolder {

        ItemImageAlbumDetailsBinding binding;
        CheckBox checkBox;
        ImageView view, photoDescriptionIndicator;


        public ViewHolderItem(ItemImageAlbumDetailsBinding binding) {
            super(binding.getRoot());

            this.binding = binding;
            this.checkBox = (CheckBox) binding.getRoot().findViewById(R.id.selection_check);
            this.view = binding.getRoot().findViewById(R.id.iv_image);
            this.photoDescriptionIndicator = binding.getRoot().findViewById(R.id.photoDescriptionIndicator);
        }


    }
}
