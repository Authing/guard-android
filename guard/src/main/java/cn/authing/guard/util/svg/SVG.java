package cn.authing.guard.util.svg;

import android.graphics.Picture;
import android.graphics.RectF;
import android.graphics.drawable.PictureDrawable;

public class SVG {

    private Picture picture;
    private RectF bounds;
    private RectF limits = null;

    SVG(Picture picture, RectF bounds) {
        this.picture = picture;
        this.bounds = bounds;
    }

    void setLimits(RectF limits) {
        this.limits = limits;
    }

    public PictureDrawable createPictureDrawable() {
        return new PictureDrawable(picture);
//        return new PictureDrawable(picture) {
//            @Override
//            public int getIntrinsicWidth() {
//                if (bounds != null) {
//                    return (int) bounds.width();
//                } else if (limits != null) {
//                    return (int) limits.width();
//                } else {
//                    return -1;
//                }
//            }
//
//            @Override
//            public int getIntrinsicHeight() {
//                if (bounds != null) {
//                    return (int) bounds.height();
//                } else if (limits != null) {
//                    return (int) limits.height();
//                } else {
//                    return -1;
//                }
//            }
//        };
    }

    public Picture getPicture() {
        return picture;
    }

    public RectF getBounds() {
        return bounds;
    }

    public RectF getLimits() {
        return limits;
    }
}