package com.ganbook.ui;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.ganbook.activities.FavoriteActivity;
import com.ganbook.communication.json.getfavorite_Response;
import com.ganbook.universalloader.PicassoManager;
import com.ganbook.utils.ImageUtils;
import com.project.ganim.R;
import com.project.ganim.databinding.ItemImageAlbumDetailsBinding;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.tonicartos.superslim.GridSLM;
import com.tonicartos.superslim.LayoutManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class FavoritesAdapter extends RecyclerView.Adapter<FavoritesAdapter.FavoriteItemViewHolder> {
    private LayoutInflater mInflater;
    private static FavoriteActivity activity;
    private static ArrayList<Item> favorites;
    private boolean selectionState = false;

    public static final int FAVORITE_ITEM = 1;
    public static final int HEADER_ITEM = 0;
    public static final int TEXT_ITEM = 2;

//    public FavoritesAdapter(Context context, int resource, List<FavoritesAdapter.Item> objects) {
//        super(context, resource, objects);
//    }

    public FavoritesAdapter(Context context, HashMap<String, ArrayList<getfavorite_Response>> map, FavoriteActivity activity) {
//        super(context, 0, mapToArrayList(map));
        favorites = mapToArrayList(map);
        mInflater = LayoutInflater.from(context);
        this.activity = activity;
    }


//    @Override
//    public int getViewTypeCount() {
//        return RowType.values().length;
//
//    }

    @Override
    public int getItemCount() {
        return favorites.size();
    }

    @Override
    public FavoriteItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View mRoot;

        if (viewType == HEADER_ITEM) {
            mRoot = mInflater.inflate(R.layout.item_composer_header, parent, false);
            return new HeaderItem.HeaderItemViewHolder(mRoot);
        }
        else if (viewType == TEXT_ITEM) {
            mRoot = mInflater.inflate(R.layout.item_composer_empty_favorite, parent, false);
            return new TextItem.TextItemViewHolder(mRoot);
        }
        else {
            mRoot = mInflater.inflate(R.layout.favorite_item, parent, false);
            mRoot.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    activity.onItemClick(v);
                }
            });
            return new FavoriteItem.FavoritetemViewHolder( mRoot);
        }
    }

    int selectedPosition = -1;
    @Override
    public void onBindViewHolder(FavoriteItemViewHolder holder, final int position) {
        favorites.get(position).onBindViewHolder(holder,position);

    }

    @Override
    public int getItemViewType(int position) {
        return favorites.get(position).getViewType();
    }


