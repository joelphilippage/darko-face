package com.turndapage.wear.watchface.watchfacedarko.adapter;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;

import com.turndapage.wear.watchface.watchfacedarko.App;
import com.turndapage.wear.watchface.watchfacedarko.R;


public class PagerIndicator extends RecyclerView.ItemDecoration {
    private int colorActive;
    private int colorInactive;

    private static final float DP = Resources.getSystem().getDisplayMetrics().density;

    /**
     * Height of the space the indicator takes up at the bottom of the view.
     */
    private final int mIndicatorHeight = (int) (DP * 16);

    /**
     * Indicator stroke width.
     */
    private final float mIndicatorStrokeWidth = DP * 2;

    /**
     * Indicator width.
     */
    private final float mIndicatorItemLength = DP * 16;
    /**
     * Padding between indicators.
     */
    private final float mIndicatorItemPadding = DP * 8;

    /**
     * Some more natural animation interpolation
     */
    private final Interpolator mInterpolator = new AccelerateDecelerateInterpolator();

    private final Paint mPaint = new Paint();

    public PagerIndicator() {
        colorActive = App.getAppContext().getColor(R.color.black);
        colorInactive = App.getAppContext().getColor(R.color.accent);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(mIndicatorStrokeWidth);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setAntiAlias(true);
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDrawOver(c, parent, state);

        int itemCount = parent.getAdapter().getItemCount();

        // center horizontally, calculate height and subtract half from center
        float totalLength = mIndicatorItemLength * itemCount;
        float paddingBetweenItems = Math.max(0, itemCount - 1) * mIndicatorItemPadding;
        float indicatorTotalHeight = totalLength + paddingBetweenItems;
        float indicatorStartY = (parent.getWidth() - indicatorTotalHeight) / 2F;

        // center vertically in the allotted space
        float indicatorPosY = parent.getHeight() - mIndicatorHeight;

        drawInactiveIndicators(c, indicatorStartY, indicatorPosY, itemCount);


        // find active page (which should be highlighted)
        LinearLayoutManager layoutManager = (LinearLayoutManager) parent.getLayoutManager();
        int activePosition = layoutManager.findLastVisibleItemPosition();
        if (activePosition == RecyclerView.NO_POSITION) {
            return;
        }

        // find offset of active page (if the user is scrolling)
        final View activeChild = layoutManager.findViewByPosition(activePosition);
        int left = activeChild.getLeft();
        int height = activeChild.getHeight();

        // on swipe the active item will be positioned from [-height, 0]
        // interpolate offset for smooth animation
        float progress = mInterpolator.getInterpolation(left * -1 / (float) height);

        drawHighlights(c, indicatorStartY, indicatorPosY, activePosition, progress, itemCount);
    }

    private void drawInactiveIndicators(Canvas c, float indicatorStartY, float indicatorPosY, int itemCount) {
        mPaint.setColor(colorInactive);
        mPaint.setStyle(Paint.Style.STROKE);

        // width of item indicator including padding
        final float itemWidth = mIndicatorItemLength + mIndicatorItemPadding;

        float start = indicatorStartY;
        for (int i = 0; i < itemCount; i++) {
            // draw the line for every item
            //    c.drawLine(start, indicatorPosY, start + mIndicatorItemLength, indicatorPosY, mPaint);
            c.drawCircle(indicatorPosY, start, (mIndicatorItemLength) / 2, mPaint);
            start += itemWidth;
        }
    }

    private void drawHighlights(Canvas c, float indicatorStartY, float indicatorPosY,
                                int highlightPosition, float progress, int itemCount) {
        mPaint.setColor(colorActive);
        mPaint.setStyle(Paint.Style.FILL);

        // width of item indicator including padding
        final float itemHeight = mIndicatorItemLength + mIndicatorItemPadding;

        if (progress == 0F) {
            // no swipe, draw a normal indicator
            float highlightStart = indicatorStartY + itemHeight * highlightPosition;
            //    c.drawLine(highlightStart, indicatorPosY,highlightStart + mIndicatorItemLength, indicatorPosY, mPaint);
            c.drawCircle(indicatorPosY, highlightStart,(mIndicatorItemLength) / 2, mPaint);
        } else {
            float highlightStart = indicatorStartY + itemHeight * highlightPosition;
            // calculate partial highlight

            // draw the highlight overlapping to the next item as well
            if (highlightPosition < itemCount - 1) {
                highlightStart += itemHeight;
                //    c.drawLine(highlightStart, indicatorPosY,highlightStart + partialLength, indicatorPosY, mPaint);
                c.drawCircle(indicatorPosY, highlightStart, (mIndicatorItemLength) / 2, mPaint);

            }
        }
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        outRect.bottom = mIndicatorHeight;
    }
}
