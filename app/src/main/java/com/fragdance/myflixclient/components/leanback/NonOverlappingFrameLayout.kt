/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.fragdance.myflixclient.components.leanback

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import kotlin.jvm.JvmOverloads

internal class NonOverlappingFrameLayout : FrameLayout {
    @JvmOverloads
    constructor(context: Context?, attrs: AttributeSet? = null) : super(
        context!!, attrs, 0
    ) {
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyle: Int) : super(
        context!!, attrs, defStyle
    ) {
    }

    /**
     * Avoid creating hardware layer when Transition is animating alpha.
     */
    override fun hasOverlappingRendering(): Boolean {
        return false
    }
}