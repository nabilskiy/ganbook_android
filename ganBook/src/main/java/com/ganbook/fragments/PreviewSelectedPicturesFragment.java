package com.ganbook.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.ganbook.adapters.ImagesPagerAdapter;
import com.ganbook.adapters.PreviewsAdapter;
import com.ganbook.amazinglist.special.CustomToast;
import com.ganbook.dividers.SpaceItemDecoration;
import com.ganbook.interfaces.PreviewInterface;
import com.ganbook.models.MediaFile;
import com.ganbook.utils.Const;
import com.project.ganim.R;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PreviewSelectedPicturesFragment extends Fragment implements PreviewInterface {

    private static final String ARG_ALBUM_NAME = "albumName";
    private static final String ARG_PICTURES_ARR = "pictures";
    public static final String TAG = PreviewSelectedPicturesFragment.class.getName();

    @BindView(R.id.previews_recycler)
    RecyclerView previewsRecycler;

    @BindView(R.id.send_btn)
    ImageView sendBtn;

    @BindView(R.id.view_pager)
    ViewPager mViewPager;

    ImagesPagerAdapter imagesPagerAdapter;

    private String albumName;
    private ArrayList<MediaFile> selectedPictures;
    PreviewsAdapter adapter;

    public PreviewSelectedPicturesFragment() {
        // Required empty public constructor
    }

    public static PreviewSelectedPicturesFragment newInstance(String param1, ArrayList<MediaFile> pics) {
        PreviewSelectedPicturesFragment fragment = new PreviewSelectedPicturesFragment();
        Bundle args = new Bundle();
        args.putString(ARG_ALBUM_NAME, param1);
        args.putParcelableArrayList(ARG_PICTURES_ARR, pics);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

        adapter = new PreviewsAdapter(getContext(), this);

        if (getArguments() != null) {

            albumName = getArguments().getString(ARG_ALBUM_NAME);
            selectedPictures = getArguments().getParcelableArrayList(ARG_PICTURES_ARR);

            Log.d(TAG, "onCreate: received album name = " + albumName + " selected pictures " +
            selectedPictures);

            adapter.addItems(selectedPictures);
            imagesPagerAdapter = new ImagesPagerAdapter(getContext(), selectedPictures);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_preview_selected_pictures, container, false);

        ButterKnife.bind(this, rootView);


        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        previewsRecycler.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        previewsRecycler.addItemDecoration(new SpaceItemDecoration(2, SpaceItemDecoration.HORIZONTAL));
        previewsRecycler.setAdapter(adapter);

        mViewPager.setAdapter(imagesPagerAdapter);

        mViewPager.setOffscreenPageLimit(0);


        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d(TAG, "onClick: send button");

                CustomToast.show(getActivity(), R.string.upload_in_progress);
                Intent data = new Intent();
                data.putParcelableArrayListExtra(Const.FILE_ARRAY, new ArrayList<>(adapter.getItems()));
                getActivity().setResult(Activity.RESULT_OK, data);
                getActivity().finish();
            }
        });

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                if (adapter.getItems().size() > 0)
                    adapter.hightlightItem(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        adapter.hightlightItem(0);
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.preview_pictures_menu, menu);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id) {

            case R.id.delete_menu:

                Log.d(TAG, "onClick: delete pic");

                if (adapter.getItems().size() > 0) {

                    adapter.removeItem(mViewPager.getCurrentItem());
                    mViewPager.setAdapter(new ImagesPagerAdapter(getContext(), new ArrayList<>(adapter.getItems())));

                    mViewPager.setCurrentItem(0);
                    adapter.hightlightItem(0);
                }
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void showViewpagerItem(int pos) {

        mViewPager.setCurrentItem(pos);
    }
}
