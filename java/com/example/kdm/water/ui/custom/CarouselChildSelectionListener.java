package com.example.kdm.water.ui.custom;


import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/* Ansh note: this libary looked like a deprecated library, without no updates by the dev for last 1 yr, so i pulled it and am currently using it inside the code ittself
 * */


public abstract class CarouselChildSelectionListener {

    @NonNull
    private final RecyclerView mRecyclerView;
    @NonNull
    private final CarouselLayoutManager mCarouselLayoutManager;

    private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(final View v) {
            final RecyclerView.ViewHolder holder = mRecyclerView.getChildViewHolder(v);
            final int position = holder.getAdapterPosition();

            if (position == mCarouselLayoutManager.getCenterItemPosition()) {
                onCenterItemClicked(mRecyclerView, mCarouselLayoutManager, v);
            } else {
                onBackItemClicked(mRecyclerView, mCarouselLayoutManager, v);
            }
        }
    };

    protected CarouselChildSelectionListener(@NonNull final RecyclerView recyclerView, @NonNull final CarouselLayoutManager carouselLayoutManager) {
        mRecyclerView = recyclerView;
        mCarouselLayoutManager = carouselLayoutManager;

        mRecyclerView.addOnChildAttachStateChangeListener(new RecyclerView.OnChildAttachStateChangeListener() {
            @Override
            public void onChildViewAttachedToWindow(@NonNull final View view) {
                view.setOnClickListener(mOnClickListener);
            }

            @Override
            public void onChildViewDetachedFromWindow(@NonNull final View view) {
                view.setOnClickListener(null);
            }
        });
    }

    protected abstract void onCenterItemClicked(@NonNull final RecyclerView recyclerView, @NonNull final CarouselLayoutManager carouselLayoutManager, @NonNull final View v);

    protected abstract void onBackItemClicked(@NonNull final RecyclerView recyclerView, @NonNull final CarouselLayoutManager carouselLayoutManager, @NonNull final View v);
}
