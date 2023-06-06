package com.seejay.badmintonkaki.utilities

import android.content.Context
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.seejay.badmintonkaki.R

class GlideLoader(val context: Context) {
    fun loadImage(img: String, iv: ImageView){
        Glide
            .with(context)
            .load(img)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(iv);
    }
}

