/*
 * Copyright (c) 2019 Gabriel Dogaru - gdogaru@gmail.com
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *  *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.gdogaru.codecamp.binding

import android.view.View
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide

/**
 * Data Binding adapters specific to the app.
 */
object BindingAdapters {

    @JvmStatic
    @BindingAdapter("visibleGone")
    fun showGone(view: View, gone: Boolean) {
        view.visibility = if (!gone) View.VISIBLE else View.GONE
    }

    @JvmStatic
    @BindingAdapter("visible")
    fun visible(view: View, visible: Boolean) {
        view.visibility = if (visible) View.VISIBLE else View.GONE
    }

//    @JvmStatic
//    @BindingAdapter("imageUrl")
//    fun showImageUrl(view: ImageView, imageUrl: String) {
//        Glide.with(view.context)
//                .load(imageUrl)
//                .into(view)
//    }

    @JvmStatic
    @BindingAdapter("imageUrl", "placeholder")
    fun loadImage(view: ImageView, imageUrl: String, placeholder: Int?) {
        val builder = Glide.with(view).load(imageUrl)
        placeholder?.let { builder.placeholder(it) }
        builder.into(view)
    }

}