//    @Override
//    public View getView(int position, View convertView, ViewGroup parent) {
//        return getItem(position).getView(mInflater, convertView, parent);
//    }
    
    public static ArrayList<Item> mapToArrayList(HashMap<String, ArrayList<getfavorite_Response>> map)
    {
    	ArrayList<Item> favorites = new ArrayList<Item>();
    	
    	if(map == null || map.isEmpty())
    	{
            Item textItem = new TextItem();
            favorites.add(textItem);
    		return favorites;
    	}
    	
    	List<String> list = new ArrayList<String>();
    	list.addAll(map.keySet());
    	
    	Collections.sort(list, new Comparator<String>() {
    	    public int compare(String o1, String o2) {
    	        Integer i1 = Integer.parseInt(o1);
    	        Integer i2 = Integer.parseInt(o2);
    	        return (i1 > i2 ? -1 : (i1 == i2 ? 0 : 1));
    	    }
    	});

        int section = 0;
    	
    	for (String year : list) 
    	{
    		Item yearItem = new HeaderItem(year, section);

            favorites.add(yearItem);
    		
    		ArrayList<getfavorite_Response> picsArr = map.get(year);

//            picsArr = new ArrayList<>();

            if(picsArr.isEmpty()) {
                Item textItem = new TextItem();
                favorites.add(textItem);
            }
            else {
                for (getfavorite_Response favorite : picsArr) {
                    Item favoritetem = new FavoriteItem(favorite.picture_id, favorite.picture_name, favorite.album_id, favorite.picture_active, favorite.video_duration, favorite.class_id, favorite.gan_id,section);
                    favorites.add(favoritetem);
                }
            }

            section ++;
    	}
    	 
    	return favorites;
    }



    public boolean isHeader(int position) {
        return favorites.get(position).getViewType() == HEADER_ITEM || favorites.get(position).getViewType() == TEXT_ITEM;
    }

    public static class FavoriteItem implements Item
    {
        public String picture_id;
        public String picture_name;
        public String album_id;
        public String picture_active;
        public String video_duration;
        public String class_id;
        public String gan_id;
        public int section;

        public FavoriteItem(String picture_id, String picture_name, String album_id, String picture_active, String video_duration, String class_id, String gan_id, int section) {

        	this.picture_id = picture_id;
            this.picture_name = picture_name;
            this.album_id = album_id;
            this.picture_active = picture_active;
            this.video_duration = video_duration;
            this.class_id = class_id;
            this.gan_id = gan_id;
            this.section = section;
        }

        static class FavoritetemViewHolder extends FavoriteItemViewHolder implements View.OnClickListener{

            @BindView(R.id.header)

            TextView header;

            @BindView(R.id.iv_image)

            ImageView album_image;

            @BindView(R.id.play)

            ImageView play;

            @BindView(R.id.duration)

            TextView duration;

            public FavoritetemViewHolder(View itemView)
            {
                super(itemView);

                header = (TextView) itemView.findViewById(R.id.header);
                album_image = (ImageView) itemView.findViewById(R.id.iv_image);
                play = (ImageView) itemView.findViewById(R.id.play);
                duration = (TextView) itemView.findViewById(R.id.duration);

            }

            @Override
            public void onClick(View v) {
                activity.onItemClick(v);
            }
        };

        @Override
        public int getViewType() {
            return FAVORITE_ITEM;
        }

        @Override
        public void onBindViewHolder(final FavoriteItemViewHolder holder, int position) {

            final View itemView = holder.itemView;

//            float imageHeight = activity.getResources().getDimension("100dp");

//            imageHeight = mContext.getResources().getDimension(R.dimen.grid_item_height);

            GridSLM.LayoutParams params = GridSLM.LayoutParams.from(itemView.getLayoutParams());
            params.setSlm(GridSLM.ID);

//            params.setNumColumns(4);
//            params.setColumnWidth(GridSLM.LayoutParams.MATCH_PARENT);

            final String url = ImageUtils.getThumbnail(gan_id, class_id, album_id, picture_name);

            PicassoManager.displayImage(activity, url, ((FavoritetemViewHolder)holder).album_image, R.drawable.empty_image);

            Picasso.with(activity)
                    .load(url)
                    .placeholder(R.drawable.empty_image)
                    .into(((FavoritetemViewHolder) holder).album_image, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {

                            Picasso.with(activity)
                                    .load(url.replace(".jpeg", ".jpg"))
                                    .placeholder(R.drawable.empty_image)
                                    .into(((FavoritetemViewHolder) holder).album_image, new Callback() {
                                        @Override
                                        public void onSuccess() {

                                        }

                                        @Override
                                        public void onError() {


                                        }
                                    });
                        }
                    });

            if(url != null && url.contains("VID"))
            {
                if(video_duration != null) {
//                    long duration_long = Long.valueOf(video_duration).longValue();
//                    String duration = String.format("%02d:%02d",
//                            video_duration);

                    ((FavoritetemViewHolder)holder).duration.setText(video_duration);
                    ((FavoritetemViewHolder)holder).duration.setVisibility(View.VISIBLE);
                }

                ((FavoritetemViewHolder)holder).play.setVisibility(View.VISIBLE);
            }
            else
            {
                ((FavoritetemViewHolder)holder).play.setVisibility(View.GONE);
                ((FavoritetemViewHolder)holder).duration.setVisibility(View.GONE);
            }

            params.setFirstPosition(section);
            itemView.setLayoutParams(params);
        }


