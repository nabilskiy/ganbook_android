package com.ganbook.adapters;

import android.content.Context;
import androidx.databinding.DataBindingUtil;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.ganbook.interfaces.GridGalleryInterface;
import com.ganbook.interfaces.Toggleable;
import com.ganbook.models.GridViewPicture;
import com.project.ganim.R;
import com.project.ganim.databinding.ItemGridGalleryBinding;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dmytro_vodnik on 7/4/16.
 * working on ganbook1 project
 */
public class GalleryPicsGridAdapter extends BaseAdapter implements Toggleable<GridViewPicture> {

    LayoutInflater inflater;
    List<GridViewPicture> items;
    int size = 0;
    int max = 0;

    GridGalleryInterface gridGalleryInterface;

    public GalleryPicsGridAdapter(Context context, List<GridViewPicture> items,
                                  GridGalleryInterface gridGalleryInterface) {
        this.items = items;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        size = (int) context.getResources().getDimension(R.dimen.big_tmb_size);
        this.gridGalleryInterface = gridGalleryInterface;
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);

    }

    @Override
    public int getCount() {
        return items.size();
    }


    @Override
    public Object getItem(int position) {
        return items.get(position);
    }


    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final GridViewPicture gridViewPicture = items.get(position);
        final int Max = max;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_grid_gallery, null);
        }

        ItemGridGalleryBinding binding = DataBindingUtil.bind(convertView);

        binding.setGridPicture(gridViewPicture);

        ImageView imageView = (ImageView) convertView.findViewById(R.id.imageView);
        CheckBox ch = (CheckBox) convertView.findViewById(R.id.selection_check);

        imageView.setImageDrawable(null);

        RequestOptions myOptions = new RequestOptions()
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .override(size, size)
                .override(100, 100);


        Glide.with(convertView.getContext())
                .load(gridViewPicture.getPath())
                .apply(myOptions)
                .into(imageView);

        ch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!gridViewPicture.isSelected() && (getSelectedItems().size() == max)) {
                    gridViewPicture.setSelected(false);
                    return;
                }
                toggleItem(gridViewPicture, position);
                gridGalleryInterface.showSelectedCount(getSelectedItems().size());
            }
        });

        if (!gridViewPicture.isDirectory())
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!gridViewPicture.isSelected() && (getSelectedItems().size() == max))
                        return;
                    toggleItem(gridViewPicture, position);
                    gridGalleryInterface.showSelectedCount(getSelectedItems().size());
                }
            });

        return convertView;
    }

    public void setMax(int max ){ this.max = max; }

    @Override
    public void toggleItem(GridViewPicture item, int position) {

        item.setSelected(!item.isSelected());
        notifyDataSetChanged();
    }

    public List<GridViewPicture> getSelectedItems() {

        List<GridViewPicture> selectedItems = new ArrayList<>();

        for (GridViewPicture gridViewPicture : items) {

            if (!gridViewPicture.isDirectory() && gridViewPicture.isSelected())
                selectedItems.add(gridViewPicture);
        }

        return selectedItems;
    }
}