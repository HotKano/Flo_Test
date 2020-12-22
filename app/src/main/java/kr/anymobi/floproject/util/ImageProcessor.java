package kr.anymobi.floproject.util;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;

public class ImageProcessor {
    ImageView imageView;

    public ImageProcessor(ImageView imageView) {
        this.imageView = imageView;
    }

    public void setImageToView(String location, Context ctx) {
        int viewWidth, viewHeight;

        viewWidth = CommFunc.getXPixel(ctx) / 2;
        viewHeight = CommFunc.getYPixel(ctx) / 4;

        Glide.with(imageView)
                .load(location)
                .transform(new CenterCrop(), new RoundedCorners(15))
                .override(viewWidth, viewHeight)
                .into(imageView);
    }
}
