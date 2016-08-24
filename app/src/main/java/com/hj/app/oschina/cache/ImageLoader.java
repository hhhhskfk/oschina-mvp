package com.hj.app.oschina.cache;

import android.graphics.Bitmap;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;


/**
 * Created by huangjie08 on 2016/8/23.
 */
public class ImageLoader {

    public static void loadImage(ImageView imageView, String url, int loadImage) {
        Picasso.with(imageView.getContext())
                .load(url)
                .placeholder(loadImage)
                .error(loadImage)
                .config(Bitmap.Config.RGB_565)
                .into(imageView);
    }

    public static void loadImage(ImageView imageView, String url) {
        Picasso.with(imageView.getContext())
                .load(url)
                .config(Bitmap.Config.RGB_565)
                .into(imageView);
    }
}
