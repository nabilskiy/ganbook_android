package com.ganbook.adapters;

import android.content.Context;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.ganbook.interfaces.CRUDAdapterInterface;
import com.ganbook.interfaces.PreviewInterface;
import com.ganbook.models.MediaFile;
import com.ganbook.utils.FragmentUtils;
import com.project.ganim.R;
import com.project.ganim.databinding.ItemPreviewBinding;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dmytro_vodnik on 6/29/16.
 * working on ganbook1 project
 */
public class PreviewsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements CRUDAdapterInterface<MediaFile> {

    private static final int TYPE_ITEM = 0;
    private static final int TYPE_FOOTER = 1;
    private static final String TAG = PreviewsAdapter.class.getName();
    Context context;
    LayoutInflater layoutInflater;
    List<MediaFile> mediaFiles;
    PreviewInterface previewInterface;

    public PreviewsAdapter(Context context, PreviewInterface previewInterface) {

        this.context = context;
        this.layoutInflater = LayoutInflater.from(context);
        this.mediaFiles = new ArrayList<>();
        this.previewInterface = previewInterface;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == TYPE_ITEM) {

            ItemPreviewBinding binding = DataBindingUtil.inflate(layoutInflater, R.layout.item_preview,
                    parent, false);

            return new ViewHolderItem(binding);
        }

        if (viewType == TYPE_FOOTER) {

            ImageView imageView = (ImageView) layoutInflater.inflate(R.layout.footer_previews, parent, false);

            return new ViewHolderFooter(imageView);
        }

        throw new RuntimeException("there is no type that matches the type " + viewType +
                " + make sure your using types correctly");
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        if (holder instanceof ViewHolderItem) {

            //handle picture

            final MediaFile mediaFile = getItem(position);

            ((ViewHolderItem) holder).binding.setMedia(mediaFile);

            ((ViewHolderItem) holder).binding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    showItem(mediaFile, position);
                }
            });
        }

        if (holder instanceof ViewHolderFooter) {

            //handle plus button

            ((ViewHolderFooter) holder).addPic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Log.d(TAG, "onClick: plus clicked ");

                    FragmentUtils.popBackStack(context);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mediaFiles.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (isPositionFooter(position))
            return TYPE_FOOTER;

        return TYPE_ITEM;
    }

    private boolean isPositionFooter(int position) {
        return position == mediaFiles.size();
    }

    @Override
    public MediaFile getItem(int position) {
        return mediaFiles.get(position);
    }

    @Override
    public void addItem(MediaFile item) {

    }

    @Override
    public void addItem(MediaFile item, int index) {

    }

    @Override
    public void removeItem(MediaFile item) {

    }

    @Override
    public void addItems(List<MediaFile> items) {

        mediaFiles.addAll(items);
        notifyDataSetChanged();
    }

    @Override
    public void clearList() {

    }

    @Override
    public void updateItem(int pos, MediaFile item) {

    }

    @Override
    public void showItem(MediaFile item, int itemPosition) {

        previewInterface.showViewpagerItem(itemPosition);
    }

    @Override
    public List<MediaFile> getItems() {
        return mediaFiles;
    }

    @Override
    public void removeItem(int currentItemPosition) {

        mediaFiles.remove(currentItemPosition);
        notifyDataSetChanged();
    }

    public void hightlightItem(int position) {

        for (int i = 0; i < mediaFiles.size(); i++) {

            MediaFile mediaFile = mediaFiles.get(i);
            mediaFile.setHighlight(i == position);
        }

        notifyDataSetChanged();
    }

    public class ViewHolderItem extends RecyclerView.ViewHolder {

        ItemPreviewBinding binding;

        public ViewHolderItem(ItemPreviewBinding binding) {
            super(binding.getRoot());

            this.binding = binding;
        }
    }

    private class ViewHolderFooter extends RecyclerView.ViewHolder {

        ImageView addPic;

        public ViewHolderFooter(ImageView imageView) {
            super(imageView);

            this.addPic = imageView;
        }
    }
}
