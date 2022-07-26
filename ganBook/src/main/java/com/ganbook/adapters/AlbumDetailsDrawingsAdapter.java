package com.ganbook.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.ganbook.fragments.DrawingDetailsFragment;
import com.ganbook.interfaces.CRUDAdapterInterface;
import com.ganbook.interfaces.Toggleable;
import com.ganbook.models.DrawingAnswer;
import com.ganbook.models.PictureAnswer;
import com.ganbook.user.User;
import com.ganbook.utils.DBUtils.HelperFactory;
import com.ganbook.utils.FragmentUtils;
import com.ganbook.utils.StrUtils;
import com.project.ganim.R;
import com.project.ganim.databinding.ItemDrawingAlbumDetailsBinding;
import com.project.ganim.databinding.ItemImageAlbumDetailsBinding;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class AlbumDetailsDrawingsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements CRUDAdapterInterface<DrawingAnswer>, Toggleable<DrawingAnswer> {

    private Context context;
    private List<DrawingAnswer> drawingAnswers;
    private List<DrawingAnswer> selectedItems;
    private DrawingAnswer selectedItem;
    private DrawingViewHolder viewHolder;
    private DrawingAnswer drawingAnswer;
    private boolean selectionState = false;

    public AlbumDetailsDrawingsAdapter(Context context) {
        this.context = context;
        this.drawingAnswers = new ArrayList<>();
        this.selectedItems = new ArrayList<>();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ItemDrawingAlbumDetailsBinding binding = DataBindingUtil.inflate(layoutInflater, R.layout.item_drawing_album_details, parent, false);
//        View view = LayoutInflater.from(parent.getContext()).inflate(
//                R.layout.item_drawing_album_details, parent, false);
        viewHolder = new DrawingViewHolder(binding);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {
        final DrawingAnswer drawingAnswer = getItem(position);
        String url = StrUtils.getDrawingAlbumFullSizeUrl(drawingAnswer.getDrawingName(), User.current.getCurrentKidId(), User.current.getCurrentClassId(),User.current.getCurrentGanId());
        ((DrawingViewHolder) holder).imageView.setColorFilter(null);

        ((DrawingViewHolder) holder).binding.setDrawing(drawingAnswer);
        ((DrawingViewHolder) holder).binding.setSelection(selectionState);

        ((DrawingViewHolder) holder).binding.selectionCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleItem(drawingAnswer, position);
            }
        });

        ((DrawingViewHolder) holder).progressBar.setVisibility(View.VISIBLE);
        RequestOptions myOptions = new RequestOptions()
                .centerCrop()
                .dontAnimate()
                .diskCacheStrategy(DiskCacheStrategy.ALL);

        RequestManager rm = Glide.with(((DrawingViewHolder) holder).imageView.getContext());
        rm
        .load(url)
        .listener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object o, Target<Drawable> target, boolean b) {
                ((DrawingViewHolder) holder).progressBar.setVisibility(View.GONE);
                return false;
            }

            @Override
            public boolean onResourceReady(Drawable drawable, Object o, Target<Drawable> target, DataSource dataSource, boolean b) {
                ((DrawingViewHolder) holder).progressBar.setVisibility(View.GONE);
                return false;
            }
        })
        .apply(myOptions)
        .into(((DrawingViewHolder) holder).imageView);


        ((DrawingViewHolder) holder).imageView.setOnClickListener(v -> showItem(drawingAnswer, position));

    }

    public void removeSelected() {
        for (final DrawingAnswer p : getSelectedItems()) {

            try {
                HelperFactory.getHelper().getDrawerAnswerDAO().delete(p);
            } catch (SQLException e) {
            }

            ((Activity) context).runOnUiThread(() -> removeItem(p));
        }
    }


    @Override
    public int getItemCount() {
        return drawingAnswers.size();
    }

    @Override
    public DrawingAnswer getItem(int position) {
        return drawingAnswers.get(position);
    }

    @Override
    public void addItem(DrawingAnswer item) {

        drawingAnswers.add(0, item);
        notifyItemInserted(drawingAnswers.size());
        notifyDataSetChanged();
    }

    @Override
    public void addItem(DrawingAnswer item, int index) {

    }

    @Override
    public void removeItem(DrawingAnswer item) {

        Iterator<DrawingAnswer> iter = drawingAnswers.iterator();

        while (iter.hasNext()) {
            DrawingAnswer str = iter.next();

            if (str.getDrawingId().equals(item.getDrawingId()))
                iter.remove();
        }

        notifyDataSetChanged();
    }

    @Override
    public void addItems(final List<DrawingAnswer> items) {

        new Thread(() -> {

            drawingAnswers.addAll(0, items);

            ((Activity) context).runOnUiThread(() -> notifyDataSetChanged());
        }).start();

    }


    @Override
    public void clearList() {
        drawingAnswers.clear();
        notifyDataSetChanged();
    }

    @Override
    public void updateItem(int pos, DrawingAnswer item) {
        if (drawingAnswers.size() > 0 && pos != -1) {
            drawingAnswers.set(pos, item);
            notifyItemChanged(pos);
        }
    }

    @Override
    public void showItem(DrawingAnswer item, int itemPosition) {

        DrawingDetailsFragment drawingDetailsFragment = DrawingDetailsFragment.newInstance(itemPosition,
                (ArrayList<DrawingAnswer>) getItems());

        FragmentUtils.openFragment(drawingDetailsFragment, R.id.content_root, DrawingDetailsFragment.TAG,
                context, true);
    }

    @Override
    public List<DrawingAnswer> getItems() {
        return drawingAnswers;
    }

    @Override
    public void removeItem(int currentItemPosition) {

    }

    @Override
    public void toggleItem(DrawingAnswer drawingAnswer, int pos) {
        drawingAnswer.setSelected(!drawingAnswer.isSelected());

    }

    public void setSelectionState() {
        this.selectionState = !selectionState;
        notifyDataSetChanged();
    }

    public List<DrawingAnswer> getSelectedItems() {
        selectedItems.clear();

        for (DrawingAnswer d : getItems()) {

            if (d.isSelected()) {
                selectedItems.add(d);
            }

        }


        return selectedItems;
    }

    public DrawingAnswer getSelectedItem() {
        for (DrawingAnswer d : getItems()) {

            if (d.isSelected())
                selectedItem = d;
        }

        return selectedItem;
    }

    public class DrawingViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public ProgressBar progressBar;
        ItemDrawingAlbumDetailsBinding binding;

        public DrawingViewHolder(ItemDrawingAlbumDetailsBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            imageView = binding.getRoot().findViewById(R.id.drawing_single_image);
            progressBar = binding.getRoot().findViewById(R.id.upload_progress);

        }

    }
}
