package com.ganbook.dividers;

import android.graphics.Rect;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

/**
 * Created by dmytro_vodnik on 6/7/16.
 * working on ganbook1 project
 */
public class SpaceItemDecoration extends RecyclerView.ItemDecoration {

    private final int mVerticalSpaceHeight;
    /**
     * 0 - vertical
     * 1 - horizontal
     */
    int type;

    public static final int HORIZONTAL = 1;
    public static final int VERTICAL = 0;

    public SpaceItemDecoration(int mVerticalSpaceHeight, int type) {
        this.mVerticalSpaceHeight = mVerticalSpaceHeight;
        this.type = type;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                               RecyclerView.State state) {
        
        switch (type) {
            
            case 0:
                outRect.bottom = mVerticalSpaceHeight;
                break;
            
            case 1:
                
                outRect.left = mVerticalSpaceHeight;
                outRect.right = mVerticalSpaceHeight;
                break;
        }
    }
}
