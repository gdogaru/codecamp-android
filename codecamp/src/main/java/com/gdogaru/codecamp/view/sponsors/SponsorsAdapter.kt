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

package com.gdogaru.codecamp.view.sponsors

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import com.android.example.github.ui.common.DataBoundListAdapter
import com.gdogaru.codecamp.R
import com.gdogaru.codecamp.api.model.Sponsor
import com.gdogaru.codecamp.databinding.SponsorsItemBinding
import com.gdogaru.codecamp.util.AppExecutors

class SponsorsAdapter(private val dataBindingComponent: DataBindingComponent,
                      appExecutors: AppExecutors,
                      private val listener: (Sponsor) -> Unit)
    : DataBoundListAdapter<Sponsor, SponsorsItemBinding>(
        appExecutors = appExecutors,
        diffCallback = object : DiffUtil.ItemCallback<Sponsor>() {
            override fun areItemsTheSame(oldItem: Sponsor, newItem: Sponsor): Boolean {
                return oldItem.name == newItem.name
            }

            override fun areContentsTheSame(oldItem: Sponsor, newItem: Sponsor): Boolean {
                return oldItem.websiteUrl == newItem.websiteUrl && oldItem.logoUrl == newItem.logoUrl
            }
        }
) {
    override fun createBinding(parent: ViewGroup): SponsorsItemBinding {
        val binding = DataBindingUtil.inflate<SponsorsItemBinding>(
                LayoutInflater.from(parent.context),
                R.layout.sponsors_item,
                parent,
                false,
                dataBindingComponent
        )
        binding.root.setOnClickListener {
            binding.sponsor?.let {
                listener.invoke(it)
            }
        }
        return binding
    }

    override fun bind(binding: SponsorsItemBinding, item: Sponsor) {
        binding.sponsor = item
    }
}