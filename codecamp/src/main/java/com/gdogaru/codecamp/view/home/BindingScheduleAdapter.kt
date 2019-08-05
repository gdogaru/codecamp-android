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

package com.gdogaru.codecamp.view.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import com.android.example.github.ui.common.DataBoundListAdapter
import com.gdogaru.codecamp.R
import com.gdogaru.codecamp.databinding.MainScheduleItemBinding
import com.gdogaru.codecamp.util.AppExecutors

class BindingScheduleAdapter(
        private val dataBindingComponent: DataBindingComponent,
        appExecutors: AppExecutors,
        private val repoClickCallback: ((MainViewItem) -> Unit)?
) : DataBoundListAdapter<MainViewItem, MainScheduleItemBinding>(
        appExecutors = appExecutors,
        diffCallback = object : DiffUtil.ItemCallback<MainViewItem>() {
            override fun areItemsTheSame(oldItem: MainViewItem, newItem: MainViewItem): Boolean {
                return oldItem.title == newItem.title && oldItem.subtitle == newItem.subtitle
            }

            override fun areContentsTheSame(oldItem: MainViewItem, newItem: MainViewItem): Boolean {
                return oldItem.title == newItem.title && oldItem.subtitle == newItem.subtitle
            }
        }
) {
    override fun createBinding(parent: ViewGroup): MainScheduleItemBinding {
        val binding = DataBindingUtil.inflate<MainScheduleItemBinding>(
                LayoutInflater.from(parent.context),
                R.layout.main_schedule_item,
                parent,
                false,
                dataBindingComponent
        )
        binding.root.setOnClickListener {
            binding.item?.let {
                repoClickCallback?.invoke(it)
            }
        }
        return binding
    }

    override fun bind(binding: MainScheduleItemBinding, item: MainViewItem) {
        binding.item = item
    }

}