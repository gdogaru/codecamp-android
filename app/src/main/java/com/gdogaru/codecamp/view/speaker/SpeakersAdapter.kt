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

package com.gdogaru.codecamp.view.speaker

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import com.android.example.github.ui.common.DataBoundListAdapter
import com.gdogaru.codecamp.R
import com.gdogaru.codecamp.api.model.Speaker
import com.gdogaru.codecamp.databinding.SpeakersItemBinding
import com.gdogaru.codecamp.util.AppExecutors

class SpeakersAdapter(
    private val dataBindingComponent: DataBindingComponent,
    appExecutors: AppExecutors,
    private val callback: (Speaker) -> Unit
) : DataBoundListAdapter<Speaker, SpeakersItemBinding>(
    appExecutors = appExecutors,
    diffCallback = object : DiffUtil.ItemCallback<Speaker>() {
        override fun areItemsTheSame(oldItem: Speaker, newItem: Speaker): Boolean {
            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(oldItem: Speaker, newItem: Speaker): Boolean {
            return oldItem == newItem
        }
    }
) {
    override fun createBinding(parent: ViewGroup): SpeakersItemBinding {
        val binding = DataBindingUtil.inflate<SpeakersItemBinding>(
            LayoutInflater.from(parent.context),
            R.layout.speakers_item,
            parent,
            false,
            dataBindingComponent
        )
        binding.root.setOnClickListener {
            binding.speaker?.let {
                callback.invoke(it)
            }
        }
        return binding
    }

    override fun bind(binding: SpeakersItemBinding, item: Speaker) {
        binding.speaker = item
    }

}