//        @Override
//        public View getView(LayoutInflater inflater, View convertView, ViewGroup parent) {
////            View view;
//            ViewHolder vholder;
//
//            if (convertView == null) {
//                vholder = new ViewHolder();
//                convertView = inflater.inflate(R.layout.single_pict_in_gallery, null);
//                vholder = new ViewHolder();
//                vholder.album_image = (ImageView) convertView.findViewById(R.id.iv_image);
//                vholder.duration = (TextView) convertView.findViewById(R.id.duration);
//                vholder.play = (ImageView) convertView.findViewById(R.id.play);
//
//                convertView.setTag(vholder);
//            } else {
//                vholder = (ViewHolder) convertView.getTag();;
//            }
//
//            String url = ImageUtils.getThumbnail(gan_id, class_id, album_id, picture_name);
//
//            PicassoManager.displayImage(activity, url, vholder.album_image, R.drawable.empty_image);
//
//            return convertView;
//        }

    }
    
    public static class HeaderItem implements Item
    {
    	public final String         name;
        public final int         section;

        public HeaderItem(String name, int section) {
            this.name = name;
            this.section = section;
        }

        static class HeaderItemViewHolder extends FavoriteItemViewHolder{

            @BindView(R.id.header)

            TextView header;

            public HeaderItemViewHolder(View itemView)
            {
                super(itemView);

                ButterKnife.bind(this, itemView);
            }

        };

        @Override
        public int getViewType() {
            return HEADER_ITEM;
        }

        @Override
        public void onBindViewHolder(FavoriteItemViewHolder holder, int position) {

            final View itemView = holder.itemView;

//            float imageHeight = activity.getResources().getDimension("100dp");


            LayoutManager.LayoutParams params = GridSLM.LayoutParams.from(itemView.getLayoutParams());
            params.setSlm(GridSLM.ID);

//            ((GridSLM.LayoutParams)params ).setNumColumns(4);
//            ((GridSLM.LayoutParams)params ).setColumnWidth(GridSLM.LayoutParams.MATCH_PARENT);

            params.isHeader = true;

            ((HeaderItemViewHolder)holder).header.setText(name);

            params.setFirstPosition(section);
            itemView.setLayoutParams(params);
        }

//        @Override
//        public View getView(LayoutInflater inflater, View convertView, ViewGroup parent) {
//            View view;
//            if (convertView == null) {
//                view = (View) inflater.inflate(R.layout.item_composer_header, parent, false);
//                // Do some initialization
//            } else {
//                view = convertView;
//            }
//
//            TextView header = (TextView) view.findViewById(R.id.header);
//            header.setText(name);
//
//            return view;
//        }
    }

    public static class TextItem implements Item
    {

        static class TextItemViewHolder extends FavoriteItemViewHolder{

//            @BindView(R.id.tv)
//            @Optional
//            TextView tv;

            public TextItemViewHolder(View itemView)
            {
                super(itemView);

                ButterKnife.bind(this, itemView);
            }

        };

        @Override
        public int getViewType() {
            return TEXT_ITEM;
        }

        @Override
        public void onBindViewHolder(FavoriteItemViewHolder holder, int position) {

            final View itemView = holder.itemView;

//            float imageHeight = activity.getResources().getDimension("100dp");

            LayoutManager.LayoutParams params = GridSLM.LayoutParams.from(itemView.getLayoutParams());
            params.setSlm(GridSLM.ID);

//            ((TextItemViewHolder)holder).tv.setText(activity.getString(R.string.empty_albums_list));

            params.setFirstPosition(position);
            itemView.setLayoutParams(params);
        }

//        public View getView() {
////            View view;
////            if (convertView == null) {
////                view = (View) inflater.inflate(R.layout.item_composer_text, parent, false);
////                // Do some initialization
////            } else {
////                view = convertView;
////            }
//
//            tv = (TextView) view.findViewById(R.id.tv);
//
//            if(User.isParent())
//            {
//                tv.setText(activity.getString(R.string.empty_albums_list));
//            }
//
//            return view;
//        }
    }
    
    public static  class FavoriteItemViewHolder extends RecyclerView.ViewHolder
    {

        public FavoriteItemViewHolder(View itemView) {
            super(itemView);


        }

    }

    public class FavoriteViewHolderItem extends RecyclerView.ViewHolder {

        ItemImageAlbumDetailsBinding binding;
        CheckBox checkBox;
        ImageView view;


        public FavoriteViewHolderItem(ItemImageAlbumDetailsBinding binding) {
            super(binding.getRoot());

            this.binding = binding;
            this.checkBox = (CheckBox) binding.getRoot().findViewById(R.id.selection_check);
            this.view = (ImageView) binding.getRoot().findViewById(R.id.iv_image);
        }


    }


    public interface Item
    {
        public int getViewType();
//        public View getView(LayoutInflater inflater, View convertView, ViewGroup parent);
        public void onBindViewHolder(FavoriteItemViewHolder holder, int position);
    }

}


