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

package com.gdogaru.codecamp.view

import android.os.Bundle
import androidx.databinding.DataBindingComponent
import androidx.fragment.app.Fragment
import butterknife.Unbinder
import com.gdogaru.codecamp.binding.FragmentDataBindingComponent
import com.gdogaru.codecamp.di.Injectable

/**
 *
 */
abstract class BaseFragment : Fragment(), Injectable {

    private var unbinder: Unbinder? = null

    var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (unbinder != null) unbinder!!.unbind()
    }

    protected fun manage(unbinder: Unbinder) {
        this.unbinder = unbinder
    }

}
